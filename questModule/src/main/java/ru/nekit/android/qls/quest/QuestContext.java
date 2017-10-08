package ru.nekit.android.qls.quest;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoButton;

import ru.nekit.android.qls.CONST;
import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.SettingsStorage;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilManager;
import ru.nekit.android.qls.quest.answer.shared.IAnswerCallback;
import ru.nekit.android.qls.quest.history.QuestHistoryItem;
import ru.nekit.android.qls.quest.persistance.QuestSaver;
import ru.nekit.android.qls.quest.persistance.QuestStatisticsSaver;
import ru.nekit.android.qls.quest.qtp.AppropriateQuestTrainingProgramRuleWrapper;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramLevel;
import ru.nekit.android.qls.quest.qtp.rule.AbstractQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.statistics.BaseStatistics;
import ru.nekit.android.qls.quest.statistics.PupilStatistics;
import ru.nekit.android.qls.quest.statistics.QuestStatistics;
import ru.nekit.android.qls.quest.types.CurrentTimeQuest;
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;
import ru.nekit.android.qls.quest.types.MetricsQuest;
import ru.nekit.android.qls.quest.types.PerimeterQuest;
import ru.nekit.android.qls.quest.types.TimeQuest;
import ru.nekit.android.qls.utils.RobotoTypefaceUtil;
import ru.nekit.android.qls.utils.ScreenHost;
import ru.nekit.android.qls.utils.TimeUtils;
import ru.nekit.android.qls.utils.Vibrate;

import static android.content.Intent.ACTION_SCREEN_ON;
import static android.content.IntentFilter.SYSTEM_HIGH_PRIORITY;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.ANSWERED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.ATTACHED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_START;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.PAUSED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.RESTORED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.SHOWN;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.STARTED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.STOPPED;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_LEVEL_UP;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_ATTACH;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_PAUSE;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_RESTART;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_RESUME;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_SHOW;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_START;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_STOP;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_RIGHT_ANSWER;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_TIC_TAC;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_UPDATE_STATISTICS;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_WRONG_ANSWER;
import static ru.nekit.android.qls.quest.history.QuestHistoryItem.RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD;
import static ru.nekit.android.qls.quest.history.QuestHistoryItem.RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD;

public class QuestContext extends ContextThemeWrapper implements IAnswerCallback, EventBus.IEventHandler {

    public static final String NAME_SESSION_TIME = "sessionTime";
    public static final String NAME_QUEST_STATE = "quest.quest_state_0";

    @NonNull
    private final EventBus mEventBus;
    private Pupil mPupil;
    private IQuest mQuest;
    private QuestSaver mQuestSaver;
    private QuestTrainingProgram mQuestTrainingProgram;
    private QuestStatisticsSaver mQuestStatisticsSaver;
    private QuestHistoryItem.Pair mQuestHistoryPair;
    @NonNull
    private PupilStatistics mPupilStatistics;
    private int mQuestState;

    private long mStartSessionTime;
    private Handler mTicTacHandler;
    private SettingsStorage mSettingsStorage;
    private Runnable mTicTacRunnable = new Runnable() {
        @Override
        public void run() {
            sendTicTacEvent();
            mTicTacHandler.postDelayed(mTicTacRunnable, 1000);
        }
    };
    private QuestResourceLibrary mQuestResourceLibrary;

    public QuestContext(@NonNull Context context, @NonNull EventBus eventBus, @StyleRes int themeResourceId) {
        super(context, themeResourceId);
        mEventBus = eventBus;
        mQuestSaver = new QuestSaver();
        mSettingsStorage = new SettingsStorage();
        mQuestResourceLibrary = new QuestResourceLibrary(context);
        mQuestStatisticsSaver = new QuestStatisticsSaver();
        PupilManager pupilManager = new PupilManager();
        mPupil = pupilManager.getCurrentPupil();
        if (mQuestStatisticsSaver.hasSavedState()) {
            mPupilStatistics = mQuestStatisticsSaver.restore();
        } else {
            mPupilStatistics = new PupilStatistics();
        }
        mTicTacHandler = new Handler();
        mEventBus.handleEvent(this, ACTION_SCREEN_ON, SYSTEM_HIGH_PRIORITY);
        //WARNING!!! only for test
        //mQuestSaver.reset();
    }

