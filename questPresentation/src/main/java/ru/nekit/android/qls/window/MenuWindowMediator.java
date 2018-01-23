package ru.nekit.android.qls.window;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ru.nekit.android.qls.CONST;
import ru.nekit.android.qls.R;
import ru.nekit.android.qls.lockScreen.LockScreen;
import ru.nekit.android.qls.lockScreen.LockScreenMediator;
import ru.nekit.android.qls.lockScreen.service.LockScreenService;
import ru.nekit.android.qls.pupil.PhoneContact;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilSex;
import ru.nekit.android.qls.pupil.avatar.PupilAvatarViewBuilder;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.session.Session;
import ru.nekit.android.qls.session.SessionType;
import ru.nekit.android.qls.setupWizard.BaseSetupWizard;
import ru.nekit.android.qls.setupWizard.adapters.PhoneContactListener;
import ru.nekit.android.qls.setupWizard.adapters.PhoneContactsAdapterForReading;
import ru.nekit.android.qls.utils.PhoneManager;
import ru.nekit.android.qls.utils.RevealPoint;
import ru.nekit.android.qls.utils.Vibrate;
import ru.nekit.android.qls.utils.ViewHolder;
import ru.nekit.android.qls.window.common.QuestWindowMediator;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.WRONG;

public class MenuWindowMediator extends QuestWindowMediator
        implements View.OnClickListener,
        PhoneContactListener,
        PatternLockViewListener {

    private static MenuWindowMediator instance;
    private static HashMap<Step, List<Integer>> icons = new HashMap<>();

    static {
        icons.put(Step.PHONE, new ArrayList<>(Arrays.asList(R.drawable.ic_phone_24dp, R.drawable.ic_phone_36dp)));
        icons.put(Step.UNLOCK, new ArrayList<>(Arrays.asList(R.drawable.ic_lock_open_24dp, R.drawable.ic_lock_open_36dp)));
        icons.put(Step.PUPIL_STATISTICS_TITLE, new ArrayList<>(Arrays.asList(R.drawable.ic_account_24dp, R.drawable.ic_account_36dp)));
        icons.put(Step.PUPIL_STATISTICS_CONTENT, new ArrayList<>(Arrays.asList(R.drawable.ic_account_24dp, R.drawable.ic_account_36dp)));
    }

    private Step mCurrentStep;
    private ViewHolder mCurrentContentHolder;
    private MenuWindowContentViewHolder mWindowContent;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Resources resources = mQuestContext.getResources();
                    if (mCurrentStep == Step.PUPIL_STATISTICS_TITLE) {
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
                    } else if (mCurrentStep == Step.PUPIL_STATISTICS_CONTENT) {
                        PupilStatisticsContentViewHolder contentHolder =
                                (PupilStatisticsContentViewHolder) mCurrentContentHolder;
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

    private MenuWindowMediator(@NonNull QuestContext questContext) {
        super(questContext);
    }

    public static void openWindow(@NonNull QuestContext questContext) {
        openWindow(questContext, null);
    }

    public static void openWindow(@NonNull QuestContext questContext, @Nullable Step step) {
        if (instance == null) {
            instance = new MenuWindowMediator(questContext);
        }
        instance.openWindow();
        if (step != null) {
            instance.setStep(step);
        }
    }

    @Override
    public void onClick(View view) {
        Step step = (Step) view.getTag();
        if (step == Step.UNLOCK) {
            if (CONST.USE_SESSION_FOR_UNLOCK && Session.isValid(SessionType.LOCK_SCREEN)) {
                mQuestContext.getEventBus().sendEvent(LockScreenMediator.ACTION_CLOSE);
            }
        }
        setStep(step);
    }

    @Override
    protected WindowContentViewHolder createWindowContent() {
        mWindowContent = new MenuWindowContentViewHolder(mQuestContext);
        boolean phoneIsAvailable = phoneIsAvailable();
        int margin = mQuestContext.getResources().getDimensionPixelOffset(R.dimen.base_semi_gap);
        Step[] steps = Step.values();
        for (Step step : steps) {
            AppCompatImageButton button = new AppCompatImageButton(mQuestContext);
            button.setTag(step);
            button.setImageResource(icons.get(step).get(1));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            params.setMargins(margin, margin, margin, margin);
            button.setLayoutParams(params);
            mWindowContent.buttonContainer.addView(button);
            button.setOnClickListener(this);
            if (step == Step.PHONE) {
                button.setVisibility(phoneIsAvailable ? VISIBLE : GONE);
            }
            if (!step.mShow) {
                button.setVisibility(GONE);
            }
        }
        setStep(phoneIsAvailable ? Step.PHONE : Step.getByOrdinal(Step.PHONE.ordinal() + 1));
        int count = 0;
        for (int i = 0; i < mWindowContent.buttonContainer.getChildCount(); i++) {
            if (mWindowContent.buttonContainer.getChildAt(i).getVisibility() == VISIBLE) {
                count++;
            }
        }
        mWindowContent.buttonContainer.setVisibility(count == 1 ? INVISIBLE : VISIBLE);
        return mWindowContent;
    }

    @Override
    protected int getWindowStyleId() {
        return R.style.Window_Menu;
    }

    private void switchToContent(@NonNull ViewHolder contentHolder) {
        View content = contentHolder.view;
        mWindowContent.contentContainer.removeAllViews();
        mWindowContent.contentContainer.addView(content);
    }

    private void setStep(Step step) {
        if (mCurrentStep != step) {
            @StringRes int titleResID = 0;
            if (mCurrentStep != null) {
                destroyContentForStep();
            }
            mCurrentStep = step;

            switch (step) {

                case PHONE:

                    titleResID = R.string.title_phone;
                    mCurrentContentHolder = new PhoneViewHolder(mQuestContext);
                    PhoneViewHolder phoneViewHolder = (PhoneViewHolder) mCurrentContentHolder;
                    PhoneContactsAdapterForReading allowContactsAdapter =
                            new PhoneContactsAdapterForReading(getPhoneContacts(), this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mQuestContext);
                    phoneViewHolder.allowContactsListView.setAdapter(allowContactsAdapter);
                    phoneViewHolder.allowContactsListView.setLayoutManager(linearLayoutManager);

                    break;

                case UNLOCK:

                    titleResID = R.string.title_unlock_secret;
                    mCurrentContentHolder = new UnlockViewHolder(mQuestContext);
                    UnlockViewHolder unlockViewHolder = (UnlockViewHolder)
                            mCurrentContentHolder;
                    unlockViewHolder.patterLockView.addPatternLockListener(this);

                    break;

                case PUPIL_STATISTICS_TITLE:

                    titleResID = R.string.title_pupil_statistics;
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
                            pupil.complexity.getName(mQuestContext),
                            mQuestContext.getQTPLevel().getName()));
                    contentViewHolder.nameTextView.setText(R.string.book_title_name);
                    mCurrentContentHolder = contentViewHolder;

                    break;
            }
            mWindowContent.titleView.setText(mQuestContext.getResources().getString(titleResID));
            for (int i = 0; i < mWindowContent.buttonContainer.getChildCount(); i++) {
                AppCompatImageButton button = (AppCompatImageButton) mWindowContent.buttonContainer.getChildAt(i);
                Step stepOfButton = (Step) button.getTag();
                button.setImageResource(icons.get(stepOfButton).get(stepOfButton == step ? 1 : 0));
            }
            switchToContent(mCurrentContentHolder);
        }
    }

    @Override
    protected void destroy() {
        destroyContentForStep();
        mWindowContent.view.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        instance = null;
    }

    private void destroyContentForStep() {
        if (mCurrentStep != null) {
            switch (mCurrentStep) {

                case PHONE:

                    PhoneViewHolder phoneViewHolder = (PhoneViewHolder)
                            mCurrentContentHolder;
                    phoneViewHolder.allowContactsListView.setAdapter(null);
                    phoneViewHolder.allowContactsListView.setLayoutManager(null);

                    break;

                case UNLOCK:

                    UnlockViewHolder unlockViewHolder = (UnlockViewHolder)
                            mCurrentContentHolder;
                    unlockViewHolder.patterLockView.removePatternLockListener(this);

                    break;
            }
        }
        mCurrentStep = null;
    }

    private List<PhoneContact> getPhoneContacts() {
        return mQuestContext.getPupil().getAllowContacts();
    }

    private boolean phoneIsAvailable() {
        return PhoneManager.phoneIsAvailable(mQuestContext) && getPhoneContacts().size() > 0;
    }

    @Override
    public void onAction(int position) {
        PhoneContact phoneContact = getPhoneContacts().get(position);
        Intent outgoingCallIntent = new Intent(LockScreenService.EVENT_OUTGOING_CALL);
        outgoingCallIntent.putExtra(PhoneContact.NAME, phoneContact);
        mQuestContext.getEventBus().sendEvent(outgoingCallIntent);
        closeWindow(RevealPoint.POSITION_BOTTOM_CENTER);
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onProgress(List<PatternLockView.Dot> progressPattern) {

    }

    @Override
    public void onComplete(List<PatternLockView.Dot> pattern) {
        UnlockViewHolder unlockViewHolder = (UnlockViewHolder) mCurrentContentHolder;
        if (pattern.size() >= BaseSetupWizard.UNLOCK_SECRET_MIN_SIZE) {
            if (LockScreen.tryToLogin(
                    PatternLockUtils.patternToMD5(unlockViewHolder.patterLockView, pattern))) {
                closeWindow(RevealPoint.POSITION_MIDDLE_CENTER);
                mQuestContext.getEventBus().sendEvent(LockScreenMediator.ACTION_CLOSE);
                unlockViewHolder.patterLockView.clearPattern();
            } else {
                unlockViewHolder.patterLockView.setViewMode(WRONG);
                Vibrate.make(mQuestContext, 400);
            }
        } else {
            unlockViewHolder.patterLockView.setViewMode(WRONG);
            Vibrate.make(mQuestContext, 400);
        }

    }

    @Override
    public void onCleared() {

    }

    @Override
    protected void onWindowContentAttach(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    public enum Step {

        PHONE,
        UNLOCK,
        PUPIL_STATISTICS_TITLE,
        PUPIL_STATISTICS_CONTENT(false);

        boolean mShow;

        Step(boolean show) {
            mShow = show;
        }

        Step() {
            mShow = true;
        }

        public static Step getByOrdinal(int ordinal) {
            return values()[ordinal];
        }
    }

    static class MenuWindowContentViewHolder extends WindowContentViewHolder {

        final ViewSwitcher contentContainer;
        final ViewGroup buttonContainer;
        final TextView titleView;

        MenuWindowContentViewHolder(@NonNull Context context) {
            super(context, R.layout.wc_menu);
            contentContainer = (ViewSwitcher) view.findViewById(R.id.container_content);
            buttonContainer = (ViewGroup) view.findViewById(R.id.container_button);
            titleView = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    static class PhoneViewHolder extends ViewHolder {

        final RecyclerView allowContactsListView;

        PhoneViewHolder(@NonNull Context context) {
            super(context, R.layout.wsc_phone);
            allowContactsListView = (RecyclerView) view.findViewById(R.id.list_phone_contacts);
        }
    }

    static class PupilStatisticsContentViewHolder extends ViewHolder {

        @NonNull
        final AppCompatImageView backgroundImage;
        @NonNull
        final TextView contentTextView;
        @NonNull
        final ScrollView scroller;

        PupilStatisticsContentViewHolder(@NonNull Context context) {
            super(context, R.layout.wsc_pupil_statistics_content);
            backgroundImage = (AppCompatImageView) view.findViewById(R.id.view_book);
            contentTextView = (TextView) view.findViewById(R.id.tv_content);
            scroller = (ScrollView) view.findViewById(R.id.container_scroll);
        }
    }

    static class UnlockViewHolder extends ViewHolder {

        PatternLockView patterLockView;

        UnlockViewHolder(@NonNull Context context) {
            super(context, R.layout.wsc_unlock);
            patterLockView = (PatternLockView) view.findViewById(R.id.unlock_secret_view);
        }

    }
}