package ru.nekit.android.qls.quest.answer.common;

public interface IAnswerCallback {

    void rightAnswer();

    void wrongAnswer();

    void emptyAnswer();

    void wrongStringInputFormat();

}
