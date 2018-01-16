package ru.nekit.android.qls.parentControl.setupWizard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.parentControl.ParentControlService;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.ISetupWizardStep;

import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.CAMERA_PERMISSION;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.PUPIL_LIST;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.PURCHASES;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.WizardStep.START;

public class ParentControlSetupWizard extends BaseSetupWizard {

    private static final String NAME = "parentControlSetupWizard";

    private EventBus mEventBus;

    public ParentControlSetupWizard(@NonNull Context context) {
        super(context);
        mEventBus = new EventBus(context);
    }

    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, ParentControlSetupWizardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Intent getStartIntent(@NonNull Context context, boolean newTask) {
        Intent intent = new Intent(context, ParentControlSetupWizardActivity.class);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    private boolean readContactsPermissionIsGranted() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private WizardStep getNextStepForAllPermission() {
        if (!readContactsPermissionIsGranted()) {
            return CAMERA_PERMISSION;
        }
        return null;
    }

    @NonNull
    @Override
    protected WizardStep calculateNextStep() {
        if (setupIsCompleted()) {
            WizardStep stepForAllPermission = getNextStepForAllPermission();
            if (stepForAllPermission != null) {
                return stepForAllPermission;
            }
        } else {
            if (mCurrentStep == null) {
                return START;
            }
            WizardStep stepForAllPermission = getNextStepForAllPermission();
            if (stepForAllPermission != null) {
                return stepForAllPermission;
            }
        }
        return PURCHASES;
    }

    @Override
    protected String getName() {
        return NAME;
    }

    public void setCurrentSetupStep(ISetupWizardStep value) {
        if (value == PUPIL_LIST) {
            completeSetupWizard();
        } else if (value != START) {
            startSetupWizard();
        }
        this.mCurrentStep = value;
    }

    public int getQuestSeriesLength() {
        return mSettingsStorage.getQuestSeriesLength();
    }

    public void setQuestSeriesLength(int value) {
        mSettingsStorage.setQuestSeriesLength(value);
    }

    public void reset() {
        completeSetupWizard(false);
        startSetupWizard(false);
    }

    public boolean bindPupilIfAble(@NonNull String bindCode) {
        Pupil pupil = createPupilFromBindCode(bindCode);
        boolean pupilIsExist = mPupilManager.addIfAble(pupil);
        if (pupilIsExist) {
            mEventBus.sendEvent(ParentControlService.ACTION_BIND_PUPIL,
                    ParentControlService.NAME_PUPIL_UUID,
                    pupil.getUuid());
        }
        return pupilIsExist;
    }

    public void unbindPupil(String pupilUuid) {
        mPupilManager.remove(pupilUuid);
    }

    public enum WizardStep implements ISetupWizardStep {

        START,
        BIND_PUPIL,
        CAMERA_PERMISSION,
        PUPIL_LIST,
        PURCHASES,
        ACQUIRE_PRESENT,
        PUPIL_INFORMATION;

        @Override
        public int getFlags() {
            return 0;
        }

        @Override
        public boolean needLogin() {
            return false;
        }

    }
}