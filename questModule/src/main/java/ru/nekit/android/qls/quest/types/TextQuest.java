package ru.nekit.android.qls.quest.types;

import android.text.InputType;

import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.base.AbstractQuest;

public class TextQuest extends AbstractQuest {

    public String[] questionStringArray;
    private String[] answerStringArray;

    public TextQuest(QuestionType questionType) {
        setQuestionType(questionType);
    }

    @Override
    public Object getAnswer() {
        return getAnswerString();
    }

    public void setAnswer(String answer) {
        answerStringArray = new String[]{answer};
    }

    public String getQuestionString() {
        StringBuilder questionString = new StringBuilder();
        int answerItemPosition = 0;
        for (String questionItem : questionStringArray) {
            if ("".equals(questionItem)) {
                questionString.append(answerStringArray[answerItemPosition]);
                answerItemPosition++;
            } else {
                questionString.append(questionItem);
            }
        }
        return questionString.toString();
    }

    private String getAnswerString() {
        StringBuilder answerString = new StringBuilder();
        for (String answerItem : answerStringArray) {
            answerString.append(answerItem);
        }
        return answerString.toString();
    }

    @Override
    public Class getAnswerClass() {
        return String.class;
    }

    @Override
    public int getAnswerInputType() {
        return InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
    }
}
