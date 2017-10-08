package ru.nekit.android.qls.quest.window;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import ru.nekit.android.qls.CONST;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.LockScreen;
import ru.nekit.android.qls.lockScreen.LockScreenMediator;
import ru.nekit.android.qls.lockScreen.service.LockScreenService;
import ru.nekit.android.qls.lockScreen.window.WindowContentViewHolder;
import ru.nekit.android.qls.pupil.PhoneContact;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.session.Session;
import ru.nekit.android.qls.session.SessionType;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.adapters.PhoneContactListener;
import ru.nekit.android.qls.setupWizard.adapters.PhoneContactsAdapterForReading;
import ru.nekit.android.qls.utils.PhoneManager;
import ru.nekit.android.qls.utils.RevealPoint;
import ru.nekit.android.qls.utils.Vibrate;
import ru.nekit.android.qls.utils.ViewHolder;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.WRONG;

public class MenuWindowMediator extends WindowMediator
        implements View.OnClickListener,
        PhoneContactListener,
        PatternLockViewListener {

    private static MenuWindowMediator instance;

    private Step mCurrentStep;
    private ViewHolder mCurrentContentHolder;
    private MenuWindowContentViewHolder mWindowContent;

    private MenuWindowMediator(@NonNull QuestContext questContext) {
        super(questContext);
    }

    public static void openWindow(@NonNull QuestContext questContext) {
        if (instance == null) {
            (instance = new MenuWindowMediator(questContext)).openWindow();
        }
    }

    @Override
    public void onClick(View view) {
        Step step = null;
        if (view == mWindowContent.unlockButton) {
            if (CONST.USE_SESSION_FOR_UNLOCK && Session.isValid(mQuestContext,
                    SessionType.LOCK_SCREEN)) {
                mQuestContext.getEventBus().sendEvent(LockScreenMediator.ACTION_CLOSE);
            } else {
                step = Step.UNLOCK;
            }
        } else if (view == mWindowContent.phoneButton) {
            step = Step.PHONE;
        }
        setStep(step);
    }

    @Override
    protected WindowContentViewHolder createWindowContent() {
        mWindowContent = new MenuWindowContentViewHolder(mQuestContext);
        mWindowContent.unlockButton.setOnClickListener(this);
        mWindowContent.phoneButton.setOnClickListener(this);
        mWindowContent.phoneButton.setSelected(true);
        boolean phoneIsAvailable = phoneIsAvailable();
        mWindowContent.phoneButton.setVisibility(phoneIsAvailable ? VISIBLE : GONE);
        setStep(phoneIsAvailable ? Step.PHONE : Step.getByOrdinal(Step.PHONE.ordinal() + 1));
        int count = 0;
        for (int i = 0; i < mWindowContent.buttonContainer.getChildCount(); i++) {
            if (mWindowContent.buttonContainer.getChildAt(i).getVisibility() == VISIBLE) {
                count++;
            }
        }
        mWindowContent.buttonContainer.setVisibility(count == 1 ? INVISIBLE : VISIBLE);
        return mWindowContent;
    }

    @Override
    protected int getWindowStyleId() {
        return R.style.Window_Menu;
    }

    private void switchToContent(@NonNull ViewHolder contentHolder) {
        View content = contentHolder.getView();
        mWindowContent.contentContainer.removeAllViews();
        mWindowContent.contentContainer.addView(content);
    }

    private void setStep(Step step) {
        if (mCurrentStep != step) {
            @StringRes int titleResID = 0;
            if (mCurrentStep != null) {
                destroyContentForStep();
            }
            mCurrentStep = step;

            switch (step) {

                case PHONE:

                    titleResID = R.string.title_phone;
                    mCurrentContentHolder = new PhoneViewHolder(mQuestContext);
                    PhoneViewHolder phoneViewHolder = (PhoneViewHolder) mCurrentContentHolder;
                    PhoneContactsAdapterForReading allowContactsAdapter =
                            new PhoneContactsAdapterForReading(getPhoneContacts(), this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mQuestContext);
                    phoneViewHolder.allowContactsListView.setAdapter(allowContactsAdapter);
                    phoneViewHolder.allowContactsListView.setLayoutManager(linearLayoutManager);

                    break;

                case UNLOCK:

                    titleResID = R.string.title_unlock_secret;
                    mCurrentContentHolder = new UnlockViewHolder(mQuestContext);
                    UnlockViewHolder unlockViewHolder = (UnlockViewHolder)
                            mCurrentContentHolder;
                    unlockViewHolder.patterLockView.addPatternLockListener(this);

                    break;
            }
            mWindowContent.titleView.setText(mQuestContext.getResources().getString(titleResID));
            switchToContent(mCurrentContentHolder);
        }
    }

    @Override
    protected void destroy() {
        destroyContentForStep();
        mWindowContent.unlockButton.setOnClickListener(null);
        mWindowContent.phoneButton.setOnClickListener(null);
        instance = null;
    }

    private void destroyContentForStep() {
        if (mCurrentStep != null) {
            switch (mCurrentStep) {

                case PHONE:

                    PhoneViewHolder phoneViewHolder = (PhoneViewHolder)
                            mCurrentContentHolder;
                    phoneViewHolder.allowContactsListView.setAdapter(null);
                    phoneViewHolder.allowContactsListView.setLayoutManager(null);

                    break;

                case UNLOCK:

                    UnlockViewHolder unlockViewHolder = (UnlockViewHolder)
                            mCurrentContentHolder;
                    unlockViewHolder.patterLockView.removePatternLockListener(this);

                    break;
            }
        }
        mCurrentStep = null;
    }

    private List<PhoneContact> getPhoneContacts() {
        return mQuestContext.getPupil().getPhoneContacts();
    }

    private boolean phoneIsAvailable() {
        return PhoneManager.phoneIsAvailable(mQuestContext) && getPhoneContacts().size() > 0;
    }

    @Override
    public void onAction(int position) {
        PhoneContact phoneContact = getPhoneContacts().get(position);
        Intent outgoingCallIntent = new Intent(LockScreenService.EVENT_OUTGOING_CALL);
        outgoingCallIntent.putExtra(PhoneContact.NAME, phoneContact);
        mQuestContext.getEventBus().sendEvent(outgoingCallIntent);
        mWindow.close(RevealPoint.POSITION_BOTTOM_CENTER);
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onProgress(List<PatternLockView.Dot> progressPattern) {

    }

    @Override
    public void onComplete(List<PatternLockView.Dot> pattern) {
        UnlockViewHolder unlockViewHolder = (UnlockViewHolder) mCurrentContentHolder;
        if (pattern.size() >= BaseSetupWizard.UNLOCK_SECRET_MIN_SIZE) {
            if (LockScreen.tryToLogin(mQuestContext,
                    PatternLockUtils.patternToMD5(unlockViewHolder.patterLockView, pattern))) {
                mWindow.close(RevealPoint.POSITION_MIDDLE_CENTER);
                mQuestContext.getEventBus().sendEvent(LockScreenMediator.ACTION_CLOSE);
            } else {
                unlockViewHolder.patterLockView.setViewMode(WRONG);
                Vibrate.make(mQuestContext, 400);
            }
        } else {
            unlockViewHolder.patterLockView.setViewMode(WRONG);
        }
    }

    @Override
    public void onCleared() {

    }

    private enum Step {

        PHONE,
        UNLOCK;

        public static Step getByOrdinal(int ordinal) {
            return values()[ordinal];
        }
    }
}