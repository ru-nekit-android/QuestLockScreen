package ru.nekit.android.qls.lockScreen.content;


import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.SettingsStorage;
import ru.nekit.android.qls.lockScreen.TransitionChoreograph;
import ru.nekit.android.qls.lockScreen.content.common.BaseLockScreenContentMediator;
import ru.nekit.android.qls.lockScreen.content.common.ILockScreenContentViewHolder;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.utils.ViewHolder;

public class SupportContentMediator extends BaseLockScreenContentMediator
        implements View.OnClickListener {

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
    }

    @NonNull
    @Override
    public ILockScreenContentViewHolder getViewHolder() {
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
                    mAdView = (AdView) ((LayoutInflater) mQuestContext.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.sc_advertise_layout, null);
                    mViewHolder.content.addView(mAdView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);

                    break;

                case LEVEL_UP:

                    title = mQuestContext.getString(R.string.title_new_level);

                    break;

            }
        }
        mViewHolder.titleView.setText(title);
    }

    private void setDefaultSettingsForTools() {
    }

    @Override
    public void onClick(View view) {
        if (view == mViewHolder.okButton) {
            mTransitionChoreograph.goNextTransition();
        }
    }

    private static class LockScreenSupportViewContentHolder extends ViewHolder
            implements ILockScreenContentViewHolder {

        Button okButton;
        View contentContainer, titleContainer;
        ViewGroup content, toolContainer;
        CheckBox showNoMore;
        TextView titleView;

        LockScreenSupportViewContentHolder(@NonNull android.content.Context context) {
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