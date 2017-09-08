package ru.nekit.android.qls.setupWizard;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.SettingsStorage;
import ru.nekit.android.qls.lockScreen.LockScreen;
import ru.nekit.android.qls.pupil.PhoneContact;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilSex;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramComplexity;
import ru.nekit.android.qls.setupWizard.view.QuestSetupWizardActivity;
import ru.nekit.android.qls.utils.PhoneManager;

import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.CALL_PHONE_AND_READ_CONTACTS_PERMISSION;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.OVERLAY_PERMISSION;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.PUPIL_AVATAR;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.PUPIL_NAME;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.PUPIL_SEX;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.QTP_COMPLEXITY;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.SETTINGS;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.SETUP_ALLOW_CONTACTS;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.SETUP_UNLOCK_SECRET;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.START;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.NEED_LOGIN;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.SETTINGS_PARENT;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.SETUP_WIZARD_PARENT;

public class QuestSetupWizard extends BaseSetupWizard {

    private static final String NAME = "questSetupWizard";

    private final EventBus mEventBus;

    public QuestSetupWizard(@NonNull Context context) {
        super(context);
        mEventBus = new EventBus(context);
    }

    private static boolean overlayPermissionIsSet(@NonNull Context content) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(content);
    }

    private static boolean callPhonePermissionIsGranted(@NonNull Context content) {
        return PhoneManager.callPhonePermissionIsGranted(content);
    }

    private static boolean readContactsPermissionIsGranted(@NonNull Context content) {
        return PhoneManager.readContactsPermissionIsGranted(content);
    }

    public static boolean allPermissionsIsGranted(@NonNull Context context) {
        boolean permissionIsGranted = true;
        if (PhoneManager.phoneIsAvailable(context)) {
            permissionIsGranted = callPhonePermissionIsGranted(context)
                    && readContactsPermissionIsGranted(context);
        }
        return permissionIsGranted && overlayPermissionIsSet(context);
    }

    public static void start(@NonNull Context context) {
        context.startActivity(getStartIntent(context, true));
    }

    public static Intent getStartIntent(@NonNull Context context, boolean newTask) {
        Intent intent = new Intent(context, QuestSetupWizardActivity.class);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    public static boolean setupIsComplete() {
        return (new SettingsStorage()).setupWizardIsCompleted(NAME);
    }

    private boolean unlockPasswordIsSet() {
        return LockScreen.getUnlockSecret(mContext) != null;
    }

    private boolean pupilIsRegistered(@Nullable Pupil pupil) {
        return pupil != null;
    }

    @Nullable
    public Pupil getPupil() {
        return mPupilManager.getCurrentPupil();
    }

    public void setPupilName(@NonNull String name) {
        Pupil pupil = getPupil();
        if (pupil == null) {
            pupil = new Pupil();
            pupil.name = name;
            mPupilManager.addIfAbleAndNotify(pupil, true, mEventBus);
        } else {
            pupil.name = name;
            mPupilManager.update(pupil);
        }
    }

    public void setPupilSex(@NonNull PupilSex sex) {
        Pupil pupil = getPupil();
        if (pupil != null) {
            pupil.sex = sex;
            mPupilManager.update(pupil);
        }
    }

    public void setPupilAvatar(@NonNull String value) {
        Pupil pupil = getPupil();
        if (pupil != null) {
            pupil.avatar = value;
            mPupilManager.update(pupil);
        }
    }

    public void setQTPComplexity(@NonNull QuestTrainingProgramComplexity complexity) {
        Pupil pupil = getPupil();
        if (pupil != null) {
            pupil.complexity = complexity;
            mPupilManager.update(pupil);
        }
    }

    @Override
    protected boolean needLogin(ISetupStep step) {
        return super.needLogin(step) || unlockPasswordIsSet();
    }

    private Step getNextStepForAllPermission() {
        if (!overlayPermissionIsSet(mContext)) {
            return OVERLAY_PERMISSION;
        }
        if (PhoneManager.phoneIsAvailable(mContext)) {
            if (!callPhonePermissionIsGranted(mContext)) {
                return CALL_PHONE_AND_READ_CONTACTS_PERMISSION;
            }
        }
        return null;
    }

    @NonNull
    @Override
    protected Step calculateNextStep() {
        if (setupIsCompleted()) {
            Step stepForAllPermission = getNextStepForAllPermission();
            if (stepForAllPermission != null) {
                return stepForAllPermission;
            }
        } else {
            if (mCurrentSetupStep == null) {
                return START;
            }
            if (!unlockPasswordIsSet()) {
                return SETUP_UNLOCK_SECRET;
            }
            Step stepForAllPermission = getNextStepForAllPermission();
            if (stepForAllPermission != null) {
                return stepForAllPermission;
            }
            Pupil pupil = getPupil();
            if (!pupilIsRegistered(pupil)) {
                return PUPIL_NAME;
            }
            if (pupil.sex == null) {
                return PUPIL_SEX;
            }
            if (pupil.complexity == null) {
                return QTP_COMPLEXITY;
            }
            if (pupil.avatar == null) {
                return PUPIL_AVATAR;
            }
            if (PhoneManager.phoneIsAvailable(mContext)) {
                List<PhoneContact> phoneContacts = getPhoneContacts();
                if (phoneContacts == null || phoneContacts.size() == 0) {
                    return SETUP_ALLOW_CONTACTS;
                }
            }
        }
        return SETTINGS;
    }

    public void setIntroductionIsPresented(boolean value) {
        mSettingsStorage.setIntroductionIsPresented(value);
    }

    @Override
    protected String getName() {
        return NAME;
    }

    public void setCurrentSetupStep(ISetupStep value) {
        if (value == SETTINGS) {
            completeSetupWizard();
        }
        if (value != START) {
            startSetupWizard();
        }
        this.mCurrentSetupStep = value;
    }

    public void setUnlockSecret(String value) {
        LockScreen.setUnlockSecret(mContext, value);
    }

    public boolean checkUnlockSecret(String password) {
        return LockScreen.tryToLogin(mContext, password);
    }

    public void saveAllowContacts(List<PhoneContact> value) {
        Pupil pupil = getPupil();
        if (pupil != null) {
            pupil.setPhoneContacts(value);
        }
        mPupilManager.update(pupil);
    }

    public List<PhoneContact> getPhoneContacts() {
        Pupil pupil = getPupil();
        if (pupil != null) {
            return pupil.getPhoneContacts();
        }
        return null;
    }

    public void show() {
        LockScreen.show(mContext);
    }

    public void switchOff() {
        LockScreen.switchOff(mContext);
    }

    public boolean phoneIsAvailable() {
        return PhoneManager.phoneIsAvailable(mContext);
    }

    public boolean lockScreenIsActive() {
        return LockScreen.isActive(mContext);
    }

    public enum Step implements ISetupStep {

        START(SETUP_WIZARD_PARENT),
        OVERLAY_PERMISSION(SETUP_WIZARD_PARENT | NEED_LOGIN),
        SETUP_UNLOCK_SECRET(SETUP_WIZARD_PARENT),
        BIND_PARENT_CONTROL(SETTINGS_PARENT | NEED_LOGIN),
        CHANGE_UNLOCK_SECRET(SETTINGS_PARENT | NEED_LOGIN),
        PUPIL_NAME(SETUP_WIZARD_PARENT | NEED_LOGIN),
        PUPIL_SEX(SETUP_WIZARD_PARENT | NEED_LOGIN),
        PUPIL_AVATAR(SETUP_WIZARD_PARENT | NEED_LOGIN),
        QTP_COMPLEXITY(SETUP_WIZARD_PARENT | NEED_LOGIN),
        CALL_PHONE_AND_READ_CONTACTS_PERMISSION(SETUP_WIZARD_PARENT | NEED_LOGIN),
        SETUP_ALLOW_CONTACTS(SETTINGS_PARENT | NEED_LOGIN),
        SETTINGS(SETTINGS_PARENT | NEED_LOGIN);

        private int mFlags;

        Step(int flags) {
            mFlags = flags;
        }

        @Override
        public int getFlags() {
            return mFlags;
        }

        @Override
        public boolean needLogin() {
            return (mFlags & StepFlag.NEED_LOGIN) != 0;
        }

    }

    static class StepFlag {

        static final int SETUP_WIZARD_PARENT = 1;
        static final int SETTINGS_PARENT = 2;
        static final int NEED_LOGIN = 4;

    }
}