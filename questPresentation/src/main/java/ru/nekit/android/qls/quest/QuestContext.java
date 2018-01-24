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
import ru.nekit.android.qls.quest.answer.common.IAnswerCallback;
import ru.nekit.android.qls.quest.history.QuestHistoryItem;
import ru.nekit.android.qls.quest.persistance.QuestSaver;
import ru.nekit.android.qls.quest.persistance.QuestStatisticsSaver;
import ru.nekit.android.qls.quest.qtp.AppropriateQuestTrainingProgramRuleWrapper;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramLevel;
import ru.nekit.android.qls.quest.qtp.rule.AbstractQuestTrainingProgramRule;
import ru.nekit.android.qls.quest.resources.QuestResourceLibrary;
import ru.nekit.android.qls.quest.statistics.BaseStatistics;
import ru.nekit.android.qls.quest.statistics.PupilStatistics;
import ru.nekit.android.qls.quest.statistics.QuestStatistics;
import ru.nekit.android.qls.quest.types.CurrentTimeQuest;
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;
import ru.nekit.android.qls.quest.types.MetricsQuest;
import ru.nekit.android.qls.quest.types.PerimeterQuest;
import ru.nekit.android.qls.quest.types.TimeQuest;
import ru.nekit.android.qls.utils.MathUtils;
import ru.nekit.android.qls.utils.RobotoTypefaceUtil;
import ru.nekit.android.qls.utils.TimeUtils;
import ru.nekit.android.qls.utils.Vibrate;
import ru.nekit.android.qls.utils.ViewHolder;
import ru.nekit.android.qls.window.AnswerWindow;

import static android.content.Intent.ACTION_SCREEN_ON;
import static android.content.IntentFilter.SYSTEM_HIGH_PRIORITY;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.ANSWERED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.ATTACHED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.DELAYED_PLAY;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.PAUSED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.PLAYED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.RESTORED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.STARTED;
import static ru.nekit.android.qls.quest.QuestContext.QuestState.STOPPED;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_LEVEL_UP;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_ATTACH;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_PAUSE;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_PLAY;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_REPLAY;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_RESUME;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_START;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_QUEST_STOP;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_RIGHT_ANSWER;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_TIC_TAC;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_UPDATE_STATISTICS;
import static ru.nekit.android.qls.quest.QuestContextEvent.EVENT_WRONG_ANSWER;
import static ru.nekit.android.qls.quest.history.QuestHistoryItem.RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD;
import static ru.nekit.android.qls.quest.history.QuestHistoryItem.RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD;
import static ru.nekit.android.qls.utils.ScreenHost.isScreenOn;

public class QuestContext extends ContextThemeWrapper implements IAnswerCallback, EventBus.IEventHandler {

    public static final String NAME_SESSION_TIME = "sessionTime";
    public static final String NAME_QUEST_STATE = "quest.quest_state_0";

    private static QuestContext mInstance;
    @NonNull
    private final EventBus mEventBus;
    private Pupil mPupil;
    private Quest mQuest;
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

    private QuestContext(@NonNull Context context,
                         @NonNull EventBus eventBus,
                         @StyleRes int themeResourceId) {
        super(context, themeResourceId);
        mEventBus = eventBus;
        mQuestSaver = new QuestSaver();
        mSettingsStorage = new SettingsStorage();
        mQuestResourceLibrary = new QuestResourceLibrary(this);
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
        mQuestSaver.reset();
    }

    public static QuestContext getInstance() {
        return mInstance;
    }

    public static QuestContext createInstance(@NonNull Context context,
                                              @NonNull EventBus eventBus,
                                              @StyleRes int themeResourceId) {
        mInstance = new QuestContext(context, eventBus, themeResourceId);
        return getInstance();
    }

    //Quest state functional
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

    private void clearQuestState() {
        setQuestState(mQuestState = 0);
    }
    //End quest state functional

    private void sendTicTacEvent() {
        sendTicTacEvent(getSessionTime());
    }

    @Nullable
    public Quest getQuest() {
        return mQuest;
    }

