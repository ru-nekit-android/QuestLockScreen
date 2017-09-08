package ru.nekit.android.qls.quest;

import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.quest.answer.CoinQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.MetricsQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.QuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.SimpleExampleAnswerChecker;
import ru.nekit.android.qls.quest.answer.TrafficLightQuestAnswerChecker;
import ru.nekit.android.qls.quest.mediator.QuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.QuestMediatorFacade;
import ru.nekit.android.qls.quest.mediator.QuestTitleMediator;
import ru.nekit.android.qls.quest.mediator.choice.ChoiceAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.coin.CoinQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.coin.CoinQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.fruitArithmetic.FruitArithmeticQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.fruitArithmetic.FruitArithmeticQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.fruitArithmetic.FruitComparisionAnswerChecker;
import ru.nekit.android.qls.quest.mediator.fruitArithmetic.FruitComparisonAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.metrics.MetricsQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.metrics.MetricsQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.perimeter.PerimeterQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.simpleExample.SimpleExampleQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.simpleExample.SimpleExampleQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.textCamouflage.TextCamouflageContentMediator;
import ru.nekit.android.qls.quest.mediator.time.CurrentTimeQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.time.TimeQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.trafficLight.TrafficLightQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.trafficLight.TrafficLightQuestContentMediator;

public class QuestViewBuilder {

    @NonNull
    private final QuestContext mQuestContext;
    private QuestMediatorFacade mQuestMediatorFacade;

    public QuestViewBuilder(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
    }

    public void build() {
        IQuest quest = mQuestContext.getQuest();
        switch (quest.getQuestType()) {

            case COINS:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        new CoinQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new CoinQuestContentMediator(),
                        new CoinQuestAlternativeAnswerMediator()
                );

                break;

            case SIMPLE_EXAMPLE:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        new SimpleExampleAnswerChecker(),
                        new QuestTitleMediator(),
                        new SimpleExampleQuestContentMediator(),
                        new SimpleExampleQuestAlternativeAnswerMediator()
                );

                break;

            case METRICS:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        new MetricsQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new MetricsQuestContentMediator(),
                        new MetricsQuestAlternativeAnswerMediator()
                );

                break;

            case PERIMETER:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new PerimeterQuestContentMediator(),
                        new SimpleExampleQuestAlternativeAnswerMediator()
                );

                break;

            case TRAFFIC_LIGHT:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        new TrafficLightQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new TrafficLightQuestContentMediator(),
                        new TrafficLightQuestAlternativeAnswerMediator()
                );

                break;

            case TEXT_CAMOUFLAGE:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new TextCamouflageContentMediator(),
                        new QuestAlternativeAnswerMediator()
                );

                break;

            case FRUIT_ARITHMETIC:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        quest.getQuestionType() == QuestionType.SOLUTION ?
                                new QuestAnswerChecker() :
                                new FruitComparisionAnswerChecker(),
                        new QuestTitleMediator(),
                        new FruitArithmeticQuestContentMediator(),
                        quest.getQuestionType() == QuestionType.SOLUTION ?
                                new FruitArithmeticQuestAlternativeAnswerMediator() :
                                new FruitComparisonAlternativeAnswerMediator()
                );

                break;

            case TIME:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new TimeQuestAlternativeAnswerMediator()
                );

                break;

            case CURRENT_TIME:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new CurrentTimeQuestAlternativeAnswerMediator()
                );

                break;

            case CHOICE:
            case MISMATCH:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new ChoiceAlternativeAnswerMediator()
                );

                break;

        }
        mQuestMediatorFacade.init(mQuestContext);
    }

    public void destroy() {
        mQuestMediatorFacade.destroy();
        mQuestMediatorFacade = null;
    }

    public View getView() {
        return mQuestMediatorFacade.getView();
    }

    public void rebuild() {
        destroy();
        build();
    }
}