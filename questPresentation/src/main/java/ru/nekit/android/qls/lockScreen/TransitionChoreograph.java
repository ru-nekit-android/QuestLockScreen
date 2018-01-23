package ru.nekit.android.qls.lockScreen;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.nekit.android.qls.CONST;
import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.SettingsStorage;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestContextEvent;
import ru.nekit.android.qls.utils.CountdownCounter;

import static android.content.IntentFilter.SYSTEM_HIGH_PRIORITY;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.ADVERT;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.EMPTY_TRANSITION;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.INTRODUCTION;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.LEVEL_UP;
import static ru.nekit.android.qls.lockScreen.TransitionChoreograph.Transition.QUEST;

public class TransitionChoreograph implements EventBus.IEventHandler {

    public static final String ACTION_PLAY_ONE_MORE_TIME = "action_play_one_more_time";
    public static final String EVENT_TRANSITION_CHANGED = "event_transition_changed";
    private static final String NAME_CURRENT_TRANSITION = "name_current_transition";
    private static final String NAME_PREVIOUS_TRANSITION = "name_previous_transition";
    private static final String ADVERT_COUNTDOWN_NAME = "advert";
    private static final String QUEST_SERIES_COUNTDOWN_NAME = "quest_series";
    private static final String ADVERT_IS_SHOWN = "advert_is_shown";
    @NonNull
    private final QuestContext mQuestContext;
    @NonNull
    private final CountdownCounter mAdvertCounter;
    @NonNull
    private final CountdownCounter mQuestSeriesCounter;
    private boolean mIntroductionIsShown = false;
    private boolean mLevelUp;
    @NonNull
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            switch (intent.getAction()) {

                case QuestContextEvent.EVENT_RIGHT_ANSWER:

                    mQuestSeriesCounter.countDown();
                    if (mQuestContext.getSettingsStorage().advertIsPresented()) {
                        mAdvertCounter.countDown();
                    }
                    saveCurrentTransition(generateNextTransition());

                    break;

                case QuestContextEvent.EVENT_LEVEL_UP:

                    mLevelUp = true;

                    break;

            }
        }
    };


    TransitionChoreograph(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuestSeriesCounter = new CountdownCounter(QUEST_SERIES_COUNTDOWN_NAME,
                mQuestContext.getQuestSeriesLength());
        mAdvertCounter = new CountdownCounter(ADVERT_COUNTDOWN_NAME,
                CONST.SHOW_ADVERT_AFTER_N_RIGHT_ANSWER);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(QuestContextEvent.EVENT_RIGHT_ANSWER);
        intentFilter.addAction(QuestContextEvent.EVENT_LEVEL_UP);
        intentFilter.setPriority(SYSTEM_HIGH_PRIORITY);
        questContext.registerReceiver(mReceiver, intentFilter);
        saveTransition(NAME_PREVIOUS_TRANSITION, null);
        saveTransition(NAME_CURRENT_TRANSITION, null);
        questContext.getEventBus().handleEvents(this, ACTION_PLAY_ONE_MORE_TIME);
        //WARNING!!! only for test
        //reset();
    }

    private void playOneMoreTime() {
        mQuestSeriesCounter.countDownUp();
        saveCurrentTransition(generateNextTransition());
    }

    @Nullable
    public Transition getCurrentTransition() {
        return getTransition(NAME_CURRENT_TRANSITION);
    }

    @Nullable
    public Transition getPreviousTransition() {
        return getTransition(NAME_PREVIOUS_TRANSITION);
    }

    @Nullable
    private Transition getTransition(@NonNull String name) {
        return Transition.getByName(SettingsStorage.getString(name));
    }

    void saveCurrentTransition(@Nullable Transition transition) {
        saveTransition(NAME_PREVIOUS_TRANSITION, getCurrentTransition());
        saveTransition(NAME_CURRENT_TRANSITION, transition);
    }

    private void saveTransition(@NonNull String name, @Nullable Transition transition) {
        SettingsStorage.setString(name, transition == null ? EMPTY_TRANSITION :
                transition.name());
    }

    public void goCurrentTransition() {
        mQuestContext.sendBroadcast(new Intent(EVENT_TRANSITION_CHANGED));
    }

    Transition generateNextTransition() {
        if (mQuestContext.getSettingsStorage().introductionIsPresented()
                && !mIntroductionIsShown) {
            mIntroductionIsShown = true;
            return INTRODUCTION;
        }
        if (mQuestSeriesCounter.zeroIsReached()) {
            if (mLevelUp) {
                mLevelUp = false;
                return LEVEL_UP;
            }
        } else {
            if (mLevelUp) {
                mLevelUp = false;
                return LEVEL_UP;
            } else {
                return QUEST;
            }
        }
        if (mAdvertCounter.zeroIsReached() && !advertIsShown()) {
            mAdvertCounter.reset();
            advertIsShown(true);
            return ADVERT;
        }
        return null;
    }

    void reset() {
        mIntroductionIsShown = false;
        advertIsShown(false);
        mQuestSeriesCounter.reset();
        saveTransition(NAME_PREVIOUS_TRANSITION, null);
        saveTransition(NAME_CURRENT_TRANSITION, null);
    }

    public void goNextTransition() {
        saveCurrentTransition(generateNextTransition());
        goCurrentTransition();
    }

    private void advertIsShown(boolean value) {
        SettingsStorage.setBoolean(ADVERT_IS_SHOWN, value);
    }

    private boolean advertIsShown() {
        return SettingsStorage.getBoolean(ADVERT_IS_SHOWN);
    }

    public void destroy() {
        mQuestContext.unregisterReceiver(mReceiver);
        Transition currentTransition = getCurrentTransition(),
                lastTransition = Transition.values()[Transition.values().length - 1];
        if (currentTransition == null || currentTransition == lastTransition) {
            reset();
        }
    }

    public int getQuestSeriesCounterValue() {
        return mQuestSeriesCounter.getValue();
    }

    public boolean questSeriesIsComplete() {
        return getQuestSeriesCounterValue() == 0;
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        playOneMoreTime();
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return "TRANSITION_CHOREOGRAPH";
    }

    public enum Transition {
        INTRODUCTION,
        QUEST,
        LEVEL_UP,
        ADVERT;

        static final String EMPTY_TRANSITION = "";

        @Nullable
        static Transition getByName(String name) {
            if (EMPTY_TRANSITION.equals(name) || name == null) {
                return null;
            }
            for (Transition transition : values()) {
                if (transition.name().equals(name)) {
                    return transition;
                }
            }
            return null;
        }
    }
}