package ru.nekit.android.qls.quest.qtp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;

import static ru.nekit.android.qls.quest.qtp.AppropriateQuestTrainingProgramRuleWrapper.BASE_CHANCE;

/**
 * Created by nekit on 20.02.17.
 */

class QuestRulePriority implements Parcelable {

    public static final Creator<QuestRulePriority> CREATOR = new Creator<QuestRulePriority>() {
        @Override
        public QuestRulePriority createFromParcel(Parcel in) {
            return new QuestRulePriority(in);
        }

        @Override
        public QuestRulePriority[] newArray(int size) {
            return new QuestRulePriority[size];
        }
    };

    QuestType questType;
    List<QuestionType> questionTypes;
    float startPriority, wrongAnswerPriority;

    QuestRulePriority() {
    }

    QuestRulePriority(QuestType questType, QuestionType questionType) {
        this(questType, questionType, 1, 1);
    }

    QuestRulePriority(QuestType questType, QuestionType questionType,
                      float startPriority,
                      float wrongAnswerPriority) {
        this.questType = questType;
        this.questionTypes = new ArrayList<>();
        questionTypes.add(questionType);
        this.startPriority = startPriority;
        this.wrongAnswerPriority = wrongAnswerPriority;
    }

    private QuestRulePriority(Parcel in) {
        startPriority = in.readFloat();
        wrongAnswerPriority = in.readFloat();
        questType = QuestType.valueOf(in.readString());
        int size = in.readInt();
        questionTypes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            questionTypes.add(QuestionType.valueOf(in.readString()));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(startPriority);
        dest.writeFloat(wrongAnswerPriority);
        dest.writeString(questType.name());
        if (questionTypes != null) {
            dest.writeInt(questionTypes.size());
            for (QuestionType questionType : questionTypes) {
                dest.writeString(questionType.name());
            }
        }
    }

    float computePriority(int rightAnswerCount, int wrongAnswerCount) {
        float chanceValue;
        if (wrongAnswerCount == 0) {
            chanceValue = startPriority * BASE_CHANCE;
        } else {
            chanceValue = BASE_CHANCE * startPriority +
                    BASE_CHANCE * wrongAnswerPriority * (float) wrongAnswerCount /
                            (Math.max(1, rightAnswerCount));
        }
        return chanceValue;
    }
}
