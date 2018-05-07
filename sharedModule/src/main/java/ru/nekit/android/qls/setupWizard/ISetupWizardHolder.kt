package ru.nekit.android.qls.setupWizard

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import io.reactivex.Single

internal interface ISetupWizardHolder {

    val setupWizard: BaseSetupWizard

    fun getNextButton(): Button

    fun getAltButton(): Button

    fun getToolContainer(): ViewGroup

    fun getView(): View

    fun showSetupWizardStep(step: ISetupWizardStep, vararg params: Any)

    fun showNextSetupWizardStep()

    fun nextAction(): Single<Boolean>

    fun altAction()
}