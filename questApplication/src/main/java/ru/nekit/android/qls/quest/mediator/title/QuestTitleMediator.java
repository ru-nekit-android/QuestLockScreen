package ru.nekit.android.qls.quest.mediator.title;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.answer.common.AnswerType;
import ru.nekit.android.qls.quest.common.Quest;
import ru.nekit.android.qls.quest.model.CoinModel;
import ru.nekit.android.qls.quest.model.ColorModel;
import ru.nekit.android.qls.quest.model.DirectionModel;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.PerimeterQuest;
import ru.nekit.android.qls.quest.types.TextQuest;
import ru.nekit.android.qls.quest.types.TimeQuest;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.utils.ViewHolder;

import static java.lang.String.format;
import static ru.nekit.android.qls.R.string.go;
import static ru.nekit.android.qls.R.string.greater;
import static ru.nekit.android.qls.R.string.less;
import static ru.nekit.android.qls.R.string.maximum;
import static ru.nekit.android.qls.R.string.minimum;
import static ru.nekit.android.qls.R.string.quest_choice_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_coins_solution_title;
import static ru.nekit.android.qls.R.string.quest_coins_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_colors_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_current_season_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_current_time_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_direction_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_fruit_arithmetic_comparison_title;
import static ru.nekit.android.qls.R.string.quest_fruit_arithmetic_solution_title;
import static ru.nekit.android.qls.R.string.quest_metrics_comparison_title;
import static ru.nekit.android.qls.R.string.quest_metrics_solution_title;
import static ru.nekit.android.qls.R.string.quest_mismatch_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_perimeter_solution_title;
import static ru.nekit.android.qls.R.string.quest_perimeter_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_simple_example_comparison_title;
import static ru.nekit.android.qls.R.string.quest_simple_example_solution_title;
import static ru.nekit.android.qls.R.string.quest_simple_example_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_simple_example_unknown_operation_title;
import static ru.nekit.android.qls.R.string.quest_text_camouflage_title;
import static ru.nekit.android.qls.R.string.quest_time_comparison_title;
import static ru.nekit.android.qls.R.string.quest_time_unknown_member_title;
import static ru.nekit.android.qls.R.string.quest_traffic_light_solution_title;
import static ru.nekit.android.qls.R.string.unknown_side;
import static ru.nekit.android.qls.R.string.wait;
import static ru.nekit.android.qls.quest.common.IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT;

public class QuestTitleMediator implements IQuestTitleMediator {

    protected QuestContext mQuestContext;
    protected ViewGroup mRootContentContainer;
    protected Quest mQuest;
    private QuestTitleViewHolder mViewHolder;
    private String mTitleText;

    private String getString(@StringRes int resId) {
        return mQuestContext.getString(resId);
    }

