package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.MEMBER_COUNT;

public class HasMemberQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<HasMemberQuestTrainingProgramRule> CREATOR =
            new Creator<HasMemberQuestTrainingProgramRule>() {
                @Override
                public HasMemberQuestTrainingProgramRule createFromParcel(Parcel in) {
                    return new HasMemberQuestTrainingProgramRule(in);
                }

                @Override
                public HasMemberQuestTrainingProgramRule[] newArray(int size) {
                    return new HasMemberQuestTrainingProgramRule[size];
                }
            };

    protected int memberCount;

    public HasMemberQuestTrainingProgramRule() {
    }

    private HasMemberQuestTrainingProgramRule(Parcel in) {
        super(in);
        memberCount = in.readInt();
    }

    @Override
    public void parse(Gson gson, JsonObject object) {
        super.parse(gson, object);
        if (object.has(MEMBER_COUNT)) {
            memberCount = object.get(MEMBER_COUNT).getAsInt();
        }
    }

    @Override
    public IQuest makeQuest(@NonNull QuestContext context, @NonNull QuestionType questionType) {
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeInt(memberCount);
    }
}
