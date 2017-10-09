package ru.nekit.android.qls.quest.qtp.rule;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.base.Quest;
import ru.nekit.android.qls.quest.generator.TextQuestGenerator;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.CAMOUFLAGE_LENGTH;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.WORD_LENGTH;

public class TextCamouflageTrainingProgramRule extends AbstractQuestTrainingProgramRule {

    public static final Creator<TextCamouflageTrainingProgramRule> CREATOR
            = new Creator<TextCamouflageTrainingProgramRule>() {
        @Override
        public TextCamouflageTrainingProgramRule createFromParcel(Parcel in) {
            return new TextCamouflageTrainingProgramRule(in);
        }

        @Override
        public TextCamouflageTrainingProgramRule[] newArray(int size) {
            return new TextCamouflageTrainingProgramRule[size];
        }
    };

    private int wordLength, camouflageLength;

    public TextCamouflageTrainingProgramRule() {
    }

    private TextCamouflageTrainingProgramRule(Parcel in) {
        super(in);
        wordLength = in.readInt();
        camouflageLength = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(wordLength);
        dest.writeInt(camouflageLength);
    }

    @Override
    public void parse(Gson gson, JsonObject object) {
        super.parse(gson, object);
        if (object.has(WORD_LENGTH)) {
            wordLength = gson.fromJson(object.get(WORD_LENGTH), int.class);
        }
        if (object.has(CAMOUFLAGE_LENGTH)) {
            camouflageLength = gson.fromJson(object.get(CAMOUFLAGE_LENGTH), int.class);
        }
    }

    @Override
    public Quest makeQuest(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        TextQuestGenerator generator = new TextQuestGenerator(questContext, questionType);
        generator.makeTextCamouflage(wordLength, camouflageLength);
        return generator.generate();
    }
}