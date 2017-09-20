package ru.nekit.android.qls.quest.qtp.rule;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.IQuestGenerator;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;
import ru.nekit.android.qls.quest.types.model.ColorModel;
import ru.nekit.android.qls.utils.MathUtils;

public class ColorsQuestTrainingRule extends SimpleMemberQuestTrainingProgramRule {

    private static final int VALUE_DEFAULT_MEMBER_COUNT = 2;

    @Override
    public IQuestGenerator makeQuestGenerator(@NonNull QuestContext questContext,
                                              @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(memberCount, 0);
        generator.setPresetValues(Arrays.copyOf(MathUtils.shuffleArray(ColorModel.values()),
                memberCount));
        return generator;
    }

    @Override
    public void parse(Gson gson, JsonObject object) {
        super.parse(gson, object);
        memberCount = Math.max(memberCount, VALUE_DEFAULT_MEMBER_COUNT);
    }
}