    //AbstractQuest state functional
    private void setQuestState(int stateValue) {
        PreferencesUtil.setInt(NAME_QUEST_STATE, stateValue);
    }

    public boolean questHasState(QuestState state) {
        return (mQuestState & state.value()) != 0;
    }

    private void addQuestState(QuestState state) {
        setQuestState(mQuestState |= state.value());
    }

    private void removeQuestState(QuestState state) {
        setQuestState(mQuestState &= ~state.value());
    }

    private void replaceQuestState(QuestState stateForRemove, QuestState stateForAdd) {
        removeQuestState(stateForRemove);
        addQuestState(stateForAdd);
    }

    private void clearQuesState() {
        setQuestState(mQuestState = 0);
    }
    //End quest state functional

    private void sendTicTacEvent() {
        sendTicTacEvent(getSessionTime());
    }

    @NonNull
    public IQuest getQuest() {
        //try to restore if mQuest == null
        if (mQuest == null && mQuestSaver.hasSavedState()) {
            //restore - @Nullable mQuest
            mQuest = mQuestSaver.restore();
            //if quest can be restored -> quest is not null
            if (mQuest != null) {
                //if quest training program rule is exist set quest state as restored
                if (getQTPRule() == null) {
                    mQuest = null;
                } else {
                    addQuestState(RESTORED);
                    removeQuestState(STARTED);
                    removeQuestState(STOPPED);
                }
            }
        }
        if (mQuest == null) {
            mQuest = makeQuest();
            mQuestSaver.save(mQuest);
            clearQuesState();
        }
        if (questHasDelayedStart()) {
            addQuestState(DELAYED_START);
        }
        return mQuest;
    }

    public Pupil getPupil() {
        return mPupil;
    }

    public long getSessionTime() {
        return TimeUtils.getCurrentTime() - mStartSessionTime;
    }

    private QuestHistoryItem createQuestHistoryItem(boolean isRightAnswer,
                                                    long sessionTime) {
        QuestHistoryItem questHistoryItem = new QuestHistoryItem();
        questHistoryItem.pupilUuid = mPupil.getUuid();
        questHistoryItem.questType = mQuest.getQuestType();
        questHistoryItem.questionType = mQuest.getQuestionType();
        questHistoryItem.isRightAnswer = isRightAnswer;
        questHistoryItem.time = sessionTime;
        questHistoryItem.timeStamp = TimeUtils.getCurrentTime();
        return questHistoryItem;
    }

    private void updateStatisticsAndHistoryItem(@NonNull BaseStatistics statistics,
                                                QuestHistoryItem historyItem,
                                                boolean isRightAnswer,
                                                long sessionTime) {
        if (isRightAnswer) {
            boolean rightAnswerSeriesLengthUpdated, bestTimeUpdated;
            statistics.rightAnswerCount++;
            statistics.rightAnswerSummandTime += sessionTime;
            statistics.rightAnswerSeriesCounter++;
            int rightAnswerSeries = Math.max(statistics.rightAnswerSeries,
                    statistics.rightAnswerSeriesCounter);
            rightAnswerSeriesLengthUpdated = rightAnswerSeries > 1 &&
                    rightAnswerSeries > statistics.rightAnswerSeries;
            statistics.rightAnswerSeries = rightAnswerSeries;
            long bestAnswerTime = Math.min(sessionTime, statistics.bestAnswerTime);
            bestTimeUpdated = statistics != mPupilStatistics &&
                    bestAnswerTime < statistics.bestAnswerTime &&
                    statistics.bestAnswerTime != Long.MAX_VALUE;
            long prevBestAnswerTime = statistics.bestAnswerTime;
            statistics.bestAnswerTime = bestAnswerTime;
            statistics.worseAnswerTime = Math.max(sessionTime, statistics.worseAnswerTime);
            if (bestTimeUpdated) {
                historyItem.recordType |= RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD;
                historyItem.prevBestAnswerTime = prevBestAnswerTime;
            }
            if (rightAnswerSeriesLengthUpdated) {
                historyItem.recordType |= RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD;
            }
            historyItem.rightAnswerSeries = rightAnswerSeries;
        } else {
            statistics.wrongAnswerCount++;
            statistics.rightAnswerSeriesCounter = 0;
        }
    }

