package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.view.View
import android.widget.RadioGroup
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.R.id.*
import ru.nekit.android.qls.shared.model.Complexity.*

class SetupQTPComplexityFragment : QuestSetupWizardFragment() {

    private lateinit var qtpComplexityGroup: RadioGroup

    override fun onSetupStart(view: View) {
        qtpComplexityGroup = view.findViewById(complexity_group)
        autoDispose {
            qtpComplexityGroup.checkedChanges().subscribe {
                update(true)
            }
        }
        setNextButtonText(R.string.label_ok)
        update(false)
    }

    override fun nextAction(): Single<Boolean> {
        return setupWizard.setQTPComplexity(qtpComplexityGroup.checkedRadioButtonId.let {
            when (it) {
                complexity_easy -> EASY
                complexity_normal -> NORMAL
                complexity_hard -> HARD
                else -> null
            }
        })
    }

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_setup_qtp_complexity

    private fun update(choiced: Boolean) {
        setNextButtonVisibility(choiced)
    }

    companion object {

        val instance: SetupQTPComplexityFragment
            get() = SetupQTPComplexityFragment()
    }
}