package ru.nekit.android.qls.setupWizard.view

import android.support.annotation.LayoutRes
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.nekit.android.qls.R
import ru.nekit.android.qls.shared.model.PupilSex
import ru.nekit.android.utils.Delay
import ru.nekit.android.utils.KeyboardHost
import ru.nekit.android.utils.ParameterlessSingletonHolder

class PupilNameAndSexFragment : QuestSetupWizardFragment() {

    private lateinit var pupilNameLayout: TextInputLayout
    private lateinit var pupilNameInput: EditText
    private lateinit var pupilSexGroup: RadioGroup

    override fun onSetupStart(view: View) {
        title = R.string.title_pupil_name_and_sex
        pupilNameLayout = view.findViewById(R.id.input_pupil_name_layout)
        pupilNameInput = view.findViewById(R.id.input_pupil_name)
        pupilSexGroup = view.findViewById(R.id.pupil_sex_group)
        autoDispose {
            Observable.combineLatest(pupilNameInput.textChanges(), pupilSexGroup.checkedChanges(),
                    BiFunction<CharSequence, Int, Unit> { name, sex ->
                        val nameIsNotBlank = name.isNotBlank()
                        val allIsSet = nameIsNotBlank && sex != -1
                        pupilNameLayout.isErrorEnabled = !nameIsNotBlank
                        if (!nameIsNotBlank)
                            pupilNameLayout.error = getString(R.string.error_pupil_name_should_be_entered)
                        toolContainerVisibility(allIsSet)
                    }).subscribe()
        }
    }

    private val pupilName
        get() = pupilNameInput.text.toString()

    private val pupilSex
        get() = pupilSexGroup.checkedRadioButtonId.let {
            if (it == R.id.pupil_sex_boy) PupilSex.BOY else PupilSex.GIRL
        }

    override fun nextAction(): Single<Boolean> = setupWizard.createPupil(pupilName, pupilSex)

    @LayoutRes
    override fun getLayoutId() = R.layout.sw_pupil_name_and_sex

    override fun onResume() {
        KeyboardHost.showKeyboard(context!!, pupilNameInput, Delay.KEYBOARD.get(context!!))
        super.onResume()
    }

    override fun onStop() {
        KeyboardHost.hideKeyboard(context!!, pupilNameInput, Delay.KEYBOARD.get(context!!))
        super.onStop()
    }

    companion object : ParameterlessSingletonHolder<PupilNameAndSexFragment>(::PupilNameAndSexFragment)

}
