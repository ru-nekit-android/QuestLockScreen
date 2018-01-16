package ru.nekit.android.qls.quest;

import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.quest.answer.CoinQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.GroupWeightComparisonQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.MetricsQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.SimpleExampleAnswerChecker;
import ru.nekit.android.qls.quest.answer.TrafficLightQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.common.QuestAnswerChecker;
import ru.nekit.android.qls.quest.common.Quest;
import ru.nekit.android.qls.quest.mediator.answer.ButtonsQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.content.EmptyQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.title.QuestTitleMediator;
import ru.nekit.android.qls.quest.mediator.types.choice.ChoiceAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.coins.CoinQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.coins.CoinQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.colored.ColoredVisualRepresentationQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.direction.DirectionQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.fruitArithmetic.FruitArithmeticQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.fruitArithmetic.FruitArithmeticQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.fruitArithmetic.FruitComparisonAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.metrics.MetricsQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.metrics.MetricsQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.perimeter.PerimeterQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.simpleExample.SimpleExampleQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.simpleExample.SimpleExampleQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.textCamouflage.TextCamouflageContentMediator;
import ru.nekit.android.qls.quest.mediator.types.time.CurrentTimeQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.time.TimeQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.trafficLight.TrafficLightQuestAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.trafficLight.TrafficLightQuestContentMediator;

public class QuestVisualBuilder {

    @NonNull
    private final QuestContext mQuestContext;
    private IQuestMediatorFacade mQuestMediatorFacade;

    public QuestVisualBuilder(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
    }

    public void create() {
        Quest quest = mQuestContext.generateQuest();
        switch (quest.getQuestType()) {

            case COINS:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new CoinQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new CoinQuestContentMediator(),
                        new CoinQuestAnswerMediator()
                );

                break;

            case SIMPLE_EXAMPLE:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new SimpleExampleAnswerChecker(),
                        new QuestTitleMediator(),
                        new SimpleExampleQuestContentMediator(),
                        new SimpleExampleQuestAnswerMediator()
                );

                break;

            case METRICS:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new MetricsQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new MetricsQuestContentMediator(),
                        new MetricsQuestAnswerMediator()
                );

                break;

            case PERIMETER:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new PerimeterQuestContentMediator(),
                        new SimpleExampleQuestAnswerMediator()
                );

                break;

            case TRAFFIC_LIGHT:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new TrafficLightQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new TrafficLightQuestContentMediator(),
                        new TrafficLightQuestAnswerMediator()
                );

                break;

            case TEXT_CAMOUFLAGE:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new TextCamouflageContentMediator(),
                        new ButtonsQuestAnswerMediator()
                );

                break;

            case FRUIT_ARITHMETIC:

                boolean isSolution = quest.getQuestionType() == QuestionType.SOLUTION;
                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        isSolution ? new QuestAnswerChecker() :
                                new GroupWeightComparisonQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        isSolution ? new FruitArithmeticQuestContentMediator() : null,
                        isSolution ? new FruitArithmeticQuestAnswerMediator() :
                                new FruitComparisonAnswerMediator()
                );

                break;

            case TIME:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new TimeQuestAnswerMediator()
                );

                break;

            case CURRENT_TIME:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new CurrentTimeQuestAnswerMediator()
                );

                break;

            case CHOICE:
            case MISMATCH:
            case CURRENT_SEASON:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new ChoiceAnswerMediator()
                );

                break;

            case COLORS:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new ColoredVisualRepresentationQuestAnswerMediator()
                );

                break;

            case DIRECTION:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new EmptyQuestContentMediator(),
                        new DirectionQuestAnswerMediator()
                );

                break;

        }
        mQuestMediatorFacade.onCreate(mQuestContext);
    }

    public View getView() {
        return mQuestMediatorFacade.getView();
    }

    public void deactivate() {
        mQuestMediatorFacade.deactivate();
    }

    public void detachView() {
        mQuestMediatorFacade.detachView();
    }

}