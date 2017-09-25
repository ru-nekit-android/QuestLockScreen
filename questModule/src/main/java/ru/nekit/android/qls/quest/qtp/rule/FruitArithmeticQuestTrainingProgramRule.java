package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator;
import ru.nekit.android.qls.quest.generator.NumberSummandQuestGenerator.Flag;
import ru.nekit.android.qls.quest.resourceLibrary.IVisualResourceItem;
import ru.nekit.android.qls.utils.Callable;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.ANSWER_VARIANTS;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.MEMBER_COUNT;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.MEMBER_MIN_AND_MAX_VALUES;
import static ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup.FRUIT;

public class FruitArithmeticQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<FruitArithmeticQuestTrainingProgramRule> CREATOR =
            new Creator<FruitArithmeticQuestTrainingProgramRule>() {
                @Override
                public FruitArithmeticQuestTrainingProgramRule createFromParcel(Parcel in) {
                    return new FruitArithmeticQuestTrainingProgramRule(in);
                }

                @Override
                public FruitArithmeticQuestTrainingProgramRule[] newArray(int size) {
                    return new FruitArithmeticQuestTrainingProgramRule[size];
                }
            };

    public static final int VALUE_DEFAULT_ANSWER_VARIANTS = 4;
    public static final int VALUE_DEFAULT_MEMBER_COUNT = 2;

    public static final int VALUE_DEFAULT_ANSWER_VARIANTS_FOR_COMPARISON = 2;
    public static final int VALUE_DEFAULT_MEMBER_COUNT_FOR_COMPARISON = 4;

    private int memberCount;
    private int answerVariants;
    private int[][] memberMinAndMaxValues;

    public FruitArithmeticQuestTrainingProgramRule() {

    }

    private FruitArithmeticQuestTrainingProgramRule(Parcel in) {
        super(in);
        memberCount = in.readInt();
        answerVariants = in.readInt();
        final int size = in.readInt();
        memberMinAndMaxValues = new int[size][];
        for (int i = 0; i < size; i++) {
            memberMinAndMaxValues[i] = in.createIntArray();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeInt(memberCount);
        dest.writeInt(answerVariants);
        dest.writeInt(memberMinAndMaxValues.length);
        for (int[] memberMinAndMaxValue : memberMinAndMaxValues) {
            dest.writeIntArray(memberMinAndMaxValue);
        }
    }

    @Override
    public void parse(Gson gson, JsonObject object) {
        super.parse(gson, object);
        if (object.has(MEMBER_COUNT)) {
            memberCount = gson.fromJson(object.get(MEMBER_COUNT), int.class);
        }
        if (object.has(ANSWER_VARIANTS)) {
            answerVariants = gson.fromJson(object.get(ANSWER_VARIANTS), int.class);
        }
        if (object.has(MEMBER_MIN_AND_MAX_VALUES)) {
            memberMinAndMaxValues = gson.fromJson(object.get(MEMBER_MIN_AND_MAX_VALUES), int[][].class);
        } else {
            memberMinAndMaxValues = new int[][]{
                    new int[]{
                            1,
                            1
                    },
                    new int[]{
                            5,
                            5
                    }
            };
        }
    }

    @Override
    public IQuest makeQuest(@NonNull final QuestContext questContext,
                            @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        if (questionType == QuestionType.SOLUTION) {
            memberCount = Math.max(VALUE_DEFAULT_MEMBER_COUNT, memberCount);
            answerVariants = Math.max(VALUE_DEFAULT_ANSWER_VARIANTS, answerVariants);
            generator.setMembersMinAndMaxValues(memberMinAndMaxValues);
            generator.setFlags(
                    Flag.AVOID_NEGATIVE_ANSWER,
                    Flag.AVOID_NEGATIVE_ANSWER_WHILE_CALCULATION,
                    Flag.AVOID_ZERO_ANSWER,
                    Flag.AVOID_ZERO_ANSWER_WHILE_CALCULATION
            );
        } else if (questionType == QuestionType.COMPARISON) {
            memberCount = Math.max(VALUE_DEFAULT_MEMBER_COUNT_FOR_COMPARISON, memberCount);
            List<IVisualResourceItem> visualResourceItemList =
                    questContext.getQuestResourceLibrary().getVisualResourceItemsByGroup(FRUIT);
            answerVariants = Math.min(Math.max(VALUE_DEFAULT_ANSWER_VARIANTS_FOR_COMPARISON,
                    answerVariants),
                    visualResourceItemList.size());
            Collections.shuffle(visualResourceItemList);
            generator.setAvailableMemberValues(visualResourceItemList.subList(0, answerVariants),
                    new Callable<IVisualResourceItem, Integer>() {
                        @Override
                        public Integer call(IVisualResourceItem value) {
                            return questContext.getQuestResourceLibrary().getQuestVisualResourceItemId(value);
                        }
                    });
        }
        generator.setMemberCounts(memberCount, 0);
        return generator.generate();
    }

    public int getAnswerVariants() {
        return answerVariants;
    }
}