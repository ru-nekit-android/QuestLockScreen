package ru.nekit.android.qls.quest.statistics;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;

public class QuestStatistics extends BaseStatistics implements Parcelable {

    public static final Creator<QuestStatistics> CREATOR =
            new Creator<QuestStatistics>() {
                @Override
                public QuestStatistics createFromParcel(Parcel in) {
                    return new QuestStatistics(in);
                }

                @Override
                public QuestStatistics[] newArray(int size) {
                    return new QuestStatistics[size];
                }
            };

    @NonNull
    public final QuestType questType;
    @NonNull
    public final QuestionType questionType;

    public QuestStatistics(@NonNull QuestType questType,
                           @NonNull QuestionType questionType) {
        super();
        this.questType = questType;
        this.questionType = questionType;
    }

    protected QuestStatistics(Parcel in) {
        super(in);
        questType = QuestType.valueOf(in.readString());
        questionType = QuestionType.valueOf(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(questType.name());
        dest.writeString(questionType.name());
    }
}