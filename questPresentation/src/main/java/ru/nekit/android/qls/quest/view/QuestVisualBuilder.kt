package ru.nekit.android.qls.quest.view

import io.reactivex.Single
import ru.nekit.android.domain.interactor.ParameterlessSingleUseCase
import ru.nekit.android.qls.domain.useCases.GenerateQuestUseCase
import ru.nekit.android.qls.quest.QuestContext
import ru.nekit.android.qls.quest.view.mediator.answer.ButtonListAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.content.EmptyQuestContentMediator
import ru.nekit.android.qls.quest.view.mediator.title.TitleMediator
import ru.nekit.android.qls.quest.view.mediator.types.choice.ChoiceAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.coins.CoinAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.coins.CoinQuestContentMediator
import ru.nekit.android.qls.quest.view.mediator.types.colored.ColoredVisualRepresentationAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.direction.DirectionAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.fruitArithmetic.FruitArithmeticAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.fruitArithmetic.FruitArithmeticQuestContentMediator
import ru.nekit.android.qls.quest.view.mediator.types.fruitArithmetic.FruitComparisonAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.metrics.MetricsAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.metrics.MetricsQuestContentMediator
import ru.nekit.android.qls.quest.view.mediator.types.perimeter.PerimeterQuestContentMediator
import ru.nekit.android.qls.quest.view.mediator.types.simpleExample.SimpleExampleAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.simpleExample.SimpleExampleQuestContentMediator
import ru.nekit.android.qls.quest.view.mediator.types.textCamouflage.TextCamouflageContentMediator
import ru.nekit.android.qls.quest.view.mediator.types.time.CurrentTimeAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.time.TimeAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.trafficLight.TrafficLightAnswerMediator
import ru.nekit.android.qls.quest.view.mediator.types.trafficLight.TrafficLightQuestContentMediator
import ru.nekit.android.qls.shared.model.QuestType.*
import ru.nekit.android.qls.shared.model.QuestionType

//ver 1.2
class QuestVisualBuilder {

    companion object {

        fun build(questContext: QuestContext): ParameterlessSingleUseCase<IQuestMediatorFacade> =
                object : ParameterlessSingleUseCase<IQuestMediatorFacade>() {

                    override fun build(): Single<IQuestMediatorFacade> =
                            GenerateQuestUseCase(questContext.repository).build().map { quest ->
                                val questMediatorFacade = when (quest.questType) {

                                    COINS ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(),
                                                CoinQuestContentMediator(),
                                                CoinAnswerMediator()
                                        )

                                    SIMPLE_EXAMPLE ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(),
                                                SimpleExampleQuestContentMediator(),
                                                SimpleExampleAnswerMediator()
                                        )

                                    METRICS ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(),
                                                MetricsQuestContentMediator(),
                                                MetricsAnswerMediator()
                                        )

                                    PERIMETER ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(),
                                                PerimeterQuestContentMediator(),
                                                SimpleExampleAnswerMediator()
                                        )

                                    TRAFFIC_LIGHT ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(),
                                                TrafficLightQuestContentMediator(),
                                                TrafficLightAnswerMediator()
                                        )

                                    TEXT_CAMOUFLAGE ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(),
                                                TextCamouflageContentMediator(),
                                                ButtonListAnswerMediator()
                                        )

                                    FRUIT_ARITHMETIC -> {

                                        val isSolution = quest.questionType == QuestionType.SOLUTION
                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(),
                                                if (isSolution) FruitArithmeticQuestContentMediator() else
                                                    null,
                                                if (isSolution)
                                                    FruitArithmeticAnswerMediator()
                                                else
                                                    FruitComparisonAnswerMediator()
                                        )
                                    }

                                    TIME ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(), null,
                                                TimeAnswerMediator()
                                        )

                                    CURRENT_TIME ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(), null,
                                                CurrentTimeAnswerMediator()
                                        )

                                    CHOICE, MISMATCH, CURRENT_SEASON ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(), null,
                                                ChoiceAnswerMediator()
                                        )

                                    COLORS ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(), null,
                                                ColoredVisualRepresentationAnswerMediator()
                                        )

                                    DIRECTION ->

                                        QuestMediatorFacade(
                                                questContext,
                                                TitleMediator(),
                                                EmptyQuestContentMediator(),
                                                DirectionAnswerMediator()
                                        )
                                }
                                questMediatorFacade.onCreate(questContext, quest)
                                questMediatorFacade
                            }

                }
    }
}