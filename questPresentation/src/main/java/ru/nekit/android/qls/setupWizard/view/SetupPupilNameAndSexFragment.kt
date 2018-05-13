package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.KeyboardHost

class SetupPupilNameAndSexFragment : QuestSetupWizardFragment(), RadioGroup.OnCheckedChangeListener {

    private lateinit var pupilNameLayout: TextInputLayout
    private lateinit var pupilNameInput: EditText
    private lateinit var pupilSexGroup: RadioGroup

    override fun onSetupStart(view: View) {
        pupilNameLayout = view.findViewById(R.id.input_pupil_name_layout)
        pupilNameInput = view.findViewById(R.id.input_pupil_name)
        pupilSexGroup = view.findViewById(R.id.pupil_sex_group)
        pupilSexGroup.setOnCheckedChangeListener(this)
        setNextButtonVisibility(true)
        setNextButtonText(R.string.label_ok)
    }

    /*
    .apply {
                val nameIsEmpty = isEmpty()
                pupilNameLayout.isErrorEnabled = nameIsEmpty
                if (nameIsEmpty)
                    pupilNameLayout.error = getString(error_pupil_name_should_be_entered)
                else
                    KeyboardHost.hideKeyboard(context!!,
                            pupilNameInput,
                            Delay.KEYBOARD.get(context!!))
            }
     */

    override fun nextAction(): Single<Boolean> =
            setupWizard.createPupil(pupilNameInput.text.toString(),
                    pupilSexGroup.checkedRadioButtonId.let {
                        if (it == R.id.pupil_sex_boy) PupilSex.BOY else PupilSex.GIRL
                    }
            )

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {
        //update(true)
    }

    /*private fun update(choiced: Boolean) {
        setNextButtonVisibility(choiced)
    }*/

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_setup_pupil_name_and_sex

    override fun onResume() {
        KeyboardHost.showKeyboard(context!!, pupilNameInput, Delay.KEYBOARD.get(context!!))
        super.onResume()
    }

    companion object {

        val instance: SetupPupilNameAndSexFragment
            get() = SetupPupilNameAndSexFragment()
    }
}
