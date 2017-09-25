package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.resourceLibrary.IVisualResourceItem;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.shared.QuestVisualRepresentationList;
import ru.nekit.android.qls.utils.MathUtils;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.TYPES;

public class ChoiceQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<ChoiceQuestTrainingProgramRule> CREATOR
            = new Creator<ChoiceQuestTrainingProgramRule>() {
        @Override
        public ChoiceQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new ChoiceQuestTrainingProgramRule(in);
        }

        @Override
        public ChoiceQuestTrainingProgramRule[] newArray(int size) {
            return new ChoiceQuestTrainingProgramRule[size];
        }
    };

    protected int[] types;
    VisualResourceGroup actualGroup;

    public ChoiceQuestTrainingProgramRule() {

    }

    ChoiceQuestTrainingProgramRule(Parcel in) {
        super(in);
        types = in.createIntArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeIntArray(types);
    }

    @Override
    public void parse(Gson gson, JsonObject object) {
        super.parse(gson, object);
        if (object.has(TYPES)) {
            String[] stringTypes = gson.fromJson(object.get(TYPES), String[].class);
            final int length = stringTypes.length;
            types = new int[stringTypes.length];
            for (int i = 0; i < length; i++) {
                types[i] = VisualResourceGroup.valueOf(stringTypes[i].toUpperCase()).getId();
            }
        } else {
            List<VisualResourceGroup> questVisualResourceGroupList =
                    VisualResourceGroup.CHOICE.getChildren();
            final int length = questVisualResourceGroupList.size();
            types = new int[length];
            for (int i = 0; i < length; i++) {
                types[i] = questVisualResourceGroupList.get(i).getId();
            }
        }
    }

    QuestVisualRepresentationList getQuestVisualRepresentationList(
            @NonNull QuestResourceLibrary questResourceLibrary) {
        actualGroup = getActualGroup();
        QuestVisualRepresentationList questVisualRepresentationList =
                new QuestVisualRepresentationList(questResourceLibrary);
        List<IVisualResourceItem> questVisualResourceItems =
                questResourceLibrary.getVisualResourceItemList();
        for (IVisualResourceItem questVisualResourceItem : questVisualResourceItems) {
            if (questVisualResourceItem.getGroups() != null) {
                for (VisualResourceGroup groupItem : questVisualResourceItem.getGroups()) {
                    if (groupItem.hasParent(actualGroup)) {
                        questVisualRepresentationList.add(questVisualResourceItem);
                    }
                }
            }
        }
        Collections.shuffle(questVisualRepresentationList.getIdsList());
        return questVisualRepresentationList;
    }

    private IQuest makeChoiceQuest(
            @NonNull final QuestVisualRepresentationList questVisualRepresentationList,
            @NonNull final QuestType questType,
            @NonNull final QuestionType questionType,
            final int unknownMemberIndex) {
        NumberSummandQuest quest = new NumberSummandQuest();
        quest.setQuestType(questType);
        quest.setQuestionType(questionType);
        final int length = questVisualRepresentationList.size();
        quest.leftNode = new int[length];
        for (int i = 0; i < length; i++) {
            quest.leftNode[i] = questVisualRepresentationList.get(i);
        }
        quest.unknownMemberIndex = unknownMemberIndex;
        return quest;
    }

    @Override
    public IQuest makeQuest(@NonNull QuestContext questContext,
                            @NonNull QuestionType questionType) {
        QuestVisualRepresentationList questVisualRepresentationList =
                getQuestVisualRepresentationList(questContext.getQuestResourceLibrary());
        return makeChoiceQuest(questVisualRepresentationList, getActualQuestType(),
                questionType, getUnknownIndex(questContext.getQuestResourceLibrary(),
                        questVisualRepresentationList));
    }

    VisualResourceGroup getActualGroup() {
        return VisualResourceGroup.getGroup(MathUtils.randItem(types));
    }

    int getUnknownIndex(@NonNull QuestResourceLibrary questResourceLibrary,
                        @NonNull QuestVisualRepresentationList questVisualRepresentationList) {
        return MathUtils.randListLength(questVisualRepresentationList.getIdsList());
    }

    QuestType getActualQuestType() {
        return QuestType.CHOICE;
    }
}