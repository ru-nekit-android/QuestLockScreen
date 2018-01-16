package ru.nekit.android.qls.setupWizard.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.nekit.android.qls.CONST;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.LockScreen;
import ru.nekit.android.qls.lockScreen.LockScreenMediator;
import ru.nekit.android.qls.setupWizard.QuestSetupWizard.QuestSetupWizardStep;
import ru.nekit.android.qls.utils.KeyboardHost;

public class SettingsFragment extends QuestSetupWizardFragment implements View.OnClickListener {

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().finish();
        }
    };
    private Button mSaveQuestSeriesLength, mShowBindParentControl, mAllowContacts;
    private EditText mQuestSeriesLengthInput;

    public static SettingsFragment getInstance() {
        return new SettingsFragment();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        mSaveQuestSeriesLength = (Button) view.findViewById(R.id.btn_quest_series_length_save);
        mShowBindParentControl = (Button) view.findViewById(R.id.btn_bind_parent_control);
        mAllowContacts = (Button) view.findViewById(R.id.btn_allow_contacts);
        mAllowContacts.setVisibility(getSetupWizard().phoneIsAvailable() ? View.VISIBLE : View.GONE);
        mQuestSeriesLengthInput = (EditText) view.findViewById(R.id.input_quest_series_length);
        setNextButtonText(R.string.label_try_now);
        mQuestSeriesLengthInput.setText(String.valueOf(getSetupWizard().getQuestSeriesLength()));
        IntentFilter intentFilter = new IntentFilter(LockScreenMediator.EVENT_SHOW);
        getContext().registerReceiver(mBroadcastReceiver, intentFilter);
        setAltButtonText(R.string.label_stop_lock);
        mSaveQuestSeriesLength.setOnClickListener(this);
        mShowBindParentControl.setOnClickListener(this);
        mAllowContacts.setOnClickListener(this);
        setAltButtonVisibility(getSetupWizard().lockScreenIsActive());
        setUnconditionedNextAction(true);
    }

    @Override
    public void onDestroy() {
        KeyboardHost.hideKeyboard(getContext(), mQuestSeriesLengthInput);
        mSaveQuestSeriesLength.setOnClickListener(null);
        mShowBindParentControl.setOnClickListener(null);
        mAllowContacts.setOnClickListener(null);
        getContext().unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected boolean nextAction() {
        LockScreen.show(getContext());
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_settings;
    }

    protected void altAction() {
        getSetupWizard().switchOff();
        setAltButtonVisibility(getSetupWizard().lockScreenIsActive());
    }

    @Override
    public void onClick(View view) {
        if (view == mShowBindParentControl) {
            showSetupWizardStep(QuestSetupWizardStep.BIND_PARENT_CONTROL);
        } else if (view == mAllowContacts) {
            showSetupWizardStep(QuestSetupWizardStep.SETUP_ALLOW_CONTACTS);
        } else if (view == mSaveQuestSeriesLength) {
            int questSeriesLength = Math.max(CONST.QUEST_SERIES_LENGTH_BY_DEFAULT,
                    Integer.valueOf(mQuestSeriesLengthInput.getText().toString()));
            getSetupWizard().setQuestSeriesLength(questSeriesLength);
        }
    }
}