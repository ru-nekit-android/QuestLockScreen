package ru.nekit.android.qls.quest.types;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.qtp.rule.AbstractQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.FruitArithmeticQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.resourceLibrary.IVisualResourceItem;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resourceLibrary.SimpleQuestVisualResourceItem;
import ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup;
import ru.nekit.android.qls.quest.types.shared.IGroupWeightComparisonQuest;
import ru.nekit.android.qls.utils.MathUtils;

public class FruitArithmeticQuest extends VisualRepresentationalNumberSummandQuest
        implements IGroupWeightComparisonQuest {

    private int comparisonType;

    public FruitArithmeticQuest(@NonNull QuestContext questContext,
                                @NonNull IQuest inQuest,
                                @NonNull AbstractQuestTrainingProgramRule inRule) {
        QuestResourceLibrary questResourceLibrary = questContext.getQuestResourceLibrary();
        QuestionType questionType = inQuest.getQuestionType();
        setQuestType(QuestType.FRUIT_ARITHMETIC);
        setQuestionType(questionType);
        FruitArithmeticQuestTrainingProgramRule outRule = (FruitArithmeticQuestTrainingProgramRule) inRule;
        NumberSummandQuest outQuest = (NumberSummandQuest) inQuest;
        final int length = outQuest.leftNode.length;
        int i = 0;
        leftNode = outQuest.leftNode;
        rightNode = outQuest.rightNode;
        if (questionType == QuestionType.SOLUTION) {
            List<IVisualResourceItem> visualResourceSourceList =
                    questResourceLibrary.getVisualResourceItemsByGroup(MathUtils.randBoolean() ? VisualResourceGroup.POMUM : VisualResourceGroup.BERRY);
            IVisualResourceItem[] questVisualResourceItems = new SimpleQuestVisualResourceItem[length];
            for (; i < length; i++) {
                questVisualResourceItems[i] = visualResourceSourceList.get(
                        MathUtils.randUnsignedInt(visualResourceSourceList.size() - 1));
                visualResourceSourceList.remove(questVisualResourceItems[i]);
            }
            for (i = 0; i < length; i++) {
                final int length2 = Math.abs(leftNode[i]);
                for (int j = 0; j < length2; j++) {
                    mVisualRepresentationList.add(questResourceLibrary.getQuestVisualResourceItemId(questVisualResourceItems[i]));
                }
                if (i < length - 1) {
                    mVisualRepresentationList.add(questResourceLibrary.getQuestVisualResourceItemId(
                            leftNode[i + 1] > 0 ? SimpleQuestVisualResourceItem.PLUS : SimpleQuestVisualResourceItem.MINUS));
                }
            }
            mVisualRepresentationList.add(questResourceLibrary.getQuestVisualResourceItemId(SimpleQuestVisualResourceItem.EQUAL));
            int answer = outQuest.getTypedAnswer();
            List<Integer> availableVariantList = new ArrayList<>();
            int leftShift = MathUtils.randUnsignedInt(Math.min(
                    outRule.getAnswerVariants() - 1, answer));
            if (answer - leftShift <= 0) {
                leftShift = 0;
            }
            int rightShift = outRule.getAnswerVariants() - leftShift - 1;
            for (i = 0; i < leftShift; i++) {
                availableVariantList.add(answer - leftShift + i);
            }
            availableVariantList.add(answer);
            for (i = 1; i <= rightShift; i++) {
                availableVariantList.add(answer + i);
            }
            Collections.shuffle(availableVariantList);
            mAvailableAnswerVariants = availableVariantList.toArray();
        } else if (questionType == QuestionType.COMPARISON) {
            comparisonType = MathUtils.randInt(MIN_GROUP_WEIGHT, MAX_GROUP_WEIGHT);
            for (; i < length; i++) {
                mVisualRepresentationList.add(questResourceLibrary.getQuestVisualResourceItemId(questResourceLibrary.getVisualResourceItem(leftNode[i])));
            }
        }
    }

    @Override
    public int getGroupComparisonType() {
        return getQuestionType() == QuestionType.COMPARISON ? comparisonType : -1;
    }
}