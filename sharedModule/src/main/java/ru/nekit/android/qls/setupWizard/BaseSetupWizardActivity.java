package ru.nekit.android.qls.setupWizard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.shared.R;

public abstract class BaseSetupWizardActivity extends FragmentActivity implements ISetupWizardHolder,
        View.OnClickListener {

    private static final String FRAGMENT_NAME = "SetupWizardFragment";

    protected EventBus mEventBus;
    protected BaseSetupWizard mSetupWizard;
    private Button mNextButton, mAltButton;
    private ViewGroup mToolContainer, mFragmentContainer;
    private boolean mUnconditionedNextAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        setContentView(R.layout.activity_setup_wizard);
        mEventBus = new EventBus(context);
        mToolContainer = (ViewGroup) findViewById(R.id.container_tool);
        mFragmentContainer = (ViewGroup) findViewById(R.id.container_fragment);
        mNextButton = (Button) findViewById(R.id.btn_next);
        mAltButton = (Button) findViewById(R.id.btn_alt);
        mNextButton.setOnClickListener(this);
        mAltButton.setOnClickListener(this);
        mSetupWizard = createSetupWizard(context);
        showNextSetupWizardStep();
    }

    public Context getContext() {
        return getApplicationContext();
    }

    private void showNextSetupWizardStep() {
        showSetupWizardStep(mSetupWizard.getNextStep());
    }

    protected abstract BaseSetupWizard createSetupWizard(@NonNull Context context);

    public Button getNextButton() {
        return mNextButton;
    }

    @Override
    public Button getAltButton() {
        return mAltButton;
    }

    @Override
    public boolean nextAction() {
        if (mUnconditionedNextAction) {
            return getCurrentFragment().nextAction();
        }
        return getSetupWizard().needLogin(getSetupWizard().getCurrentStep()) || getCurrentFragment().nextAction();
    }

    public void setUnconditionedNextAction(boolean value) {
        mUnconditionedNextAction = value;
    }

    private BaseSetupWizardFragment getCurrentFragment() {
        return (BaseSetupWizardFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_NAME);
    }

    @Override
    public void altAction() {
        getCurrentFragment().altAction();
    }

    @Override
    public ViewGroup getToolContainer() {
        return mToolContainer;
    }

    @Override
    protected void onDestroy() {
        mNextButton.setOnClickListener(null);
        mAltButton.setOnClickListener(null);
        mSetupWizard = null;
        super.onDestroy();
    }

    @Override
    public BaseSetupWizard getSetupWizard() {
        return mSetupWizard;
    }

    @Override
    public abstract void showSetupWizardStep(@NonNull ISetupWizardStep step, Object... params);

    protected void replaceFragment(@NonNull BaseSetupWizardFragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.slide_horizontal_in_short, R.anim.slide_horizontal_out_short);
        //fragmentTransaction.disallowAddToBackStack();
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (fragment.addToBackStack()) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(R.id.container_fragment, fragment, FRAGMENT_NAME);
        fragmentTransaction.commit();
        //Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_short);
        //Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_short);
        //fadeInAnimation.setStartTime(700);
        //AnimationSet mAnimationSet = new AnimationSet(true);
        //mAnimationSet.addAnimation(fadeOutAnimation);
        //mAnimationSet.addAnimation(fadeInAnimation);
        //mToolContainer.startAnimation(mAnimationSet);
        //RelativeLayout.LayoutParams fragmentContainerLayoutParams =
        //        (RelativeLayout.LayoutParams) mFragmentContainer.getLayoutParams();
        //int margins[] = mCurrentFragment.getContainerMargins(getResources());
        //fragmentContainerLayoutParams.setMargins(margins[0], margins[1], margins[2], margins[3]);
        //mFragmentContainer.requestLayout();
    }

    @Override
    public View getView() {
        return mFragmentContainer;
    }

    @Override
    public void onClick(View view) {
        if (view == mNextButton) {
            if (nextAction()) {
                showNextSetupWizardStep();
            }
        } else if (view == mAltButton) {
            altAction();
        }
    }
}