package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.common.Quest;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.EACH_MEMBER_MIN_AND_MAX_VALUES;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.FLAGS;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.MEMBER_COUNTS;

public class MetricsQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<MetricsQuestTrainingProgramRule> CREATOR
            = new Creator<MetricsQuestTrainingProgramRule>() {
        @Override
        public MetricsQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new MetricsQuestTrainingProgramRule(in);
        }

        @Override
        public MetricsQuestTrainingProgramRule[] newArray(int size) {
            return new MetricsQuestTrainingProgramRule[size];
        }
    };
    private int[] memberCounts;
    private int[][] eachMemberMinAndMaxValues;
    private int flags;

    public MetricsQuestTrainingProgramRule() {

    }

    private MetricsQuestTrainingProgramRule(Parcel in) {
        super(in);
        memberCounts = in.createIntArray();
        int size = in.readInt();
        eachMemberMinAndMaxValues = new int[size][];
        for (int i = 0; i < size; i++) {
            eachMemberMinAndMaxValues[i] = in.createIntArray();
        }
        flags = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeIntArray(memberCounts);
        dest.writeInt(eachMemberMinAndMaxValues.length);
        for (int[] eachMemberMinAndMaxValue : eachMemberMinAndMaxValues) {
            dest.writeIntArray(eachMemberMinAndMaxValue);
        }
        dest.writeInt(flags);
    }

    @Override
    public void parse(Gson gson, JsonObject object) {
        super.parse(gson, object);
        memberCounts = gson.fromJson(object.get(MEMBER_COUNTS), int[].class);
        eachMemberMinAndMaxValues = gson.fromJson(object.get(EACH_MEMBER_MIN_AND_MAX_VALUES),
                int[][].class);
        NumberSummandQuestGenerator.Flag[] flags = gson.fromJson(object.get(FLAGS),
                NumberSummandQuestGenerator.Flag[].class);
        this.flags = 0;
        if (flags != null) {
            for (NumberSummandQuestGenerator.Flag item : flags) {
                this.flags |= item.value();
            }
        }
    }

    @Override
    public Quest makeQuest(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(memberCounts);
        generator.setEachMemberMinAndMaxValues(eachMemberMinAndMaxValues);
        generator.setFlags(flags);
        generator.setLeftNodeMembersZeroValueChance(new int[]{85, 25, 0});
        return generator.generate();
    }
}
