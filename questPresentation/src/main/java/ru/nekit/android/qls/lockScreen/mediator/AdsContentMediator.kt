package ru.nekit.android.qls.lockScreen.mediator

import android.content.Context
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import at.grabner.circleprogress.CircleProgressView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import io.reactivex.Completable
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.useCases.AdsUseCases.listenAdsSkipTimer
import ru.nekit.android.qls.domain.useCases.TransitionChoreographUseCases
import ru.nekit.android.qls.lockScreen.LockScreen
import ru.nekit.android.qls.lockScreen.mediator.LockScreenContentMediatorAction.HIDE
import ru.nekit.android.qls.lockScreen.mediator.LockScreenContentMediatorAction.SHOW
import ru.nekit.android.qls.lockScreen.mediator.common.AbstractLockScreenContentMediator
import ru.nekit.android.qls.lockScreen.mediator.common.ILockScreenContentViewHolder
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.utils.ViewHolder

class AdsContentMediator(override var questContext: QuestContext) :
        AbstractLockScreenContentMediator() {

    companion object {
        internal const val ADS_EVENT_NAME = "ads"
    }

    override lateinit var viewHolder: LockScreenAdsViewContentHolder
    private lateinit var interstitialAd: InterstitialAd

    init {
        viewHolder = LockScreenAdsViewContentHolder(questContext)
        viewHolder.okButton.click {
            TransitionChoreographUseCases.doNextTransition()
        }
        autoDispose(ADS_EVENT_NAME) {
            listenAdsSkipTimer { current, max ->
                viewHolder.apply {
                    loadingIndicator.maxValue = max.toFloat()
                    loadingIndicatorView.text = String.format(questContext.getString(R.string.ads_timeout_title), max - current)
                    loadingIndicator.setValue(current.toFloat())
                    if (current >= max) {
                        okButton.visibility = VISIBLE
                        loadingIndicatorContainer.visibility = INVISIBLE
                        dispose(ADS_EVENT_NAME)
                    }
                }
            }
        }
    }

    override fun deactivate() {
        interstitialAd.adListener = null
        dispose()
    }

    override fun detachView() {
        viewHolder.content.removeAllViews()
    }

    override fun attachView(callback: () -> Unit) = autoDispose {
        Completable.fromAction {
            MobileAds.initialize(questContext, getString(R.string.admob_app_id))
            interstitialAd = InterstitialAd(questContext).apply {
                adUnitId = getString(R.string.admob_ad_id)
                adListener = (object : AdListener() {
                    override fun onAdClosed() {
                        interstitialAd.adListener = null
                        TransitionChoreographUseCases.getNextTransition { transition ->
                            if (transition != null)
                                sendEvent(SHOW)
                            TransitionChoreographUseCases.doNextTransition()
                        }
                    }

                    override fun onAdFailedToLoad(errorType: Int) {
                        interstitialAd.adListener = null
                        viewHolder.okButton.isEnabled = true
                        dispose(ADS_EVENT_NAME)
                        TransitionChoreographUseCases.doNextTransition()
                    }

                    override fun onAdLeftApplication() {
                        LockScreen.hide()
                    }

                    override fun onAdOpened() {
                        sendEvent(HIDE)
                    }

                    override fun onAdLoaded() {
                        dispose(ADS_EVENT_NAME)
                        interstitialAd.show()
                    }

                    override fun onAdClicked() {}
                    override fun onAdImpression() {}
                })
            }
            val adRequest = AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build()
            interstitialAd.setImmersiveMode(true)
            interstitialAd.loadAd(adRequest)
            viewHolder.okButton.visibility = INVISIBLE
            viewHolder.titleView.text = questContext.getString(R.string.title_advert_loading)
        }.subscribe(callback)
    }

    class LockScreenAdsViewContentHolder internal constructor(context: Context) :
            ViewHolder(context, R.layout.layout_lock_screen_ads_view_container), ILockScreenContentViewHolder {

        internal var okButton: Button = view.findViewById(R.id.btn_ok)
        override val contentContainer: View = view.findViewById(R.id.container_content)
        internal val loadingIndicator: CircleProgressView = view.findViewById(R.id.loading_indicator)
        internal val loadingIndicatorView: TextView = view.findViewById(R.id.tv_loading_indicator)
        internal val loadingIndicatorContainer: ViewGroup = view.findViewById(R.id.container_loading_indicator)
        override val titleContentContainer: View = view.findViewById(R.id.container_title)
        internal val content: ViewGroup = view.findViewById(R.id.content)
        internal val titleView: TextView = view.findViewById(R.id.tv_title)

        init {
            (view as ViewGroup).removeAllViews()
        }
    }
}