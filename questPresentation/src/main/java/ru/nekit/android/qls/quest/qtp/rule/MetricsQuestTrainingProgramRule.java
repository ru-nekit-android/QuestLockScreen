package ru.nekit.android.qls.quest.qtp.rule;

public class MetricsQuestTrainingProgramRule {
}
        /*extends IQuestCreator {

    public static final Creator<MetricsQuestTrainingProgramRule> CREATOR
            = new Creator<MetricsQuestTrainingProgramRule>() {
        @Override
        public MetricsQuestTrainingProgramRule createFromParcel(Parcel in) {
            return new MetricsQuestTrainingProgramRule(in);
        }

        @Override
        public MetricsQuestTrainingProgramRule[] newArray(int size) {
            return new MetricsQuestTrainingProgramRule[size];
        }
    };
    private int[] memberCounts;
    private int[][] eachMemberMinAndMaxValues;
    private int flags;

    public MetricsQuestTrainingProgramRule() {

    }

    private MetricsQuestTrainingProgramRule(Parcel in) {
        super(in);
        memberCounts = in.createIntArray();
        int size = in.readInt();
        eachMemberMinAndMaxValues = new int[size][];
        for (int i = 0; i < size; i++) {
            eachMemberMinAndMaxValues[i] = in.createIntArray();
        }
        flags = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeIntArray(memberCounts);
        dest.writeInt(eachMemberMinAndMaxValues.length);
        for (int[] eachMemberMinAndMaxValue : eachMemberMinAndMaxValues) {
            dest.writeIntArray(eachMemberMinAndMaxValue);
        }
        dest.writeInt(flags);
    }

    @Override
    public void from(Gson gson, JsonObject object) {
        super.from(gson, object);
        memberCounts = gson.fromJson(object.restoreQuest(MEMBER_COUNTS), int[].class);
        eachMemberMinAndMaxValues = gson.fromJson(object.restoreQuest(EACH_MEMBER_MIN_AND_MAX_VALUES),
                int[][].class);
        NumberSummandQuestGenerator.Flag[] flags = gson.fromJson(object.restoreQuest(FLAGS),
                NumberSummandQuestGenerator.Flag[].class);
        this.flags = 0;
        if (flags != null) {
            for (NumberSummandQuestGenerator.Flag item : flags) {
                this.flags |= item.visibility();
            }
        }
    }

    @Override
    public Quest create(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(memberCounts);
        generator.setEachMemberMinAndMaxValues(eachMemberMinAndMaxValues);
        generator.setFlags(flags);
        generator.setLeftNodeMembersZeroValueChance(new int[]{85, 25, 0});
        return generator.generate();
    }
}*/
