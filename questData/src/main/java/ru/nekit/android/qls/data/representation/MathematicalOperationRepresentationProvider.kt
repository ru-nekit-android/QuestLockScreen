package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.math.MathematicalOperation
import ru.nekit.android.questData.R.string.*

object MathematicalOperationRepresentationProvider : StringIdRepresentationProvider<MathematicalOperation>() {

    init {
        createRepresentation(MathematicalOperation.ADDITION,
                mathematical_operation_addition_title)
        createRepresentation(MathematicalOperation.DIVISION,
                mathematical_operation_division_title)
        createRepresentation(MathematicalOperation.MULTIPLICATION,
                mathematical_operation_multiplication_title)
        createRepresentation(MathematicalOperation.SUBTRACTION,
                mathematical_operation_subtraction_title)
    }

}

fun MathematicalOperation.getRepresentationAsSign() = MathematicalOperationRepresentationProvider.getRepresentation(this)
