package ru.nekit.android.qls.quest.window;


import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;

import ru.nekit.android.qls.lockScreen.window.Window;
import ru.nekit.android.qls.lockScreen.window.WindowContentViewHolder;
import ru.nekit.android.qls.quest.QuestContext;

abstract class WindowMediator implements Window.WindowListener {

    @NonNull
    protected final QuestContext mQuestContext;
    @NonNull
    private final WindowContentViewHolder mWindowContentViewHolder;
    Window mWindow;
    private Window.WindowListener mWindowListener;
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

    WindowMediator(@NonNull QuestContext questContext,
                   @NonNull Window.WindowListener windowListener) {
        mQuestContext = questContext;
        mWindowListener = windowListener;
        mWindowContentViewHolder = createWindowContent();
    }

    protected abstract WindowContentViewHolder createWindowContent();

    @StyleRes
    protected abstract int getWindowStyleId();

    final void openWindow() {
        mWindow = createWindow();
        mWindow.setWindowListener(this);
        mWindow.open();
    }

    private Window createWindow() {
        mWindowContentViewHolder.getView().addOnAttachStateChangeListener(onAttachStateChangeListener);
        return new Window(mQuestContext, mWindowContentViewHolder, getWindowStyleId());
    }

    protected void onWindowContentAttach(View view) {
    }

    @Override
    final public void onWindowOpened(@NonNull Window window) {
        mWindowListener.onWindowOpened(window);
    }

    @Override
    final public void onWindowClosed(@NonNull Window window, boolean internal) {
        mWindowListener.onWindowClosed(window, internal);
        mWindowContentViewHolder.getView().removeOnAttachStateChangeListener(onAttachStateChangeListener);
        destroy();
    }

    @Override
    public void onWindowOpen(@NonNull Window window) {
        mWindowListener.onWindowOpen(window);
    }

    @Override
    public void onWindowClose(@NonNull Window window, boolean internal) {
        mWindowListener.onWindowClose(window, internal);
    }

    protected void destroy() {

    }

}