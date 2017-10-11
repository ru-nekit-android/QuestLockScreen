package ru.nekit.android.qls.quest.mediator.answer;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class AbstractListableQuestAlternativeAnswerMediator<ListDataType, ListAdapterType
        extends RecyclerView.Adapter> extends QuestAlternativeAnswerMediator {

    protected ListAdapterType mListAdapter;
    protected RecyclerView mListView;
    private RelativeLayout mListViewContainer;

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
        mListViewContainer = new RelativeLayout(mQuestContext);
        mListView = new RecyclerView(mQuestContext);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mQuestContext,
                getColumnCount(), LinearLayoutManager.VERTICAL, false);
        mListView.setHasFixedSize(true);
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mListView.setLayoutManager(gridLayoutManager);
        mListAdapter = getListAdapter(getListData());
        mListView.setAdapter(mListAdapter);
        mListViewContainer.addView(mListView);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mListView.getLayoutParams();
        layoutParams.height = WRAP_CONTENT;
        layoutParams.width = WRAP_CONTENT;
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        RelativeLayout.LayoutParams contentLayoutParams = new RelativeLayout.LayoutParams(
                MATCH_PARENT, MATCH_PARENT
        );
        mListViewContainer.setLayoutParams(contentLayoutParams);
        mListViewContainer.requestLayout();
    }

    @CallSuper
    @Override
    public void detachView() {
        mListView.setAdapter(null);
        mListView.setLayoutManager(null);
        mRootContentContainer.removeAllViews();
        super.detachView();
    }

    @Override
    public void updateSize() {

    }

    protected int getColumnCount() {
        return 2;
    }

    @Override
    public View getView() {
        return mListViewContainer;
    }

    @NonNull
    protected abstract List<ListDataType> getListData();

    @NonNull
    protected abstract ListAdapterType getListAdapter(List<ListDataType> listData);

    @Override
    public boolean onWrongAnswer() {
        return true;
    }
}
