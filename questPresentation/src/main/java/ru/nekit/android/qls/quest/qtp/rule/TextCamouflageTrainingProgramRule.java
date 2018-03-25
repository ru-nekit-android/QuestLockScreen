package ru.nekit.android.qls.quest.qtp.rule;

public class TextCamouflageTrainingProgramRule {
}/* extends IQuestCreator {

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
    public void from(Gson gson, JsonObject object) {
        super.from(gson, object);
        if (object.has(WORD_LENGTH)) {
            wordLength = gson.fromJson(object.restoreQuest(WORD_LENGTH), int.class);
        }
        if (object.has(CAMOUFLAGE_LENGTH)) {
            camouflageLength = gson.fromJson(object.restoreQuest(CAMOUFLAGE_LENGTH), int.class);
        }
    }

    @Override
    public Quest create(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        TextQuestGenerator generator = new TextQuestGenerator(questContext, questionType);
        generator.makeTextCamouflage(wordLength, camouflageLength);
        return generator.generate();
    }
}*/