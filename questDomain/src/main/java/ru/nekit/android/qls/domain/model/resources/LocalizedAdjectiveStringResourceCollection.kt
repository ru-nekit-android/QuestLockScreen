package ru.nekit.android.qls.domain.model.resources

enum class LocalizedAdjectiveStringResourceCollection {

    WHITE,
    BLACK,
    RED,
    GREEN;

    val id: Int
        get() = ordinal

    companion object {

        fun getById(id: Int): LocalizedAdjectiveStringResourceCollection {
            return values()[id]
        }
    }
}