package ru.nekit.android.qls.quest;

import android.support.annotation.NonNull;
import android.widget.ViewSwitcher;

import ru.nekit.android.qls.quest.answer.CoinQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.GroupWeightComparisonQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.MetricsQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.SimpleExampleAnswerChecker;
import ru.nekit.android.qls.quest.answer.TrafficLightQuestAnswerChecker;
import ru.nekit.android.qls.quest.answer.shared.QuestAnswerChecker;
import ru.nekit.android.qls.quest.mediator.choice.ChoiceAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.coin.CoinQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.coin.CoinQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.fruitArithmetic.FruitArithmeticQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.fruitArithmetic.FruitArithmeticQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.fruitArithmetic.FruitComparisonAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.metrics.MetricsQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.metrics.MetricsQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.perimeter.PerimeterQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.shared.answer.QuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.shared.title.QuestTitleMediator;
import ru.nekit.android.qls.quest.mediator.simpleExample.SimpleExampleQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.simpleExample.SimpleExampleQuestContentMediator;
import ru.nekit.android.qls.quest.mediator.textCamouflage.TextCamouflageContentMediator;
import ru.nekit.android.qls.quest.mediator.time.CurrentTimeQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.time.TimeQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.trafficLight.TrafficLightQuestAlternativeAnswerMediator;
import ru.nekit.android.qls.quest.mediator.trafficLight.TrafficLightQuestContentMediator;

public class QuestVisualBuilder {

    @NonNull
    private final QuestContext mQuestContext;
    private IQuestMediatorFacade mQuestMediatorFacade;

    public QuestVisualBuilder(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
    }

    public void create(@NonNull ViewSwitcher contentContainer) {
        IQuest quest = mQuestContext.getQuest();
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

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        quest.getQuestionType() == QuestionType.SOLUTION ?
                                new QuestAnswerChecker() :
                                new GroupWeightComparisonQuestAnswerChecker(),
                        new QuestTitleMediator(),
                        new FruitArithmeticQuestContentMediator(),
                        quest.getQuestionType() == QuestionType.SOLUTION ?
                                new FruitArithmeticQuestAlternativeAnswerMediator() :
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

                mQuestMediatorFacade = new QuestMediatorFacade(
                        mQuestContext,
                        new QuestAnswerChecker(),
                        new QuestTitleMediator(),
                        null,
                        new ChoiceAlternativeAnswerMediator()
                );

                break;

        }
        mQuestMediatorFacade.onCreateQuest(mQuestContext, contentContainer);
    }

    public IQuestMediatorFacade getQuestMediatorFacade() {
        return mQuestMediatorFacade;
    }
}