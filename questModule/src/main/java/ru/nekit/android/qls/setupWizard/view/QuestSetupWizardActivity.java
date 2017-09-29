package ru.nekit.android.qls.setupWizard.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.lockScreen.service.LockScreenService;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardActivity;
import ru.nekit.android.qls.setupWizard.ISetupStep;
import ru.nekit.android.qls.setupWizard.QuestSetupWizard;

import static ru.nekit.android.qls.setupWizard.BaseSetupStep.ENTER_UNLOCK_SECRET;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.BIND_PARENT_CONTROL;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.CALL_PHONE_AND_READ_CONTACTS_PERMISSION;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.CHANGE_UNLOCK_SECRET;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.OVERLAY_PERMISSION;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.PUPIL_AVATAR;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.PUPIL_NAME;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.PUPIL_SEX;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.QTP_COMPLEXITY;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.SETTINGS;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.SETUP_ALLOW_CONTACTS;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.SETUP_UNLOCK_SECRET;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.START;

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
        if (step.equals(START)) {
            replaceFragment(StartSetupWizardFragment.getInstance());
        } else if (OVERLAY_PERMISSION.equals(step)) {
            replaceFragment(OverlayPermissionFragment.getInstance());
        } else if (SETUP_UNLOCK_SECRET.equals(step) ||
                step.equals(ENTER_UNLOCK_SECRET) ||
                step.equals(CHANGE_UNLOCK_SECRET)) {
            replaceFragment(UnlockSecretFragment.getInstance(step));
        } else if (PUPIL_NAME.equals(step)) {
            replaceFragment(SetupPupilNameFragment.getInstance());
        } else if (PUPIL_SEX.equals(step)) {
            replaceFragment(PupilSetSexFragment.getInstance());
        } else if (QTP_COMPLEXITY.equals(step)) {
            replaceFragment(SetupQTPComplexityFragment.getInstance());
        } else if (PUPIL_AVATAR.equals(step)) {
            replaceFragment(SetupPupilAvatarFragment.getInstance());
        } else if (CALL_PHONE_AND_READ_CONTACTS_PERMISSION.equals(step)) {
            replaceFragment(CallPhoneAndReadContactsPermissionFragment.getInstance());
        } else if (SETUP_ALLOW_CONTACTS.equals(step)) {
            replaceFragment(PhoneContactsFragment.getInstance());
        } else if (BIND_PARENT_CONTROL.equals(step)) {
            replaceFragment(BindParentFragment.getInstance());
        } else if (SETTINGS.equals(step)) {
            replaceFragment(SettingsFragment.getInstance());
        }
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        showSetupWizardStep(SETTINGS);
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }
}