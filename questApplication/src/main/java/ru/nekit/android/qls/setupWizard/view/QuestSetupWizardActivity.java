package ru.nekit.android.qls.setupWizard.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.lockScreen.service.LockScreenService;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardActivity;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardStep;
import ru.nekit.android.qls.setupWizard.ISetupWizardStep;
import ru.nekit.android.qls.setupWizard.QuestSetupWizard;

import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.BIND_PARENT_CONTROL;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.CALL_PHONE_AND_READ_CONTACTS_PERMISSION;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.CHANGE_UNLOCK_SECRET;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.DEVICE_ADMIN;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.OVERLAY_PERMISSION;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.PUPIL_AVATAR;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.PUPIL_NAME;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.PUPIL_SEX;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.QTP_COMPLEXITY;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SETTINGS;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SETUP_ALLOW_CONTACTS;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SETUP_UNLOCK_SECRET;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.START;

public class QuestSetupWizardActivity extends BaseSetupWizardActivity implements EventBus.IEventHandler {

    @Override
    public QuestSetupWizard getSetupWizard() {
        return (QuestSetupWizard) super.getSetupWizard();
    }

    @Override
    protected BaseSetupWizard createSetupWizard(@NonNull Context context) {
        return QuestSetupWizard.getInstance(context);
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
    public void showSetupWizardStep(@NonNull ISetupWizardStep step, Object... params) {
        getSetupWizard().commitCurrentSetupStep(step);
        if (step.equals(START)) {
            replaceFragment(StartSetupWizardFragment.getInstance());
        } else if (OVERLAY_PERMISSION.equals(step)) {
            replaceFragment(OverlayPermissionFragment.getInstance());
        } else if (SETUP_UNLOCK_SECRET.equals(step) ||
                step.equals(BaseSetupWizardStep.UNLOCK_SECRET) ||
                step.equals(CHANGE_UNLOCK_SECRET)) {
            replaceFragment(UnlockSecretFragment.getInstance(step));
        } else if (DEVICE_ADMIN.equals(step)) {
            replaceFragment(SetupDeviceAdminFragment.getInstance());
        } else if (PUPIL_NAME.equals(step)) {
            replaceFragment(SetupPupilNameFragment.getInstance());
        } else if (PUPIL_SEX.equals(step)) {
            replaceFragment(SetupPupilSexFragment.getInstance());
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