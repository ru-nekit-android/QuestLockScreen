package ru.nekit.android.qls.quest.qtp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.qtp.rule.AbstractQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.ChoiceQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.CoinsQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.CurrentTimeQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.FruitArithmeticQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.MetricsQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.MismatchQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.PerimeterQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.SimpleExampleQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.TextCamouflageTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.TimeQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.qtp.rule.TrafficLightQuestTrainingProgramRule;

import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.DELAYED_START;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.DESCRIPTION;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.NAME;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.POINTS_MULTIPLIER;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.QUEST_TYPE;
import static ru.nekit.android.qls.quest.qtp.QuestTrainingProgram.Dictionary.RULES;

public class QuestTrainingProgramLevel implements Parcelable {

    public static final Creator<QuestTrainingProgramLevel> CREATOR = new Creator<QuestTrainingProgramLevel>() {
        @Override
        public QuestTrainingProgramLevel createFromParcel(Parcel in) {
            return new QuestTrainingProgramLevel(in);
        }

        @Override
        public QuestTrainingProgramLevel[] newArray(int size) {
            return new QuestTrainingProgramLevel[size];
        }
    };
    int index, pointsWeight;
    List<AbstractQuestTrainingProgramRule> questRules;
    private String name, description;
    private float pointsMultiplier;
    private int delayedStart;

    QuestTrainingProgramLevel() {

    }

    protected QuestTrainingProgramLevel(Parcel in) {
        index = in.readInt();
        pointsWeight = in.readInt();
        name = in.readString();
        description = in.readString();
        pointsMultiplier = in.readFloat();
        questRules = in.readArrayList(AbstractQuestTrainingProgramRule.class.getClassLoader());
        delayedStart = in.readInt();
    }

    public int getPointsWeight() {
        return pointsWeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeInt(pointsWeight);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeFloat(pointsMultiplier);
        dest.writeList(questRules);
        dest.writeInt(delayedStart);
    }

    public float getPointsMultiplier() {
        return pointsMultiplier;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return Integer.toString(getIndex() + 1);
    }

    public int getDelayedStart() {
        return delayedStart;
    }

    public void parse(@NonNull Context context, @NonNull JsonObject object) {
        Gson gson = new Gson();
        if (object.has(NAME)) {
            name = object.get(NAME).getAsString();
        }
        if (object.has(DESCRIPTION)) {
            description = object.get(DESCRIPTION).getAsString();
        }
        if (object.has(DELAYED_START)) {
            delayedStart = object.get(DELAYED_START).getAsBoolean() ? 1 : 0;
        } else {
            delayedStart = -1;
        }
        pointsWeight = object.get(QuestTrainingProgram.Dictionary.POINTS_WEIGHT).getAsInt();
        if (object.has(POINTS_MULTIPLIER)) {
            pointsMultiplier = object.get(POINTS_MULTIPLIER).getAsFloat();
        }
        pointsMultiplier = Math.min(1, pointsMultiplier);
        if (object.has(RULES)) {
            JsonArray rulesObject = object.get(RULES).getAsJsonArray();
            questRules = new ArrayList<>(rulesObject.size());
            for (JsonElement item : rulesObject) {
                String questTypeNameOrSynonym = item.getAsJsonObject().get(QUEST_TYPE).getAsString();
                QuestType questType = QuestType.getByNameOrSynonym(context, questTypeNameOrSynonym);
                AbstractQuestTrainingProgramRule rule = null;
                if (questType != null) {
                    switch (questType) {
                        case COINS:

                            rule = new CoinsQuestTrainingProgramRule();

                            break;

                        case SIMPLE_EXAMPLE:

                            rule = new SimpleExampleQuestTrainingProgramRule();

                            break;

                        case TRAFFIC_LIGHT:

                            rule = new TrafficLightQuestTrainingProgramRule();

                            break;

                        case METRICS:

                            rule = new MetricsQuestTrainingProgramRule();

                            break;

                        case PERIMETER:

                            rule = new PerimeterQuestTrainingProgramRule();

                            break;

                        case TEXT_CAMOUFLAGE:

                            rule = new TextCamouflageTrainingProgramRule();

                            break;

                        case FRUIT_ARITHMETIC:

                            rule = new FruitArithmeticQuestTrainingProgramRule();

                            break;

                        case TIME:

                            rule = new TimeQuestTrainingProgramRule();

                            break;

                        case CHOICE:

                            rule = new ChoiceQuestTrainingProgramRule();

                            break;

                        case MISMATCH:

                            rule = new MismatchQuestTrainingProgramRule();

                            break;

                        case CURRENT_TIME:

                            rule = new CurrentTimeQuestTrainingProgramRule();

                            break;

                    }
                    if (rule != null) {
                        rule.setQuestType(questType);
                        rule.parse(gson, item.getAsJsonObject());
                        if (rule.getEnabled()) {
                            questRules.add(rule);
                        }
                    }
                } else {
                    //ignore
                }
            }
        }
    }
}