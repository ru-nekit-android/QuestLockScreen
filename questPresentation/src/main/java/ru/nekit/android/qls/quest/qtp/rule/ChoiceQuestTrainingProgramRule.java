package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.Quest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resources.collections.VisualQuestResourceGroupCollection;
import ru.nekit.android.qls.quest.resources.common.IVisualQuestResourceHolder;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
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
    VisualQuestResourceGroupCollection targetGroup;

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
                types[i] = VisualQuestResourceGroupCollection.valueOf(stringTypes[i].toUpperCase()).getId();
            }
        } else {
            List<VisualQuestResourceGroupCollection> questVisualQuestResourceGroupCollectionList =
                    VisualQuestResourceGroupCollection.CHOICE.getChildren();
            final int length = questVisualQuestResourceGroupCollectionList.size();
            types = new int[length];
            for (int i = 0; i < length; i++) {
                types[i] = questVisualQuestResourceGroupCollectionList.get(i).getId();
            }
        }
    }

    @NonNull
    List<Integer> getQuestVisualRepresentationList(
            @NonNull QuestResourceLibrary questResourceLibrary) {
        targetGroup = getTargetGroup();
        List<Integer> questVisualRepresentationList = new ArrayList<>();
        List<IVisualQuestResourceHolder> visualResourceModels =
                questResourceLibrary.getVisualQuestResourceList();
        for (IVisualQuestResourceHolder model : visualResourceModels) {
            if (model.getGroups() != null) {
                for (VisualQuestResourceGroupCollection groupItem : model.getGroups()) {
                    if (groupItem.hasParent(targetGroup)) {
                        questVisualRepresentationList.add(questResourceLibrary.getQuestVisualResourceId(model));
                    }
                }
            }
        }
        Collections.shuffle(questVisualRepresentationList);
        return questVisualRepresentationList;
    }

    @Override
    public Quest makeQuest(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        List<Integer> questVisualRepresentationList =
                getQuestVisualRepresentationList(questContext.getQuestResourceLibrary());
        NumberSummandQuest quest = new NumberSummandQuest();
        quest.setQuestType(getQuestType());
        quest.setQuestionType(questionType);
        final int length = questVisualRepresentationList.size();
        quest.leftNode = new int[length];
        for (int i = 0; i < length; i++) {
            quest.leftNode[i] = questVisualRepresentationList.get(i);
        }
        quest.unknownMemberIndex = getUnknownIndex(questContext.getQuestResourceLibrary(),
                questVisualRepresentationList);
        return quest;
    }

    VisualQuestResourceGroupCollection getTargetGroup() {
        return VisualQuestResourceGroupCollection.getGroup(MathUtils.randItem(types));
    }

    int getUnknownIndex(@NonNull QuestResourceLibrary questResourceLibrary,
                        @NonNull List<Integer> questVisualRepresentationList) {
        return MathUtils.randListLength(questVisualRepresentationList);
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.CHOICE;
    }
}