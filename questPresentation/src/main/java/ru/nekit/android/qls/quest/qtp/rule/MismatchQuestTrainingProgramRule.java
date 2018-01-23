package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resources.collections.VisualResourceGroupCollection;
import ru.nekit.android.qls.quest.resources.common.IVisualResourceHolder;

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
    List<Integer> getQuestVisualRepresentationList(
            @NonNull QuestResourceLibrary questResourceLibrary) {
        List<Integer> questVisualRepresentationList =
                super.getQuestVisualRepresentationList(questResourceLibrary);
        VisualResourceGroupCollection mismatchGroup = null;
        List<VisualResourceGroupCollection> questVisualResourceGroupCollection =
                Arrays.asList(VisualResourceGroupCollection.values());
        Collections.shuffle(questVisualResourceGroupCollection);
        for (VisualResourceGroupCollection group : questVisualResourceGroupCollection) {
            if (!group.hasParent(targetGroup) && !targetGroup.hasParent(group)) {
                mismatchGroup = group;
                break;
            }
        }
        List<IVisualResourceHolder> mismatchQVRItemIdList =
                mismatchGroup.getVisualResourceItems(questResourceLibrary);
        unknownMemberIndex = randListLength(questVisualRepresentationList);
        questVisualRepresentationList.add(unknownMemberIndex,
                questResourceLibrary.getQuestVisualResourceId(
                        mismatchQVRItemIdList.get(randListLength(mismatchQVRItemIdList))));
        questVisualRepresentationList.remove(unknownMemberIndex + 1);
        return questVisualRepresentationList;
    }


    @Override
    public QuestType getQuestType() {
        return QuestType.MISMATCH;
    }

    @Override
    int getUnknownIndex(@NonNull QuestResourceLibrary questResourceLibrary,
                        @NonNull List<Integer> questVisualRepresentationList) {
        return unknownMemberIndex;
    }
}