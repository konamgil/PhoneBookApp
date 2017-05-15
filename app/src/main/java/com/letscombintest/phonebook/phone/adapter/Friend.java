package com.letscombintest.phonebook.phone.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by jisun on 2017-05-14.
 */

public class Friend {
    int contact;
    int raw_contact_id;
    String name;
    String email;
    String photo;
    String phoneNum;



    public Friend(String name, String phoneNum, String photo,int raw_contact_id,int contact) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.photo = photo;
        this.raw_contact_id = raw_contact_id;
        this.contact = contact;

    }
    public int getContact() {
        return contact;
    }

    public void setContact(int contact) {
        this.contact = contact;
    }

    public int getRaw_contact_id() {
        return raw_contact_id;
    }

    public void setRaw_contact_id(int raw_contact_id) {
        this.raw_contact_id = raw_contact_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String emailNo) {
        this.email = emailNo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
