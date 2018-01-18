package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.FLAGS;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.MEMBER_COUNTS;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.MEMBER_MIN_AND_MAX_VALUES;

public class SimpleExampleQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<SimpleExampleQuestTrainingProgramRule> CREATOR
            = new Creator<SimpleExampleQuestTrainingProgramRule>() {
        @Override
        public SimpleExampleQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new SimpleExampleQuestTrainingProgramRule(in);
        }

        @Override
        public SimpleExampleQuestTrainingProgramRule[] newArray(int size) {
            return new SimpleExampleQuestTrainingProgramRule[size];
        }
    };
    private int[] memberCounts;
    private int[][] memberMinAndMaxValues;
    private int flags;

    public SimpleExampleQuestTrainingProgramRule() {

    }

    private SimpleExampleQuestTrainingProgramRule(Parcel in) {
        super(in);
        memberCounts = in.createIntArray();
        int size = in.readInt();
        memberMinAndMaxValues = new int[size][];
        for (int i = 0; i < size; i++) {
            memberMinAndMaxValues[i] = in.createIntArray();
        }
        flags = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeIntArray(memberCounts);
        dest.writeInt(memberMinAndMaxValues.length);
        for (int[] memberMinAndMaxValue : memberMinAndMaxValues) {
            dest.writeIntArray(memberMinAndMaxValue);
        }
        dest.writeInt(flags);
    }

    @Override
    public void parse(Gson gson, JsonObject object) {
        super.parse(gson, object);
        memberCounts = gson.fromJson(object.get(MEMBER_COUNTS), int[].class);
        memberMinAndMaxValues = gson.fromJson(object.get(MEMBER_MIN_AND_MAX_VALUES), int[][].class);
        NumberSummandQuestGenerator.Flag[] flags = gson.fromJson(object.get(FLAGS),
                NumberSummandQuestGenerator.Flag[].class);
        this.flags = 0;
        for (NumberSummandQuestGenerator.Flag item : flags) {
            this.flags |= item.value();
        }
    }

    @Override
    public Quest makeQuest(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(memberCounts);
        generator.setMembersMinAndMaxValues(memberMinAndMaxValues);
        generator.setFlags(flags);
        return generator.generate();
    }
}