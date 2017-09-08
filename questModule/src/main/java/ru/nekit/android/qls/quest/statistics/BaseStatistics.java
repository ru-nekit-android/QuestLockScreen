package ru.nekit.android.qls.quest.statistics;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseStatistics implements Parcelable {

    public static final Creator<BaseStatistics> CREATOR = new Creator<BaseStatistics>() {
        @Override
        public BaseStatistics createFromParcel(Parcel in) {
            return new BaseStatistics(in);
        }

        @Override
        public BaseStatistics[] newArray(int size) {
            return new BaseStatistics[size];
        }
    };
    public int rightAnswerCount;
    public int rightAnswerSeries;
    public int rightAnswerSeriesCounter;
    public int wrongAnswerCount;
    public long bestAnswerTime;
    public long worseAnswerTime;
    public long rightAnswerSummandTime;

    public BaseStatistics() {
        bestAnswerTime = Long.MAX_VALUE;
        worseAnswerTime = 0;
        rightAnswerSummandTime = 0;
    }

    protected BaseStatistics(Parcel in) {
        rightAnswerCount = in.readInt();
        rightAnswerSeries = in.readInt();
        rightAnswerSeriesCounter = in.readInt();
        wrongAnswerCount = in.readInt();
        bestAnswerTime = in.readLong();
        worseAnswerTime = in.readLong();
        worseAnswerTime = in.readLong();
        rightAnswerSummandTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rightAnswerCount);
        dest.writeInt(rightAnswerSeries);
        dest.writeInt(rightAnswerSeriesCounter);
        dest.writeInt(wrongAnswerCount);
        dest.writeLong(bestAnswerTime);
        dest.writeLong(worseAnswerTime);
        dest.writeLong(rightAnswerSummandTime);
    }

    public float getAverageRightAnswerTime() {
        return (float) rightAnswerSummandTime / rightAnswerCount;
    }
}
