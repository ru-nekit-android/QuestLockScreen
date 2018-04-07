package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View
import android.widget.RadioGroup
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.shared.model.Complexity
import ru.nekit.android.qls.shared.model.Complexity.*

class SetupQTPComplexityFragment : QuestSetupWizardFragment() {

    private lateinit var qtpComplexityGroup: RadioGroup

    override fun onSetupStart(view: View) {
        qtpComplexityGroup = view.findViewById(R.id.complexity_group)
        autoDispose {
            qtpComplexityGroup.checkedChanges().subscribe {
                update(true)
            }
        }
        setNextButtonText(R.string.label_ok)
        update(false)
    }

    override fun nextAction(): Single<Boolean> {
        val selectedComplexity = qtpComplexityGroup.checkedRadioButtonId
        val complexity: Complexity? =
                when (selectedComplexity) {
                    R.id.complexity_easy -> EASY
                    R.id.complexity_normal -> NORMAL
                    R.id.complexity_hard -> HARD
                    else -> null
                }
        return setupWizard.setQTPComplexity(complexity)
    }

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.sw_setup_qtp_complexity
    }

    private fun update(choiced: Boolean) {
        setNextButtonVisibility(choiced)
    }

    companion object {

        val instance: SetupQTPComplexityFragment
            get() = SetupQTPComplexityFragment()
    }
}