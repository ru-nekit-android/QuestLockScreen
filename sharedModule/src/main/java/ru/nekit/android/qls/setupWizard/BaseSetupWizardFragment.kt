package ru.nekit.android.qls.setupWizard

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
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
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDefaultSettingsForTools()
        onSetupStart(view)
    }

    protected fun showSetupWizardStep(step: ISetupWizardStep, vararg params: Any) {
        setupWizardHolder.showSetupWizardStep(step, *params)
    }

    protected open fun goNext() {
        setupWizardHolder.showNextSetupWizardStep()
    }

    open var title: Int
        get() = 0
        set(value) = setupWizardHolder.setTitle(value)

    open val addToBackStack: Boolean = false

    private fun setDefaultSettingsForTools() {
        nextButtonText(R.string.label_next)
        nextButtonVisibility(true)
        altButtonVisibility(false)
        toolContainerVisibility(true)
    }

    open var unconditionedNextAction: Boolean = false

    protected fun toolContainerVisibility(visibility: Boolean) {
        toolContainer.visibility = if (visibility) VISIBLE else GONE
    }

    protected fun nextButtonVisibility(visibility: Boolean) {
        nextButton.visibility = if (visibility) VISIBLE else INVISIBLE
    }

    protected fun altButtonVisibility(visibility: Boolean) {
        altButton.visibility = if (visibility) VISIBLE else INVISIBLE
    }

    protected fun nextButtonText(@StringRes textResId: Int) {
        var textResIdLocal = textResId
        if (textResId == 0) {
            textResIdLocal = R.string.label_next
        }
        nextButtonText(getString(textResIdLocal))
    }

    protected fun nextButtonText(text: String) {
        nextButton.text = text
    }

    protected fun altButtonText(@StringRes textResId: Int) {
        altButtonVisibility(true)
        altButton.text = getString(textResId)
    }

    open fun nextAction() = true.toSingle()

    open fun altAction() {
        if (addToBackStack) {
            goBack()
        }
    }

    protected fun goBack() = activity?.supportFragmentManager?.popBackStack()

    protected fun backAction(): Single<Boolean> = Single.fromCallable {
        goBack()
        false
    }

    @CallSuper
    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }

}