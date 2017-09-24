package ru.nekit.android.qls.setupWizard.view;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilManager;
import ru.nekit.android.qls.pupil.PupilSex;
import ru.nekit.android.qls.pupil.avatar.IPupilAvatarPart;
import ru.nekit.android.qls.pupil.avatar.PupilAvatarConverter;
import ru.nekit.android.qls.pupil.avatar.PupilBoyAvatarPart;
import ru.nekit.android.qls.pupil.avatar.PupilGirlAvatarPart;
import ru.nekit.android.qls.utils.MathUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SetupPupilAvatarFragment extends QuestSetupWizardFragment
        implements View.OnLayoutChangeListener {

    protected int[] mPupilPartVariantCurrentPosition;
    private List<ImageView> mAvatarPartImageList;
    private List<View> mPrevButtonList, mNextButtonList;
    private Pupil mPupil;
    private View.OnClickListener mNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int avatarPartPosition = (int) view.getTag();
            updateAvatarPart(getAvatarPartAndVariantPositionList(avatarPartPosition, true));
        }
    };
    private View.OnClickListener mPrevClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int avatarPartPosition = (int) view.getTag();
            updateAvatarPart(getAvatarPartAndVariantPositionList(avatarPartPosition, false));
        }
    };

    public static SetupPupilAvatarFragment getInstance() {
        return new SetupPupilAvatarFragment();
    }

    protected IPupilAvatarPart[] getAvatarParts() {
        return mPupil.sex == PupilSex.BOY ?
                PupilBoyAvatarPart.values() : PupilGirlAvatarPart.values();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_setup_pupil_avatar;
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        PupilManager pupilManager = new PupilManager();
        //noinspection ConstantConditions
        mPupil = pupilManager.getCurrentPupil();
        mAvatarPartImageList = new ArrayList<>();
        mPrevButtonList = new ArrayList<>();
        mNextButtonList = new ArrayList<>();
        setNextButtonText(R.string.label_create);
        ViewGroup contentContainer = (ViewGroup) view.findViewById(R.id.container_content);
        IPupilAvatarPart[] avatarParts = getAvatarParts();
        final int length = avatarParts.length;
        mPupilPartVariantCurrentPosition = new int[length];
        for (int avatarPartPosition = 0; avatarPartPosition < length; avatarPartPosition++) {
            mPupilPartVariantCurrentPosition[avatarPartPosition] = 0;
            ImageView avatarPartImage = new ImageView(getContext());
            IPupilAvatarPart avatarPart = getAvatarParts()[avatarPartPosition];
            avatarPartImage.setImageResource(avatarPart.getVariant(0));
            //horizontal
            RelativeLayout partImageContainer = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams avatarPartImageLayoutParams =
                    new RelativeLayout.LayoutParams(MATCH_PARENT,
                            MATCH_PARENT);
            avatarPartImage.setLayoutParams(avatarPartImageLayoutParams);
            ImageButton prevButton = new ImageButton(getContext());
            prevButton.setImageResource(R.drawable.ic_keyboard_arrow_left_black_36px);
            prevButton.setTag(avatarPartPosition);
            prevButton.setOnClickListener(mPrevClickListener);
            ImageButton nextButton = new ImageButton(getContext());
            nextButton.setTag(avatarPartPosition);
            nextButton.setOnClickListener(mNextClickListener);
            nextButton.setImageResource(R.drawable.ic_keyboard_arrow_right_black_36px);
            RelativeLayout.LayoutParams partImageContainerLayoutParams =
                    new RelativeLayout.LayoutParams(MATCH_PARENT,
                            MATCH_PARENT);
            partImageContainer.setLayoutParams(partImageContainerLayoutParams);
            FrameLayout prevButtonContainer = new FrameLayout(getContext());
            FrameLayout.LayoutParams prevButtonContainerLayoutParams =
                    new FrameLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
            prevButtonContainer.setLayoutParams(prevButtonContainerLayoutParams);
            FrameLayout.LayoutParams prevButtonLayoutParams =
                    new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            prevButtonContainer.setLayoutParams(prevButtonContainerLayoutParams);
            prevButton.setLayoutParams(prevButtonLayoutParams);
            prevButtonContainer.addView(prevButton);
            FrameLayout nextButtonContainer = new FrameLayout(getContext());
            RelativeLayout.LayoutParams nextButtonContainerLayoutParams =
                    new RelativeLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
            nextButtonContainerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            nextButtonContainer.setLayoutParams(nextButtonContainerLayoutParams);
            FrameLayout.LayoutParams nextButtonLayoutParams =
                    new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            nextButton.setLayoutParams(nextButtonLayoutParams);
            nextButtonContainer.addView(nextButton);
            partImageContainer.addView(prevButtonContainer);
            partImageContainer.addView(nextButtonContainer);
            partImageContainer.addView(avatarPartImage);
            contentContainer.addView(partImageContainer);
            partImageContainer.requestLayout();
            mAvatarPartImageList.add(avatarPartImage);
            mPrevButtonList.add(prevButton);
            mNextButtonList.add(nextButton);
        }
        setAltButtonVisibility(true);
        setAltButtonText(R.string.label_set_random_avatar);
        view.addOnLayoutChangeListener(this);
    }

    @Override
    protected boolean nextButtonAction() {
        getSetupWizard().setPupilAvatar(PupilAvatarConverter.toString(getAvatarParts(),
                mPupilPartVariantCurrentPosition));
        return true;
    }

    private void update(boolean choiced) {
        setNextButtonVisibility(choiced);
    }

    @Override
    public void onDestroyView() {
        assert getView() != null;
        getView().removeOnLayoutChangeListener(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        for (int i = 0; i < mAvatarPartImageList.size(); i++) {
            View prevButton = mPrevButtonList.get(i);
            View nextButton = mNextButtonList.get(i);
            prevButton.setOnClickListener(null);
            nextButton.setOnClickListener(null);
        }
        super.onDestroy();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        int avatarPartPosition = 0;
        for (View imageAvatarPart : mAvatarPartImageList) {
            float scale = (float) imageAvatarPart.getHeight() /
                    getResources().getDimensionPixelSize(mPupil.sex == PupilSex.BOY ?
                            R.dimen.boy_avatar_height : R.dimen.girl_avatar_height);
            int avatarPartTopPosition = getAvatarParts()[avatarPartPosition].getY();
            View prevButton = mPrevButtonList.get(avatarPartPosition);
            View nextButton = mNextButtonList.get(avatarPartPosition);
            if (avatarPartTopPosition != 0) {
                float y = getResources().getDimensionPixelSize(avatarPartTopPosition) * scale;
                prevButton.setY(y);
                nextButton.setY(y);
            } else {
                prevButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
            }
            avatarPartPosition++;
        }
    }

    private List<Pair<Integer, Integer>> getAvatarPartAndVariantPositionList(int avatarPartPosition,
                                                                             boolean increment) {
        List<Pair<Integer, Integer>> avatarPartVariantPositionList = new ArrayList<>();
        int avatarPartVariantPosition = mPupilPartVariantCurrentPosition[avatarPartPosition];
        if (increment) {
            avatarPartVariantPosition++;
        } else {
            avatarPartVariantPosition--;
        }
        IPupilAvatarPart avatarPart = getAvatarParts()[avatarPartPosition];
        IPupilAvatarPart[] dependentList = avatarPart.getDependentItems();
        if (dependentList != null) {
            for (IPupilAvatarPart dependentItem : dependentList) {
                int avatarPartPositionLocal = 0;
                for (IPupilAvatarPart searchingItem : getAvatarParts()) {
                    if (searchingItem.equals(dependentItem)) {
                        break;
                    }
                    avatarPartPositionLocal++;
                }
                avatarPartVariantPositionList.addAll(getAvatarPartAndVariantPositionList(
                        avatarPartPositionLocal,
                        increment));
            }
        }
        if (increment) {
            if (avatarPartVariantPosition >= avatarPart.getVariantCount()) {
                avatarPartVariantPosition = 0;
            }
        } else {
            if (avatarPartVariantPosition < 0) {
                avatarPartVariantPosition = avatarPart.getVariantCount() - 1;
            }
        }
        avatarPartVariantPositionList.add(new Pair<>(avatarPartPosition, avatarPartVariantPosition));
        return avatarPartVariantPositionList;
    }

    private void updateAvatarPart(List<Pair<Integer, Integer>> avatarPartAndVariantPositionList) {
        for (Pair<Integer, Integer> avatarPartAndVariantPositionItem : avatarPartAndVariantPositionList) {
            IPupilAvatarPart avatarPart = getAvatarParts()[avatarPartAndVariantPositionItem.first];
            mPupilPartVariantCurrentPosition[avatarPartAndVariantPositionItem.first] = avatarPartAndVariantPositionItem.second;
            @DrawableRes int partId = avatarPart.getVariant(avatarPartAndVariantPositionItem.second);
            mAvatarPartImageList.get(avatarPartAndVariantPositionItem.first).setImageResource(partId);
        }
    }

    @Override
    protected void altButtonAction() {
        randomAvatar();
    }

    private void randomAvatar() {
        IPupilAvatarPart[] avatarParts = getAvatarParts();
        final int length = avatarParts.length;
        int[] resultVariantPositions = new int[length];
        for (int avatarPartPosition = 0; avatarPartPosition < length; avatarPartPosition++) {
            resultVariantPositions[avatarPartPosition] = -1;
        }
        for (int avatarPartPosition = 0; avatarPartPosition < length; avatarPartPosition++) {
            if (resultVariantPositions[avatarPartPosition] == -1) {
                IPupilAvatarPart avatarPart = getAvatarParts()[avatarPartPosition];
                IPupilAvatarPart[] dependentList = avatarPart.getDependentItems();
                int variantPosition = MathUtils.randUnsignedInt(avatarPart.getVariantCount() - 1);
                resultVariantPositions[avatarPartPosition] = variantPosition;
                if (dependentList != null) {
                    for (IPupilAvatarPart dependentItem : dependentList) {
                        int avatarPartPositionLocal = 0;
                        for (IPupilAvatarPart searchingItem : getAvatarParts()) {
                            if (searchingItem.equals(dependentItem)) {
                                break;
                            }
                            avatarPartPositionLocal++;
                        }
                        resultVariantPositions[avatarPartPositionLocal] = variantPosition;
                    }
                } else {
                    resultVariantPositions[avatarPartPosition] = variantPosition;
                }
            }
        }
        for (int avatarPartPosition = 0; avatarPartPosition < length; avatarPartPosition++) {
            Pair<Integer, Integer> pair = new Pair<>(avatarPartPosition,
                    resultVariantPositions[avatarPartPosition]);
            List<Pair<Integer, Integer>> pairList = new ArrayList<>();
            pairList.add(pair);
            updateAvatarPart(pairList);
        }
    }
}