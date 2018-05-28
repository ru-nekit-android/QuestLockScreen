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
import io.reactivex.subjects.PublishSubject
import ru.nekit.android.qls.R
import ru.nekit.android.qls.domain.model.PhoneContact
import ru.nekit.android.qls.view.adapters.PhoneContactsAdapterForModification
import ru.nekit.android.utils.ParameterlessSingletonHolder

class PhoneContactsFragment : QuestSetupWizardFragment() {

    private lateinit var phoneContactsList: MutableList<PhoneContact>
    private lateinit var phoneContactsAdapterForModification: PhoneContactsAdapterForModification
    private val actionListener = PublishSubject.create<Int>().toSerialized()

    override fun onSetupStart(view: View) {
        title = R.string.title_allow_contacts
        altButtonText(R.string.label_pick_contact)
        nextButtonText(R.string.label_ok)
        val phoneContactsListView: RecyclerView = view.findViewById(R.id.list_phone_contacts)
        phoneContactsListView.layoutManager = LinearLayoutManager(context)
        autoDisposeList(
                setupWizard.phoneContacts.subscribe { it ->
                    phoneContactsList = it.toMutableList()
                    phoneContactsAdapterForModification =
                            PhoneContactsAdapterForModification(phoneContactsList, actionListener)
                    phoneContactsListView.adapter = phoneContactsAdapterForModification
                },
                actionListener.subscribe { position ->
                    AlertDialog.Builder(context!!).apply {
                        setTitle(R.string.title_do_you_really_want_to_delete)
                        setPositiveButton(R.string.label_yes) { _, _ ->
                            autoDispose {
                                setupWizard.removePhoneContact(phoneContactsList[position]).subscribe {
                                    phoneContactsList.removeAt(position)
                                    phoneContactsAdapterForModification.notifyDataSetChanged()
                                }
                            }
                        }
                        setNegativeButton(R.string.label_no, null)
                        setCancelable(true)
                        show()
                    }
                }
        )
    }

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

    @LayoutRes
    override fun getLayoutId(): Int = R.layout.sw_phone_contacts

    override val addToBackStack: Boolean = true

    override fun altAction() =
            startActivityForResult(Intent(Intent.ACTION_PICK, CONTENT_URI), PICK_CONTACT)

    override fun nextAction(): Single<Boolean> = backAction()

    companion object : ParameterlessSingletonHolder<PhoneContactsFragment>(::PhoneContactsFragment) {

        private const val PICK_CONTACT = 1

    }
}
