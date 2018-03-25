package ru.nekit.android.qls.domain.model.resources

enum class LocalizedNounStringResourceCollection {

    CAR,
    BOOTS,
    DOLL_SKIRT,
    DOLL_BLOUSE,
    WINTER,
    FALL,
    SPRING,
    SUMMER,
    ORANGE,
    STRAWBERRY,
    APPLE,
    PEAR,
    CHERRY,
    RASPBERRY,
    PINEAPPLE,
    BLACKBERRY;

    val id: Int
        get() = ordinal

    companion object {

        fun getById(id: Int): LocalizedNounStringResourceCollection {
            return values()[id]
        }
    }

}