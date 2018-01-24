package ru.nekit.android.qls.quest.qtp.rule;

import android.support.annotation.NonNull;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;
import ru.nekit.android.qls.quest.resources.collections.CoinVisualResourceCollection;
import ru.nekit.android.qls.utils.Callable;

public class CoinsQuestTrainingRule extends HasMemberQuestTrainingProgramRule {

    @Override
    public Quest makeQuest(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(memberCount, 0);
        generator.setAvailableMemberValues(CoinVisualResourceCollection.values(), new Callable<CoinVisualResourceCollection, Integer>() {
            @Override
            public Integer call(CoinVisualResourceCollection value) {
                return value.getId();
            }
        });
        generator.setFlags(NumberSummandQuestGenerator.Flag.ONLY_POSITIVE_SUMMANDS);
        return generator.generate();
    }

}