    @Override
    public void rightAnswer() {
        if (mQuest != null && !questHasState(ANSWERED)) {
            replaceQuestState(STARTED, ANSWERED);
            long sessionTime = getSessionTime();
            QuestTrainingProgramLevel levelBeforeReward = getQTPLevel();
            //award for right answer quest obtain from quest training program rule
            AbstractQuestTrainingProgramRule rule = getQTPRule();
            QuestHistoryItem questHistoryItem = createQuestHistoryItem(true, sessionTime);
            QuestHistoryItem globalQuestHistoryItem = createQuestHistoryItem(true, sessionTime);
            updateStatisticsAndHistoryItem(getQuestStatistics(), questHistoryItem,
                    true, sessionTime);
            updateStatisticsAndHistoryItem(mPupilStatistics, globalQuestHistoryItem, true,
                    sessionTime);
            if (rule != null) {
                mPupilStatistics.score += rule.getReward() * levelBeforeReward.getPointsMultiplier();
            }
            saveStatisticsAndNotify();
            QuestTrainingProgramLevel levelAfterReward = getQTPLevel();
            if (levelBeforeReward.getIndex() < levelAfterReward.getIndex()) {
                mEventBus.sendEvent(EVENT_LEVEL_UP);
                questHistoryItem.isLevelUp = true;
            }
            mQuestHistoryPair = new QuestHistoryItem.Pair(globalQuestHistoryItem, questHistoryItem);
            mEventBus.sendEvent(EVENT_RIGHT_ANSWER);
            //detachView
            destroyTicTac();
            mQuestSaver.reset();
            clearQuesState();
            mQuest = null;
        }
    }

    public EventBus getEventBus() {
        return mEventBus;
    }

    private void saveStatisticsAndNotify() {
        mQuestStatisticsSaver.save(mPupilStatistics, mPupil.getUuid());
        mEventBus.sendEvent(EVENT_UPDATE_STATISTICS);
    }

    public QuestHistoryItem.Pair getQuestHistoryPair() {
        return mQuestHistoryPair;
    }

    @Override
    public void wrongAnswer() {
        if (!questHasState(ANSWERED)) {
            long sessionTime = getSessionTime();
            QuestHistoryItem questHistoryItem = createQuestHistoryItem(false, sessionTime);
            QuestHistoryItem globalQuestHistoryItem = createQuestHistoryItem(false, sessionTime);
            updateStatisticsAndHistoryItem(getQuestStatistics(), questHistoryItem, false,
                    sessionTime);
            updateStatisticsAndHistoryItem(mPupilStatistics, globalQuestHistoryItem, false, sessionTime);
            saveStatisticsAndNotify();
            mQuestHistoryPair = new QuestHistoryItem.Pair(globalQuestHistoryItem, questHistoryItem);
            mEventBus.sendEvent(EVENT_WRONG_ANSWER);
            Vibrate.make(this, 800);
        }
    }

    @Nullable
    private QuestStatistics findQuestStatistics() {
        for (QuestStatistics statistics :
                mPupilStatistics.questStatistics) {
            if (statistics.questType == mQuest.getQuestType()
                    && statistics.questionType == mQuest.getQuestionType()) {
                return statistics;
            }
        }
        return null;
    }

