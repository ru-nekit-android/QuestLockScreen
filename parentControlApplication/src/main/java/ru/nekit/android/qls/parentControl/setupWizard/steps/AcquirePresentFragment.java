package ru.nekit.android.qls.parentControl.setupWizard.steps;

import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.parentControl.billing.BillingProvider;
import ru.nekit.android.qls.parentControl.skulist.CardsWithHeadersDecoration;
import ru.nekit.android.qls.parentControl.skulist.SkusAdapter;
import ru.nekit.android.qls.parentControl.skulist.row.SkuRowData;
import ru.nekit.android.qls.parentControl.skulist.row.UiManager;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardFragment;

import static com.android.billingclient.api.BillingClient.BillingResponse;

public class AcquirePresentFragment extends BaseSetupWizardFragment {

    private RecyclerView mRecyclerView;
    private SkusAdapter mAdapter;
    private View mLoadingView;
    private TextView mErrorTextView;
    private BillingProvider mBillingProvider;

    public static AcquirePresentFragment getInstance() {
        return new AcquirePresentFragment();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        mErrorTextView = (TextView) view.findViewById(R.id.tv_error);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mLoadingView = view.findViewById(R.id.screen_wait);
        if (mBillingProvider != null) {
            handleManagerAndUiReady();
        }
    }

    @LayoutRes
    @Override
    protected int getLayoutId() {
        return R.layout.acquire_fragment;
    }

    public void refreshUI() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onManagerReady(BillingProvider billingProvider) {
        mBillingProvider = billingProvider;
        if (mRecyclerView != null) {
            handleManagerAndUiReady();
        }
    }

    private void setWaitScreen(boolean set) {
        mRecyclerView.setVisibility(set ? View.GONE : View.VISIBLE);
        mLoadingView.setVisibility(set ? View.VISIBLE : View.GONE);
    }

    private void handleManagerAndUiReady() {
        setWaitScreen(true);
        querySkuDetails();
    }

    private void displayAnErrorIfNeeded() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        mLoadingView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
        int billingResponseCode = mBillingProvider.getBillingManager()
                .getBillingClientResponseCode();

        switch (billingResponseCode) {
            case BillingResponse.OK:
                mErrorTextView.setText("error_no_skus");
                break;
            case BillingResponse.BILLING_UNAVAILABLE:
                mErrorTextView.setText("error_billing_unavailable");
                break;
            default:
                mErrorTextView.setText("error_billing_default");
        }
    }

    private void querySkuDetails() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            List<SkuRowData> dataList = new ArrayList<>();
            mAdapter = new SkusAdapter();
            UiManager uiManager = createUiManager(mAdapter, mBillingProvider);
            mAdapter.setUiManager(uiManager);
            List<String> inAppSkus = uiManager.getDelegatesFactory().getSkuList(SkuType.INAPP);
            addSkuRows(dataList, inAppSkus);
        }
    }

    private void addSkuRows(final List<SkuRowData> inList, List<String> skusList) {
        mBillingProvider.getBillingManager().querySkuDetailsAsync(SkuType.INAPP, skusList,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        if (responseCode == BillingResponse.OK &&
                                skuDetailsList != null && skuDetailsList.size() > 0) {
                            for (SkuDetails details : skuDetailsList) {
                                inList.add(new SkuRowData(details));
                            }
                            if (inList.size() == 0) {
                                displayAnErrorIfNeeded();
                            } else {
                                if (mRecyclerView.getAdapter() == null) {
                                    mRecyclerView.setAdapter(mAdapter);
                                    Resources res = getContext().getResources();
                                    mRecyclerView.addItemDecoration(new CardsWithHeadersDecoration(
                                            mAdapter, (int) res.getDimension(R.dimen.header_gap),
                                            (int) res.getDimension(R.dimen.row_gap)));
                                    mRecyclerView.setLayoutManager(
                                            new LinearLayoutManager(getContext()));
                                }
                                mAdapter.updateData(inList);
                                setWaitScreen(false);
                            }
                        }
                    }
                });
    }

    protected UiManager createUiManager(SkusAdapter adapter, BillingProvider provider) {
        return new UiManager(adapter, provider);
    }
}