package ru.nekit.android.qls.quest.qtp.rule;

public class PerimeterQuestTrainingProgramRule {
}/* extends IQuestCreator {

    public static final Creator<PerimeterQuestTrainingProgramRule> CREATOR =
            new Creator<PerimeterQuestTrainingProgramRule>() {
                @Override
                public PerimeterQuestTrainingProgramRule createFromParcel(Parcel in) {
                    return new PerimeterQuestTrainingProgramRule(in);
                }

                @Override
                public PerimeterQuestTrainingProgramRule[] newArray(int size) {
                    return new PerimeterQuestTrainingProgramRule[size];
                }
            };
    private static final int PERIMETER_SQUARE_FIGURE_CHANCE = 25;
    private int[][] memberMinAndMaxValues;

    public PerimeterQuestTrainingProgramRule() {

    }

    private PerimeterQuestTrainingProgramRule(Parcel in) {
        super(in);
        int size = in.readInt();
        memberMinAndMaxValues = new int[size][];
        for (int i = 0; i < size; i++) {
            memberMinAndMaxValues[i] = in.createIntArray();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int _flags) {
        super.writeToParcel(dest, _flags);
        dest.writeInt(memberMinAndMaxValues.length);
        for (int[] memberMinAndMaxValue : memberMinAndMaxValues) {
            dest.writeIntArray(memberMinAndMaxValue);
        }
    }

    @Override
    public void from(Gson gson, JsonObject object) {
        super.from(gson, object);
        memberMinAndMaxValues = gson.fromJson(object.restoreQuest(QuestTrainingProgram.Dictionary.MEMBER_MIN_AND_MAX_VALUES), int[][].class);
    }

    @Override
    public Quest create(@NonNull QuestContext questContext,
                           @NonNull QuestionType questionType) {
        NumberSummandQuestGenerator generator = new NumberSummandQuestGenerator(questionType);
        generator.setMemberCounts(2, 1);
        generator.setFlags(NumberSummandQuestGenerator.Flag.ONLY_POSITIVE_SUMMANDS);
        generator.setLeftNodeMembersZeroValueChance(new int[]{0, PERIMETER_SQUARE_FIGURE_CHANCE});
        generator.setMembersMinAndMaxValues(memberMinAndMaxValues);
        return generator.generate();
    }
}*/