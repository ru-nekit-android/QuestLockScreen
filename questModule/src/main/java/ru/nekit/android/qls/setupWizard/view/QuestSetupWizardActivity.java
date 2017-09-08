package ru.nekit.android.qls.setupWizard.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.lockScreen.service.LockScreenService;
import ru.nekit.android.qls.setupWizard.BaseSetupStep;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardActivity;
import ru.nekit.android.qls.setupWizard.ISetupStep;
import ru.nekit.android.qls.setupWizard.QuestSetupWizard;
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step;

public class QuestSetupWizardActivity extends BaseSetupWizardActivity implements EventBus.IEventHandler {

    @Override
    public QuestSetupWizard getSetupWizard() {
        return (QuestSetupWizard) super.getSetupWizard();
    }

    @Override
    protected BaseSetupWizard createSetupWizard(@NonNull Context context) {
        return new QuestSetupWizard(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus.handleEvents(this,
                LockScreenService.PUPIL_BIND_OK);
    }

    @Override
    protected void onDestroy() {
        mEventBus.stopHandleEvents(this);
        super.onDestroy();

    }


    @Override
    public void showSetupWizardStep(@NonNull ISetupStep step, Object... params) {
        getSetupWizard().setCurrentSetupStep(step);
        if (step.equals(Step.START)) {
            replaceFragment(StartSetupWizardFragment.getInstance());
        } else if (Step.OVERLAY_PERMISSION.equals(step)) {
            replaceFragment(OverlayPermissionFragment.getInstance());
        } else if (Step.SETUP_UNLOCK_SECRET.equals(step) ||
                step.equals(BaseSetupStep.ENTER_UNLOCK_SECRET) ||
                step.equals(Step.CHANGE_UNLOCK_SECRET)) {
            replaceFragment(UnlockSecretFragment.getInstance(step));
        } else if (Step.PUPIL_NAME.equals(step)) {
            replaceFragment(SetupPupilNameFragment.getInstance());
        } else if (Step.PUPIL_SEX.equals(step)) {
            replaceFragment(PupilSetSexFragment.getInstance());
        } else if (Step.QTP_COMPLEXITY.equals(step)) {
            replaceFragment(SetupQTPComplexityFragment.getInstance());
        } else if (Step.PUPIL_AVATAR.equals(step)) {
            replaceFragment(SetupPupilAvatarFragment.getInstance());
        } else if (Step.CALL_PHONE_AND_READ_CONTACTS_PERMISSION.equals(step)) {
            replaceFragment(CallPhoneAndReadContactsPermissionFragment.getInstance());
        } else if (Step.SETUP_ALLOW_CONTACTS.equals(step)) {
            replaceFragment(PhoneContactsFragment.getInstance());
        } else if (Step.BIND_PARENT_CONTROL.equals(step)) {
            replaceFragment(BindParentFragment.getInstance());
        } else if (Step.SETTINGS.equals(step)) {
            replaceFragment(SettingsFragment.getInstance());
        }
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        showSetupWizardStep(Step.SETTINGS);
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }
}