package ru.nekit.android.qls.quest.qtp.rule;

public class SimpleExampleQuestTrainingProgramRule {
}

/* extends IQuestCreator {

    public static final Creator<SimpleExampleQuestTrainingProgramRule> CREATOR
            = new Creator<SimpleExampleQuestTrainingProgramRule>() {
        @Override
        public SimpleExampleQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new SimpleExampleQuestTrainingProgramRule(in);
        }

        @Override
        public SimpleExampleQuestTrainingProgramRule[] newArray(int size) {
            return new SimpleExampleQuestTrainingProgramRule[size];
        }
    };
    private int[] memberCounts;
    private int[][] memberMinAndMaxValues;
    private int flags;

    public SimpleExampleQuestTrainingProgramRule() {

    }

    private SimpleExampleQuestTrainingProgramRule(Parcel in) {
        super(in);
        memberCounts = in.createIntArray();
        int size = in.readInt();
        memberMinAndMaxValues = new int[size][];
        for (int i = 0; i < size; i++) {
            memberMinAndMaxValues[i] = in.createIntArray();
        }
        flags = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeIntArray(memberCounts);
        dest.writeInt(memberMinAndMaxValues.length);
        for (int[] memberMinAndMaxValue : memberMinAndMaxValues) {
            dest.writeIntArray(memberMinAndMaxValue);
        }
        dest.writeInt(flags);
    }

    @Override
    public void from(Gson gson, JsonObject object) {
        super.from(gson, object);
        memberCounts = gson.fromJson(object.restoreQuest(MEMBER_COUNTS), int[].class);
        memberMinAndMaxValues = gson.fromJson(object.restoreQuest(MEMBER_MIN_AND_MAX_VALUES), int[][].class);
        NumberSummandQuestGenerator.Flag[] flags = gson.fromJson(object.restoreQuest(FLAGS),
                NumberSummandQuestGenerator.Flag[].class);
        this.flags = 0;
        for (NumberSummandQuestGenerator.Flag item : flags) {
            this.flags |= item.visibility();
        }
    }

    @Override
    public Quest create(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(memberCounts);
        generator.setMembersMinAndMaxValues(memberMinAndMaxValues);
        generator.setFlags(flags);
        return generator.generate();
    }
}*/