package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View
import android.widget.RadioGroup
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.R.id.*
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.Complexity.*
import ru.nekit.android.utils.ParameterlessSingletonHolder

class QTPComplexityFragment : QuestSetupWizardFragment() {

    private lateinit var qtpComplexityGroup: RadioGroup
    private var complexity: Complexity? = null

    override fun onSetupStart(view: View) {
        title = R.string.title_qtp_complexity
        qtpComplexityGroup = view.findViewById(complexity_group)
        autoDispose {
            qtpComplexityGroup.checkedChanges().subscribe {
                complexity = when (it) {
                    complexity_easy -> EASY
                    complexity_normal -> NORMAL
                    complexity_hard -> HARD
                    else -> null
                }

                update(complexity != null)
            }
        }
        update(false)
    }

    override fun nextAction(): Single<Boolean> = setupWizard.setQTPComplexity(complexity)

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_qtp_complexity

    private fun update(isSet: Boolean) {
        toolContainerVisibility(isSet)
    }

    companion object : ParameterlessSingletonHolder<QTPComplexityFragment>(::QTPComplexityFragment)

}