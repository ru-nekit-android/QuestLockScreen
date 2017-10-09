package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.nekit.android.qls.CONST;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.base.Quest;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;
import ru.nekit.android.qls.utils.Callable;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.ACCURACY;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.MEMBER_COUNT;

public class TimeQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<TimeQuestTrainingProgramRule> CREATOR
            = new Creator<TimeQuestTrainingProgramRule>() {
        @Override
        public TimeQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new TimeQuestTrainingProgramRule(in);
        }

        @Override
        public TimeQuestTrainingProgramRule[] newArray(int size) {
            return new TimeQuestTrainingProgramRule[size];
        }
    };
    private static final int VALUE_DEFAULT_ACCURACY = 5;
    private static final int VALUE_MAX_TIME = 12 * 60;
    int accuracy;
    private int memberCount;

    public TimeQuestTrainingProgramRule() {

    }

    protected TimeQuestTrainingProgramRule(Parcel in) {
        super(in);
        memberCount = in.readInt();
        accuracy = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeInt(memberCount);
        dest.writeInt(accuracy);
    }

    @Override
    public void parse(Gson gson, JsonObject object) {
        super.parse(gson, object);
        if (object.has(MEMBER_COUNT)) {
            memberCount = gson.fromJson(object.get(MEMBER_COUNT), int.class);
        } else {
            memberCount = CONST.AVAILABLE_ANSWER_VARIANT_COUNT;
        }
        if (object.has(ACCURACY)) {
            accuracy = gson.fromJson(object.get(ACCURACY), int.class);
        } else {
            accuracy = VALUE_DEFAULT_ACCURACY;
        }
    }

    @Override
    public Quest makeQuest(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(memberCount, 0);
        int[][] values = new int[2][memberCount];
        for (int i = 0; i < 2; i++) {
            values[i] = new int[memberCount];
            for (int j = 0; j < memberCount; j++) {
                values[i][j] = i * VALUE_MAX_TIME;
            }
        }
        generator.setLeftNodeEachItemTransformationFunction(new Callable<Integer, Integer>() {
            @Override
            public Integer call(Integer value) {
                return Math.max(accuracy, value - value % accuracy);
            }
        });
        generator.setMembersMinAndMaxValues(values);
        generator.setFlags(NumberSummandQuestGenerator.Flag.ONLY_POSITIVE_SUMMANDS);
        return generator.generate();
    }
}