    @NonNull
    public QuestStatistics getQuestStatistics() {
        QuestStatistics questStatistics = findQuestStatistics();
        if (questStatistics == null) {
            questStatistics =
                    new QuestStatistics(mQuest.getQuestType(),
                            mQuest.getQuestionType());
            mPupilStatistics.questStatistics.add(questStatistics);
        }
        return questStatistics;
    }

    @Override
    public void emptyAnswer() {
        Vibrate.make(this, 400);
    }

    @Override
    public void wrongStringInputFormat() {
        Vibrate.make(this, 400);
    }

    public boolean startQuest() {
        boolean screenIsOn = ScreenHost.isScreenOn(this);
        boolean questIsStarted = questHasState(STARTED);
        if (screenIsOn) {
            if (!questIsStarted) {
                replaceQuestState(ATTACHED, STARTED);
                if (questHasState(STOPPED)) {
                    mEventBus.sendEvent(EVENT_QUEST_RESTART);
                } else {
                    mEventBus.sendEvent(EVENT_QUEST_START);
                }
                destroyTicTac();
                mStartSessionTime = TimeUtils.getCurrentTime();
                mTicTacHandler.postDelayed(mTicTacRunnable,
                        questHasDelayedStart() && CONST.PLAY_ANIMATION_ON_DELAYED_START ?
                                getQuestDelayedStartAnimationDuration() : 0);
            }
        }
        return screenIsOn && !questIsStarted;
    }

    public void stopQuest() {
        destroyTicTac();
        if (questHasState(STARTED)) {
            replaceQuestState(STARTED, STOPPED);
            mEventBus.sendEvent(EVENT_QUEST_STOP);
            sendTicTacEvent(0);
        }
    }

    private void sendTicTacEvent(long value) {
        if (!questHasState(PAUSED)) {
            mEventBus.sendEvent(EVENT_TIC_TAC, NAME_SESSION_TIME, value);
        }
    }

    private void destroyTicTac() {
        mTicTacHandler.removeCallbacks(mTicTacRunnable);
    }

    public void destroy() {
        destroyTicTac();
        clearQuesState();
        mQuest = null;
    }

    @NonNull
    private IQuest makeQuest() {
        AppropriateQuestTrainingProgramRuleWrapper ruleWrapper =
                mQuestTrainingProgram.getAppropriateRuleChanceByStatistics(mPupilStatistics);
        IQuest quest = ruleWrapper.makeQuest(this);
        quest.setQuestType(ruleWrapper.questType);
        switch (quest.getQuestType()) {

            case METRICS:

                quest = MetricsQuest.convert(quest);

                break;

            case PERIMETER:

                quest = new PerimeterQuest(quest);

                break;

            case FRUIT_ARITHMETIC:

                quest = new FruitArithmeticQuest(this, quest, ruleWrapper.getQtpRule());

                break;

            case TIME:

                quest = new TimeQuest(quest);

                break;

            case CURRENT_TIME:

                quest = new CurrentTimeQuest(quest);

                break;

        }
        return quest;
    }

    public void attachQuest() {
        if (mQuest != null) {
            addQuestState(ATTACHED);
            mEventBus.sendEvent(EVENT_QUEST_ATTACH);
        }
    }

    public void showAndStartQuestIfAble() {
        if (mQuest != null) {
            addQuestState(SHOWN);
            mEventBus.sendEvent(EVENT_QUEST_SHOW);
            if (!questHasDelayedStart()) {
                startQuest();
            }
        }
    }

    /*public IQuest getRandomQuest() {
        AbstractQuestTrainingProgramRule rule = getQuestTrainingProgram().getRandomRuleByStatistics(mPupilStatistics);
        IQuest quest = rule.makeQuestGeneratorForRandomQuestionType().generate();
        quest.setQuestType(rule.questType);
        return convert(quest);
    }*/

    private void updateQuestTrainingProgramVersion(QuestTrainingProgram questTrainingProgram) {
        float currentVersion = mSettingsStorage.getQuestTrainingProgramVersion();
        float newVersion = questTrainingProgram.getVersion();
        if (currentVersion < newVersion) {
            mQuestSaver.reset();
            mSettingsStorage.setQuestTrainingProgramVersion(newVersion);
        }
    }

