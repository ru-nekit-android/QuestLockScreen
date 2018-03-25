package ru.nekit.android.qls.domain.model.resources

enum class TrafficLightResourceCollection {

    GREEN,
    RED;

    companion object {

        fun getById(id: Int): TrafficLightResourceCollection {
            return values()[id]
        }

    }

}
