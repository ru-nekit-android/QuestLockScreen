package ru.nekit.android.qls.quest.view.mediator.answer

import android.support.annotation.LayoutRes
import ru.nekit.android.qls.data.representation.getDrawableRepresentation
import ru.nekit.android.qls.quest.view.adapter.SquareImageAdapter

//ver 1.0
abstract class SimpleImageAdapterAnswerMediator : ListableAnswerMediator<Int, SquareImageAdapter>() {

    @get:LayoutRes
    protected abstract val adapterItemLayoutResId: Int

    override fun createListAdapter(listData: List<Int>): SquareImageAdapter =
            SquareImageAdapter(adapterItemLayoutResId, listData,
                    listData.map { id ->
                        questContext.questResourceRepository
                                .getVisualResourceItemById(id)
                                .getDrawableRepresentation().drawableResourceId
                    }, answerPublisher)

}