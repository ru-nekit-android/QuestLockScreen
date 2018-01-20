package ru.nekit.android.qls.setupWizard.view;

import android.support.annotation.NonNull;
import android.view.View;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.BaseSetupWizardStep;
import ru.nekit.android.qls.setupWizard.ISetupWizardStep;
import ru.nekit.android.qls.utils.KeyboardHost;
import ru.nekit.android.qls.utils.Vibrate;

import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.WRONG;
import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep.SETUP_UNLOCK_SECRET;

public class UnlockSecretFragment extends QuestSetupWizardFragment
        implements PatternLockViewListener {

    private ISetupWizardStep mStep;
    private PatternLockView mPatternLockView;
    private List<PatternLockView.Dot> mPattern;

    public static UnlockSecretFragment getInstance(@NonNull ISetupWizardStep step) {
        UnlockSecretFragment fragment = new UnlockSecretFragment();
        fragment.mStep = step;
        return fragment;
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onProgress(List<PatternLockView.Dot> pattern) {
        if (mStep == SETUP_UNLOCK_SECRET) {
            setAltButtonVisibility(pattern.size() >= BaseSetupWizard.UNLOCK_SECRET_MIN_SIZE);
        }
    }

    @Override
    public void onResume() {
        KeyboardHost.hideKeyboard(getActivity().getApplicationContext(), mPatternLockView);
        super.onResume();
    }

    @Override
    public void onComplete(List<PatternLockView.Dot> pattern) {
        if (pattern.size() < BaseSetupWizard.UNLOCK_SECRET_MIN_SIZE) {
            mPatternLockView.setViewMode(WRONG);
        } else {
            if (mStep == SETUP_UNLOCK_SECRET) {
                mPattern = pattern;
                update(true);
            } else if (mStep == BaseSetupWizardStep.UNLOCK_SECRET) {
                if (getSetupWizard().checkUnlockSecret(
                        PatternLockUtils.patternToMD5(mPatternLockView, pattern))) {
                    showNextSetupWizardStep();
                } else {
                    mPatternLockView.setViewMode(WRONG);
                    Vibrate.make(getContext(), 400);
                }
            }
        }
    }

    @Override
    public void onCleared() {
        resetPattern();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        mPatternLockView = (PatternLockView) view.findViewById(R.id.unlock_secret_view);
        mPatternLockView.addPatternLockListener(this);
        mPatternLockView.setDotCount(3);
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        if (mStep == SETUP_UNLOCK_SECRET) {
            setNextButtonText(R.string.label_set_unlock_secret);
            setAltButtonText(R.string.label_reset_unlock_secret);
        } else if (mStep == BaseSetupWizardStep.UNLOCK_SECRET) {
            setToolContainerVisibility(false);
        }
        setAltButtonVisibility(false);
        update(false);
    }

    public void onDestroy() {
        mPatternLockView.removePatternLockListener(this);
        super.onDestroy();
    }

    @Override
    protected boolean nextAction() {
        getSetupWizard().setUnlockSecret(PatternLockUtils.patternToMD5(mPatternLockView, mPattern));
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_unlock_secret;
    }

    private void update(boolean unlockSecretIsSet) {
        setNextButtonVisibility(unlockSecretIsSet);
    }

    @Override
    protected void altAction() {
        mPatternLockView.clearPattern();
        resetPattern();
    }

    private void resetPattern() {
        mPattern = null;
        setAltButtonVisibility(false);
        setNextButtonVisibility(false);
    }
}