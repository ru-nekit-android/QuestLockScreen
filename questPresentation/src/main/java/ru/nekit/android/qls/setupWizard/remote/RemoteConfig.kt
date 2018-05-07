package ru.nekit.android.qls.setupWizard.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.qls.BuildConfig
import ru.nekit.android.qls.R
import ru.nekit.android.qls.data.repository.QuestSetupWizardSettingRepository
import ru.nekit.android.qls.domain.providers.DependenciesHolder
import ru.nekit.android.qls.domain.repository.IQuestSetupWizardSettingRepository

class RemoteConfig : DependenciesHolder() {

    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private val questSetupWizardRepository: IQuestSetupWizardSettingRepository
        get() = repositoryHolder.getQuestSetupWizardSettingRepository()

    companion object {

        private var mInstance: RemoteConfig? = null

        fun getInstance(): RemoteConfig {
            if (mInstance == null) {
                val configSettings = FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build()
                mInstance = RemoteConfig().apply {
                    firebaseRemoteConfig.apply {
                        setConfigSettings(configSettings)
                        setDefaults(R.xml.remote_config_defaults)
                        firebaseRemoteConfig.fetch(0).addOnCompleteListener({
                            if (it.isSuccessful) {
                                firebaseRemoteConfig.apply {
                                    activateFetched()
                                    eventSender.send(RemoteConfigFetchCompleteEvent)
                                    questSetupWizardRepository.apply {
                                        skipAfterRightAnswer =
                                                getBoolean(QuestSetupWizardSettingRepository.SKIP_AFTER_RIGHT_ANSWER)
                                        version =
                                                getString(QuestSetupWizardSettingRepository.VERSION)
                                        timeoutToSkipAfterRightAnswer =
                                                getLong(QuestSetupWizardSettingRepository.TIME_OUT_TO_SKIP_AFTER_RIGHT_ANSWER)
                                        maxGameSessionTime =
                                                getLong(QuestSetupWizardSettingRepository.MAX_GAME_SESSION_TIME)
                                        adsSkipTimeout =
                                                getLong(QuestSetupWizardSettingRepository.ADS_SKIP_TIMEOUT)
                                        useRemoteQTP =
                                                getBoolean(QuestSetupWizardSettingRepository.USE_REMOTE_QTP)
                                    }
                                }
                            } else {
                            }
                        })
                    }
                }
            }
            return mInstance!!
        }
    }
}

object RemoteConfigFetchCompleteEvent : IEvent {

    override val eventName: String = javaClass.name

}