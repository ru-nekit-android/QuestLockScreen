package ru.nekit.android.qls.domain.creator

import ru.nekit.android.qls.domain.model.TimeQuestTrainingProgramRule

class CurrentTimeQuestCreator(rule: TimeQuestTrainingProgramRule) : TimeQuestCreator(rule) {

    //cant use zero - because cant divide by zero
    override val accuracy: Int
        get() = 1

}
