package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.IQuestGenerator;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.quest.types.CoinModel;
import ru.nekit.android.qls.utils.Callable;

public class CoinsQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<CoinsQuestTrainingProgramRule> CREATOR =
            new Creator<CoinsQuestTrainingProgramRule>() {
                @Override
                public CoinsQuestTrainingProgramRule createFromParcel(Parcel in) {
                    return new CoinsQuestTrainingProgramRule(in);
                }

                @Override
                public CoinsQuestTrainingProgramRule[] newArray(int size) {
                    return new CoinsQuestTrainingProgramRule[size];
                }
            };
    private int memberCount;

    public CoinsQuestTrainingProgramRule() {
    }

    private CoinsQuestTrainingProgramRule(Parcel in) {
        super(in);
        memberCount = in.readInt();
    }

    @Override
    public void parse(Gson gson, JsonObject object) {
        super.parse(gson, object);
        memberCount = object.get(QuestTrainingProgram.Dictionary.MEMBER_COUNT).getAsInt();
    }

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

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeInt(memberCount);
    }
}
