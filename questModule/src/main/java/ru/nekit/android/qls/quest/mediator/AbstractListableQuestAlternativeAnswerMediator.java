package ru.nekit.android.qls.quest.mediator;

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

public abstract class AbstractListableQuestAlternativeAnswerMediator extends QuestAlternativeAnswerMediator {

    protected RecyclerView mListView;
    private RelativeLayout mContent;
    @NonNull
    private List mDataList;

    @Override
    public void init(@NonNull QuestContext questContext) {
        super.init(questContext);
        mContent = new RelativeLayout(questContext);
        mListView = new RecyclerView(questContext);
        RecyclerView.Adapter adapter = createAdapter(mDataList = createDataList());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(questContext,
                getSpanCount(), LinearLayoutManager.VERTICAL, false);
        mListView.setHasFixedSize(true);
        mListView.setAdapter(adapter);
        mListView.setLayoutManager(gridLayoutManager);
        mContent.addView(mListView);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) mListView.getLayoutParams();
        layoutParams.height = WRAP_CONTENT;
        layoutParams.width = WRAP_CONTENT;
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        RelativeLayout.LayoutParams contentLayoutParams = new RelativeLayout.LayoutParams(
                MATCH_PARENT, MATCH_PARENT
        );
        mContent.setLayoutParams(contentLayoutParams);
        mContent.requestLayout();
        mListView.requestLayout();
    }

    @NonNull
    protected List getDataList() {
        return mDataList;
    }

    protected int getSpanCount() {
        return 2;
    }

    @NonNull
    protected abstract RecyclerView.Adapter createAdapter(@NonNull List list);

    @NonNull
    protected abstract List createDataList();

    @Override
    public View getView() {
        return mContent;
    }
}
