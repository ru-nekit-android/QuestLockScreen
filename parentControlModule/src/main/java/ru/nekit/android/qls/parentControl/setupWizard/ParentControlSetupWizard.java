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
import ru.nekit.android.qls.parentControl.setupWizard.view.ParentControlSetupWizardActivity;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.ISetupStep;

import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.Step.CAMERA_PERMISSION;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.Step.PUPIL_LIST;
import static ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard.Step.START;

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

    private static boolean readContactsPermissionIsGranted(@NonNull Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
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

    private Step getNextStepForAllPermission() {
        if (!readContactsPermissionIsGranted(mContext)) {
            return CAMERA_PERMISSION;
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
            Step stepForAllPermission = getNextStepForAllPermission();
            if (stepForAllPermission != null) {
                return stepForAllPermission;
            }
        }
        return PUPIL_LIST;
    }

    @Override
    protected String getName() {
        return NAME;
    }

    public void setCurrentSetupStep(ISetupStep value) {
        if (value == PUPIL_LIST) {
            completeSetupWizard();
        } else if (value != START) {
            startSetupWizard();
        }
        this.mCurrentSetupStep = value;
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

    public enum Step implements ISetupStep {

        START,
        BIND_PUPIL,
        CAMERA_PERMISSION,
        PUPIL_LIST,
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