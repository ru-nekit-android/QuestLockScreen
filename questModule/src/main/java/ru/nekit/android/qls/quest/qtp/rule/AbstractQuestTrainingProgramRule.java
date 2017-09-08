package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.IQuestGenerator;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.utils.MathUtils;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.DELAYED_START;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.ENABLED;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.REWARD;

public abstract class AbstractQuestTrainingProgramRule implements Parcelable {

    private QuestType mQuestType;
    private int mReward;
    private List<QuestionType> mQuestionTypes;
    private int mDelayedStart;
    private boolean mEnabled;

    AbstractQuestTrainingProgramRule() {
    }

    protected AbstractQuestTrainingProgramRule(Parcel in) {
        mReward = in.readInt();
        mQuestType = QuestType.valueOf(in.readString());
        int size = in.readInt();
        mQuestionTypes = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            mQuestionTypes.add(QuestionType.valueOf(in.readString()));
        }
        mDelayedStart = in.readInt();
        mEnabled = in.readInt() == 1;
    }

    public QuestType getQuestType() {
        return mQuestType;
    }

    public void setQuestType(QuestType questType) {
        mQuestType = questType;
    }

    public int getReward() {
        return mReward;
    }

    public List<QuestionType> getQuestionTypes() {
        return mQuestionTypes;
    }

    public int getDelayedStart() {
        return mDelayedStart;
    }

    public boolean getEnabled() {
        return mEnabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        dest.writeInt(mReward);
        dest.writeString(mQuestType.name());
        dest.writeInt(mQuestionTypes.size());
        for (QuestionType questionType : mQuestionTypes) {
            dest.writeString(questionType.name());
        }
        dest.writeInt(mDelayedStart);
        dest.writeInt(mEnabled ? 1 : 0);
    }

    public void parse(Gson gson, JsonObject object) {
        QuestionType[] localQuestionTypes;
        if (object.has(QuestTrainingProgram.Dictionary.QUESTION_TYPES)) {
            JsonArray questionTypesJArray = object.get(QuestTrainingProgram.Dictionary.QUESTION_TYPES).getAsJsonArray();
            if (questionTypesJArray.size() == 1 && QuestTrainingProgram.Dictionary.ALL.toLowerCase().equals(
                    questionTypesJArray.get(0).getAsString().toLowerCase())) {
                localQuestionTypes = mQuestType.getSupportQuestionTypes();
            } else {
                String[] stringQuestionTypes = gson.fromJson(object.get(QuestTrainingProgram.Dictionary.QUESTION_TYPES),
                        String[].class);
                localQuestionTypes = new QuestionType[stringQuestionTypes.length];
                int index = 0;
                for (String stringQuestionType : stringQuestionTypes) {
                    stringQuestionType = stringQuestionType.toUpperCase();
                    QuestionType questionType = QuestionType.valueOf(stringQuestionType);
                    localQuestionTypes[index++] = questionType;
                }
            }
        } else {
            localQuestionTypes = new QuestionType[]{mQuestType.getDefaultQuestionType() == null ?
                    QuestionType.QUESTION_TYPE_BY_DEFAULT : mQuestType.getDefaultQuestionType()};
        }
        if (object.has(DELAYED_START)) {
            mDelayedStart = object.get(DELAYED_START).getAsBoolean() ? 1 : 0;
        } else {
            mDelayedStart = -1;
        }
        mQuestionTypes = Arrays.asList(localQuestionTypes);
        if (object.has(REWARD)) {
            mReward = object.get(REWARD).getAsInt();
        } else {
            mReward = QuestTrainingProgram.REWARD_BY_DEFAULT;
        }
        mEnabled = !object.has(ENABLED) || object.get(ENABLED).getAsBoolean();
    }

    public abstract IQuestGenerator makeQuestGenerator(@NonNull QuestContext context,
                                                       @NonNull QuestionType questionType);

    QuestionType getRandomQuestionType() {
        return mQuestionTypes.get(MathUtils.randUnsignedInt(mQuestionTypes.size() - 1));
    }
}