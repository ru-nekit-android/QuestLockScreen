package ru.nekit.android.qls.quest.history;

import android.os.Parcel;
import android.os.Parcelable;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;

public class QuestHistoryItem implements Parcelable {

    public static final int RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD = 1;
    public static final int RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD = 2;

    public static final String NAME = "questHistoryItem";
    public static final Creator<QuestHistoryItem> CREATOR = new Creator<QuestHistoryItem>() {
        @Override
        public QuestHistoryItem createFromParcel(Parcel in) {
            return new QuestHistoryItem(in);
        }

        @Override
        public QuestHistoryItem[] newArray(int size) {
            return new QuestHistoryItem[size];
        }
    };
    public long id;
    public String pupilUuid;
    public long time;
    public QuestType questType;
    public QuestionType questionType;
    public boolean isRightAnswer;
    public int rightAnswerSeries;
    public int recordType;
    public boolean isLevelUp;
    public long timeStamp;
    public long prevBestAnswerTime;

    public QuestHistoryItem() {

    }

    protected QuestHistoryItem(Parcel in) {
        id = in.readLong();
        pupilUuid = in.readString();
        time = in.readLong();
        questType = QuestType.valueOf(in.readString());
        questionType = QuestionType.valueOf(in.readString());
        isRightAnswer = in.readInt() != 0;
        rightAnswerSeries = in.readInt();
        recordType = in.readInt();
        isLevelUp = in.readInt() != 0;
        timeStamp = in.readLong();
        prevBestAnswerTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(pupilUuid);
        dest.writeLong(time);
        dest.writeString(questType.name());
        dest.writeString(questionType.name());
        dest.writeInt(isRightAnswer ? 1 : 0);
        dest.writeInt(rightAnswerSeries);
        dest.writeInt(recordType);
        dest.writeInt(isLevelUp ? 1 : 0);
        dest.writeLong(timeStamp);
        dest.writeLong(prevBestAnswerTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static class Pair implements Parcelable {

        public static final String NAME = "questHistoryPairItem";
        public static final Creator<Pair> CREATOR = new Creator<Pair>() {
            @Override
            public Pair createFromParcel(Parcel in) {
                return new Pair(in);
            }

            @Override
            public Pair[] newArray(int size) {
                return new Pair[size];
            }
        };
        public final QuestHistoryItem globalQuestHistory, questHistory;

        public Pair(QuestHistoryItem globalQuestHistory, QuestHistoryItem questHistory) {
            this.globalQuestHistory = globalQuestHistory;
            this.questHistory = questHistory;
        }

        protected Pair(Parcel in) {
            globalQuestHistory = in.readParcelable(QuestHistoryItem.class.getClassLoader());
            questHistory = in.readParcelable(QuestHistoryItem.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(globalQuestHistory, flags);
            dest.writeParcelable(questHistory, flags);
        }
    }
}
