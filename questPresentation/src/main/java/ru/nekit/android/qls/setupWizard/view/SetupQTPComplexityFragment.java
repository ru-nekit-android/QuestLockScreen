package ru.nekit.android.qls.setupWizard.view;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioGroup;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramComplexity;

public class SetupQTPComplexityFragment extends QuestSetupWizardFragment
        implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup mQTPComplexityGroup;

    public static SetupQTPComplexityFragment getInstance() {
        return new SetupQTPComplexityFragment();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        mQTPComplexityGroup = (RadioGroup) view.findViewById(R.id.complexity_group);
        mQTPComplexityGroup.setOnCheckedChangeListener(this);
        setNextButtonText(R.string.label_ok);
        update(false);
    }

    @Override
    protected boolean nextAction() {
        QuestTrainingProgramComplexity complexity = null;
        int selectedComplexity = mQTPComplexityGroup.getCheckedRadioButtonId();
        if (selectedComplexity != -1) {
            if (selectedComplexity == R.id.complexity_easy) {
                complexity = QuestTrainingProgramComplexity.EASY;
            } else if (selectedComplexity == R.id.complexity_normal) {
                complexity = QuestTrainingProgramComplexity.NORMAL;
            } else if (selectedComplexity == R.id.complexity_hard) {
                complexity = QuestTrainingProgramComplexity.HARD;
            }
        }
        if (complexity != null) {
            getSetupWizard().setQTPComplexity(complexity);
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_setup_qtp_complexity;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        update(true);
    }

    private void update(boolean choiced) {
        setNextButtonVisibility(choiced);
    }

    @Override
    public void onDestroy() {
        mQTPComplexityGroup.setOnCheckedChangeListener(null);
        super.onDestroy();
    }
}
