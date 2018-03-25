package ru.nekit.android.domain.event

data class KeyboardHideAction(val action: () -> Unit) : IEvent {

    override val eventName: String = javaClass.name

}


