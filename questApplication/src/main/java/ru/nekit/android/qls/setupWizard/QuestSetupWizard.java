package ru.nekit.android.qls.setupWizard;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.deviceAdminSupport.DeviceAdminComponent;
import ru.nekit.android.qls.lockScreen.LockScreen;
import ru.nekit.android.qls.pupil.PhoneContact;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilSex;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramComplexity;
import ru.nekit.android.qls.setupWizard.view.QuestSetupWizardActivity;
import ru.nekit.android.qls.utils.PhoneManager;

import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SETTINGS;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.START;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.CAN_BE_RESET_AFTER_SET;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.SETTINGS_PARENT;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.StepFlag.SETUP_WIZARD_PARENT;

public class QuestSetupWizard extends BaseSetupWizard {

    private static final String NAME = "questSetupWizard";

    @SuppressLint("StaticFieldLeak")
    private static QuestSetupWizard instance;

    private final EventBus mEventBus;

    private QuestSetupWizard(@NonNull Context context) {
        super(context);
        mEventBus = new EventBus(context);
    }

    public static QuestSetupWizard getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new QuestSetupWizard(context);
        }
        return instance;
    }

    private boolean overlayPermissionIsSet() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(mContext);
    }

    private boolean callPhonePermissionIsGranted() {
        return PhoneManager.callPhonePermissionIsGranted(mContext);
    }

    private boolean readContactsPermissionIsGranted() {
        return PhoneManager.readContactsPermissionIsGranted(mContext);
    }

    public boolean allPermissionsIsGranted() {
        boolean permissionIsGranted = true;
        if (PhoneManager.phoneIsAvailable(mContext)) {
            permissionIsGranted = callPhonePermissionIsGranted()
                    && readContactsPermissionIsGranted();
        }
        return permissionIsGranted && overlayPermissionIsSet();
    }

    public void start() {
        mContext.startActivity(getStartIntent(true));
    }

    public Intent getStartIntent(boolean newTask) {
        Intent intent = new Intent(mContext, QuestSetupWizardActivity.class);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    public boolean setupIsComplete() {
        return mSettingsStorage.setupWizardIsCompleted(NAME);
    }

    private boolean unlockPasswordIsSet() {
        return LockScreen.getUnlockSecret() != null;
    }

    private boolean knoxIsSupport() {
        PackageManager packageManager = mContext.getPackageManager();
        return packageManager.hasSystemFeature("com.sec.android.mdm");
    }

    public ComponentName getDeviceAdminComponent() {
        return new ComponentName(mContext, DeviceAdminComponent.class);
    }

    public boolean deviceAdminIsActive() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return devicePolicyManager.isAdminActive(getDeviceAdminComponent());
    }

    private boolean pupilIsRegistered() {
        return getPupil() != null;
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

    @NonNull
    @Override
    protected QuestSetupWizardStep calculateNextStep() {
        QuestSetupWizardStep[] steps = QuestSetupWizardStep.values();
        if (setupIsCompleted()) {
            for (QuestSetupWizardStep step : steps) {
                int flags = step.getFlags();
                if ((flags & CAN_BE_RESET_AFTER_SET) != 0 && (flags & SETUP_WIZARD_PARENT) != 0) {
                    if (!step.setupIsComplete(this)) {
                        return step;
                    }
                }
            }
        } else {
            for (QuestSetupWizardStep step : steps) {
                if (mCurrentStep == null) {
                    return step;
                } else {
                    if ((step.getFlags() & SETUP_WIZARD_PARENT) != 0) {
                        if (!step.setupIsComplete(this)) {
                            return step;
                        }
                    }
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

    public void commitCurrentSetupStep(ISetupWizardStep value) {
        if (value == SETTINGS) {
            completeSetupWizard();
        }
        if (value != START) {
            startSetupWizard();
        }
        this.mCurrentStep = value;
    }

    public void setUnlockSecret(String value) {
        LockScreen.setUnlockSecret(value);
    }

    public boolean checkUnlockSecret(String password) {
        return LockScreen.tryToLogin(password);
    }

    public void setAllowContacts(List<PhoneContact> value) {
        Pupil pupil = getPupil();
        if (pupil != null) {
            pupil.setAllowContacts(value);
        }
        mPupilManager.update(pupil);
    }

    public List<PhoneContact> getPhoneContacts() {
        Pupil pupil = getPupil();
        if (pupil != null) {
            return pupil.getAllowContacts();
        }
        return null;
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

    public void setDeviceAdminRemovable(boolean value) {
        if (knoxIsSupport()) {
            /*EnterpriseLicenseManager enterpriseLicenseManager = EnterpriseLicenseManager.getInstance(mContext);
            enterpriseLicenseManager.activateLicense();
            EnterpriseDeviceManager edm = new EnterpriseDeviceManager(mContext);
            edm.setAdminRemovable(value);*/
        }
    }

    public enum QuestSetupWizardStep implements ISetupWizardStep {

        START(SETUP_WIZARD_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return true;
            }
        },
        SETUP_UNLOCK_SECRET(SETUP_WIZARD_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return setupWizard.unlockPasswordIsSet();
            }
        },
        DEVICE_ADMIN(SETUP_WIZARD_PARENT | CAN_BE_RESET_AFTER_SET) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return setupWizard.deviceAdminIsActive();
            }
        },
        /*SAMSUNG_ENTERPRISE(SETUP_WIZARD_PARENT | CAN_BE_RESET_AFTER_SET) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return false;
            }
        },*/
        OVERLAY_PERMISSION(SETUP_WIZARD_PARENT | CAN_BE_RESET_AFTER_SET) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return setupWizard.overlayPermissionIsSet();
            }
        },
        BIND_PARENT_CONTROL(SETTINGS_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                throw new UnsupportedOperationException();
            }
        },
        CHANGE_UNLOCK_SECRET(SETTINGS_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                throw new UnsupportedOperationException();
            }
        },
        CALL_PHONE_AND_READ_CONTACTS_PERMISSION(SETUP_WIZARD_PARENT | CAN_BE_RESET_AFTER_SET) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return setupWizard.phoneIsAvailable() && setupWizard.callPhonePermissionIsGranted();
            }
        },
        PUPIL_NAME(SETUP_WIZARD_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return setupWizard.pupilIsRegistered();
            }
        },
        PUPIL_SEX(SETUP_WIZARD_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return setupWizard.getPupil().sex != null;
            }
        },
        QTP_COMPLEXITY(SETUP_WIZARD_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return setupWizard.getPupil().complexity != null;
            }
        },
        PUPIL_AVATAR(SETUP_WIZARD_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                return setupWizard.getPupil().avatar != null;
            }
        },
        SETUP_ALLOW_CONTACTS(SETTINGS_PARENT | SETUP_WIZARD_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                List<PhoneContact> phoneContacts = setupWizard.getPhoneContacts();
                return setupWizard.phoneIsAvailable()
                        && phoneContacts != null
                        && phoneContacts.size() >= 0;
            }
        },
        SETTINGS(SETTINGS_PARENT) {
            @Override
            public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard) {
                throw new UnsupportedOperationException();
            }
        };

        private int mFlags;

        QuestSetupWizardStep(int flags) {
            mFlags = flags;
        }

        @Override
        public int getFlags() {
            return mFlags;
        }

        @Override
        public boolean needLogin() {
            return !(this == SETUP_UNLOCK_SECRET || this == START);
        }

        @Override
        public boolean needInternetConnection() {
            return this == BIND_PARENT_CONTROL;
        }

        abstract public boolean setupIsComplete(@NonNull QuestSetupWizard setupWizard);
    }

    static class StepFlag {

        static final int SETUP_WIZARD_PARENT = 1;
        static final int SETTINGS_PARENT = 2;
        static final int CAN_BE_RESET_AFTER_SET = 4;

    }
}