package ru.nekit.android.qls.domain.model

data class PhoneContact(val contactId: Long, val name: String, val phoneNumber: String) {

    companion object {
        const val ID = "phoneContactId"

        val EMERGENCY_PHONE_NUMBER: PhoneContact = PhoneContact(-1, "1", "2")
    }

}