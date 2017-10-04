package ru.nekit.android.qls.quest.window;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.window.Window;
import ru.nekit.android.qls.quest.QuestContext;

import static ru.nekit.android.qls.quest.history.QuestHistoryItem.Pair;
import static ru.nekit.android.qls.quest.history.QuestHistoryItem.RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD;
import static ru.nekit.android.qls.quest.history.QuestHistoryItem.RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD;

public class RightAnswerWindow extends Window {

    public RightAnswerWindow(@NonNull QuestContext context) {
        super(context);
    }

    public static class Builder {

        private final QuestContext mQuestContext;
        @NonNull
        private final RightAnswerWindow mWindow;
        private RightAnswerWindowContentViewHolder mContent;
        @StyleRes
        private int mStyleResId;


        public Builder(@NonNull QuestContext context) {
            mQuestContext = context;
            mWindow = new RightAnswerWindow(context);
        }

        public Builder setContent(RightAnswerWindowContentViewHolder content) {
            mContent = content;
            return this;
        }

        public Builder setContent(@LayoutRes int contentResId) {
            mContent = new RightAnswerWindowContentViewHolder(mQuestContext, contentResId);
            return this;
        }

        public Builder setStyle(@StyleRes int styleResId) {
            mStyleResId = styleResId;
            return this;
        }

        public void open() {
            Pair questHistoryPair = mQuestContext.getQuestHistoryPair();
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss.SSS",
                    Locale.getDefault());
            String text = mQuestContext.getString(R.string.congratulation_for_right_answer);
            text += "\n" + String.format(mQuestContext.getString(R.string.right_answer_time_formatter),
                    dateFormat.format(questHistoryPair.questHistory.time));
            if ((questHistoryPair.questHistory.recordType & RIGHT_ANSWER_BEST_TIME_UPDATE_RECORD) != 0) {
                text += "\n";
                text += String.format(mQuestContext.getString(R.string.right_answer_best_time_update_formatter),
                        dateFormat.format(questHistoryPair.questHistory.time));
            }
            if ((questHistoryPair.globalQuestHistory.recordType & RIGHT_ANSWER_SERIES_LENGTH_UPDATE_RECORD) != 0) {
                text += "\n";
                text += String.format(mQuestContext.getString(R.string.right_answer_series_length_update_formatter),
                        questHistoryPair.globalQuestHistory.rightAnswerSeries);
            }
            mContent.titleView.setText(text);
            mWindow.open(mContent, mStyleResId);
        }
    }
}