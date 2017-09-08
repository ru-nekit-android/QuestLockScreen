package ru.nekit.android.qls.setupWizard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.nekit.android.qls.R;
import ru.nekit.android.qls.pupil.PhoneContact;

public class PhoneContactsAdapterForReading extends
        RecyclerView.Adapter<PhoneContactViewHolder> {

    private PhoneContactListener phoneContactListener;
    private List<PhoneContact> mData;

    public PhoneContactsAdapterForReading(List<PhoneContact> data,
                                          PhoneContactListener phoneContactListener) {
        mData = data;
        this.phoneContactListener = phoneContactListener;
    }

    @Override
    public PhoneContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhoneContactViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ill_phone_contact_for_reading, parent, false));
    }

    @Override
    public void onBindViewHolder(final PhoneContactViewHolder holder, int position) {
        PhoneContact contact = mData.get(position);
        holder.titleView.setText(contact.name);
        holder.informationView.setText(contact.phoneNumber);
        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneContactListener.onAction(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}