    @Override
    public void onCreate(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuest = questContext.getQuest();
        QuestionType questionType = mQuest.getQuestionType();
        mViewHolder = new QuestTitleViewHolder(questContext);
        QuestResourceLibrary questResourceLibrary = questContext.getQuestResourceLibrary();
        mTitleText = "";
        if (mQuest instanceof NumberSummandQuest) {
            NumberSummandQuest numberSummandQuest = (NumberSummandQuest) mQuest;
            switch (mQuest.getQuestType()) {

                case COINS:

                    switch (questionType) {

                        case UNKNOWN_MEMBER:

                            int nominationSum = 0;
                            for (int id : numberSummandQuest.leftNode) {
                                nominationSum += CoinModel.getById(id).nomination;
                            }
                            mTitleText = format(getString(quest_coins_unknown_member_title), nominationSum);

                            break;

                        case SOLUTION:

                            mTitleText = getString(quest_coins_solution_title);

                            break;
                    }

                    break;

                case SIMPLE_EXAMPLE:

                    switch (questionType) {

                        case UNKNOWN_MEMBER:

                            mTitleText = getString(quest_simple_example_unknown_member_title);

                            break;

                        case SOLUTION:

                            mTitleText = getString(quest_simple_example_solution_title);

                            break;

                        case COMPARISON:

                            mTitleText = getString(quest_simple_example_comparison_title);

                            break;

                        case UNKNOWN_OPERATION:

                            mTitleText =
                                    getString(quest_simple_example_unknown_operation_title);

                            break;
                    }

                    break;

                case METRICS:

                    switch (questionType) {

                        case SOLUTION:

                            mTitleText = getString(quest_metrics_solution_title);

                            break;

                        case COMPARISON:

                            mTitleText = getString(quest_metrics_comparison_title);

                            break;

                    }

                    break;

                case PERIMETER:

                    PerimeterQuest perimeterQuest = (PerimeterQuest) mQuest;

                    switch (questionType) {

                        case SOLUTION:


                            mTitleText = format(getString(quest_perimeter_solution_title), perimeterQuest.getFigureName(questContext));

                            break;

                        case UNKNOWN_MEMBER:

                            mTitleText = format(getString(quest_perimeter_unknown_member_title), getString(unknown_side), perimeterQuest.getPerimeter());

                            break;

                    }

                    break;

                case TRAFFIC_LIGHT:

                    switch (questionType) {

                        case SOLUTION:

                            mTitleText = format(getString(quest_traffic_light_solution_title),
                                    getString(wait), getString(go));

                            break;

                    }

                    break;

                case FRUIT_ARITHMETIC:

                    FruitArithmeticQuest fruitArithmeticQuest = (FruitArithmeticQuest) mQuest;

                    switch (questionType) {

                        case SOLUTION:

                            mTitleText = getString(quest_fruit_arithmetic_solution_title);

                            break;

                        case COMPARISON:

                            mTitleText = format(getString(quest_fruit_arithmetic_comparison_title),
                                    fruitArithmeticQuest.getGroupComparisonType() == MAX_GROUP_WEIGHT ?
                                            getString(greater).toLowerCase()
                                            : getString(less).toLowerCase());


                            break;

                    }

                    break;

                case TIME:

                    TimeQuest timeQuest = (TimeQuest) mQuest;

                    switch (questionType) {

                        case UNKNOWN_MEMBER:

                            mTitleText = format(getString(quest_time_unknown_member_title),
                                    timeQuest.getUnknownTimeString());

                            break;

                        case COMPARISON:

                            mTitleText = format(getString(quest_time_comparison_title),
                                    timeQuest.getGroupComparisonType() == MAX_GROUP_WEIGHT ?
                                            getString(maximum) : getString(minimum));

                            break;

                    }

                    break;

                case CURRENT_TIME:

                    mTitleText = getString(quest_current_time_unknown_member_title);

                    break;

                case CHOICE:

                    switch (questionType) {

                        case UNKNOWN_MEMBER:

                            mTitleText = format(getString(quest_choice_unknown_member_title),
                                    questResourceLibrary.getVisualResourceItem(
                                            numberSummandQuest.getUnknownMember()).
                                            getTitle(mQuestContext));

                            break;

                    }

                    break;

                case MISMATCH:

                    mTitleText = getString(quest_mismatch_unknown_member_title);

                    break;

                case CURRENT_SEASON:

                    mTitleText = getString(quest_current_season_unknown_member_title);

                    break;


                case COLORS:

                    VisualRepresentationalNumberSummandQuest visualRepresentationalNumberSummandQuest
                            = (VisualRepresentationalNumberSummandQuest) mQuest;
                    int unknownIndex = visualRepresentationalNumberSummandQuest.unknownMemberIndex;
                    mTitleText = format(getString(quest_colors_unknown_member_title),
                            ColorModel.getById(visualRepresentationalNumberSummandQuest.leftNode[unknownIndex]).getTitle(questContext),
                            questResourceLibrary.getVisualResourceItem(visualRepresentationalNumberSummandQuest.getVisualRepresentationList().get(unknownIndex)).getTitle(questContext)
                    );

                    break;

                case DIRECTION:

                    mTitleText = format(getString(quest_direction_unknown_member_title),
                            DirectionModel.fromOrdinal(numberSummandQuest.getAnswer()).getTitle(questContext));

                    break;
            }
        } else if (mQuest instanceof TextQuest) {
            TextQuest textQuest = (TextQuest) mQuest;
            mTitleText = format(getString(quest_text_camouflage_title),
                    textQuest.questionStringArray[0].length());
        }
        mViewHolder.titleView.setText(mTitleText);
        mViewHolder.view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQuestAttach(@NonNull ViewGroup rootContentContainer) {
        mRootContentContainer = rootContentContainer;
    }

    @Override
    public void onQuestStart(boolean delayedPlay) {

    }

    @Override
    public void onQuestPlay(boolean delayedPlay) {

    }

    @Override
    public boolean onAnswer(@NonNull AnswerType answerType) {
        if (answerType == AnswerType.RIGHT) {
            mViewHolder.view.setVisibility(View.INVISIBLE);
        }
        return true;
    }

    @Override
    public void onQuestPause() {

    }

    @Override
    public void onQuestResume() {

    }

    @Override
    public void onQuestStop() {

    }

    @Override
    public final void deactivate() {

    }

    @Override
    public void onQuestReplay() {

    }

    @Override
    @CallSuper
    public void detachView() {
    }

    @Override
    public View getView() {
        return mViewHolder.view;
    }

    @Override
    public void updateSize() {
    }

    @Override
    public String getTitle() {
        return mTitleText;
    }

    static class QuestTitleViewHolder extends ViewHolder {

        @NonNull
        final TextView titleView;

        QuestTitleViewHolder(@NonNull Context context) {
            super(context, R.layout.layout_quest_title);
            titleView = (TextView) view.findViewById(R.id.tv_title);
        }

    }
}