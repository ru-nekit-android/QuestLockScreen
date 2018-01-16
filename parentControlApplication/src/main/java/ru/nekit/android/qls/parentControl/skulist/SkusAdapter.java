package ru.nekit.android.qls.parentControl.skulist;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.parentControl.skulist.row.RowDataProvider;
import ru.nekit.android.qls.parentControl.skulist.row.RowViewHolder;
import ru.nekit.android.qls.parentControl.skulist.row.SkuRowData;
import ru.nekit.android.qls.parentControl.skulist.row.UiManager;

public class SkusAdapter extends RecyclerView.Adapter<RowViewHolder> implements RowDataProvider {

    private UiManager mUiManager;
    private List<SkuRowData> mListData;

    public void setUiManager(UiManager uiManager) {
        mUiManager = uiManager;
    }

    public void updateData(List<SkuRowData> data) {
        mListData = data;
        notifyDataSetChanged();
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mUiManager.onCreateViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        mUiManager.onBindViewHolder(getData(position), holder);
    }

    @Override
    public int getItemCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public SkuRowData getData(int position) {
        return mListData == null ? null : mListData.get(position);
    }
}

