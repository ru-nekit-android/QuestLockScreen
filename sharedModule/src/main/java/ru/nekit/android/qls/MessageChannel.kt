package ru.nekit.android.qls


import ru.nekit.android.qls.shared.model.Pupil

class MessageChannel(val name: String) {

    constructor(pupil: Pupil) : this(pupil.uuid)

}