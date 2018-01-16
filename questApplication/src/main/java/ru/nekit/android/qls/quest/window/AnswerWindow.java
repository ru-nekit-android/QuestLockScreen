package ru.nekit.android.qls.quest.window;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.window.Window;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.utils.ViewHolder;

public class AnswerWindow extends Window {

    private AnswerWindow(@NonNull QuestContext questContext, @NonNull String name) {
        super(questContext);
        mName = name;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public enum Variant {

        RIGHT,
        WRONG;

        public String getName() {
            return name();
        }
    }

    public static class Builder {

        private final QuestContext mQuestContext;
        @NonNull
        private final Variant mVariant;
        @NonNull
        private AnswerWindow mWindow;
        @Nullable
        private AnswerWindowContainer mWindowContainer;
        @Nullable
        private ViewHolder mContentHolder, mToolContentHolder;
        @StyleRes
        private int mStyleResId = -1;

        public Builder(@NonNull QuestContext context, @NonNull Variant variant) {
            mQuestContext = context;
            mVariant = variant;
        }

        @NonNull
        public Builder setContent(@NonNull ViewHolder content) {
            mContentHolder = content;
            return this;
        }

        @NonNull
        public Builder setContent(@LayoutRes int contentResId) {
            mContentHolder = new ViewHolder(mQuestContext, contentResId);
            return this;
        }

        @NonNull
        public Builder setToolContent(@NonNull ViewHolder content) {
            mToolContentHolder = content;
            return this;
        }

        @NonNull
        public Builder setToolContent(@LayoutRes int toolContentResId) {
            mToolContentHolder = new ViewHolder(mQuestContext, toolContentResId);
            return this;
        }

        @NonNull
        public Builder setStyle(@StyleRes int styleResId) {
            mStyleResId = styleResId;
            return this;
        }

        @NonNull
        public AnswerWindow create() {
            mWindow = new AnswerWindow(mQuestContext, mVariant.getName());
            mWindowContainer = new AnswerWindowContainer(mQuestContext);
            if (mContentHolder != null) {
                mWindowContainer.contentContainer.addView(mContentHolder.view);
            }
            if (mToolContentHolder != null) {
                mWindowContainer.toolContainer.addView(mToolContentHolder.view);
            }
            mWindow.mContent = mWindowContainer;
            mWindow.mStyleResId = mStyleResId == -1 ? mVariant == Variant.RIGHT ?
                    R.style.Window_RightAnswer : R.style.Window_WrongAnswer : mStyleResId;
            return mWindow;
        }
    }
}