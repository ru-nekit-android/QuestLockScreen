package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.IQuestGenerator;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem;
import ru.nekit.android.qls.utils.MathUtils;

public class MismatchQuestTrainingProgramRule extends ChoiceQuestTrainingProgramRule {

    public static final Creator<MismatchQuestTrainingProgramRule> CREATOR
            = new Creator<MismatchQuestTrainingProgramRule>() {
        @Override
        public MismatchQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new MismatchQuestTrainingProgramRule(in);
        }

        @Override
        public MismatchQuestTrainingProgramRule[] newArray(int size) {
            return new MismatchQuestTrainingProgramRule[size];
        }
    };

    public MismatchQuestTrainingProgramRule() {

    }

    private MismatchQuestTrainingProgramRule(Parcel in) {
        super(in);
    }

    @Override
    public IQuestGenerator makeQuestGenerator(@NonNull QuestContext questContext,
                                              @NonNull QuestionType questionType) {
        QuestVisualResourceGroup mismatchGroup = null;
        List<Integer> questVisualResourceItemIdList = getVisualResourceItemIdList(questContext);
        List<QuestVisualResourceGroup> questVisualResourceGroups =
                Arrays.asList(QuestVisualResourceGroup.values());
        Collections.shuffle(questVisualResourceGroups);
        for (QuestVisualResourceGroup group : questVisualResourceGroups) {
            if (!group.hasParent(currentGroup) && !currentGroup.hasParent(group)) {
                mismatchGroup = group;
                break;
            }
        }
        List<QuestVisualResourceItem> mismatchQuestVisualResourceItemIdList =
                mismatchGroup.getQuestVisualItems();
        int unknownMemberIndex = MathUtils.randUnsignedInt(questVisualResourceItemIdList.size() - 1);
        questVisualResourceItemIdList.add(unknownMemberIndex,
                mismatchQuestVisualResourceItemIdList.get(
                        MathUtils.randUnsignedInt(mismatchQuestVisualResourceItemIdList.size() - 1)
                ).ordinal());
        questVisualResourceItemIdList.remove(unknownMemberIndex + 1);
        return makeChoiceQuestGenerator(questVisualResourceItemIdList, QuestType.MISMATCH, questionType,
                unknownMemberIndex);
    }
}
