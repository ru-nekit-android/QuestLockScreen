package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.generator.IQuestGenerator;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.MEMBER_COUNT;

public class SimpleMemberQuestTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<SimpleMemberQuestTrainingProgramRule> CREATOR =
            new Creator<SimpleMemberQuestTrainingProgramRule>() {
                @Override
                public SimpleMemberQuestTrainingProgramRule createFromParcel(Parcel in) {
                    return new SimpleMemberQuestTrainingProgramRule(in);
                }

                @Override
                public SimpleMemberQuestTrainingProgramRule[] newArray(int size) {
                    return new SimpleMemberQuestTrainingProgramRule[size];
                }
            };

    protected int memberCount;

    public SimpleMemberQuestTrainingProgramRule() {
    }

    private SimpleMemberQuestTrainingProgramRule(Parcel in) {
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
    public IQuestGenerator makeQuestGenerator(@NonNull QuestContext context, @NonNull QuestionType questionType) {
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeInt(memberCount);
    }
}
