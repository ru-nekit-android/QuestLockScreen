package ru.nekit.android.qls.quest.statistics;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class PupilStatistics extends BaseStatistics implements Parcelable {

    public int score;
    public List<QuestStatistics> questStatistics;

    public PupilStatistics() {
        super();
        questStatistics = new ArrayList<>();
    }

    protected PupilStatistics(Parcel in) {
        super(in);
        score = in.readInt();
        questStatistics = new ArrayList<>();
        in.readTypedList(questStatistics, QuestStatistics.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(score);
        dest.writeTypedList(questStatistics);
    }
}

