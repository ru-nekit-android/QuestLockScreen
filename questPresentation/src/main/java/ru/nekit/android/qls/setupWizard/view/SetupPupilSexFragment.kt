package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View
import android.widget.RadioGroup
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.shared.model.PupilSex

class SetupPupilSexFragment : QuestSetupWizardFragment(), RadioGroup.OnCheckedChangeListener {

    private lateinit var pupilSexGroup: RadioGroup

    override fun onSetupStart(view: View) {
        pupilSexGroup = view.findViewById(R.id.pupil_sex_group)
        pupilSexGroup.setOnCheckedChangeListener(this)
        setNextButtonText(R.string.label_ok)
        update(false)
    }

    private fun update(choiced: Boolean) {
        setNextButtonVisibility(choiced)
    }

    override fun nextAction(): Single<Boolean> {
        val selectedSex = pupilSexGroup.checkedRadioButtonId
        val pupilSex = if (selectedSex == R.id.pupil_sex_boy) PupilSex.BOY else PupilSex.GIRL
        return setupWizard.setPupilSex(pupilSex)
    }

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.sw_setup_pupil_sex
    }

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {
        update(true)
    }

    override fun onDestroy() {
        pupilSexGroup.setOnCheckedChangeListener(null)
        super.onDestroy()
    }

    companion object {

        val instance: SetupPupilSexFragment
            get() = SetupPupilSexFragment()
    }
}
