package ru.nekit.android.domain.event

data class KeyboardAction(val action: Action, val actionBody: () -> Unit) : IEvent {

    override val eventName: String = javaClass.name

    enum class Action {
        SHOW,
        HIDE;
    }

}


