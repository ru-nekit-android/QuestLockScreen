package ru.nekit.android.qls.parentControl.setupWizard.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import ru.nekit.android.qls.parentControl.ParentControlService;
import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilManager;

public class PupilInformationFragment
        extends ParentControlSetupWizardFragment implements View.OnClickListener {

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
        view.findViewById(R.id.btn_pupil_unbind).setOnClickListener(this);
    }

    @Override
    protected void altButtonAction() {
        showSetupWizardStep(ParentControlSetupWizard.Step.PUPIL_LIST);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_pupil_information;
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
        ad.setTitle(R.string.title_do_you_really_want_to_delete);
        ad.setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int argument) {
                String pupilUuid = getArguments().getString(ParentControlService.NAME_PUPIL_UUID);
                getSetupWizard().unbindPupil(pupilUuid);
                showSetupWizardStep(ParentControlSetupWizard.Step.PUPIL_LIST);
            }
        });
        ad.setNegativeButton(R.string.label_no, null);
        ad.setCancelable(true);
        ad.show();

    }
}