package ru.nekit.android.qls.quest.mediator;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.resourceLibrary.QuestResourceLibrary;
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.PerimeterQuest;
import ru.nekit.android.qls.quest.types.TextQuest;
import ru.nekit.android.qls.quest.types.TimeQuest;

import static ru.nekit.android.qls.quest.types.IComparisionTypeQuest.COMPARISON_TYPE_MAX;

public class QuestTitleMediator implements IQuestTitleMediator {

    private QuestContext mQuestContext;
    private IQuest mQuest;
    private QuestTitleViewHolder mViewHolder;
    private String mTitleText;
    private boolean mIsDestroyed;

    @Override
    public void init(@NonNull QuestContext questContext) {
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

                            mTitleText = String.format("Какой монеты не хватает, если в стопке %s рублей?", numberSummandQuest.getLeftNodeSum());

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


                            mTitleText = String.format("Найдите периметр %sа!", perimeterQuest.getFigureName(mQuestContext));

                            break;

                        case UNKNOWN_MEMBER:

                            mTitleText = String.format("Найдите сторону %s, если периметр равен %s см.", questContext.getString(R.string.unknown_side), perimeterQuest.getPerimeter());

                            break;

                    }

                    break;

                case TRAFFIC_LIGHT:

                    switch (questionType) {

                        case SOLUTION:

                            mTitleText = String.format("%s или %s", questContext.getString(R.string.wait), questContext.getString(R.string.go));

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
                                    fruitArithmeticQuest.getComparisonType() == COMPARISON_TYPE_MAX ? "больше" : "меньше");


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
                                    timeQuest.getComparisonType() == COMPARISON_TYPE_MAX ? "максимальное" : "минимальное");

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
                                            numberSummandQuest.getUnknownMember()).
                                            getTitle(questContext));

                            break;

                    }

                    break;

                case MISMATCH:

                    mTitleText = "Найди лишнее";

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
    @CallSuper
    public void destroy() {
        mIsDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return mIsDestroyed;
    }

    @Override
    public View getView() {
        return mViewHolder.getView();
    }

    @Override
    public void updateSize(int width, int height) {
    }

    @Override
    public void playAnimationOnDelayedStart(int duration, @Nullable View view) {
        //do nothing - title always show
    }

    @Override
    public String getTitle() {
        return mTitleText;
    }
}
