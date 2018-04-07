package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.EditText
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.KeyboardHost
import ru.nekit.android.utils.toSingle

class SetupPupilNameFragment : QuestSetupWizardFragment() {

    private lateinit var pupilNameLayout: TextInputLayout
    private lateinit var pupilNameInput: EditText

    override fun onSetupStart(view: View) {
        pupilNameLayout = view.findViewById(R.id.input_pupil_name_layout)
        pupilNameInput = view.findViewById(R.id.input_pupil_name)
        setNextButtonText(R.string.label_ok)
    }

    override fun nextAction(): Single<Boolean> {
        val pupilName = pupilNameInput.text.toString()
        val nameIsEmpty = pupilName.isEmpty()
        pupilNameLayout.isErrorEnabled = nameIsEmpty
        if (nameIsEmpty) {
            pupilNameLayout.error = getString(R.string.error_pupil_name_should_be_entered)
        } else {
            KeyboardHost.hideKeyboard(context!!, pupilNameInput, Delay.KEYBOARD.get(context!!))
        }
        return Single.just(pupilName).flatMap {
            setupWizard.createPupilAndSetAsCurrent().flatMap {
                if (it)
                    setupWizard.setPupilName(pupilName)
                else
                    false.toSingle()
            }
        }
    }

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.sw_setup_pupil_name
    }

    override fun onResume() {
        KeyboardHost.showKeyboard(context!!, pupilNameInput, Delay.KEYBOARD.get(context!!))
        super.onResume()
    }

    companion object {

        val instance: SetupPupilNameFragment
            get() = SetupPupilNameFragment()
    }
}
