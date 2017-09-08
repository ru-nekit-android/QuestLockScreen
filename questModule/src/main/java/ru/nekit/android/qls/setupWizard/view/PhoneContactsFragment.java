package ru.nekit.android.qls.setupWizard.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.pupil.PhoneContact;
import ru.nekit.android.qls.setupWizard.QuestSetupWizard;
import ru.nekit.android.qls.setupWizard.adapters.PhoneContactListener;
import ru.nekit.android.qls.setupWizard.adapters.PhoneContactsAdapterForModification;

public class PhoneContactsFragment extends QuestSetupWizardFragment implements
        PhoneContactListener {

    private static final int RESULT_PICK_CONTACT = 800;

    private List<PhoneContact> mPhoneContactsList;
    private PhoneContactsAdapterForModification mPhoneContactsAdapterForModification;

    public static PhoneContactsFragment getInstance() {
        return new PhoneContactsFragment();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        mPhoneContactsList = getSetupWizard().getPhoneContacts();
        if (mPhoneContactsList == null) {
            mPhoneContactsList = new ArrayList<>();
        }
        RecyclerView phoneContactsListView = (RecyclerView) view.findViewById(R.id.list_phone_contacts);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        phoneContactsListView.setLayoutManager(linearLayoutManager);
        mPhoneContactsAdapterForModification =
                new PhoneContactsAdapterForModification(mPhoneContactsList, this);
        phoneContactsListView.setAdapter(mPhoneContactsAdapterForModification);
        setAltButtonText(R.string.label_pick_contact);
        setNextButtonText(R.string.label_ok);
    }

    @Override
    protected boolean addToBackStack() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    onContactPicked(data);
                    break;
            }
        }
    }

    @Override
    protected void altButtonAction() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_allow_contacts;
    }

    private void onContactPicked(Intent data) {
        String phoneNumber;
        String name;
        Uri uri = data.getData();
        if (uri != null) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null,
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                phoneNumber = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);
                PhoneContact phoneContact = new PhoneContact(id, name, phoneNumber);
                mPhoneContactsList.add(phoneContact);
                mPhoneContactsAdapterForModification.notifyDataSetChanged();
                getSetupWizard().saveAllowContacts(mPhoneContactsList);
                cursor.close();
            }
        }
    }

    @Override
    protected boolean nextButtonAction() {
        showSetupWizardStep(QuestSetupWizard.Step.SETTINGS);
        return true;
    }

    /*@Override
    public int[] getContainerMargins(@NonNull Resources resources) {
        return new int[]{0, 0, 0, 0};
    }*/

    @Override
    public void onAction(final int position) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
        ad.setTitle(R.string.title_do_you_really_want_to_delete);
        ad.setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int argument) {
                mPhoneContactsList.remove(position);
                mPhoneContactsAdapterForModification.notifyDataSetChanged();
                getSetupWizard().saveAllowContacts(mPhoneContactsList);
            }
        });
        ad.setNegativeButton(R.string.label_no, null);
        ad.setCancelable(true);
        ad.show();
    }
}
