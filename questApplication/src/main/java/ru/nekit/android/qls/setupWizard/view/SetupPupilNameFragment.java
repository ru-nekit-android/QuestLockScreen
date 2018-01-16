package ru.nekit.android.qls.setupWizard.view;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.utils.KeyboardHost;

public class SetupPupilNameFragment extends QuestSetupWizardFragment {

    private TextInputLayout mPupilNameLayout;
    private EditText mPupilNameInput;

    public static SetupPupilNameFragment getInstance() {
        return new SetupPupilNameFragment();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        mPupilNameLayout = (TextInputLayout) view.findViewById(R.id.input_pupil_name_layout);
        mPupilNameInput = (EditText) view.findViewById(R.id.input_pupil_name);
        setNextButtonText(R.string.label_ok);
    }

    @Override
    protected boolean nextAction() {
        String pupilName = mPupilNameInput.getText().toString();
        boolean nameIsEmpty = pupilName.isEmpty();
        mPupilNameLayout.setErrorEnabled(nameIsEmpty);
        if (nameIsEmpty) {
            mPupilNameLayout.setError(getString(R.string.error_pupil_name_should_be_entered));
        } else {
            getSetupWizard().setPupilName(pupilName);
            KeyboardHost.hideKeyboard(getContext(), mPupilNameInput);
        }
        return !nameIsEmpty;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_setup_pupil_name;
    }

    @Override
    public void onResume() {
        KeyboardHost.showKeyboard(getContext(), mPupilNameInput);
        super.onResume();
    }
}
