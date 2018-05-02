/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.nekit.android.qls.setupWizard.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.*
import com.android.billingclient.api.Purchase.PurchasesResult
import ru.nekit.android.qls.data.representation.SKURepresentation
import ru.nekit.android.qls.data.representation.toSKUDetails
import ru.nekit.android.qls.data.representation.toSKUPurchase
import ru.nekit.android.qls.domain.useCases.IBilling
import ru.nekit.android.qls.domain.useCases.SKUUseCases
import java.io.IOException
import java.util.*

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */
class Billing(val context: Context) : PurchasesUpdatedListener, IBilling {

    /**
     * A reference to BillingClient
     */
    private var billingClient: BillingClient? = null

    /**
     * True if billing service is connected now.
     */
    private var isServiceConnected: Boolean = false

    private var tokensToBeConsumed: MutableSet<String>? = null

    /**
     * Returns the value Billing client response code or BILLING_MANAGER_NOT_INITIALIZED if the
     * clien connection response was not received yet.
     */
    var billingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED
        private set

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    interface BillingUpdatesListener {
        fun onBillingClientSetupFinished()

        fun onConsumeFinished(token: String, @BillingResponse result: Int)

        fun onPurchasesUpdated(purchases: List<Purchase>)
    }

    private val billingUpdatesListener = object : Billing.BillingUpdatesListener {

        override fun onBillingClientSetupFinished() {
            querySKUDetails()
        }

        override fun onConsumeFinished(token: String, result: Int) {
            Log.i(TAG, "onConsumeFinished")
        }

        override fun onPurchasesUpdated(purchases: List<Purchase>) {
            Log.i(TAG, "onPurchasesUpdated $purchases")
            SKUUseCases.saveSKUPurchases(purchases.map {
                it.toSKUPurchase(context)
            })
        }

    }

    private fun querySubscriptionsDetails() {
        querySkuDetailsAsync(BillingClient.SkuType.SUBS,
                SKURepresentation.getSubscriptionSkuIdList(context), SkuDetailsResponseListener { responseCode, skuDetailsList ->
            if (responseCode == BillingClient.BillingResponse.OK) {
                Log.i(TAG, "querySubscriptionsDetails")
                SKUUseCases.saveSKUDetails(skuDetailsList.map {
                    it.toSKUDetails(context)
                })
            } else {
                //DO NOTHING
            }
        })
    }

    override fun querySKUDetails() {
        querySubscriptionsDetails()
    }

    /**
     * Listener for the Billing client state to become connected
     */
    interface ServiceConnectedListener {
        fun onServiceConnected(@BillingResponse resultCode: Int)
    }

    init {
        Log.d(TAG, "Creating Billing client.")
        billingClient = BillingClient.newBuilder(context).setListener(this).build()
        Log.d(TAG, "Starting setup.")
        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.

    }

    override fun start() {
        val wasServiceConnected = isServiceConnected
        executeServiceRequest {
            if (!wasServiceConnected) {
                billingUpdatesListener.onBillingClientSetupFinished()
            }
            Log.d(TAG, "Setup successful. Querying inventory.")
            querySKUPurchases()
        }
    }

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    override fun onPurchasesUpdated(resultCode: Int, purchases: List<Purchase>?) {
        // val localPurchases = listOf(Purchase())
        when (resultCode) {
            BillingResponse.OK -> {
                Log.i(TAG, "onPurchasesUpdated: $purchases")
                for (purchase in purchases!!) {
                    handlePurchase(purchase)
                }
                billingUpdatesListener.onPurchasesUpdated(purchases)
            }
            BillingResponse.USER_CANCELED -> Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping")
            else -> Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: $resultCode")
        }
    }

    /**
     * Start a purchase flow
     */
    fun initiatePurchaseFlow(activity: Activity,
                             skuId: String, @SkuType billingType: String) {
        initiatePurchaseFlow(activity, skuId, null, billingType)
    }

    /**
     * Start a purchase or subscription replace flow
     */
    fun initiatePurchaseFlow(activity: Activity, skuId: String, oldSku: String?,
                             @SkuType billingType: String) {
        val purchaseFlowRequest: () -> Unit = {
            Log.d(TAG, "Launching in-app purchase flow. Replace old SKU? " + (oldSku != null))
            val purchaseParams = BillingFlowParams.newBuilder()
                    .setReplaceSkusProration(true)
                    .setSku(skuId).setType(billingType).setOldSkus(arrayListOf(oldSku)).build()
            billingClient!!.launchBillingFlow(activity, purchaseParams)
        }
        executeServiceRequest(purchaseFlowRequest)
    }

    /**
     * Clear the resources
     */
    override fun destroy() {
        Log.d(TAG, "Destroying the manager.")

        if (billingClient != null && billingClient!!.isReady) {
            billingClient!!.endConnection()
            billingClient = null
        }
    }

    fun querySkuDetailsAsync(@SkuType itemType: String, skuList: List<String>,
                             listener: SkuDetailsResponseListener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        val queryRequest = {
            // Query the purchase async
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(itemType)
            billingClient!!.querySkuDetailsAsync(params.build(), listener)
        }

        executeServiceRequest(queryRequest)
    }

