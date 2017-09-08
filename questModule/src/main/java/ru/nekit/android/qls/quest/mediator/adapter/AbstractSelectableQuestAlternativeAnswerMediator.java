package ru.nekit.android.qls.quest.mediator.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.mediator.AbstractListableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;

public abstract class AbstractSelectableQuestAlternativeAnswerMediator
        extends AbstractListableQuestAlternativeAnswerMediator {

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    protected List createDataList() {
        NumberSummandQuest quest = (NumberSummandQuest) mQuest;
        List data = new ArrayList<Integer>();
        for (int i = 0; i < quest.leftNode.length; i++) {
            data.add(quest.leftNode[i]);
        }
        return data;
    }

    @NonNull
    @Override
    protected RecyclerView.Adapter createAdapter(@NonNull List dataList) {
        List<Integer> drawableResourceIds = new ArrayList<>();
        int index;
        for (Object dataItem : dataList) {
            index = (int) dataItem;
            drawableResourceIds.add(mQuestContext.getQuestResourceLibrary().
                    getVisualResourceItem(index).getDrawableResourceId());
        }
        return new SelectableDrawableResourceAdapter(getLayoutResId(), dataList,
                drawableResourceIds,
                this);
    }

    @LayoutRes
    abstract protected int getLayoutResId();

    @Override
    public void updateSize(int width, int height) {
        int margin = mQuestContext.getResources().getDimensionPixelSize(R.dimen.base_semi_gap);
        final int dataLength = getDataList().size();
        final int rowCount = dataLength / getSpanCount();
        int size = rowCount > getSpanCount() ? Math.max(width, height) : Math.min(width, height);
        for (int i = 0; i < dataLength; ++i) {
            IContentContainerViewHolder holder =
                    (IContentContainerViewHolder) mListView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                ViewGroup.LayoutParams viewHolderLP = holder.getView().getLayoutParams();
                viewHolderLP.width = (size - margin * getSpanCount() * 2) / rowCount;
                viewHolderLP.height = size / rowCount - margin * 2;
                holder.getView().requestLayout();
                GridLayoutManager.LayoutParams imageViewLP =
                        (GridLayoutManager.LayoutParams) holder.getView().getLayoutParams();
                imageViewLP.setMargins(margin, margin, margin, margin);
                holder.getContentContainer().requestLayout();
            }
        }
    }
}