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

    protected EventBus mEventBus;
    protected BaseSetupWizard mSetupWizard;
    private Button mNextButton, mAltButton;
    private ViewGroup mToolContainer, mFragmentContainer;
    private BaseSetupWizardFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_wizard);
        mEventBus = new EventBus(this);
        mToolContainer = (ViewGroup) findViewById(R.id.container_tool);
        mFragmentContainer = (ViewGroup) findViewById(R.id.container_fragment);
        mNextButton = (Button) findViewById(R.id.btn_next);
        mAltButton = (Button) findViewById(R.id.btn_alt);
        mNextButton.setOnClickListener(this);
        mAltButton.setOnClickListener(this);
        mSetupWizard = createSetupWizard(getApplicationContext());
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
    public boolean nextButtonAction() {
        return mCurrentFragment.nextButtonAction();
    }

    @Override
    public void altButtonAction() {
        mCurrentFragment.altButtonAction();
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
    public abstract void showSetupWizardStep(@NonNull ISetupStep step, Object... params);

    protected void replaceFragment(@NonNull BaseSetupWizardFragment fragment) {
        mCurrentFragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.slide_in_short, R.anim.slide_out_short);
        //fragmentTransaction.disallowAddToBackStack();
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (fragment.addToBackStack()) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(R.id.container_fragment, fragment);
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
            if (nextButtonAction()) {
                showSetupWizardStep(mSetupWizard.getNextStep());
            }
        } else if (view == mAltButton) {
            altButtonAction();
        }
    }
}