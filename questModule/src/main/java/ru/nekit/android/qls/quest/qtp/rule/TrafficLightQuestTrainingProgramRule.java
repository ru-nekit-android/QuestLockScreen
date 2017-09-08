package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.IQuestGenerator;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;
import ru.nekit.android.qls.quest.types.TrafficLightType;

public class TrafficLightQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<TrafficLightQuestTrainingProgramRule> CREATOR
            = new Creator<TrafficLightQuestTrainingProgramRule>() {
        @Override
        public TrafficLightQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new TrafficLightQuestTrainingProgramRule(in);
        }

        @Override
        public TrafficLightQuestTrainingProgramRule[] newArray(int size) {
            return new TrafficLightQuestTrainingProgramRule[size];
        }
    };

    public TrafficLightQuestTrainingProgramRule() {
    }

    private TrafficLightQuestTrainingProgramRule(Parcel in) {
        super(in);
    }

    @Override
    public IQuestGenerator makeQuestGenerator(@NonNull QuestContext questContext,
                                              @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(1, 0);
        generator.setAvailableMemberValues(TrafficLightType.values());
        return generator;
    }
}
