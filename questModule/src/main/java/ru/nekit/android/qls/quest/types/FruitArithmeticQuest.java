package ru.nekit.android.qls.quest.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.qtp.rule.AbstractQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.FruitArithmeticQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem;
import ru.nekit.android.qls.utils.MathUtils;

import static ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup.BERRY;
import static ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup.POMUM;
import static ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem.EQUAL;
import static ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem.MINUS;
import static ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem.PLUS;

public class FruitArithmeticQuest extends NumberSummandQuest implements IQuestVisualRepresentation,
        IGroupWeightComparisonQuest {

    @Nullable
    private QuestVisualRepresentationList mVisualRepresentationList;

    private int comparisonType;

    public FruitArithmeticQuest(@NonNull QuestContext questContext,
                                @NonNull IQuest inQuest,
                                @NonNull AbstractQuestTrainingProgramRule inRule) {
        QuestionType questionType = inQuest.getQuestionType();
        setQuestType(QuestType.FRUIT_ARITHMETIC);
        setQuestionType(questionType);
        FruitArithmeticQuestTrainingProgramRule outRule = (FruitArithmeticQuestTrainingProgramRule) inRule;
        NumberSummandQuest outQuest = (NumberSummandQuest) inQuest;
        final int length = outQuest.leftNode.length;
        int i = 0;
        leftNode = outQuest.leftNode;
        rightNode = outQuest.rightNode;
        QuestResourceLibrary questResourceLibrary = questContext.getQuestResourceLibrary();
        mVisualRepresentationList = new QuestVisualRepresentationList();
        if (questionType == QuestionType.SOLUTION) {
            List<QuestVisualResourceItem> visualResourceSourceList =
                    questResourceLibrary.getVisualResourceItemsByGroup(MathUtils.randBoolean() ? POMUM : BERRY);
            QuestVisualResourceItem[] questVisualResourceItems = new QuestVisualResourceItem[length];
            for (; i < length; i++) {
                questVisualResourceItems[i] = visualResourceSourceList.get(
                        MathUtils.randUnsignedInt(visualResourceSourceList.size() - 1));
                visualResourceSourceList.remove(questVisualResourceItems[i]);
            }
            for (i = 0; i < length; i++) {
                final int length2 = Math.abs(leftNode[i]);
                for (int j = 0; j < length2; j++) {
                    mVisualRepresentationList.add(questVisualResourceItems[i]);
                }
                if (i < length - 1) {
                    mVisualRepresentationList.add(leftNode[i + 1] > 0 ? PLUS : MINUS);
                }
            }
            mVisualRepresentationList.add(EQUAL);
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
                mVisualRepresentationList.add(questResourceLibrary.getVisualResourceItem(leftNode[i]));
            }
        }
    }

    @Override
    @Nullable
    public QuestVisualRepresentationList getVisualRepresentationList() {
        return mVisualRepresentationList;
    }

    @Override
    public void setVisualRepresentationList(@NonNull QuestVisualRepresentationList value) {
        mVisualRepresentationList = value;
    }

    @Override
    public int getGroupComparisonType() {
        return getQuestionType() == QuestionType.COMPARISON ? comparisonType : -1;
    }
}