    @NonNull
    public Quest generateQuest() {
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
                    removeQuestState(PLAYED);
                    removeQuestState(STOPPED);
                }
            }
        }
        if (mQuest == null) {
            mQuest = makeQuest();
            mQuestSaver.save(mQuest);
            clearQuestState();
        }
        if (questHasDelayedPlay()) {
            addQuestState(DELAYED_PLAY);
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
            boolean rightAnswerSeriesLengthUpdated;
            statistics.rightAnswerCount++;
            statistics.rightAnswerSummandTime += sessionTime;
            statistics.rightAnswerSeriesCounter++;
            int rightAnswerSeries = Math.max(statistics.rightAnswerSeries,
                    statistics.rightAnswerSeriesCounter);
            rightAnswerSeriesLengthUpdated = rightAnswerSeries > 1 &&
                    rightAnswerSeries > statistics.rightAnswerSeries;
            statistics.rightAnswerSeries = rightAnswerSeries;
            if (sessionTime < statistics.bestAnswerTime && statistics != mPupilStatistics &&
                    statistics.bestAnswerTime != Long.MAX_VALUE) {
                historyItem.recordType |= RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD;
                historyItem.prevBestAnswerTime = statistics.bestAnswerTime;
                statistics.bestAnswerTime = sessionTime;
            }
            statistics.worseAnswerTime = Math.max(sessionTime, statistics.worseAnswerTime);
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
            replaceQuestState(PLAYED, ANSWERED);
            long sessionTime = getSessionTime();
            QuestTrainingProgramLevel levelBeforeReward = getQTPLevel();
            //award for right answer quest obtain from quest training program rule
            AbstractQuestTrainingProgramRule rule = getQTPRule();
            QuestHistoryItem questHistoryItem = createQuestHistoryItem(true, sessionTime);
            QuestHistoryItem globalHistoryItem = createQuestHistoryItem(true, sessionTime);
            updateStatisticsAndHistoryItem(getQuestStatistics(), questHistoryItem,
                    true, sessionTime);
            updateStatisticsAndHistoryItem(mPupilStatistics, globalHistoryItem, true,
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
            mQuestHistoryPair = new QuestHistoryItem.Pair(globalHistoryItem, questHistoryItem);
            mEventBus.sendEvent(EVENT_RIGHT_ANSWER);
            //detachView
            destroyTicTac();
            mQuestSaver.reset();
            clearQuestState();
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
        if (mQuest != null && !questHasState(ANSWERED)) {
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
        mEventBus.sendEvent(QuestContextEvent.EVENT_EMPTY_ANSWER);
    }

    @Override
    public void wrongStringInputFormat() {
        mEventBus.sendEvent(QuestContextEvent.EVENT_ERROR_ANSWER);
    }

    public boolean playQuest() {
        boolean screenIsOn = isScreenOn(this);
        boolean questIsStarted = questHasState(PLAYED);
        if (screenIsOn) {
            if (!questIsStarted) {
                replaceQuestState(ATTACHED, PLAYED);
                if (questHasState(STOPPED)) {
                    mEventBus.sendEvent(EVENT_QUEST_REPLAY);
                } else {
                    mEventBus.sendEvent(EVENT_QUEST_PLAY);
                }
                destroyTicTac();
                mStartSessionTime = TimeUtils.getCurrentTime();
                mTicTacHandler.postDelayed(mTicTacRunnable,
                        questHasDelayedPlay() && CONST.PLAY_ANIMATION_ON_DELAYED_START ?
                                getQuestDelayedPlayAnimationDuration() : 0);
            }
        }
        return screenIsOn && !questIsStarted;
    }

    public void stopQuest() {
        destroyTicTac();
        if (questHasState(PLAYED)) {
            replaceQuestState(PLAYED, STOPPED);
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
        clearQuestState();
        mQuest = null;
    }

    @NonNull
    private Quest makeQuest() {
        AppropriateQuestTrainingProgramRuleWrapper ruleWrapper =
                mQuestTrainingProgram.getAppropriateQTPRule(mPupilStatistics,
                        MathUtils.randItem(QuestTrainingProgram.AppropriateType.values()));
        Quest quest = ruleWrapper.makeQuest(this);
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
            addQuestState(STARTED);
            mEventBus.sendEvent(EVENT_QUEST_START);
            if (!questHasDelayedPlay()) {
                playQuest();
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

    private boolean questHasDelayedPlay() {
        AbstractQuestTrainingProgramRule rule = getQTPRule();
        if (rule.getDelayedPlay() == -1) {
            QuestTrainingProgramLevel questTrainingProgramLevel = getQTPLevel();
            if (questTrainingProgramLevel.getDelayedPlay() == -1) {
                return CONST.VALUE_DELAYED_PLAY_BY_DEFAULT;
            }
            return questTrainingProgramLevel.getDelayedPlay() == 1;
        }
        return rule.getDelayedPlay() == 1;
    }

    public int getQuestDelayedPlayAnimationDuration() {
        return getResources().getInteger(R.integer.quest_delayed_start_animation_duration);
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        if (mQuest != null) {
            if (!questHasDelayedPlay() || questHasState(STOPPED)) {
                playQuest();
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

    public void openAnswerWindow(AnswerWindow.Type type, int style, int content, int toolContent) {
        new AnswerWindow.Builder(this, type).
                setStyle(style).
                setContent(content).
                setToolContent(toolContent).
                create().
                open();
    }

    public void openAnswerWindow(AnswerWindow.Type type, int style, ViewHolder content, int toolContent) {
        new AnswerWindow.Builder(this, type).
                setStyle(style).
                setContent(content).
                setToolContent(toolContent).
                create().
                open();
    }

    //onCreate (view builder) -> attach -> start -> play/pause/resume -> stop
    public enum QuestState {
        RESTORED,//mix
        DELAYED_PLAY,//mix
        ATTACHED,
        STARTED,
        PLAYED,
        PAUSED,
        STOPPED,
        ANSWERED;//mix

        public int value() {
            return (int) Math.pow(2, ordinal());
        }
    }
}