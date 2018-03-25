package ru.nekit.android.qls.setupWizard


interface ISetupWizardStep {

    fun needLogin(): Boolean

    fun needInternetConnection(): Boolean

}