package ru.nekit.android.qls.quest.mediator.shared.answer;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.mediator.shared.adapter.SquareImageAdapter;

public abstract class AbstractSquareImageQuestAlternativeAnswerMediator
        extends AbstractListableQuestAlternativeAnswerMediator<Integer, SquareImageAdapter> {

    @LayoutRes
    protected abstract int getListItemLayoutResId();

    @NonNull
    @Override
    protected SquareImageAdapter getListAdapter(List<Integer> listData) {
        List<Integer> imageResourceIds = new ArrayList<>();
        for (Integer dataItem : listData) {
            imageResourceIds.add(mQuestContext.getQuestResourceLibrary().
                    getVisualResourceItem(dataItem).getDrawableResourceId());
        }
        return new SquareImageAdapter(getListItemLayoutResId(), listData, imageResourceIds,
                this);
    }
}
