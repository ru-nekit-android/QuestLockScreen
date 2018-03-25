package ru.nekit.android.qls.quest.resources.representation

import android.support.annotation.StringRes
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.math.MathematicalSignComparison
import ru.nekit.android.qls.quest.resources.representation.common.StringListIdRepresentation
import ru.nekit.android.qls.quest.resources.representation.common.StringListIdRepresentationProvider
import ru.nekit.android.qls.quest.resources.representation.common.StringRepresentation
import ru.nekit.android.qls.quest.resources.representation.common.StringRepresentationProvider

object MathematicalOperationSignComparisonRepresentationProviderAsSign : StringRepresentationProvider<MathematicalSignComparison>() {

    init {
        createRepresentation(MathematicalSignComparison.EQUAL, "=")
        createRepresentation(MathematicalSignComparison.GREATER, ">")
        createRepresentation(MathematicalSignComparison.LESS, "<")
    }

    private fun createRepresentation(key: MathematicalSignComparison, string: String) {
        createRepresentation(key, StringRepresentation(string))
    }

}

object MathematicalOperationSignComparisonRepresentationProviderAsWord : StringListIdRepresentationProvider<MathematicalSignComparison>() {

    init {
        createRepresentation(MathematicalSignComparison.EQUAL, R.string.equal)
        createRepresentation(MathematicalSignComparison.GREATER, R.string.greater)
        createRepresentation(MathematicalSignComparison.LESS, R.string.less)
    }

    private fun createRepresentation(key: MathematicalSignComparison, @StringRes stringIds: List<Int>) {
        createRepresentation(key, StringListIdRepresentation(stringIds))
    }

    private fun createRepresentation(key: MathematicalSignComparison, @StringRes stringId: Int) {
        createRepresentation(key, StringListIdRepresentation(listOf(stringId)))
    }

}

fun MathematicalSignComparison.getRepresentationAsSign() = MathematicalOperationSignComparisonRepresentationProviderAsSign.getRepresentation(this)
fun MathematicalSignComparison.getRepresentationAsWord() = MathematicalOperationSignComparisonRepresentationProviderAsWord.getRepresentation(this)
fun MathematicalSignComparison.getRepresentation() = this.getRepresentationAsWord()