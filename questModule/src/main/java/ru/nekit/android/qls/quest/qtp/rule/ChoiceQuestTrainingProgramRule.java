package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.resourceLibrary.IVisualResourceModel;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.resourceLibrary.VisualResourceGroup;
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
    VisualResourceGroup targetGroup;

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

    @NonNull
    List<Integer> getQuestVisualRepresentationList(
            @NonNull QuestResourceLibrary questResourceLibrary) {
        targetGroup = getTargetGroup();
        List<Integer> questVisualRepresentationList = new ArrayList<>();
        List<IVisualResourceModel> visualResourceModels =
                questResourceLibrary.getVisualResourceItems();
        for (IVisualResourceModel model : visualResourceModels) {
            if (model.getGroups() != null) {
                for (VisualResourceGroup groupItem : model.getGroups()) {
                    if (groupItem.hasParent(targetGroup)) {
                        questVisualRepresentationList.add(questResourceLibrary.getQuestVisualResourceItemId(model));
                    }
                }
            }
        }
        Collections.shuffle(questVisualRepresentationList);
        return questVisualRepresentationList;
    }

    @Override
    public IQuest makeQuest(@NonNull QuestContext questContext,
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

    VisualResourceGroup getTargetGroup() {
        return VisualResourceGroup.getGroup(MathUtils.randItem(types));
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