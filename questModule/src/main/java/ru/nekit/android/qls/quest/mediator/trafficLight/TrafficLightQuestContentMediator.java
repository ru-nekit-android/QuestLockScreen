package ru.nekit.android.qls.quest.mediator.trafficLight;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.pupil.avatar.PupilAvatarViewBuilder;
import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.mediator.AbstractQuestContentMediator;
import ru.nekit.android.qls.quest.types.TrafficLightType;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class TrafficLightQuestContentMediator extends AbstractQuestContentMediator {

    private TrafficLightQuestViewHolder mViewHolder;
    private QuestContext mQuestContext;

    @Override
    public void init(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mViewHolder = new TrafficLightQuestViewHolder(questContext);
        IQuest quest = questContext.getQuest();
        TrafficLightType answer = TrafficLightType.fromOrdinal((int) quest.getAnswer());
        mViewHolder.trafficRedLight.setVisibility(INVISIBLE);
        mViewHolder.trafficGreenLight.setVisibility(INVISIBLE);
        if (answer == TrafficLightType.GREEN) {
            mViewHolder.trafficGreenLight.setVisibility(VISIBLE);
        } else {
            mViewHolder.trafficRedLight.setVisibility(VISIBLE);
        }
        PupilAvatarViewBuilder.build(questContext, questContext.getPupil(),
                mViewHolder.pupilAvatarContainer);
    }

    @Override
    public View getView() {
        return mViewHolder.getView();
    }

    @Override
    public EditText getAnswerInput() {
        return null;
    }

    @Override
    public boolean includeInLayout() {
        return false;
    }

    @Override
    public void updateSize(int width, int height) {
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

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void playAnimationOnDelayedStart(int duration, @Nullable View view) {

    }
}