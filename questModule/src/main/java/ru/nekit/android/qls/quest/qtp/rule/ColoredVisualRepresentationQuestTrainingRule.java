package ru.nekit.android.qls.quest.qtp.rule;

import android.support.annotation.NonNull;

import java.util.Arrays;

import ru.nekit.android.qls.pupil.PupilSex;
import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.resourceLibrary.IQuestVisualResourceItem;
import ru.nekit.android.qls.quest.types.model.ColorModel;
import ru.nekit.android.qls.quest.types.quest.NumberSummandQuest;
import ru.nekit.android.qls.utils.MathUtils;

import static ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup.BOY;
import static ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup.GIRL;

public class ColoredVisualRepresentationQuestTrainingRule extends HasMemberQuestTrainingProgramRule {

    private static final int VALUE_DEFAULT_MEMBER_COUNT = 2;

    @Override
    public IQuest makeQuest(@NonNull QuestContext questContext,
                            @NonNull QuestionType questionType) {
        ColorModel[] allColors = ColorModel.values();
        memberCount = Math.min(Math.max(memberCount, VALUE_DEFAULT_MEMBER_COUNT), allColors.length);
        NumberSummandQuest quest =
                new NumberSummandQuest();
        quest.setQuestType(QuestType.COLORS);
        quest.setQuestionType(questionType);
        quest.leftNode = new int[memberCount];
        quest.rightNode = new int[memberCount];
        ColorModel[] targetColors = Arrays.copyOf(MathUtils.shuffleArray(allColors), memberCount);
        int i = 0;
        IQuestVisualResourceItem questVisualResourceItem =
                MathUtils.randItem(questContext.getQuestResourceLibrary().
                        getVisualResourceItemsByGroup(questContext.getPupil().sex == PupilSex.BOY ? BOY : GIRL));
        for (; i < memberCount; i++) {
            quest.leftNode[i] = targetColors[i].getId();
            quest.rightNode[i] = -1;
            // quest.getVisualRepresentationList().add(questVisualResourceItem);
        }
        quest.unknownMemberIndex = MathUtils.randUnsignedInt(memberCount);
        for (i = 0; i < memberCount; i++) {
            ColorModel colorModel;
            while (quest.leftNode[i] == (colorModel = MathUtils.randItem(allColors)).getId()
                    || find(colorModel.getId(), quest.rightNode)) ;
            quest.rightNode[i] = colorModel.getId();
        }
        return quest;
    }

    private boolean find(int value, int[] values) {
        for (int item : values) {
            if (item == value) return true;
        }
        return false;
    }
}
