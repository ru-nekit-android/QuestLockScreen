package ru.nekit.android.qls.quest.generator

import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.shared.model.QuestionType

class TextQuestGenerator(private val mQuestContext: QuestContext, questionType: QuestionType?) /*: IQuestGenerator<TextQuest> */ {

    /*private var mType: Type? = null
    private val mQuest: TextQuest
    private var mCamouflageLength: Int = 0
    private var mWordLength: Int = 0

    init {
        mQuest = TextQuest()
        mQuest.questionType = questionType
    }

    private fun makeNoisePattern(context: android.content.Context,
                                 @StringRes supportCharsResourceIds: IntArray,
                                 charCountBefore: Int, charCountAfter: Int): String {
        val noisePattern = StringBuilder()
        val supportCharsBuilder = StringBuilder()
        for (resId in supportCharsResourceIds) {
            supportCharsBuilder.append(context.getString(resId))
        }
        val supportChars = supportCharsBuilder.to()
        val supportCharsLength = supportChars.length - 1
        var i = 0
        var _char: Char
        while (i < charCountBefore) {
            _char = supportChars[MathUtils.randUnsignedInt(supportCharsLength)]
            noisePattern.append(_char)
            i++
        }
        i = 0
        noisePattern.append("%s")
        while (i < charCountAfter) {
            _char = supportChars[MathUtils.randUnsignedInt(supportCharsLength)]
            noisePattern.append(_char)
            i++
        }
        return noisePattern.to()
    }

    override fun generate(): TextQuest {
        if (mType == Type.TEXT_CAMOUFLAGE) {
            val wordList = mQuestContext.questResourceRepository.getWordList(mWordLength)
            var answer = DEFAULT_TEXT_CAMOUFLAGE_WORD
            if (wordList.isNotEmpty()) {
                answer = wordList[MathUtils.randUnsignedInt(wordList.size - 1)]
            }
            val questionStringArray = arrayOfNulls<String>(answer.length)
            for (i in 0 until answer.length) {
                val answerChar = answer[i]
                questionStringArray[i] = String.format(makeNoisePattern(mQuestContext,
                        intArrayOf(R.string.text_quest_support_chars_ru, R.string.text_quest_support_chars_number),
                        mCamouflageLength, 0), answerChar)
            }
            mQuest.questionStringArray = questionStringArray
            mQuest.setAnswer(answer)
            return mQuest
        }
        return null
    }

    fun makeTextCamouflage(wordLength: Int, camouflageLength: Int) {
        mWordLength = wordLength
        if (mWordLength == 0) {
            mWordLength = DEFAULT_TEXT_CAMOUFLAGE_WORD_LENGTH
        }
        mCamouflageLength = camouflageLength
        if (mCamouflageLength == 0) {
            mCamouflageLength = DEFAULT_TEXT_CAMOUFLAGE_LENGTH
        }
        mType = Type.TEXT_CAMOUFLAGE
    }

    private enum class Type {
        TEXT_CAMOUFLAGE
    }

    companion object {

        private val DEFAULT_TEXT_CAMOUFLAGE_LENGTH = 4
        private val DEFAULT_TEXT_CAMOUFLAGE_WORD_LENGTH = 5
        private val DEFAULT_TEXT_CAMOUFLAGE_WORD = "слово"
    }*/
}
