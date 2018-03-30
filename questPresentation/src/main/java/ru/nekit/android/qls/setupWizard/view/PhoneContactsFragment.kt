package ru.nekit.android.qls.setupWizard.view

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract.CommonDataKinds.Phone.*
import android.support.annotation.LayoutRes
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.Single
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.PhoneContact
import ru.nekit.android.qls.setupWizard.QuestSetupWizard
import ru.nekit.android.qls.view.adapters.PhoneContactListener
import ru.nekit.android.qls.view.adapters.PhoneContactsAdapterForModification

class PhoneContactsFragment : QuestSetupWizardFragment(), PhoneContactListener {

    private lateinit var phoneContactsList: MutableList<PhoneContact>
    private lateinit var phoneContactsAdapterForModification: PhoneContactsAdapterForModification

    override fun onSetupStart(view: View) {
        val phoneContactsListView: RecyclerView = view.findViewById(R.id.list_phone_contacts)
        phoneContactsListView.layoutManager = LinearLayoutManager(context)
        autoDispose {
            setupWizard.phoneContacts.subscribe { it ->
                phoneContactsList = it.toMutableList()
                phoneContactsAdapterForModification = PhoneContactsAdapterForModification(phoneContactsList, this)
                phoneContactsListView.adapter = phoneContactsAdapterForModification
            }
        }
        setAltButtonText(R.string.label_pick_contact)
        setNextButtonText(R.string.label_ok)
    }

    override val addToBackStack: Boolean = true

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_CONTACT -> if (data != null) {
                    val uri = data.data
                    if (uri != null) {
                        activity!!.contentResolver.query(uri, null, null, null, null)?.apply {
                            if (this.moveToFirst()) {
                                with(this) {
                                    val id = getLong(getColumnIndex(CONTACT_ID))
                                    val phoneIndex = getColumnIndex(NUMBER)
                                    val nameIndex = getColumnIndex(DISPLAY_NAME)
                                    val phoneNumber = getString(phoneIndex)
                                    val name = getString(nameIndex)
                                    val phoneContact = PhoneContact(id, name, phoneNumber)
                                    autoDispose {
                                        setupWizard.addPhoneContact(phoneContact).doFinally {
                                            close()
                                        }.subscribe {
                                                    phoneContactsList.add(phoneContact)
                                                    phoneContactsAdapterForModification.notifyDataSetChanged()
                                                }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun altAction() {
        val contactPickerIntent = Intent(Intent.ACTION_PICK,
                CONTENT_URI)
        startActivityForResult(contactPickerIntent, PICK_CONTACT)
    }

    @LayoutRes
    override fun getLayoutId(): Int = R.layout.sw_allow_contacts

    override fun nextAction(): Single<Boolean> = Single.fromCallable {
        showSetupWizardStep(QuestSetupWizard.QuestSetupWizardStep.SETTINGS)
        true
    }

    override fun onAction(position: Int) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.title_do_you_really_want_to_delete)
        builder.setPositiveButton(R.string.label_yes) { _, _ ->
            autoDispose {
                setupWizard.removePhoneContact(phoneContactsList[position]).subscribe {
                    phoneContactsList.removeAt(position)
                    phoneContactsAdapterForModification.notifyDataSetChanged()
                }
            }
        }
        builder.setNegativeButton(R.string.label_no, null)
        builder.setCancelable(true)
        builder.show()
    }

    companion object {

        private const val PICK_CONTACT = 1

        val instance: PhoneContactsFragment
            get() = PhoneContactsFragment()
    }
}
