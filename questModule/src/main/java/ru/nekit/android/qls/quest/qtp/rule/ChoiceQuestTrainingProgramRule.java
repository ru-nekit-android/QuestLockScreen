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
import ru.nekit.android.qls.quest.generator.IQuestGenerator;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceGroup;
import ru.nekit.android.qls.quest.resourceLibrary.QuestVisualResourceItem;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.QuestVisualRepresentationList;
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
    QuestVisualResourceGroup actualGroup;

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
                types[i] = QuestVisualResourceGroup.valueOf(stringTypes[i].toUpperCase()).getId();
            }
        } else {
            List<QuestVisualResourceGroup> questVisualResourceGroupList =
                    QuestVisualResourceGroup.CHOICE.getChildren();
            final int length = questVisualResourceGroupList.size();
            types = new int[length];
            for (int i = 0; i < length; i++) {
                types[i] = questVisualResourceGroupList.get(i).getId();
            }
        }
    }

    QuestVisualRepresentationList getQuestVisualRepresentationList(
            @NonNull QuestContext questContext) {
        actualGroup = getActualGroup();
        QuestVisualRepresentationList questVisualRepresentationList =
                new QuestVisualRepresentationList();
        QuestVisualResourceItem[] questVisualResourceItems =
                questContext.getQuestResourceLibrary().getVisualResourceItemList();
        for (QuestVisualResourceItem questVisualResourceItem : questVisualResourceItems) {
            if (questVisualResourceItem.getGroups() != null) {
                for (QuestVisualResourceGroup groupItem : questVisualResourceItem.getGroups()) {
                    if (groupItem.hasParent(actualGroup)) {
                        questVisualRepresentationList.add(questVisualResourceItem);
                    }
                }
            }
        }
        Collections.shuffle(questVisualRepresentationList.getIdsList());
        return questVisualRepresentationList;
    }

    private IQuestGenerator makeChoiceQuestGenerator(
            @NonNull final QuestVisualRepresentationList questVisualRepresentationList,
            @NonNull final QuestType questType,
            @NonNull final QuestionType questionType,
            final int unknownMemberIndex) {
        return new IQuestGenerator() {
            @Override
            public IQuest generate() {
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
        };
    }

    @Override
    public IQuestGenerator makeQuestGenerator(@NonNull QuestContext questContext,
                                              @NonNull QuestionType questionType) {
        QuestVisualRepresentationList questVisualRepresentationList =
                getQuestVisualRepresentationList(questContext);
        return makeChoiceQuestGenerator(questVisualRepresentationList, getActualQuestType(),
                questionType, getUnknownIndex(questVisualRepresentationList));
    }

    QuestVisualResourceGroup getActualGroup() {
        return QuestVisualResourceGroup.getGroup(MathUtils.randItem(types));
    }

    int getUnknownIndex(QuestVisualRepresentationList questVisualRepresentationList) {
        return MathUtils.randListLength(questVisualRepresentationList.getIdsList());
    }

    QuestType getActualQuestType() {
        return QuestType.CHOICE;
    }
}