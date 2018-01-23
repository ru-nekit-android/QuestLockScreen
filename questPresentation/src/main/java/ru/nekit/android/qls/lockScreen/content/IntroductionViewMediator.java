package ru.nekit.android.qls.lockScreen.content;


import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.content.common.BaseLockScreenContentMediator;
import ru.nekit.android.qls.lockScreen.content.common.ILockScreenContentViewHolder;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.utils.ViewHolder;
import ru.nekit.android.qls.window.MenuWindowMediator;

public class IntroductionViewMediator extends BaseLockScreenContentMediator
        implements View.OnClickListener {

    @NonNull
    private final QuestContext mQuestContext;
    @NonNull
    private final LockScreenIntroductionViewContentHolder mViewHolder;

    public IntroductionViewMediator(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mViewHolder = new LockScreenIntroductionViewContentHolder(questContext);
        mViewHolder.startButton.setOnClickListener(this);
        mViewHolder.menuButton.setOnClickListener(this);
    }

    @NonNull
    @Override
    public ILockScreenContentViewHolder getViewHolder() {
        return mViewHolder;
    }

    @Override
    public void deactivate() {
        mViewHolder.startButton.setOnClickListener(null);
        mViewHolder.menuButton.setOnClickListener(null);
    }

    @Override
    public void detachView() {
        mViewHolder.content.removeAllViews();
    }

    @Override
    public void attachView() {
        mViewHolder.titleView.setText(mQuestContext.getString(R.string.title_introduction));
    }

    @Override
    public void onClick(View view) {
        if (view == mViewHolder.startButton) {
            mTransitionChoreograph.goNextTransition();
        } else if (view == mViewHolder.menuButton) {
            MenuWindowMediator.openWindow(mQuestContext);
        }
    }

    private static class LockScreenIntroductionViewContentHolder extends ViewHolder
            implements ILockScreenContentViewHolder {

        View startButton, menuButton;
        View contentContainer, titleContainer;
        ViewGroup content, toolContainer;
        TextView titleView;

        LockScreenIntroductionViewContentHolder(@NonNull android.content.Context context) {
            super(context, R.layout.layout_lock_screen_intoduction_view_container);
            titleContainer = view.findViewById(R.id.container_title);
            content = (ViewGroup) view.findViewById(R.id.content);
            toolContainer = (ViewGroup) view.findViewById(R.id.container_tool);
            titleView = (TextView) view.findViewById(R.id.tv_title);
            contentContainer = view.findViewById(R.id.container_content);
            startButton = view.findViewById(R.id.btn_start);
            menuButton = view.findViewById(R.id.btn_menu);
            ((ViewGroup) view).removeAllViews();
        }

        @NonNull
        @Override
        public View getTitleContentContainer() {
            return titleContainer;
        }

        @NonNull
        @Override
        public View getContentContainer() {
            return contentContainer;
        }
    }
}