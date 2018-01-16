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
    public void onCreate(@NonNull QuestContext questContext) {
        super.onCreate(questContext);
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
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        super.onQuestAttach(rootContentContainer);
        updateSizeInternal();
    }

    @Override
    public void onQuestPlay(boolean delayedPlay) {
        updateSizeInternal();
        super.onQuestPlay(delayedPlay);
    }

    @Override
    protected void playDelayedStartAnimation() {
        //override for no start delay animation
    }

    @Override
    public View getView() {
        return mViewHolder.view;
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