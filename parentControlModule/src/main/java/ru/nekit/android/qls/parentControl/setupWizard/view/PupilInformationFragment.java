package ru.nekit.android.qls.parentControl.setupWizard.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.parentControl.ParentControlService;
import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilManager;

public class PupilInformationFragment
        extends ParentControlSetupWizardFragment {

    PupilManager pupilManager;
    Pupil pupil;

    public static PupilInformationFragment getInstance(Object... params) {
        Bundle arguments = new Bundle();
        arguments.putString(ParentControlService.NAME_PUPIL_UUID, (String) params[0]);
        PupilInformationFragment fragment = new PupilInformationFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        pupilManager = new PupilManager(getContext());
        pupil = pupilManager.getPupilByUuid(
                getArguments().getString(ParentControlService.NAME_PUPIL_UUID));
        setAltButtonVisibility(true);
        setNextButtonVisibility(false);
        setAltButtonText(R.string.back);
    }

    @Override
    protected void altButtonAction() {
        showSetupWizardStep(ParentControlSetupWizard.Step.PUPIL_LIST);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_pupil_information;
    }
}