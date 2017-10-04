package ru.nekit.android.qls.quest.mediator.types.trafficLight;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.pupil.avatar.PupilAvatarViewBuilder;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.content.AbstractQuestContentMediator;
import ru.nekit.android.qls.quest.model.TrafficLightModel;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class TrafficLightQuestContentMediator extends AbstractQuestContentMediator {

    private TrafficLightQuestViewHolder mViewHolder;

    @Override
    public void activate(@NonNull QuestContext questContext, @NonNull ViewGroup rootContentContainer) {
        super.activate(questContext, rootContentContainer);
        mViewHolder = new TrafficLightQuestViewHolder(questContext);
        TrafficLightModel answer = TrafficLightModel.fromOrdinal((int) mQuest.getAnswer());
        mViewHolder.trafficRedLight.setVisibility(INVISIBLE);
        mViewHolder.trafficGreenLight.setVisibility(INVISIBLE);
        if (answer == TrafficLightModel.GREEN) {
            mViewHolder.trafficGreenLight.setVisibility(VISIBLE);
        } else {
            mViewHolder.trafficRedLight.setVisibility(VISIBLE);
        }
        PupilAvatarViewBuilder.build(mQuestContext, mQuestContext.getPupil(),
                mViewHolder.pupilAvatarContainer);
    }

    @Override
    public void onCreateQuest() {
        super.onCreateQuest();
        updateSizeInternal();
    }

    @Override
    public void onStartQuest(boolean delayedStart) {
        updateSizeInternal();
        super.onStartQuest(delayedStart);
    }

    protected void playDelayedStartAnimation() {
        View view = getView();
        if (view != null) {
            view.setAlpha(0);
            view.animate().withLayer().alpha(1).
                    setDuration(mQuestContext.getQuestDelayedStartAnimationDuration());
        }
    }

    @Override
    public View getView() {
        return mViewHolder.getView();
    }

    @Override
    public void updateSize() {

    }

    @Override
    public EditText getAnswerInput() {
        return null;
    }

    @Override
    public boolean includeInLayout() {
        return false;
    }


    private void updateSizeInternal() {
        Resources resources = mQuestContext.getResources();
        View pupilAvatarView = mViewHolder.pupilAvatarContainer;
        float globalScale = (float) mViewHolder.trafficLightBackground.getHeight() /
                resources.getDimensionPixelSize(R.dimen.traffic_light_background_height);
        float pupilAvatarWidth = globalScale * resources.getDimensionPixelSize(R.dimen.traffic_light_avatar_pupil_width);
        float pupilAvatarScale = pupilAvatarWidth / pupilAvatarView.getWidth();
        for (int i = 0; i < mViewHolder.pupilAvatarContainer.getChildCount(); i++) {
            ImageView pupilAvatarPartImage =
                    (ImageView) mViewHolder.pupilAvatarContainer.getChildAt(i);
            pupilAvatarPartImage.setScaleX(pupilAvatarScale);
            pupilAvatarPartImage.setScaleY(pupilAvatarScale);
            pupilAvatarPartImage.setY(
                    resources.getDimensionPixelSize(R.dimen.traffic_light_quest_pupil_avatar_y) * globalScale
                            - pupilAvatarView.getHeight() * (1 + pupilAvatarScale) / 2
            );
        }
    }
}