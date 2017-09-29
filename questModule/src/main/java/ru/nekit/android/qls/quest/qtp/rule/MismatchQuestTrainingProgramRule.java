package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.resourceLibrary.IVisualResourceModel;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup;
import ru.nekit.android.qls.quest.types.shared.QuestVisualRepresentationList;

import static ru.nekit.android.qls.utils.MathUtils.randListLength;

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
    @NonNull
    QuestVisualRepresentationList getQuestVisualRepresentationList(
            @NonNull QuestResourceLibrary questResourceLibrary) {
        QuestVisualRepresentationList questVisualRepresentationList =
                super.getQuestVisualRepresentationList(questResourceLibrary);
        VisualResourceGroup mismatchGroup = null;
        List<VisualResourceGroup> questVisualResourceGroups =
                Arrays.asList(VisualResourceGroup.values());
        Collections.shuffle(questVisualResourceGroups);
        for (VisualResourceGroup group : questVisualResourceGroups) {
            if (!group.hasParent(targetGroup) && !targetGroup.hasParent(group)) {
                mismatchGroup = group;
                break;
            }
        }
        List<IVisualResourceModel> mismatchQVRItemIdList =
                mismatchGroup.getVisualResourceItems(questResourceLibrary);
        unknownMemberIndex = randListLength(questVisualRepresentationList.getIdsList());
        questVisualRepresentationList.getIdsList().add(unknownMemberIndex,
                questResourceLibrary.getQuestVisualResourceItemId(
                        mismatchQVRItemIdList.get(randListLength(mismatchQVRItemIdList))));
        questVisualRepresentationList.getIdsList().remove(unknownMemberIndex + 1);
        return questVisualRepresentationList;
    }


    @Override
    public QuestType getQuestType() {
        return QuestType.MISMATCH;
    }

    @Override
    int getUnknownIndex(@NonNull QuestResourceLibrary questResourceLibrary,
                        @NonNull QuestVisualRepresentationList questVisualRepresentationList) {
        return unknownMemberIndex;
    }
}