    public PupilStatistics getPupilStatistics() {
        return mPupilStatistics;
    }

    private void resetSessionTimer() {
        mStartSessionTime = TimeUtils.getCurrentTime();
        sendTicTacEvent();
    }

    public TextView setUpFonts(@NonNull TextView texView, int styleResourceId) {
        RobotoTypefaceUtil.setUpTypeface(this, texView, styleResourceId);
        return texView;
    }

    @NonNull
    public Button createButton(@StyleRes int textStyleResourceId) {
        Button button = new RobotoButton(this);
        setUpFonts(button, textStyleResourceId);
        return button;
    }

    @NonNull
    public Button createButton() {
        return createButton(R.style.Button);
    }

    public boolean answerButtonVisible() {
        TypedArray ta = obtainStyledAttributes(R.style.Quest, R.styleable.QuestStyle);
        boolean showAnswerButton = ta.getBoolean(R.styleable.QuestStyle_answerButtonVisible, false);
        ta.recycle();
        return showAnswerButton;
    }

    public QuestResourceLibrary getQuestResourceLibrary() {
        return mQuestResourceLibrary;
    }

    public int getQuestSeriesLength() {
        return mSettingsStorage.getQuestSeriesLength();
    }

    public SettingsStorage getSettingsStorage() {
        return mSettingsStorage;
    }

    public QuestTrainingProgram getQuestTrainingProgram() {
        return mQuestTrainingProgram;
    }

    public void setQuestTrainingProgram(@Nullable QuestTrainingProgram questTrainingProgram) {
        if (questTrainingProgram == null) {
            questTrainingProgram = QuestTrainingProgram.buildForCurrentPupil(this);
        }
        updateQuestTrainingProgramVersion(questTrainingProgram);
        mQuestTrainingProgram = questTrainingProgram;
    }

    public AbstractQuestTrainingProgramRule getQTPRule() {
        return mQuestTrainingProgram.findQTPRuleByQuestAndQuestionType(
                mPupilStatistics, mQuest.getQuestType(), mQuest.getQuestionType());
    }

    public QuestTrainingProgramLevel getQTPLevel() {
        return mQuestTrainingProgram.getCurrentLevel(mPupilStatistics);
    }

    private boolean questHasDelayedStart() {
        AbstractQuestTrainingProgramRule rule = getQTPRule();
        if (rule.getDelayedStart() == -1) {
            QuestTrainingProgramLevel questTrainingProgramLevel = getQTPLevel();
            if (questTrainingProgramLevel.getDelayedStart() == -1) {
                return CONST.VALUE_DELAYED_START_BY_DEFAULT;
            }
            return questTrainingProgramLevel.getDelayedStart() == 1;
        }
        return rule.getDelayedStart() == 1;
    }

    public int getQuestDelayedStartAnimationDuration() {
        return getResources().getInteger(R.integer.quest_delayed_start_animation_duration);
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        if (mQuest != null) {
            if (!questHasDelayedStart() || questHasState(STOPPED)) {
                startQuest();
            }
        }
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }

    public void pauseQuest() {
        resetSessionTimer();
        addQuestState(PAUSED);
        mEventBus.sendEvent(EVENT_QUEST_PAUSE);
    }

    public void resumeQuest() {
        resetSessionTimer();
        removeQuestState(PAUSED);
        mEventBus.sendEvent(EVENT_QUEST_RESUME);
    }

    //create (view builder) -> attach -> show -> start -> [pause -> resume] -> stop
    public enum QuestState {
        RESTORED,//mix
        DELAYED_START,//mix
        ATTACHED,
        SHOWN,
        STARTED,
        STOPPED,
        PAUSED,
        ANSWERED;//mix

        public int value() {
            return (int) Math.pow(2, ordinal());
        }
    }
}