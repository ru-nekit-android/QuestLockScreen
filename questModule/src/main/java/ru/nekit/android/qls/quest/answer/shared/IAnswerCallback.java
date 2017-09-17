package ru.nekit.android.qls.quest.answer.shared;

public interface IAnswerCallback {

    void rightAnswer();

    void wrongAnswer();

    void emptyAnswer();

    void wrongStringInputFormat();

}
