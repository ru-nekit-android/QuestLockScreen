package ru.nekit.android.qls.setupWizard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.nekit.android.shared.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public abstract class BaseSetupWizardFragment extends Fragment {

    protected abstract void onSetupStart(@NonNull View view);

    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = /*getLayoutId() == 0 ? getLayoutView() :*/
                inflater.inflate(getLayoutId(), container, false);
        setDefaultSettingsForTools();
        onSetupStart(view);
        return view;
    }

    protected BaseSetupWizard getSetupWizard() {
        return getSetupWizardHolder().getSetupWizard();
    }

    protected void showSetupWizardStep(@NonNull ISetupWizardStep step, Object... params) {
        getSetupWizardHolder().showSetupWizardStep(step, params);
    }

    private ISetupWizardHolder getSetupWizardHolder() {
        return (ISetupWizardHolder) getActivity();
    }

    protected void showNextSetupWizardStep() {
        showSetupWizardStep(getSetupWizard().getNextStep());
    }

    protected boolean addToBackStack() {
        return false;
    }

    private void setDefaultSettingsForTools() {
        setNextButtonText(R.string.label_next);
        setNextButtonVisibility(true);
        setAltButtonVisibility(false);
        setToolContainerVisibility(true);
    }

    public void setUnconditionedNextAction(boolean value) {
        getSetupWizardHolder().setUnconditionedNextAction(value);
    }

    final protected Button getNextButton() {
        return getSetupWizardHolder().getNextButton();
    }

    final protected Button getAltButton() {
        return getSetupWizardHolder().getAltButton();
    }

    final protected View getToolContainer() {
        return getSetupWizardHolder().getToolContainer();
    }

    final protected void setToolContainerVisibility(boolean visibility) {
        getToolContainer().setVisibility(visibility ? VISIBLE : INVISIBLE);
    }

    final protected void setNextButtonVisibility(boolean visibility) {
        getNextButton().setVisibility(visibility ? VISIBLE : INVISIBLE);
    }

    final protected void setAltButtonVisibility(boolean visibility) {
        getAltButton().setVisibility(visibility ? VISIBLE : INVISIBLE);
    }

    final protected void setNextButtonText(@StringRes int textResId) {
        if (textResId == 0) {
            textResId = R.string.label_next;
        }
        setNextButtonText(getString(textResId));
    }

    final protected void setNextButtonText(@NonNull String text) {
        getNextButton().setText(text);
    }

    final protected void setAltButtonText(@StringRes int textResId) {
        setAltButtonVisibility(true);
        getAltButton().setText(getString(textResId));
    }

    protected boolean nextAction() {
        return true;
    }

    protected void altAction() {
    }

    protected abstract int getLayoutId();

    /*protected View getLayoutView() {
        return null;
    }

    @Size(value = 4)
    public int[] getContainerMargins(@NonNull Resources resources) {
        int margin = resources.getDimensionPixelSize(R.dimen.base_semi_gap);
        return new int[]{
                margin,
                margin,
                margin,
                margin
        };
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

       Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                BaseSetupWizardFragment.this.onAnimationEnd();
            }
        });

        return anim;
    }

    protected void onAnimationEnd() {

    }*/

}