    fun consumeAsync(purchaseToken: String) {
        // If we've already scheduled to consume this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
        if (tokensToBeConsumed == null) {
            tokensToBeConsumed = HashSet()
        } else if (tokensToBeConsumed!!.contains(purchaseToken)) {
            Log.i(TAG, "Token was already scheduled to be consumed - skipping...")
            return
        }
        tokensToBeConsumed!!.add(purchaseToken)

        // Generating Consume Response listener
        val onConsumeListener = { responseCode: Int, purchaseTokenLocal: String ->
            // If billing service was disconnected, we try to reconnect 1 time
            // (feel free to introduce your retry policy here).
            billingUpdatesListener.onConsumeFinished(purchaseTokenLocal, responseCode)
        }

        // Creating a runnable from the request to use it inside our connection retry policy below
        val consumeRequest = {
            // Consume the purchase async
            billingClient!!.consumeAsync(purchaseToken, onConsumeListener)
        }

        executeServiceRequest(consumeRequest)
    }

    /**
     * Handles the purchase
     *
     * Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * See [Security.verifyPurchase]
     *
     *
     * @param purchase Purchase to be handled
     */
    private fun handlePurchase(purchase: Purchase) {
        if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
            Log.i(TAG, "Got a purchase: $purchase; but signature is bad. Skipping...")
            return
        }
        Log.d(TAG, "Got a verified purchase: $purchase")
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private fun onQueryPurchasesFinished(result: PurchasesResult) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (billingClient == null || result.responseCode != BillingResponse.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.responseCode
                    + ") was bad - quitting")
            return
        }

        Log.d(TAG, "Query inventory was successful.")

        // Update the UI and purchases inventory with new list of purchases
        onPurchasesUpdated(BillingResponse.OK, result.purchasesList)
    }

    /**
     * Checks if subscriptionDetails are supported for current client
     *
     * Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after querySKUPurchases execution, which already has
     * a retry-mechanism implemented.
     *
     */
    fun areSubscriptionsSupported(): Boolean {
        val responseCode = billingClient!!.isFeatureSupported(FeatureType.SUBSCRIPTIONS)
        if (responseCode != BillingResponse.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: $responseCode")
        }
        return responseCode == BillingResponse.OK
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    override fun querySKUPurchases() {
        val queryToExecute = {
            val time = System.currentTimeMillis()
            val purchasesResult = billingClient!!.queryPurchases(SkuType.INAPP)
            Log.i(TAG, "Querying purchases elapsed time: " + (System.currentTimeMillis() - time)
                    + "ms")
            // If there are subscriptionDetails supported, we add subscription rows as well
            if (areSubscriptionsSupported()) {
                val subscriptionResult = billingClient!!.queryPurchases(SkuType.SUBS)
                Log.i(TAG, "Querying purchases and subscriptionDetails elapsed time: "
                        + (System.currentTimeMillis() - time) + "ms")
                Log.i(TAG, "Querying subscriptionDetails result code: "
                        + subscriptionResult.responseCode
                        + " res: " + subscriptionResult.purchasesList.size)

                if (subscriptionResult.responseCode == BillingResponse.OK) {
                    purchasesResult.purchasesList.addAll(
                            subscriptionResult.purchasesList)
                } else {
                    Log.e(TAG, "Got an error response trying to query subscription purchases")
                }
            } else if (purchasesResult.responseCode == BillingResponse.OK) {
                Log.i(TAG, "Skipped subscription purchases query since they are not supported")
            } else {
                Log.w(TAG, "querySKUPurchases() got an error response code: " + purchasesResult.responseCode)
            }
            onQueryPurchasesFinished(purchasesResult)
        }
        executeServiceRequest(queryToExecute)
    }

    private fun startServiceConnection(executeOnSuccess: () -> Unit) {
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {
                Log.d(TAG, "Setup finished. Response code: $billingResponseCode")
                if (billingResponseCode == BillingResponse.OK) {
                    isServiceConnected = true
                    executeOnSuccess()
                }
                billingClientResponseCode = billingResponseCode
            }

            override fun onBillingServiceDisconnected() {
                isServiceConnected = false
            }
        })
    }

    private fun executeServiceRequest(runnable: () -> Unit) {
        if (isServiceConnected) {
            runnable()
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable)
        }
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     *
     * Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     *
     */
    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't set these checks on your app!)
        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
            throw RuntimeException("Please update your app's public key at: " + "BASE_64_ENCODED_PUBLIC_KEY")
        }
        return try {
            Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature)
        } catch (e: IOException) {
            Log.e(TAG, "Got an exception trying to validate a purchase: $e")
            false
        }
    }

    companion object {
        const val BILLING_MANAGER_NOT_INITIALIZED = -1

        private val TAG = "ru.nekit.android.qls"

        private const val BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5nlWPcDCGp3ObtbAzn/m3pQL0mAP5DbZ77B2q2kTBnkbzNtNn0v1stL87atnXcDhp99GfsyyOpOTxUWXzGD2IJlxXysxX21SPefND5CCaw2frfJdk6szEdnpXSnqA+BagtJjIVagfTgXUjDvKa2rcBW+bi+BJvLH1xsdFENGXsTx5FvMMRBtbda9CGwnm1XC+G+eu830+yuTQGcF1nk9cwGzPt09p5BNheZB/7FHM0FfRINTFbZKw4UpJCnDYv3Pn/iC35uZi1qS4zxRNq2okWZtz5cF/bqsTX5naghVRzBrtmtsXdAbLJH2w0cRd9Paam2XbjcvJlhaF038BuZATwIDAQAB"
    }
}

