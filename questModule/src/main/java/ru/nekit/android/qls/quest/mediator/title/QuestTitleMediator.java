package ru.nekit.android.qls.quest.mediator.title;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.base.Quest;
import ru.nekit.android.qls.quest.model.CoinModel;
import ru.nekit.android.qls.quest.model.ColorModel;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.PerimeterQuest;
import ru.nekit.android.qls.quest.types.TextQuest;
import ru.nekit.android.qls.quest.types.TimeQuest;
import ru.nekit.android.qls.quest.types.VisualRepresentationalNumberSummandQuest;
import ru.nekit.android.qls.utils.ViewHolder;

import static ru.nekit.android.qls.quest.base.IGroupWeightComparisonQuest.MAX_GROUP_WEIGHT;

public class QuestTitleMediator implements IQuestTitleMediator {

    protected QuestContext mQuestContext;
    protected ViewGroup mRootContentContainer;
    protected Quest mQuest;
    private QuestTitleViewHolder mViewHolder;
    private String mTitleText;

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
                            mTitleText = String.format("Какой монеты не хватает, если в стопке %s рублей?", nominationSum);

                            break;

                        case SOLUTION:

                            mTitleText = "Сколько рублей в стопке монет?";

                            break;
                    }

                    break;

                case SIMPLE_EXAMPLE:

                    switch (questionType) {

                        case UNKNOWN_MEMBER:

                            mTitleText = "Найдите неизвестный член!";

                            break;

                        case SOLUTION:

                            mTitleText = "Решите пример!";

                            break;

                        case COMPARISON:

                            mTitleText = "Неравенство!";

                            break;

                        case UNKNOWN_OPERATION:

                            mTitleText = "Какой знак нужно выбрать, чтобы решить пример!";

                            break;
                    }

                    break;

                case METRICS:

                    switch (questionType) {

                        case SOLUTION:

                            mTitleText = "Сколько в сантиметрах!";

                            break;

                        case COMPARISON:

                            mTitleText = "Длинее, равно или короче";

                            break;

                    }

                    break;

                case PERIMETER:

                    PerimeterQuest perimeterQuest = (PerimeterQuest) mQuest;

                    switch (questionType) {

                        case SOLUTION:


                            mTitleText = String.format("Найдите периметр %sа!", perimeterQuest.getFigureName(questContext));

                            break;

                        case UNKNOWN_MEMBER:

                            mTitleText = String.format("Найдите сторону %s, если периметр равен %s см.", mQuestContext.getString(R.string.unknown_side), perimeterQuest.getPerimeter());

                            break;

                    }

                    break;

                case TRAFFIC_LIGHT:

                    switch (questionType) {

                        case SOLUTION:

                            mTitleText = String.format("%s или %s", mQuestContext.getString(R.string.wait), mQuestContext.getString(R.string.go));

                            break;

                    }

                    break;

                case FRUIT_ARITHMETIC:

                    FruitArithmeticQuest fruitArithmeticQuest = (FruitArithmeticQuest) mQuest;

                    switch (questionType) {

                        case SOLUTION:

                            mTitleText = "Фруктовая арифметика";

                            break;

                        case COMPARISON:

                            mTitleText = String.format("Фруктовая арифметика: каких фруктов  %s?",
                                    fruitArithmeticQuest.getGroupComparisonType() == MAX_GROUP_WEIGHT ? "больше" : "меньше");


                            break;

                    }

                    break;

                case TIME:

                    TimeQuest timeQuest = (TimeQuest) mQuest;

                    switch (questionType) {

                        case UNKNOWN_MEMBER:

                            mTitleText = String.format("Выберите циферблат, на которых изображено %s",
                                    timeQuest.getUnknownTimeString());

                            break;

                        case COMPARISON:

                            mTitleText = String.format("Выберите циферблат, на которых изображено %s время",
                                    timeQuest.getGroupComparisonType() == MAX_GROUP_WEIGHT ? "максимальное" : "минимальное");

                            break;

                    }

                    break;

                case CURRENT_TIME:

                    mTitleText = "Выберите циферблат с текущем временем";

                    break;

                case CHOICE:

                    switch (questionType) {

                        case UNKNOWN_MEMBER:

                            mTitleText = String.format("Выберите %s",
                                    questResourceLibrary.getVisualResourceItem(
                                            numberSummandQuest.getUnknownValue()).
                                            getTitle(mQuestContext));

                            break;

                    }

                    break;

                case MISMATCH:

                    mTitleText = "Найди лишнее";

                    break;

                case CURRENT_SEASON:

                    mTitleText = "Выберите текущее время года";

                    break;


                case COLORS:

                    VisualRepresentationalNumberSummandQuest visualRepresentationalNumberSummandQuest
                            = (VisualRepresentationalNumberSummandQuest) mQuest;
                    int unknownIndex = visualRepresentationalNumberSummandQuest.unknownMemberIndex;
                    mTitleText = String.format("Выберите %s %s",
                            ColorModel.getById(visualRepresentationalNumberSummandQuest.leftNode[unknownIndex]).getTitle(questContext),
                            questResourceLibrary.getVisualResourceItem(visualRepresentationalNumberSummandQuest.getVisualRepresentationList().get(unknownIndex)).getTitle(questContext)
                    );

                    break;
            }
        } else if (mQuest instanceof TextQuest) {
            TextQuest textQuest = (TextQuest) mQuest;
            mTitleText = String.format("Составте слово идя по спирале и выберая каждую %s-ую букву",
                    textQuest.questionStringArray[0].length());
        }
        mViewHolder.titleView.setText(mTitleText);
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
    public boolean onRightAnswer() {
        return true;
    }

    @Override
    public boolean onWrongAnswer() {
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
        return mViewHolder.getView();
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
            titleView = (TextView) mView.findViewById(R.id.tv_title);
        }

    }
}