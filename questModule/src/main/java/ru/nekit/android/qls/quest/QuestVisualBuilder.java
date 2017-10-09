package ru.nekit.android.qls.quest;

import android.support.annotation.NonNull;
import android.view.View;

import ru.nekit.android.qls.quest.answer.CoinQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.GroupWeightComparisonQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.MetricsQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.SimpleExampleAnswerChecker;
import ru.nekit.android.qls.quest.answer.TrafficLightQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.shared.QuestAnswerChecker;
import ru.nekit.android.qls.quest.base.Quest;
import ru.nekit.android.qls.quest.mediator.answer.QuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.title.QuestTitleMediator;
import ru.nekit.android.qls.quest.mediator.types.choice.ChoiceAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.coins.CoinQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.coins.CoinQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.colored.ColoredVisualRepresentationQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.fruitArithmetic.FruitArithmeticQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.fruitArithmetic.FruitArithmeticQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.fruitArithmetic.FruitComparisonAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.metrics.MetricsQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.metrics.MetricsQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.perimeter.PerimeterQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.simpleExample.SimpleExampleQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.simpleExample.SimpleExampleQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.types.textCamouflage.TextCamouflageContentMediator;
import ru.nekit.android.qls.quest.mediator.types.time.CurrentTimeQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.time.TimeQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.trafficLight.TrafficLightQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.types.trafficLight.TrafficLightQuestContentMediator;

import static ru.nekit.android.qls.quest.QuestionType.SOLUTION;

public class QuestVisualBuilder {

    @NonNull
    private final QuestContext mQuestContext;
    private IQuestMediatorFacade mQuestMediatorFacade;

    public QuestVisualBuilder(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
    }

    public void create() {
        Quest quest = mQuestContext.getQuest();
        switch (quest.getQuestType()) {

            case COINS:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new CoinQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new CoinQuestContentMediator(),
                        new CoinQuestAlternativeAnswerMediator()
                );

                break;

            case SIMPLE_EXAMPLE:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new SimpleExampleAnswerChecker(),
                        new QuestTitleMediator(),
                        new SimpleExampleQuestContentMediator(),
                        new SimpleExampleQuestAlternativeAnswerMediator()
                );

                break;

            case METRICS:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new MetricsQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new MetricsQuestContentMediator(),
                        new MetricsQuestAlternativeAnswerMediator()
                );

                break;

            case PERIMETER:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new PerimeterQuestContentMediator(),
                        new SimpleExampleQuestAlternativeAnswerMediator()
                );

                break;

            case TRAFFIC_LIGHT:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new TrafficLightQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new TrafficLightQuestContentMediator(),
                        new TrafficLightQuestAlternativeAnswerMediator()
                );

                break;

            case TEXT_CAMOUFLAGE:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new TextCamouflageContentMediator(),
                        new QuestAlternativeAnswerMediator()
                );

                break;

            case FRUIT_ARITHMETIC:

                boolean isSolution = quest.getQuestionType() == SOLUTION;
                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        isSolution ? new QuestAnswerChecker() :
                                new GroupWeightComparisonQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        isSolution ? new FruitArithmeticQuestContentMediator() : null,
                        isSolution ? new FruitArithmeticQuestAlternativeAnswerMediator() :
                                new FruitComparisonAlternativeAnswerMediator()
                );

                break;

            case TIME:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new TimeQuestAlternativeAnswerMediator()
                );

                break;

            case CURRENT_TIME:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new CurrentTimeQuestAlternativeAnswerMediator()
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
                        new ChoiceAlternativeAnswerMediator()
                );

                break;

            case COLORS:

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new ColoredVisualRepresentationQuestAlternativeAnswerMediator()
                );

                break;

        }
        mQuestMediatorFacade.create(mQuestContext);
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