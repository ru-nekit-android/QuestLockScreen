package ru.nekit.android.qls.setupWizard.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import ru.nekit.android.domain.event.IEvent
import ru.nekit.android.qls.BuildConfig
import ru.nekit.android.qls.R
import ru.nekit.android.qls.data.repository.SettingsRepository
import ru.nekit.android.qls.domain.providers.DependenciesHolder
import ru.nekit.android.qls.domain.repository.ISettingsRepository
import ru.nekit.android.utils.ParameterlessSingletonHolder

class SettingsRemoteDataSource : DependenciesHolder() {

    private var firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    private val settingsRepository: ISettingsRepository
        get() = repositoryHolder.getSettingsRepository()

    companion object : ParameterlessSingletonHolder<SettingsRemoteDataSource>(::SettingsRemoteDataSource)

    fun fetchConfig() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        firebaseRemoteConfig.apply {
            setConfigSettings(configSettings)
            setDefaults(R.xml.remote_config_defaults)
            firebaseRemoteConfig.fetch(0).addOnCompleteListener({
                if (it.isSuccessful) {
                    firebaseRemoteConfig.apply {
                        activateFetched()
                        eventSender.send(RemoteConfigFetchCompleteEvent)
                        settingsRepository.apply {
                            skipAfterRightAnswer =
                                    getBoolean(SettingsRepository.SKIP_AFTER_RIGHT_ANSWER)
                            timeoutToSkipAfterRightAnswer =
                                    getLong(SettingsRepository.TIME_OUT_TO_SKIP_AFTER_RIGHT_ANSWER)
                            maxGameSessionTime =
                                    getLong(SettingsRepository.MAX_GAME_SESSION_TIME)
                            adsSkipTimeout =
                                    getLong(SettingsRepository.ADS_SKIP_TIMEOUT)
                            useRemoteQTP =
                                    getBoolean(SettingsRepository.USE_REMOTE_QTP)
                            useQTPComplexity =
                                    getBoolean(SettingsRepository.USE_QTP_COMPLEXITY)
                            useSexForQTP =
                                    getBoolean(SettingsRepository.USE_SEX_FOR_QTP)
                            useSingleQTP =
                                    getBoolean(SettingsRepository.USE_SINGLE_QTP)
                            QTPGroup =
                                    getString(SettingsRepository.QTP_GROUP)
                        }
                    }
                } else {
                }
            })
        }
    }

}

object RemoteConfigFetchCompleteEvent : IEvent {

    override val eventName: String = javaClass.name

}