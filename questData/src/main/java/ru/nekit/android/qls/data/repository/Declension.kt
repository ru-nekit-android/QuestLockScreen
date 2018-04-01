package ru.nekit.android.qls.data.repository

object Declension {

    fun declineAdjectiveByNoun(adjectiveBase: String,
                               nounBase: String,
                               format: String,
                               gender: Gender,
                               isPlural: Boolean): String {
        return String.format(format, declineAdjective(adjectiveBase, gender, isPlural),
                declineNoun(nounBase, gender, isPlural))
    }

    fun declineNoun(base: String, gender: Gender, isPlural: Boolean): String {
        return base + if (gender == Gender.NEUTER) "о" else if (isPlural) "и" else "у"
    }

    private fun declineAdjective(base: String, gender: Gender, isPlural: Boolean): String = base + if (isPlural) {
        when (gender) {
            Gender.MALE -> "ые"
            Gender.FEMALE -> "ые"
            Gender.NEUTER -> "ые"
        }
    } else {
        when (gender) {
            Gender.MALE -> "ой"
            Gender.FEMALE -> "ую"
            Gender.NEUTER -> "ое"
        }
    }

    enum class Gender {
        NEUTER,
        MALE,
        FEMALE
    }

}
