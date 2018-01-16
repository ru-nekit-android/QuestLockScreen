package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.common.Quest;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;
import ru.nekit.android.qls.quest.model.DirectionModel;

public class DirectionQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<DirectionQuestTrainingProgramRule> CREATOR
            = new Creator<DirectionQuestTrainingProgramRule>() {
        @Override
        public DirectionQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new DirectionQuestTrainingProgramRule(in);
        }

        @Override
        public DirectionQuestTrainingProgramRule[] newArray(int size) {
            return new DirectionQuestTrainingProgramRule[size];
        }
    };

    public DirectionQuestTrainingProgramRule() {
    }

    private DirectionQuestTrainingProgramRule(Parcel in) {
        super(in);
    }

    @Override
    public Quest makeQuest(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(1, 0);
        generator.setAvailableMemberValues(DirectionModel.values());
        return generator.generate();
    }
}
