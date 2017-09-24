package ru.nekit.android.qls.quest.qtp;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilSex;
import ru.nekit.android.qls.quest.QuestType;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.persistance.PupilSaver;
import ru.nekit.android.qls.quest.qtp.rule.AbstractQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.statistics.PupilStatistics;
import ru.nekit.android.qls.quest.statistics.QuestStatistics;
import ru.nekit.android.qls.utils.MathUtils;

public class QuestTrainingProgram implements Parcelable {

    public static final Creator<QuestTrainingProgram> CREATOR = new Creator<QuestTrainingProgram>() {
        @Override
        public QuestTrainingProgram createFromParcel(Parcel in) {
            return new QuestTrainingProgram(in);
        }

        @Override
        public QuestTrainingProgram[] newArray(int size) {
            return new QuestTrainingProgram[size];
        }
    };

    public static final int REWARD_BY_DEFAULT = 10;
    private static final String QTP_FOLDER_NAME = "questTrainingProgramResources";
    private static final String QTP_PRIORITY_FILE_NAME = "quest_priority_rule";
    private static final String QTP_FILE_EXT = "json";
    private static final String QTP_FILE_BASE_NAME = "qtp";
    private static final String QTP_FILE_NAME_SEPARATOR = "_";
    private float version;
    private QuestTrainingProgramComplexity complexity;
    private String name, description;
    private List<QuestTrainingProgramLevel> levels;
    private PupilSex sex;
    private List<QuestRulePriority> mQuestRulePriorities;

    public QuestTrainingProgram() {
    }

    protected QuestTrainingProgram(Parcel in) {
        name = in.readString();
        description = in.readString();
        version = in.readFloat();
        complexity = QuestTrainingProgramComplexity.valueOf(in.readString());
        sex = PupilSex.valueOf(in.readString());
        levels = in.createTypedArrayList(QuestTrainingProgramLevel.CREATOR);
        mQuestRulePriorities = in.createTypedArrayList(QuestRulePriority.CREATOR);
    }

    @NonNull
    public static QuestTrainingProgram buildForCurrentPupil(
            @NonNull Context context) {
        QuestTrainingProgram questTrainingProgram = new QuestTrainingProgram();
        PupilSaver pupilSaver = new PupilSaver();
        Pupil pupil = pupilSaver.restore();
        questTrainingProgram.buildForPupil(context, pupil);
        return questTrainingProgram;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeFloat(version);
        dest.writeString(complexity.name());
        dest.writeString(sex.name());
        dest.writeTypedList(levels);
        dest.writeTypedList(mQuestRulePriorities);
    }

