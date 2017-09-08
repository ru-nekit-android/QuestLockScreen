package ru.nekit.android.qls.quest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import ru.nekit.android.shared.R;

import static ru.nekit.android.qls.quest.QuestionType.COMPARISON;
import static ru.nekit.android.qls.quest.QuestionType.SOLUTION;
import static ru.nekit.android.qls.quest.QuestionType.UNKNOWN_MEMBER;

public enum QuestType implements ITitleable {

    //16 variants
    SIMPLE_EXAMPLE(QuestionType.values(),
            R.string.quest_simple_example_title),

    TRAFFIC_LIGHT(R.string.quest_traffic_light_synonym,
            new QuestionType[]{SOLUTION},
            R.string.quest_traffic_light_title),

    COINS(R.string.quest_coins_synonym,
            new QuestionType[]{SOLUTION, UNKNOWN_MEMBER},
            R.string.quest_coins_title,
            SOLUTION),

    METRICS(new QuestionType[]{SOLUTION, COMPARISON},
            R.string.quest_metrics_title),

    PERIMETER(new QuestionType[]{SOLUTION, UNKNOWN_MEMBER},
            R.string.quest_perimeter_title),

    FRUIT_ARITHMETIC(R.string.quest_fruit_arithmetic_synonym,
            new QuestionType[]{SOLUTION, QuestionType.COMPARISON},
            R.string.quest_fruit_arithmetic_title,
            SOLUTION),

    TEXT_CAMOUFLAGE(R.string.quest_text_camouflage_title),

    TIME(R.string.quest_time_synonym,
            new QuestionType[]{UNKNOWN_MEMBER, COMPARISON},
            R.string.quest_time_title,
            UNKNOWN_MEMBER),

    CURRENT_TIME(R.string.quest_current_time_synonym,
            new QuestionType[]{UNKNOWN_MEMBER},
            R.string.quest_current_time_title,
            UNKNOWN_MEMBER),

    CHOICE(R.string.quest_choice_synonym,
            new QuestionType[]{UNKNOWN_MEMBER},
            R.string.quest_choice_title,
            UNKNOWN_MEMBER),

    MISMATCH(R.string.quest_mismatch_synonym,
            new QuestionType[]{UNKNOWN_MEMBER},
            R.string.quest_mismatch_title,
            UNKNOWN_MEMBER);

    private QuestionType[] mSupportQuestionTypes;
    private QuestionType mDefaultQuestionType;
    @StringRes
    private int mTitleResourceId;
    @StringRes

    private int mSynonymResourceId;

    QuestType(@StringRes int synonymResourceId, QuestionType[] supportQuestionTypes,
              @StringRes int titleResourceId, @Nullable QuestionType defaultQuestionType) {
        mSynonymResourceId = synonymResourceId;
        mSupportQuestionTypes = supportQuestionTypes;
        mTitleResourceId = titleResourceId;
        mDefaultQuestionType = defaultQuestionType;
    }

    QuestType(@StringRes int synonymResourceId, QuestionType[] supportQuestionTypes,
              @StringRes int titleResourceId) {
        this(synonymResourceId, supportQuestionTypes, titleResourceId, null);
    }

    QuestType(QuestionType[] supportQuestionTypes,
              @StringRes int titleResourceId, @Nullable QuestionType defaultQuestionType) {
        this(0, supportQuestionTypes, titleResourceId, defaultQuestionType);
    }

    QuestType(QuestionType[] supportQuestionTypes, @StringRes int titleResourceId) {
        this(0, supportQuestionTypes, titleResourceId);
    }

    QuestType(@StringRes int titleResourceId) {
        this(0, new QuestionType[]{QuestionType.QUESTION_TYPE_BY_DEFAULT},
                titleResourceId);
    }

    @Nullable
    public static QuestType getByNameOrSynonym(@NonNull Context context, @NonNull String value) {
        value = value.toLowerCase();
        for (QuestType questType : values()) {
            String synonym = null;
            if (questType.mSynonymResourceId != 0) {
                synonym = context.getString(questType.mSynonymResourceId);
            }
            if (questType.name().toLowerCase().equals(value) ||
                    (synonym != null && synonym.equals(value))) {
                return questType;
            }
        }
        return null;
    }

    public QuestionType getDefaultQuestionType() {
        return mDefaultQuestionType;
    }

    public QuestionType[] getSupportQuestionTypes() {
        return mSupportQuestionTypes;
    }

    @Nullable
    public String getTitle(@NonNull Context context) {
        return context.getString(mTitleResourceId);
    }
}