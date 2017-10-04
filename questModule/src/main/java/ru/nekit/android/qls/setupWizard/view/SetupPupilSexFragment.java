package ru.nekit.android.qls.setupWizard.view;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RadioGroup;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.pupil.PupilSex;

public class SetupPupilSexFragment extends QuestSetupWizardFragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup mPupilSexGroup;

    public static SetupPupilSexFragment getInstance() {
        return new SetupPupilSexFragment();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        mPupilSexGroup = (RadioGroup) view.findViewById(R.id.pupil_sex_group);
        mPupilSexGroup.setOnCheckedChangeListener(this);
        setNextButtonText(R.string.label_ok);
        update(false);
    }

    private void update(boolean choiced) {
        setNextButtonVisibility(choiced);
    }

    @Override
    protected boolean nextButtonAction() {
        PupilSex pupilSex = null;
        int selectedSex = mPupilSexGroup.getCheckedRadioButtonId();
        if (selectedSex != -1) {
            pupilSex = selectedSex == R.id.pupil_sex_boy ? PupilSex.BOY : PupilSex.GIRL;
        }
        if (pupilSex != null) {
            getSetupWizard().setPupilSex(pupilSex);
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_setup_pupil_sex;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        update(true);
    }

    @Override
    public void onDestroy() {
        mPupilSexGroup.setOnCheckedChangeListener(null);
        super.onDestroy();
    }
}
