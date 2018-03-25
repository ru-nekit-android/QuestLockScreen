package ru.nekit.android.qls.domain.model.resources

enum class DirectionResourceCollection {

    UP,
    RIGHT,
    DOWN,
    LEFT;

    companion object {

        fun getById(id: Int): DirectionResourceCollection {
            return values()[id]
        }

    }


}
