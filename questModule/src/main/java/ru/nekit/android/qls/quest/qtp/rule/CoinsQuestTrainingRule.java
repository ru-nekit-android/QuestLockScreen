package ru.nekit.android.qls.quest.qtp.rule;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.IQuestGenerator;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;
import ru.nekit.android.qls.quest.types.model.CoinModel;
import ru.nekit.android.qls.utils.Callable;

public class CoinsQuestTrainingRule extends SimpleMemberQuestTrainingProgramRule {

    @Override
    public IQuestGenerator makeQuestGenerator(@NonNull QuestContext questContext,
                                              @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(memberCount, 0);
        generator.setAvailableMemberValues(CoinModel.values(), new Callable<CoinModel, Integer>() {
            @Override
            public Integer call(CoinModel value) {
                return value.nomination;
            }
        });
        generator.setFlags(NumberSummandQuestGenerator.Flag.ONLY_POSITIVE_SUMMANDS);
        return generator;
    }

}
