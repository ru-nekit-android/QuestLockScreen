package ru.nekit.android.qls.quest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import ru.nekit.android.shared.R;

public enum QuestionType implements INameHolder {

    SOLUTION(R.string.question_solution_title),
    UNKNOWN_MEMBER(R.string.question_unknown_member_title),
    UNKNOWN_OPERATION(R.string.question_unknown_operator_title),
    COMPARISON(R.string.question_comparison_title);

    public static final QuestionType QUESTION_TYPE_BY_DEFAULT = SOLUTION;
    @StringRes
    private int mTitleResourceId;

    QuestionType(@StringRes int titleResourceId) {
        mTitleResourceId = titleResourceId;
    }

    @NonNull
    public String getName(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }

}