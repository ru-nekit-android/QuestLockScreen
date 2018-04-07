package ru.nekit.android.qls.setupWizard

enum class BaseSetupWizardStep constructor(val flags: Int) : ISetupWizardStep {

    UNLOCK_SECRET(0),
    SETUP_INTERNET_CONNECTION(0);

    override fun needLogin(): Boolean {
        return false
    }

    override fun needInternetConnection(): Boolean {
        return false
    }
}