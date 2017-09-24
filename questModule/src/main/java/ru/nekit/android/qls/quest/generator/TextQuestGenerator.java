package ru.nekit.android.qls.quest.generator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.types.TextQuest;
import ru.nekit.android.qls.utils.MathUtils;

public class TextQuestGenerator implements IQuestGenerator {

    private static final int DEFAULT_TEXT_CAMOUFLAGE_LENGTH = 4;
    private static final int DEFAULT_TEXT_CAMOUFLAGE_WORD_LENGTH = 5;
    private static final String DEFAULT_TEXT_CAMOUFLAGE_WORD = "слово";

    private QuestContext mQuestContext;
    private Type mType;
    private TextQuest mQuest;
    private int mCamouflageLength, mWordLength;

    public TextQuestGenerator(@NonNull QuestContext context, @Nullable QuestionType questionType) {
        mQuestContext = context;
        mQuest = new TextQuest(questionType);
    }

    private String makeNoisePattern(@NonNull Context context,
                                    @StringRes int supportCharsResourceIds[],
                                    int charCountBefore, int charCountAfter) {
        StringBuilder noisePattern = new StringBuilder();
        StringBuilder supportCharsBuilder = new StringBuilder();
        for (int resId : supportCharsResourceIds) {
            supportCharsBuilder.append(context.getString(resId));
        }
        String supportChars = supportCharsBuilder.toString();
        final int supportCharsLength = supportChars.length() - 1;
        int i = 0;
        char _char;
        for (; i < charCountBefore; i++) {
            _char = supportChars.charAt(MathUtils.randUnsignedInt(supportCharsLength));
            noisePattern.append(_char);
        }
        i = 0;
        noisePattern.append("%s");
        for (; i < charCountAfter; i++) {
            _char = supportChars.charAt(MathUtils.randUnsignedInt(supportCharsLength));
            noisePattern.append(_char);
        }
        return noisePattern.toString();
    }

    @Override
    public IQuest generate() {
        if (mType == Type.TEXT_CAMOUFLAGE) {
            List<String> wordList = mQuestContext.getQuestResourceLibrary().getWordList(mWordLength);
            String answer = DEFAULT_TEXT_CAMOUFLAGE_WORD;
            if (wordList.size() > 0) {
                answer = wordList.get(MathUtils.randUnsignedInt(wordList.size() - 1));
            }
            String[] questionStringArray = new String[answer.length()];
            for (int i = 0; i < answer.length(); i++) {
                char answerChar = answer.charAt(i);
                questionStringArray[i] = String.format(makeNoisePattern(mQuestContext,
                        new int[]{
                                R.string.text_quest_support_chars_ru,
                                R.string.text_quest_support_chars_number},
                        mCamouflageLength, 0), answerChar);
            }
            mQuest.questionStringArray = questionStringArray;
            mQuest.setAnswer(answer);
            return mQuest;
        }
        return null;
    }

    public void makeTextCamouflage(int wordLength, int camouflageLength) {
        mWordLength = wordLength;
        if (mWordLength == 0) {
            mWordLength = DEFAULT_TEXT_CAMOUFLAGE_WORD_LENGTH;
        }
        mCamouflageLength = camouflageLength;
        if (mCamouflageLength == 0) {
            mCamouflageLength = DEFAULT_TEXT_CAMOUFLAGE_LENGTH;
        }
        mType = Type.TEXT_CAMOUFLAGE;
    }

    private enum Type {
        TEXT_CAMOUFLAGE
    }
}
