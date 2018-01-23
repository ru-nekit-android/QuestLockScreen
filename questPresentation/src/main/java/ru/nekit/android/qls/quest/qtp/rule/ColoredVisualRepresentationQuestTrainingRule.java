package ru.nekit.android.qls.quest.qtp.rule;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resources.collections.ColorResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.VisualResourceGroupCollection;
import ru.nekit.android.qls.quest.resources.common.IVisualResourceHolder;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.utils.MathUtils;

public class ColoredVisualRepresentationQuestTrainingRule extends HasMemberQuestTrainingProgramRule {

    private static final int VALUE_DEFAULT_MEMBER_COUNT = 2;

    @Override
    public Quest makeQuest(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        //TODO: exclude default colors
        ColorResourceCollection[] allColors = ColorResourceCollection.values();
        memberCount = Math.min(Math.max(memberCount, VALUE_DEFAULT_MEMBER_COUNT), allColors.length);
        QuestResourceLibrary questResourceLibrary = questContext.getQuestResourceLibrary();
        VisualRepresentationalNumberSummandQuest quest = new VisualRepresentationalNumberSummandQuest();
        quest.setQuestType(QuestType.COLORS);
        quest.setQuestionType(questionType);
        quest.leftNode = new int[memberCount];
        quest.rightNode = new int[memberCount];
        List<Integer> questVisualRepresentationList = new ArrayList<>();
        ColorResourceCollection[] targetColors = Arrays.copyOf(MathUtils.shuffleArray(allColors), memberCount);
        int i = 0;
        IVisualResourceHolder questVisualResourceItem = MathUtils.randItem(questResourceLibrary.
                getVisualResourceItemsByGroup(VisualResourceGroupCollection.CHILDREN_TOY));//questContext.getPupil().sex == PupilSex.BOY ? BOY : GIRL));
        for (; i < memberCount; i++) {
            quest.leftNode[i] = targetColors[i].getId();
            quest.rightNode[i] = -1;
            questVisualRepresentationList.add(questResourceLibrary.getQuestVisualResourceId(questVisualResourceItem));
        }
        quest.unknownMemberIndex = MathUtils.randLength(memberCount);
        List<ColorResourceCollection> secondaryColors = new ArrayList<>(Arrays.asList(Arrays.copyOf(MathUtils.shuffleArray(allColors), memberCount)));

        for (i = 0; i < memberCount; i++) {
            ColorResourceCollection colorResourceCollection;
            do {
                colorResourceCollection = MathUtils.randItem(secondaryColors);
                if (i == memberCount - 1 && quest.leftNode[i] == colorResourceCollection.getId()) {
                    //swap
                    int colorModelId = colorResourceCollection.getId();
                    colorResourceCollection = ColorResourceCollection.getById(quest.rightNode[i - 1]);
                    quest.rightNode[i - 1] = colorModelId;
                    break;
                }
            }
            while (quest.leftNode[i] == colorResourceCollection.getId());
            quest.rightNode[i] = colorResourceCollection.getId();
            secondaryColors.remove(colorResourceCollection);
        }
        quest.setVisualRepresentationList(questVisualRepresentationList);
        return quest;
    }
}
