package ru.nekit.android.qls.data.representation

import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison
import ru.nekit.android.questData.R.string.*

object MathematicalOperationSignComparisonRepresentationProviderAsSign : StringRepresentationProvider<MathematicalSignComparison>() {

    init {
        createRepresentation(MathematicalSignComparison.EQUAL, "=")
        createRepresentation(MathematicalSignComparison.GREATER, ">")
        createRepresentation(MathematicalSignComparison.LESS, "<")
    }

}

object MathematicalOperationSignComparisonRepresentationProviderAsWord : StringIdRepresentationProvider<MathematicalSignComparison>() {

    init {
        createRepresentation(MathematicalSignComparison.EQUAL, equal)
        createRepresentation(MathematicalSignComparison.GREATER, greater)
        createRepresentation(MathematicalSignComparison.LESS, less)
    }

}

fun MathematicalSignComparison.getRepresentationAsSign() = MathematicalOperationSignComparisonRepresentationProviderAsSign.getRepresentation(this)
fun MathematicalSignComparison.getRepresentationAsWord() = MathematicalOperationSignComparisonRepresentationProviderAsWord.getRepresentation(this)
fun MathematicalSignComparison.getRepresentation() = this.getRepresentationAsWord()