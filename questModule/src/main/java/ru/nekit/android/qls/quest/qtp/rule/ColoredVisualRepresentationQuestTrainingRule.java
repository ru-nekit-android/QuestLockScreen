package ru.nekit.android.qls.quest.qtp.rule;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.model.ColorModel;
import ru.nekit.android.qls.quest.resourceLibrary.IVisualResourceModel;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.utils.MathUtils;

public class ColoredVisualRepresentationQuestTrainingRule extends HasMemberQuestTrainingProgramRule {

    private static final int VALUE_DEFAULT_MEMBER_COUNT = 2;

    @Override
    public IQuest makeQuest(@NonNull QuestContext questContext,
                            @NonNull QuestionType questionType) {
        //TODO: exclude default colors
        ColorModel[] allColors = ColorModel.values();
        memberCount = Math.min(Math.max(memberCount, VALUE_DEFAULT_MEMBER_COUNT), allColors.length);
        QuestResourceLibrary questResourceLibrary = questContext.getQuestResourceLibrary();
        VisualRepresentationalNumberSummandQuest quest =
                new VisualRepresentationalNumberSummandQuest();
        quest.setQuestType(QuestType.COLORS);
        quest.setQuestionType(questionType);
        quest.leftNode = new int[memberCount];
        quest.rightNode = new int[memberCount];
        List<Integer> questVisualRepresentationList = new ArrayList<>();
        ColorModel[] targetColors = Arrays.copyOf(MathUtils.shuffleArray(allColors), memberCount);
        int i = 0;
        IVisualResourceModel questVisualResourceItem = MathUtils.randItem(questResourceLibrary.
                getVisualResourceItemsByGroup(VisualResourceGroup.CHILDREN_TOY));//questContext.getPupil().sex == PupilSex.BOY ? BOY : GIRL));
        for (; i < memberCount; i++) {
            quest.leftNode[i] = targetColors[i].getId();
            quest.rightNode[i] = -1;
            questVisualRepresentationList.add(questResourceLibrary.getQuestVisualResourceItemId(questVisualResourceItem));
        }
        quest.unknownMemberIndex = MathUtils.randLength(memberCount);
        List<ColorModel> secondaryColors =
                new ArrayList<>(Arrays.asList(Arrays.copyOf(MathUtils.shuffleArray(allColors), memberCount)));
        for (i = 0; i < memberCount; i++) {
            ColorModel colorModel;
            do {
                colorModel = MathUtils.randItem(secondaryColors);
                if (i == memberCount - 1 && quest.leftNode[i] == colorModel.getId()) {
                    //swap
                    int colorModelId = colorModel.getId();
                    colorModel = ColorModel.getById(quest.rightNode[i - 1]);
                    quest.rightNode[i - 1] = colorModelId;
                    break;
                }
            }
            while (quest.leftNode[i] == colorModel.getId());
            quest.rightNode[i] = colorModel.getId();
            secondaryColors.remove(colorModel);
        }
        quest.setVisualRepresentationList(questVisualRepresentationList);
        return quest;
    }
}
