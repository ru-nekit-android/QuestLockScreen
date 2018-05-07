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
    }

    private val publisher: SingleSubject<Optional<String>> = SingleSubject.create<Optional<String>>()

    override fun create(sex: PupilSex, complexity: Complexity): Single<Optional<String>> {
        FirebaseFirestore.getInstance()
                .collection(QTP_BASE).document(sex.name.toLowerCase())
                .collection(complexity.name.toLowerCase())
                .document("default").get().addOnSuccessListener {
                    QuestTrainingProgramUseCases.getVersion { localVersion ->
                        val remoteVersion = it.getDouble("version")
                        publisher.onSuccess(Optional(if (localVersion < remoteVersion)
                            it.getString("value") else null))
                    }
                }
        return publisher
    }

}