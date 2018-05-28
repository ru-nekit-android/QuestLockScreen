package ru.nekit.android.qls.setupWizard.remote

import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import ru.nekit.android.domain.model.Optional
import ru.nekit.android.qls.domain.repository.IQuestTrainingProgramDataSource
import ru.nekit.android.qls.domain.useCases.QuestTrainingProgramUseCases
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.PupilSex

class RemoteQuestTrainingProgramDataSource : IQuestTrainingProgramDataSource {

    companion object {
        private const val QTP_BASE = "qtp"
        private const val DEFAULT: String = "default"
        private const val VALUE: String = "value"
        private const val VERSION: String = "version"
    }

    private val publisher: SingleSubject<Optional<String>> = SingleSubject.create<Optional<String>>()

    override fun create(sex: PupilSex?, complexity: Complexity?, qtpGroup: String?): Single<Optional<String>> {
        FirebaseFirestore.getInstance()
                .collection(QTP_BASE)
                .let {
                    if (sex != null)
                        it.document(sex.name.toLowerCase())
                    else
                        it.document(DEFAULT)
                }.let { document ->
                    if (complexity != null)
                        document.collection(complexity.name.toLowerCase())
                    else
                        document.collection(DEFAULT)
                }.document(qtpGroup ?: DEFAULT)
                .get().addOnSuccessListener {
                    if (it.exists())
                        QuestTrainingProgramUseCases.getVersion { localVersion ->
                            try {
                                val remoteVersion = it.getDouble(VERSION)
                                publisher.onSuccess(Optional(if (localVersion < remoteVersion)
                                    it.getString(VALUE) else null))
                            } catch (exp: Exception) {

                            }
                        }
                }
        return publisher
    }
}