    private QuestTrainingProgram build(@NonNull Context context, @NonNull PupilSex sex,
                                       @NonNull QuestTrainingProgramComplexity complexity) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        AssetManager assetManager = context.getAssets();
        String fullQTPFilePath = QTP_FOLDER_NAME +
                "/" +
                getTrainingProgramResourcePath(sex, complexity) +
                "." +
                QTP_FILE_EXT;
        InputStream qtpStream;
        InputStream qtpPriorityStream;
        try {
            qtpStream = assetManager.open(fullQTPFilePath);
            qtpPriorityStream = assetManager.open(QTP_FOLDER_NAME +
                    "/" +
                    QTP_PRIORITY_FILE_NAME +
                    "." +
                    QTP_FILE_EXT);

            if (qtpStream != null && qtpPriorityStream != null) {
                JsonParser jsonParser = new JsonParser();
                JsonObject qtpObject = jsonParser.parse(
                        new JsonReader(new InputStreamReader(qtpStream))).getAsJsonObject();
                JsonArray qPriorityArray = jsonParser.parse(
                        new JsonReader(new InputStreamReader(qtpPriorityStream))).getAsJsonArray();
                version = qtpObject.get(Dictionary.VERSION).getAsFloat();
                name = qtpObject.get(Dictionary.NAME).getAsString();
                description = qtpObject.get(Dictionary.DESCRIPTION).getAsString();
                this.sex = gson.fromJson(qtpObject.get(Dictionary.SEX), PupilSex.class);
                this.complexity =
                        gson.fromJson(qtpObject.get(Dictionary.COMPLEXITY),
                                QuestTrainingProgramComplexity.class);
                if (qtpObject.has(Dictionary.LEVELS)) {
                    JsonArray QTPLs = qtpObject.get(Dictionary.LEVELS).getAsJsonArray();
                    levels = new ArrayList<>(QTPLs.size());
                    for (JsonElement item : QTPLs) {
                        QuestTrainingProgramLevel level = new QuestTrainingProgramLevel();
                        level.parse(context, item.getAsJsonObject());
                        level.index = levels.size();
                        levels.add(level);
                    }
                }
                mQuestRulePriorities = new ArrayList<>();
                for (JsonElement item : qPriorityArray) {
                    JsonObject questPriorityObject = item.getAsJsonObject();
                    QuestRulePriority questRulePriority = new QuestRulePriority();
                    QuestType questType =
                            gson.fromJson(questPriorityObject.get(Dictionary.QUEST_TYPE),
                                    QuestType.class);
                    questRulePriority.questType = questType;
                    questRulePriority.startPriority =
                            questPriorityObject.get(Dictionary.START_PRIORITY).getAsFloat();
                    questRulePriority.wrongAnswerPriority =
                            questPriorityObject.get(Dictionary.WRONG_ANSWER_PRIORITY).getAsFloat();
                    JsonArray questionTypes =
                            questPriorityObject.get(Dictionary.QUESTION_TYPES).getAsJsonArray();
                    if (questionTypes.size() == 1 &&
                            Dictionary.ALL.equals(questionTypes.get(0).getAsString().toLowerCase())) {
                        questRulePriority.questionTypes =
                                Arrays.asList(questType.getSupportQuestionTypes());
                    } else {
                        questRulePriority.questionTypes = new ArrayList<>();
                        for (JsonElement questionTypeItem : questionTypes) {
                            questRulePriority.questionTypes.add(gson.fromJson(
                                    questionTypeItem.getAsJsonObject(), QuestionType.class));
                        }
                    }
                    mQuestRulePriorities.add(questRulePriority);
                }
                qtpPriorityStream.close();
                qtpStream.close();
                return this;
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public QuestTrainingProgram buildForPupil(@NonNull Context context, @NonNull Pupil pupil) {
        assert pupil.sex != null;
        assert pupil.complexity != null;
        return build(context, pupil.sex, pupil.complexity);
    }

    private String getTrainingProgramResourcePath(@Nullable PupilSex sex, @Nullable QuestTrainingProgramComplexity complexity) {
        StringBuilder resourceNameBuilder = new StringBuilder(QTP_FILE_BASE_NAME);
        if (sex != null) {
            resourceNameBuilder.append(QTP_FILE_NAME_SEPARATOR);
            resourceNameBuilder.append(sex.name().toLowerCase());
        }
        if (complexity != null) {
            resourceNameBuilder.append(QTP_FILE_NAME_SEPARATOR);
            resourceNameBuilder.append(complexity.name().toLowerCase());
        }
        return resourceNameBuilder.toString();
    }

    private int getTrainingProgramResourceId(@NonNull Context context, @Nullable PupilSex sex,
                                             @Nullable QuestTrainingProgramComplexity complexity) {
        return context.getResources().getIdentifier(
                getTrainingProgramResourcePath(sex, complexity), "raw",
                context.getPackageName());
    }

    public AbstractQuestTrainingProgramRule getRandomRuleByStatistics(PupilStatistics pupilStatistics) {
        QuestTrainingProgramLevel currentLevel = getCurrentLevel(pupilStatistics);
        int length = currentLevel.questRules.size();
        return currentLevel.questRules.get(MathUtils.randUnsignedInt(length - 1));
    }

    public AppropriateQuestTrainingProgramRuleWrapper getAppropriateRuleChanceByStatistics(PupilStatistics pupilStatistics) {
        List<AppropriateQuestTrainingProgramRuleWrapper> appropriateQuestTrainingProgramRuleWrapperList = createQuestTrainingProgramRuleChanceList(pupilStatistics);
        for (QuestStatistics statisticsQuestAndQuestionTypePair : pupilStatistics.questStatistics) {
            for (AppropriateQuestTrainingProgramRuleWrapper appropriateQuestTrainingProgramRuleWrapper : appropriateQuestTrainingProgramRuleWrapperList) {
                if (statisticsQuestAndQuestionTypePair.questType == appropriateQuestTrainingProgramRuleWrapper.questType && statisticsQuestAndQuestionTypePair.questionType == appropriateQuestTrainingProgramRuleWrapper.questionType) {
                    int wrongAnswerCount = statisticsQuestAndQuestionTypePair.wrongAnswerCount;
                    int rightAnswerCount = statisticsQuestAndQuestionTypePair.rightAnswerCount;
                    QuestRulePriority questRulePriority = getQuestRulePriorityByQuestAndQuestionType(statisticsQuestAndQuestionTypePair.questType, statisticsQuestAndQuestionTypePair.questionType);
                    appropriateQuestTrainingProgramRuleWrapper.chanceValue =
                            questRulePriority.computePriority(rightAnswerCount, wrongAnswerCount);
                }
            }
        }
        int total = 0;
        for (AppropriateQuestTrainingProgramRuleWrapper appropriateQuestTrainingProgramRuleWrapper : appropriateQuestTrainingProgramRuleWrapperList) {
            total += appropriateQuestTrainingProgramRuleWrapper.chanceValue;
        }
        int randomValue = MathUtils.randUnsignedInt(total);
        total = 0;
        AppropriateQuestTrainingProgramRuleWrapper resultAppropriateQuestTrainingProgramRuleWrapper = null;
        for (AppropriateQuestTrainingProgramRuleWrapper appropriateQuestTrainingProgramRuleWrapper : appropriateQuestTrainingProgramRuleWrapperList) {
            appropriateQuestTrainingProgramRuleWrapper.lowerValue = total;
            total += appropriateQuestTrainingProgramRuleWrapper.chanceValue;
            appropriateQuestTrainingProgramRuleWrapper.upperValue = total - 1;
        }
        for (AppropriateQuestTrainingProgramRuleWrapper appropriateQuestTrainingProgramRuleWrapper : appropriateQuestTrainingProgramRuleWrapperList) {
            if (randomValue >= appropriateQuestTrainingProgramRuleWrapper.lowerValue && randomValue <= appropriateQuestTrainingProgramRuleWrapper.upperValue) {
                resultAppropriateQuestTrainingProgramRuleWrapper = appropriateQuestTrainingProgramRuleWrapper;
                break;
            }
        }
        if (resultAppropriateQuestTrainingProgramRuleWrapper == null) {
            resultAppropriateQuestTrainingProgramRuleWrapper =
                    appropriateQuestTrainingProgramRuleWrapperList.get(0);
        }
        return resultAppropriateQuestTrainingProgramRuleWrapper;
    }

    @NonNull
    private QuestRulePriority
    getQuestRulePriorityByQuestAndQuestionType(@NonNull QuestType questType,
                                               @NonNull QuestionType questionType) {
        for (QuestRulePriority questRulePriority : mQuestRulePriorities) {
            if (questRulePriority.questType == questType) {
                for (QuestionType questionTypeItem : questRulePriority.questionTypes) {
                    if (questionTypeItem == questionType) {
                        return questRulePriority;
                    }
                }
            }
        }
        return new QuestRulePriority(questType, questionType);
    }

    private List<AppropriateQuestTrainingProgramRuleWrapper>
    createQuestTrainingProgramRuleChanceList(@NonNull PupilStatistics pupilStatistics) {
        List<AppropriateQuestTrainingProgramRuleWrapper> list = new ArrayList<>();
        QuestTrainingProgramLevel currentLevel = getCurrentLevel(pupilStatistics);
        for (AbstractQuestTrainingProgramRule rule : currentLevel.questRules) {
            for (QuestionType questionType : rule.getQuestionTypes()) {
                list.add(new AppropriateQuestTrainingProgramRuleWrapper(rule, rule.getQuestType(),
                        questionType, getQuestRulePriorityByQuestAndQuestionType(rule.getQuestType(),
                        questionType).startPriority));
            }
        }
        return list;
    }

    @NonNull
    private QuestTrainingProgramLevel getCurrentLevel(int score) {
        int pointsWeight = 0;
        QuestTrainingProgramLevel currentLevel = null;
        for (QuestTrainingProgramLevel level : levels) {
            currentLevel = level;
            pointsWeight += level.pointsWeight;
            if (pointsWeight >= score) {
                return level;
            }
        }
        //for reach max points
        //noinspection ConstantConditions
        return currentLevel;
    }

    @NonNull
    public QuestTrainingProgramLevel getCurrentLevel(PupilStatistics pupilStatistics) {
        return getCurrentLevel(pupilStatistics.score);
    }

    private int getLevelAllPoints(int score) {
        return getLevelAllPoints(getCurrentLevel(score));
    }

    public int getLevelAllPoints(@NonNull QuestTrainingProgramLevel level) {
        int pointsWeight = 0;
        for (QuestTrainingProgramLevel item : levels) {
            pointsWeight += level.pointsWeight;
            if (item == level) {
                break;
            }
        }
        return pointsWeight;
    }

    public int getLevelAllPoints(PupilStatistics pupilStatistics) {
        return getLevelAllPoints(pupilStatistics.score);
    }

    @Nullable
    public AbstractQuestTrainingProgramRule findQTPRuleByQuestAndQuestionType(PupilStatistics pupilStatistics, QuestType questType, QuestionType questionType) {
        QuestTrainingProgramLevel currentLevel = getCurrentLevel(pupilStatistics);
        for (AbstractQuestTrainingProgramRule rule : currentLevel.questRules) {
            if (rule.getQuestType() == questType) {
                for (QuestionType questionTypeItem : rule.getQuestionTypes()) {
                    if (questionTypeItem == questionType) {
                        return rule;
                    }
                }
            }
        }
        return null;
    }

    public QuestTrainingProgramLevel getLastLevel() {
        return getLevelByIndex(levels.size() - 1);
    }

    public QuestTrainingProgramLevel getLevelByIndex(int index) {
        return levels.get(index);
    }

    public float getVersion() {
        return version;
    }

    public class Dictionary {
        public final static String ENABLED = "enabled";
        public final static String QUESTION_TYPES = "questionTypes";
        public final static String TYPES = "types";
        public final static String REWARD = "reward";
        public final static String MEMBER_COUNTS = "memberCounts";
        public final static String MEMBER_COUNT = "memberCount";
        public final static String ACCURACY = "accuracy";
        public final static String EACH_MEMBER_MIN_AND_MAX_VALUES = "eachMemberMinAndMaxValues";
        public final static String MEMBER_MIN_AND_MAX_VALUES = "memberMinAndMaxValues";
        public final static String FLAGS = "flags";
        public final static String ALL = "all";
        public static final String WORD_LENGTH = "wordLength";
        public static final String CAMOUFLAGE_LENGTH = "camouflageLength";
        public static final String DELAYED_START = "delayedStart";
        public static final String ANSWER_VARIANTS = "answerVariants";
        final static String VERSION = "version";
        final static String NAME = "name";
        final static String DESCRIPTION = "description";
        final static String SEX = "sex";
        final static String COMPLEXITY = "complexity";
        final static String LEVELS = "levels";
        final static String QUEST_TYPE = "questType";
        final static String POINTS_MULTIPLIER = "pointsMultiplier";
        final static String POINTS_WEIGHT = "pointsWeight";
        final static String RULES = "quests";
        final static String START_PRIORITY = "startPriority";
        final static String WRONG_ANSWER_PRIORITY = "wrongAnswerPriority";

    }
}