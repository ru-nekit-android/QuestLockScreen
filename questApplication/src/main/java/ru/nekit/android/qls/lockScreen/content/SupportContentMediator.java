package ru.nekit.android.qls.lockScreen.content;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

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
    private final LockScreenSupportViewContentHolder mViewHolder;
    @NonNull
    private final SettingsStorage mSettingsStorage;

    @NonNull
    private AdView mAdView;

    public SupportContentMediator(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mSettingsStorage = new SettingsStorage();
        mViewHolder = new LockScreenSupportViewContentHolder(questContext);
        mViewHolder.okButton.setOnClickListener(this);
        mViewHolder.showNoMore.setOnCheckedChangeListener(this);
    }

    @NonNull
    @Override
    public ILockScreenContentContainerViewHolder getContentContainerViewHolder() {
        return mViewHolder;
    }

    @Override
    public void deactivate() {
        mViewHolder.okButton.setOnClickListener(null);
    }

    @Override
    public void detachView() {
        mViewHolder.content.removeAllViews();
    }

    @Override
    public void attachView() {
        TransitionChoreograph.Transition transition = mTransitionChoreograph.getCurrentTransition();
        String title = "";
        setDefaultSettingsForTools();
        if (transition != null) {
            switch (transition) {

                case ADVERT:

                    MobileAds.initialize(mQuestContext,
                            mQuestContext.getString(R.string.admob_app_id));
                    title = mQuestContext.getString(R.string.title_advert);
                    mAdView = (AdView) ((LayoutInflater) mQuestContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.sc_advertise_layout, null);
                    mViewHolder.content.addView(mAdView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);

                    break;

                case LEVEL_UP:

                    title = mQuestContext.getString(R.string.title_new_level);

                    break;

                case INTRODUCTION:

                    title = mQuestContext.getString(R.string.title_introduction);
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

    private static class LockScreenSupportViewContentHolder extends ViewHolder
            implements ILockScreenContentContainerViewHolder {

        Button okButton;
        View contentContainer, titleContainer;
        ViewGroup content, toolContainer;
        CheckBox showNoMore;
        TextView titleView;

        LockScreenSupportViewContentHolder(@NonNull Context context) {
            super(context, R.layout.layout_lock_screen_support_view_container);
            titleContainer = view.findViewById(R.id.container_title);
            content = (ViewGroup) view.findViewById(R.id.content);
            toolContainer = (ViewGroup) view.findViewById(R.id.container_tool);
            showNoMore = (CheckBox) view.findViewById(R.id.check_show_no_more);
            titleView = (TextView) view.findViewById(R.id.tv_title);
            contentContainer = view.findViewById(R.id.container_content);
            okButton = (Button) contentContainer.findViewById(R.id.btn_ok);
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