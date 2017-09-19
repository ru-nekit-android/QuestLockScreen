package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem;
import ru.nekit.android.qls.quest.types.QuestVisualRepresentationList;
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

    private int unknownMemberIndex;

    public MismatchQuestTrainingProgramRule() {
    }

    private MismatchQuestTrainingProgramRule(Parcel in) {
        super(in);
    }

    @Override
    QuestVisualRepresentationList getQuestVisualRepresentationList(
            @NonNull QuestContext questContext) {
        QuestVisualResourceGroup mismatchGroup = null;
        QuestVisualRepresentationList questVisualRepresentationList =
                super.getQuestVisualRepresentationList(questContext);
        List<QuestVisualResourceGroup> questVisualResourceGroups =
                Arrays.asList(QuestVisualResourceGroup.values());
        Collections.shuffle(questVisualResourceGroups);
        for (QuestVisualResourceGroup group : questVisualResourceGroups) {
            if (!group.hasParent(actualGroup) && !actualGroup.hasParent(group)) {
                mismatchGroup = group;
                break;
            }
        }
        List<QuestVisualResourceItem> mismatchQVRItemIdList = mismatchGroup.getQuestVisualItems();
        unknownMemberIndex = MathUtils.randListLength(questVisualRepresentationList.getIdsList());
        questVisualRepresentationList.getIdsList().add(unknownMemberIndex,
                mismatchQVRItemIdList.get(MathUtils.randListLength(mismatchQVRItemIdList)).getId());
        questVisualRepresentationList.getIdsList().remove(unknownMemberIndex + 1);
        return questVisualRepresentationList;
    }


    QuestType getActualQuestType() {
        return QuestType.MISMATCH;
    }

    @Override
    int getUnknownIndex(QuestVisualRepresentationList questVisualRepresentationList) {
        return unknownMemberIndex;
    }
}
