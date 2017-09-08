package ru.nekit.android.qls.pupil;

import android.os.Parcel;
import android.os.Parcelable;

public class PhoneContact implements Parcelable {

    public static final String NAME = "phoneContact";
    public static final Creator<PhoneContact> CREATOR = new Creator<PhoneContact>() {
        @Override
        public PhoneContact createFromParcel(Parcel in) {
            return new PhoneContact(in);
        }

        @Override
        public PhoneContact[] newArray(int size) {
            return new PhoneContact[size];
        }
    };
    public long id;
    public String name;
    public String phoneNumber;

    public PhoneContact(long id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    protected PhoneContact(Parcel in) {
        name = in.readString();
        phoneNumber = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);
    }

    public String toString() {
        return String.format("%s (%s)", name, phoneNumber);
    }
}
