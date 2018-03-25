package ru.nekit.android.qls.domain.model

data class PhoneContact(val contactId: Long, val name: String, val phoneNumber: String) {

    companion object {
        val ID = "phoneContactId"
    }

}