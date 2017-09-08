package ru.nekit.android.qls.quest.mediator.fruitArithmetic;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.ViewGroup;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.adapter.AbstractSelectableQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.adapter.IContentContainerViewHolder;
import ru.nekit.android.qls.utils.TimeUtils;

public class FruitComparisonAlternativeAnswerMediator extends
        AbstractSelectableQuestAlternativeAnswerMediator {

    private int mSize = 0;
    private long currentTime = 0;

    @Override
    protected int getSpanCount() {
        return 8;
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.ill_fruit;
    }

    @Override
    public void init(@NonNull QuestContext questContext) {
        super.init(questContext);


    }

    @Override
    public void updateSize(int width, int height) {
        int margin = mQuestContext.getResources().getDimensionPixelSize(R.dimen.base_semi_gap);
        final int dataListLength = getDataList().size();
        final int rowCount = (int) Math.floor(dataListLength / getSpanCount());
        int size = Math.min(width, height);
        //if (mSize != size)
        if (TimeUtils.getCurrentTime() - currentTime > 300) {
            currentTime = TimeUtils.getCurrentTime();
            mSize = size;
            for (int i = 0; i < dataListLength; ++i) {
                IContentContainerViewHolder holder =
                        (IContentContainerViewHolder) mListView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    ViewGroup.LayoutParams viewHolderLP = holder.getView().getLayoutParams();
                    viewHolderLP.width = (size - margin * getSpanCount() * 2) / getSpanCount();
                    viewHolderLP.height = size / rowCount - margin * 2;
                    GridLayoutManager.LayoutParams imageViewLP =
                            (GridLayoutManager.LayoutParams) holder.getView().getLayoutParams();
                    imageViewLP.setMargins(margin, margin, margin, margin);
                    holder.getContentContainer().requestLayout();
                }
            }
        }
    }
}
