package ru.nekit.android.qls.quest.resources.representation

import ru.nekit.android.qls.domain.model.math.MathematicalOperation
import ru.nekit.android.qls.quest.resources.representation.common.StringRepresentation
import ru.nekit.android.qls.quest.resources.representation.common.StringRepresentationProvider

object MathematicalOperationRepresentationProvider : StringRepresentationProvider<MathematicalOperation>() {

    init {
        createRepresentation(MathematicalOperation.ADDITION, "+")
        createRepresentation(MathematicalOperation.DIVISION, ":")
        createRepresentation(MathematicalOperation.MULTIPLICATION, "×")
        createRepresentation(MathematicalOperation.SUBTRACTION, "−")
    }

    private fun createRepresentation(key: MathematicalOperation, string: String) {
        createRepresentation(key, StringRepresentation(string))
    }

}

fun MathematicalOperation.getRepresentationAsSign() = MathematicalOperationRepresentationProvider.getRepresentation(this)
