package ru.nekit.android.qls.window.common;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.window.Window;
import ru.nekit.android.qls.window.WindowContentViewHolder;

public abstract class QuestWindowMediator implements EventBus.IEventHandler {

    @NonNull
    protected final QuestContext mQuestContext;
    @NonNull
    private WindowContentViewHolder mWindowContentViewHolder;
    private Window mWindow;
    private View.OnAttachStateChangeListener onAttachStateChangeListener =
            new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                    onWindowContentAttach(view);
                }

                @Override
                public void onViewDetachedFromWindow(View view) {

                }
            };

    public QuestWindowMediator(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
    }

    protected abstract WindowContentViewHolder createWindowContent();

    @StyleRes
    protected abstract int getWindowStyleId();

    public final void openWindow() {
        if (mWindow == null) {
            mWindowContentViewHolder = createWindowContent();
            mWindow = new QuestWindow(mQuestContext,
                    "QuestWindowMediator",
                    mWindowContentViewHolder,
                    getWindowStyleId());
            mQuestContext.getEventBus().handleEvents(this, QuestWindow.EVENT_WINDOW_OPEN);
        }
        mWindowContentViewHolder.view.addOnAttachStateChangeListener(onAttachStateChangeListener);
        mWindow.open();
    }

    protected void onWindowContentAttach(View view) {
    }

    protected void destroy() {
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        mWindowContentViewHolder.view.removeOnAttachStateChangeListener(onAttachStateChangeListener);
    }

    protected void closeWindow(String position) {
        mWindow.close(position);
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return null;
    }
}