package ru.nekit.android.qls.lockScreen.content;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.SettingsStorage;
import ru.nekit.android.qls.lockScreen.TransitionChoreograph;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.utils.ViewHolder;

public class SupportContentMediator extends AbstractLockScreenContentMediator
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @NonNull
    private final QuestContext mQuestContext;
    @NonNull
    private final LockScreenSupportContentViewHolder mViewHolder;
    @NonNull
    private final SettingsStorage mSettingsStorage;

    public SupportContentMediator(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mSettingsStorage = new SettingsStorage();
        mViewHolder = new LockScreenSupportContentViewHolder(questContext);
        mViewHolder.okButton.setOnClickListener(this);
        mViewHolder.showNoMore.setOnCheckedChangeListener(this);
    }

    @NonNull
    @Override
    public ILockScreenContentContainerViewHolder getViewHolder() {
        return mViewHolder;
    }

    @Override
    public void deactivate() {
        mViewHolder.okButton.setOnClickListener(null);
    }

    @Override
    public void detachView() {

    }

    @Override
    public void attachView() {
        TransitionChoreograph.Transition transition = mTransitionChoreograph.getCurrentTransition();
        String title = "";
        setDefaultSettingsForTools();
        if (transition != null) {
            switch (transition) {

                case ADVERT:

                    title = "Реклама";

                    break;

                case LEVEL_UP:

                    title = "Новый уровень";

                    break;

                case INTRODUCTION:

                    title = "Приветствие";
                    setShowNoMoreVisibility(true);
                    mViewHolder.showNoMore.setChecked(!mSettingsStorage.introductionIsPresented());

                    break;

            }
        }
        mViewHolder.titleView.setText(title);
    }

    private void setDefaultSettingsForTools() {
        setShowNoMoreVisibility(false);
        mViewHolder.okButton.setText(mQuestContext.getString(R.string.label_ok));
    }

    private void setShowNoMoreVisibility(boolean visibility) {
        mViewHolder.showNoMore.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view == mViewHolder.okButton) {
            mTransitionChoreograph.goNextTransition();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        TransitionChoreograph.Transition transition = mTransitionChoreograph.getCurrentTransition();
        if (transition == TransitionChoreograph.Transition.INTRODUCTION) {
            mSettingsStorage.setIntroductionIsPresented(!isChecked);
        }
    }

    private static class LockScreenSupportContentViewHolder extends ViewHolder
            implements ILockScreenContentContainerViewHolder {

        Button okButton;
        View contentContainer, titleContainer;
        ViewGroup content, toolContainer;
        CheckBox showNoMore;
        TextView titleView;

        LockScreenSupportContentViewHolder(@NonNull Context context) {
            super(context, R.layout.layout_lock_screen_support_content);
            titleContainer = getView().findViewById(R.id.container_title);
            content = (ViewGroup) getView().findViewById(R.id.content);
            toolContainer = (ViewGroup) getView().findViewById(R.id.container_tool);
            showNoMore = (CheckBox) getView().findViewById(R.id.check_show_no_more);
            titleView = (TextView) getView().findViewById(R.id.tv_title);
            contentContainer = getView().findViewById(R.id.container_content);
            okButton = (Button) contentContainer.findViewById(R.id.btn_ok);
            ((ViewGroup) getView()).removeAllViews();
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