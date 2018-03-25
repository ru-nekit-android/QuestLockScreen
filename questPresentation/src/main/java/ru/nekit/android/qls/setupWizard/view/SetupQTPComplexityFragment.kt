package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View
import android.widget.RadioGroup
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.shared.model.Complexity

class SetupQTPComplexityFragment : QuestSetupWizardFragment(), RadioGroup.OnCheckedChangeListener {

    private lateinit var qtpComplexityGroup: RadioGroup

    override fun onSetupStart(view: View) {
        qtpComplexityGroup = view.findViewById(R.id.complexity_group)
        qtpComplexityGroup.setOnCheckedChangeListener(this)
        setNextButtonText(R.string.label_ok)
        update(false)
    }

    override fun nextAction(): Single<Boolean> {
        val selectedComplexity = qtpComplexityGroup.checkedRadioButtonId
        val complexity: Complexity? =
                when (selectedComplexity) {
                    R.id.complexity_easy -> Complexity.EASY
                    R.id.complexity_normal -> Complexity.NORMAL
                    R.id.complexity_hard -> Complexity.HARD
                    else -> null
                }
        return setupWizard.setQTPComplexity(complexity)
    }

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.sw_setup_qtp_complexity
    }

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {
        update(true)
    }

    private fun update(choiced: Boolean) {
        setNextButtonVisibility(choiced)
    }

    override fun onDestroy() {
        qtpComplexityGroup.setOnCheckedChangeListener(null)
        super.onDestroy()
    }

    companion object {

        val instance: SetupQTPComplexityFragment
            get() = SetupQTPComplexityFragment()
    }
}