package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.questData.R.string.*

object PupilSexRepresentationProvider : StringIdRepresentationProvider<PupilSex>() {

    init {
        createRepresentation(PupilSex.BOY, pupil_sex_boy_title)
        createRepresentation(PupilSex.GIRL, pupil_sex_girl_title)

    }
}

object ComplexityRepresentationProvider : StringIdRepresentationProvider<Complexity>() {

    init {
        createRepresentation(Complexity.EASY, complexity_easy_title)
        createRepresentation(Complexity.NORMAL, complexity_normal_title)
        createRepresentation(Complexity.HARD, complexity_hard_title)


    }
}

fun PupilSex.getRepresentation() = PupilSexRepresentationProvider.getRepresentation(this)
fun Complexity.getRepresentation() = ComplexityRepresentationProvider.getRepresentation(this)