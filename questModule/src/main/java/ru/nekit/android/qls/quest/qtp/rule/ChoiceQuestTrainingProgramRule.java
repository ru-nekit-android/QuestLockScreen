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
import ru.nekit.android.qls.quest.generator.IQuestGenerator;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem;
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
    QuestVisualResourceGroup currentGroup;

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
            types = new int[stringTypes.length];
            int index = 0;
            for (String type : stringTypes) {
                type = type.toUpperCase();
                QuestVisualResourceGroup group = QuestVisualResourceGroup.valueOf(type);
                types[index++] = group.ordinal();
            }
        } else {
            List<QuestVisualResourceGroup> questVisualResourceGroupList =
                    QuestVisualResourceGroup.CHOICE.getChildren();
            int length = questVisualResourceGroupList.size();
            types = new int[length];
            for (int i = 0; i < length; i++) {
                types[i] = questVisualResourceGroupList.get(i).getId();
            }
        }
    }

    List<Integer> getVisualResourceItemIdList(@NonNull QuestContext questContext) {
        currentGroup = QuestVisualResourceGroup.values()[MathUtils.randItem(types)];
        List<Integer> questVisualResourceItemIdList = new ArrayList<>();
        QuestVisualResourceItem[] questVisualResourceItems =
                questContext.getQuestResourceLibrary().getVisualResourceItemList();
        for (QuestVisualResourceItem questVisualResourceItem : questVisualResourceItems) {
            if (questVisualResourceItem.getGroups() != null) {
                for (QuestVisualResourceGroup groupItem : questVisualResourceItem.getGroups()) {
                    if (groupItem.hasParent(currentGroup)) {
                        questVisualResourceItemIdList.add(questVisualResourceItem.getId());
                    }
                }
            }
        }
        Collections.shuffle(questVisualResourceItemIdList);
        return questVisualResourceItemIdList;
    }

    IQuestGenerator makeChoiceQuestGenerator(@NonNull final List<Integer> questVisualResourceItems,
                                             @NonNull final QuestType questType,
                                             @NonNull final QuestionType questionType,
                                             final int unknownMemberIndex) {
        return new IQuestGenerator() {
            @Override
            public IQuest generate() {
                NumberSummandQuest quest = new NumberSummandQuest();
                quest.setQuestType(questType);
                quest.setQuestionType(questionType);
                final int length = questVisualResourceItems.size();
                quest.leftNode = new int[length];
                for (int i = 0; i < length; i++) {
                    quest.leftNode[i] = questVisualResourceItems.get(i);
                }
                quest.unknownMemberIndex = unknownMemberIndex;
                return quest;
            }
        };
    }

    @Override
    public IQuestGenerator makeQuestGenerator(@NonNull QuestContext questContext,
                                              @NonNull QuestionType questionType) {
        List<Integer> questVisualResourceItemList = getVisualResourceItemIdList(questContext);
        return makeChoiceQuestGenerator(questVisualResourceItemList, QuestType.CHOICE, questionType,
                MathUtils.randListLength(questVisualResourceItemList));
    }
}