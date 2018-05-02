package ru.nekit.android.qls.setupWizard

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import ru.nekit.android.shared.R
import ru.nekit.android.utils.IAutoDispose
import ru.nekit.android.utils.toSingle

abstract class BaseSetupWizardFragment : Fragment(), IAutoDispose {

    open val setupWizard: BaseSetupWizard
        get() = setupWizardHolder.setupWizard

    private val setupWizardHolder: ISetupWizardHolder
        get() = activity as ISetupWizardHolder

    private val nextButton: Button
        get() = setupWizardHolder.getNextButton()

    private val altButton: Button
        get() = setupWizardHolder.getAltButton()

    private val toolContainer: View
        get() = setupWizardHolder.getToolContainer()

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun onSetupStart(view: View)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(getLayoutId(), container, false).apply {
            setDefaultSettingsForTools()
            onSetupStart(this)
        }
    }

    protected fun showSetupWizardStep(step: ISetupWizardStep, vararg params: Any) {
        setupWizardHolder.showSetupWizardStep(step, *params)
    }

    protected fun showNextSetupWizardStep() {
        setupWizardHolder.showNextSetupWizardStep()
    }

    open val addToBackStack: Boolean = false

    private fun setDefaultSettingsForTools() {
        setNextButtonText(R.string.label_next)
        setNextButtonVisibility(true)
        setAltButtonVisibility(false)
        setToolContainerVisibility(true)
    }

    open var unconditionedNextAction: Boolean = false

    protected fun setToolContainerVisibility(visibility: Boolean) {
        toolContainer.visibility = if (visibility) VISIBLE else INVISIBLE
    }

    protected fun setNextButtonVisibility(visibility: Boolean) {
        nextButton.visibility = if (visibility) VISIBLE else INVISIBLE
    }

    protected fun setAltButtonVisibility(visibility: Boolean) {
        altButton.visibility = if (visibility) VISIBLE else INVISIBLE
    }

    protected fun setNextButtonText(@StringRes textResId: Int) {
        var textResIdLocal = textResId
        if (textResId == 0) {
            textResIdLocal = R.string.label_next
        }
        setNextButtonText(getString(textResIdLocal))
    }

    protected fun setNextButtonText(text: String) {
        nextButton.text = text
    }

    protected fun setAltButtonText(@StringRes textResId: Int) {
        setAltButtonVisibility(true)
        altButton.text = getString(textResId)
    }

    open fun nextAction(): Single<Boolean> {
        return true.toSingle()
    }

    open fun altAction() {
        if (addToBackStack) {
            back()
        }
    }

    protected fun back() = activity?.supportFragmentManager?.popBackStack()

    @CallSuper
    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }

}