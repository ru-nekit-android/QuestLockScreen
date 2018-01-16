package ru.nekit.android.qls.parentControl.skulist.row;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.parentControl.billing.BillingProvider;

public class UiManager implements RowViewHolder.OnButtonClickListener {
    private final RowDataProvider mRowDataProvider;
    private final UiDelegatesFactory mDelegatesFactory;

    public UiManager(RowDataProvider rowDataProvider, BillingProvider billingProvider) {
        mRowDataProvider = rowDataProvider;
        mDelegatesFactory = new UiDelegatesFactory(billingProvider);
    }

    public UiDelegatesFactory getDelegatesFactory() {
        return mDelegatesFactory;
    }

    public final RowViewHolder onCreateViewHolder(ViewGroup parent) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sku_details_row, parent, false);
        return new RowViewHolder(item, this);
    }

    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        if (data != null) {
            holder.title.setText(data.getTitle());
            mDelegatesFactory.onBindViewHolder(data, holder);
        }
    }

    public void onButtonClicked(int position) {
        SkuRowData data = mRowDataProvider.getData(position);
        if (data != null) {
            mDelegatesFactory.onButtonClicked(data);
        }
    }
}