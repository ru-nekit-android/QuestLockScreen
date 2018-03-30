package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison
import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison.*
import ru.nekit.android.questData.R.string.*

object MathematicalOperationSignComparisonRepresentationProviderAsSign : StringIdRepresentationProvider<MathematicalSignComparison>() {

    init {
        createRepresentation(EQUAL, mathematical_operation_sign_comparison_equal_title)
        createRepresentation(GREATER, mathematical_operation_sign_comparison_greater_title)
        createRepresentation(LESS, mathematical_operation_sign_comparison_less_title)
    }

}

object MathematicalOperationSignComparisonRepresentationProviderAsWord : StringIdRepresentationProvider<MathematicalSignComparison>() {

    init {
        createRepresentation(EQUAL, equal)
        createRepresentation(GREATER, greater)
        createRepresentation(LESS, less)
    }

}

fun MathematicalSignComparison.getRepresentationAsSign() = MathematicalOperationSignComparisonRepresentationProviderAsSign.getRepresentation(this)
fun MathematicalSignComparison.getRepresentationAsWord() = MathematicalOperationSignComparisonRepresentationProviderAsWord.getRepresentation(this)
fun MathematicalSignComparison.getRepresentation() = this.getRepresentationAsWord()