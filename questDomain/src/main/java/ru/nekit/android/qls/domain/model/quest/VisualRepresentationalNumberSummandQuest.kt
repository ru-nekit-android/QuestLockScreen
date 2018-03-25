package ru.nekit.android.qls.domain.model.quest

import ru.nekit.android.qls.domain.model.resources.common.IVisualRepresentationHolder

open class VisualRepresentationalNumberSummandQuest : NumberSummandQuest(), IVisualRepresentationHolder {

    override var visualRepresentationList: MutableList<Int> = ArrayList()

}