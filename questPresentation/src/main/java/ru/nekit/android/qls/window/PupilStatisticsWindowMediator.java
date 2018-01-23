package ru.nekit.android.qls.window;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilSex;
import ru.nekit.android.qls.pupil.avatar.PupilAvatarViewBuilder;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgram;
import ru.nekit.android.qls.quest.qtp.QuestTrainingProgramLevel;
import ru.nekit.android.qls.quest.statistics.PupilStatistics;
import ru.nekit.android.qls.quest.statistics.QuestStatistics;
import ru.nekit.android.qls.utils.ViewHolder;
import ru.nekit.android.qls.window.common.QuestWindow;
import ru.nekit.android.qls.window.common.QuestWindowMediator;

public class PupilStatisticsWindowMediator extends QuestWindowMediator implements View.OnClickListener,
        EventBus.IEventHandler {

    private static PupilStatisticsWindowMediator instance;
    private Step mCurrentStep;
    private ViewHolder mCurrentContentHolder;
    private PupilStatisticsWindowContentViewHolder mWindowContent;


    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Resources resources = mQuestContext.getResources();
                    if (mCurrentStep == Step.TITLE) {
                        PupilStatisticsTitleViewHolder contentHolder =
                                (PupilStatisticsTitleViewHolder) mCurrentContentHolder;
                        float bookTitleXScale =
                                ((float) contentHolder.backgroundImage.getWidth()) /
                                        resources.getDimensionPixelSize(R.dimen.book_title_width);
                        float bookTitleYScale =
                                ((float) contentHolder.backgroundImage.getHeight()) /
                                        resources.getDimensionPixelSize(R.dimen.book_title_height);
                        ViewGroup pupilAvatarMaskContainer = contentHolder.pupilAvatarMaskContainer;
                        pupilAvatarMaskContainer.setX(bookTitleXScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_avatar_x));
                        pupilAvatarMaskContainer.setY(bookTitleYScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_avatar_y));
                        ViewGroup.LayoutParams pupilAvatarMaskContainerLayoutParams =
                                pupilAvatarMaskContainer.getLayoutParams();
                        pupilAvatarMaskContainerLayoutParams.width = (int) (bookTitleXScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_avatar_width));
                        pupilAvatarMaskContainerLayoutParams.height = (int) (bookTitleYScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_avatar_height));
                        ViewGroup.LayoutParams pupilAvatarContainerLayoutParams =
                                contentHolder.pupilAvatarContainer.getLayoutParams();
                        float pupilAvatarScale = mQuestContext.getPupil().sex == PupilSex.BOY ?
                                (float) resources.getDimensionPixelSize(ru.nekit.android.shared.R.dimen.boy_avatar_height) /
                                        resources.getDimensionPixelSize(ru.nekit.android.shared.R.dimen.boy_avatar_width)
                                :
                                (float) resources.getDimensionPixelSize(ru.nekit.android.shared.R.dimen.girl_avatar_height) /
                                        resources.getDimensionPixelSize(ru.nekit.android.shared.R.dimen.girl_avatar_width);
                        pupilAvatarContainerLayoutParams.height =
                                (int) (pupilAvatarMaskContainerLayoutParams.width * pupilAvatarScale);
                        pupilAvatarContainerLayoutParams.width =
                                pupilAvatarMaskContainerLayoutParams.width;
                        contentHolder.pupilAvatarContainer.requestLayout();
                        TextView titleTextView = contentHolder.titleTextView;
                        titleTextView.setX(bookTitleXScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_title_x));
                        titleTextView.setY(bookTitleYScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_title_y));
                        ViewGroup.LayoutParams titleTextViewLayoutParams =
                                titleTextView.getLayoutParams();
                        titleTextViewLayoutParams.width = (int) (bookTitleXScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_title_width));
                        titleTextViewLayoutParams.height = (int) (bookTitleYScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_title_height));
                        TextView nameTextView = contentHolder.nameTextView;
                        nameTextView.setX(bookTitleXScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_name_x));
                        nameTextView.setY(bookTitleYScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_name_y));
                        ViewGroup.LayoutParams nameTextViewLayoutParams =
                                nameTextView.getLayoutParams();
                        nameTextViewLayoutParams.width = (int) (bookTitleXScale *
                                resources.getDimensionPixelSize(R.dimen.book_title_name_width));
                        mWindowContent.contentContainer.setVisibility(View.VISIBLE);
                    } else if (mCurrentStep == Step.CONTENT) {
                        MenuWindowMediator.PupilStatisticsContentViewHolder contentHolder =
                                (MenuWindowMediator.PupilStatisticsContentViewHolder) mCurrentContentHolder;
                        RelativeLayout.LayoutParams scrollerLayoutParams =
                                (RelativeLayout.LayoutParams) contentHolder.scroller.getLayoutParams();
                        int[] margin = new int[]{
                                resources.getDimensionPixelSize(R.dimen.book_content_left),
                                resources.getDimensionPixelSize(R.dimen.book_content_top),
                                resources.getDimensionPixelSize(R.dimen.book_content_right),
                                resources.getDimensionPixelSize(R.dimen.book_content_bottom)
                        };
                        float bookXScale = ((float) contentHolder.backgroundImage.getWidth()) /
                                resources.getDimensionPixelSize(R.dimen.book_content_width);
                        float bookYScale = ((float) contentHolder.backgroundImage.getHeight()) /
                                resources.getDimensionPixelSize(R.dimen.book_content_height);
                        scrollerLayoutParams.setMargins((int) (margin[0] * bookXScale),
                                (int) (margin[1] * bookYScale),
                                0,
                                (int) (margin[3] * bookYScale));
                        scrollerLayoutParams.width = (int) ((resources.getDimensionPixelSize(R.dimen.book_content_width) -
                                margin[0] - margin[2]) * bookXScale);
                        contentHolder.scroller.requestLayout();
                    }
                }
            };

    private PupilStatisticsWindowMediator(@NonNull QuestContext questContext) {
        super(questContext);
        questContext.getEventBus().handleEvents(this, QuestWindow.EVENT_WINDOW_OPENED);
    }

    public static void openWindow(@NonNull QuestContext questContext) {
        if (instance == null) {
            (instance = new PupilStatisticsWindowMediator(questContext)).openWindow();
        }
    }

    @Override
    protected WindowContentViewHolder createWindowContent() {
        mWindowContent = new PupilStatisticsWindowContentViewHolder(mQuestContext);
        mWindowContent.bookContent.setOnClickListener(this);
        mWindowContent.bookTitle.setOnClickListener(this);
        setStep(Step.TITLE);
        return mWindowContent;
    }

    private void setStep(Step step) {
        if (mCurrentStep != step) {
            if (mCurrentStep != null) {
                destroyContentForStep();
            }
            mCurrentStep = step;
            switch (step) {

                case TITLE: {

                    PupilStatisticsTitleViewHolder contentViewHolder =
                            new PupilStatisticsTitleViewHolder(mQuestContext);
                    contentViewHolder.backgroundImage.setImageResource(
                            mQuestContext.getPupil().sex == PupilSex.BOY ?
                                    R.drawable.book_title_boy :
                                    R.drawable.book_title_girl);
                    Pupil pupil = mQuestContext.getPupil();
                    PupilAvatarViewBuilder.build(mQuestContext, pupil,
                            contentViewHolder.pupilAvatarContainer);
                    contentViewHolder.titleTextView.setText(String.format("%s\n%s\n%s", pupil.name,
                            pupil.complexity.getString(mQuestContext),
                            mQuestContext.getQTPLevel().getName()));
                    contentViewHolder.nameTextView.setText(R.string.book_title_name);
                    mCurrentContentHolder = contentViewHolder;
                }

                break;

                case CONTENT:

                    mCurrentContentHolder = new MenuWindowMediator.PupilStatisticsContentViewHolder(mQuestContext);
                    MenuWindowMediator.PupilStatisticsContentViewHolder contentViewHolder =
                            (MenuWindowMediator.PupilStatisticsContentViewHolder) mCurrentContentHolder;
                    PupilStatistics pupilStatistics = mQuestContext.getPupilStatistics();
                    int score = pupilStatistics.score;
                    QuestTrainingProgram qtp = mQuestContext.getQuestTrainingProgram();
                    QuestTrainingProgramLevel currentLevel = mQuestContext.getQTPLevel();
                    int scoreOnLevel;
                    if (currentLevel.getIndex() == 0) {
                        scoreOnLevel = score;
                    } else {
                        QuestTrainingProgramLevel previousLevel =
                                qtp.getLevelByIndex(currentLevel.getIndex() - 1);
                        scoreOnLevel = score - qtp.getLevelAllPoints(previousLevel);
                    }
                    contentViewHolder.backgroundImage.setImageResource(
                            mQuestContext.getPupil().sex == PupilSex.BOY ?
                                    R.drawable.book_content_boy :
                                    R.drawable.book_content_girl);
                    StringBuilder textContent = new StringBuilder(String.format(
                            mQuestContext.getString(R.string.pupil_statistics_level_and_score_formatter),
                            currentLevel.toString(),
                            currentLevel.getDescription(),
                            scoreOnLevel,
                            currentLevel.getPointsWeight()
                    ));
                    textContent.append(String.format(
                            mQuestContext.getString(R.string.pupil_statistics_count_played_games_formatter),
                            pupilStatistics.rightAnswerCount + pupilStatistics.wrongAnswerCount));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss",
                            Locale.getDefault());
                    for (QuestStatistics statistics : pupilStatistics.questStatistics) {
                        textContent.append(String.format(
                                mQuestContext.getString(R.string.pupil_statistics_item_formatter),
                                statistics.questType.getString(mQuestContext),
                                statistics.questionType.getString(mQuestContext),
                                statistics.rightAnswerCount,
                                statistics.wrongAnswerCount,
                                dateFormat.format(statistics.bestAnswerTime),
                                dateFormat.format(statistics.worseAnswerTime)
                        ));
                    }
                    contentViewHolder.contentTextView.setText(textContent.toString());

                    break;
            }
            switchToContent(mCurrentContentHolder);
        }
    }

    @Override
    protected void onWindowContentAttach(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    @Override
    protected int getWindowStyleId() {
        return R.style.Window_PupilStatistics;
    }

    @Override
    protected void destroy() {
        mWindowContent.view.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        mWindowContent.bookContent.setOnClickListener(null);
        mWindowContent.bookTitle.setOnClickListener(null);
        mQuestContext.getEventBus().stopHandleEvents(this);
        instance = null;
    }


    private void destroyContentForStep() {
        if (mCurrentStep != null) {
            switch (mCurrentStep) {

                case TITLE:

                    break;

                case CONTENT:


                    break;
            }
        }
        mCurrentStep = null;
    }

    private void switchToContent(@NonNull ViewHolder contentHolder) {
        View content = contentHolder.view;
        boolean isFirstContent = mWindowContent.contentContainer.getChildCount() == 0;
        mWindowContent.contentContainer.removeAllViews();
        mWindowContent.contentContainer.addView(content);
        if (isFirstContent) {
            content.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mWindowContent.bookContent) {
            setStep(Step.CONTENT);
        } else if (view == mWindowContent.bookTitle) {
            setStep(Step.TITLE);
        }
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {

                case QuestWindow.EVENT_WINDOW_OPENED:

                    View view = mCurrentContentHolder.view;
                    view.setVisibility(View.VISIBLE);
                    view.setScaleX(0.1f);
                    view.setScaleY(0.1f);
                    view.animate().scaleX(1).scaleY(1).setInterpolator(new BounceInterpolator())
                            .setDuration(mQuestContext.getResources().getInteger(R.integer.long_animation_duration));

                    break;
            }
        }
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }

    private enum Step {

        TITLE,
        CONTENT

    }
}