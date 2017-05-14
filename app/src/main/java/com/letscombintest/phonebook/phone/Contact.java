package com.letscombintest.phonebook.phone;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import com.letscombintest.phonebook.phone.adapter.Friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jisun on 2017-05-14.
 */

public class Contact {
    private String TAG = getClass().getSimpleName();
    private ArrayList<Friend> mContacts;
    private Context mContext;
    private ContentResolver cr;

    /**
     * 생성자
     *
     * @param context
     */
    public Contact(Context context) {
        this.mContext = context;
        dataThread.start();
    }

    DataThread dataThread = new DataThread();

    public void loadContacts() {

        // map to store and update the data as we loop through all type of data
        HashMap<Integer, Friend> tempContacts = new LinkedHashMap<>();

        // Loading All Contacts
        final String[] PROJECTION = new String[]{
                ContactsContract.Data.RAW_CONTACT_ID,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.DATA1,
                ContactsContract.Data.PHOTO_URI,
                ContactsContract.Data.MIMETYPE
        };

        long start = System.currentTimeMillis();
        Log.d(TAG, "Contacts query cursor initialized. Querying..");

        cr = mContext.getContentResolver();


        // We need the record from the ContactsContract.Data table if
        // the mime type is Email or Phone
        // And the sort order should be by name
        Cursor cursor = cr.query(
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                ContactsContract.Data.MIMETYPE + " = ?" + " OR " + ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                },
                "lower(" + ContactsContract.Data.DISPLAY_NAME + ")"
        );

        Log.d(TAG, "Total Rows :" + cursor.getCount());

        try {
            final int raw_contact = cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
            final int idPos = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
            final int namePos = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
            final int photoPos = cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
//            final int emailNoPos = cursor.getColumnIndex(ContactsContract.Data.DATA1);
            final int numNoPos = cursor.getColumnIndex(ContactsContract.Data.DATA1);
            final int mimePos = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);

            while (cursor.moveToNext()) {
                int raw_id = cursor.getInt(raw_contact);
                int contactId = cursor.getInt(idPos);
//              String emailNo = cursor.getString(emailNoPos);
                String numNo = cursor.getString(numNoPos);
                String photo = cursor.getString(photoPos);
                String name = cursor.getString(namePos);
                String mime = cursor.getString(mimePos);


                // If contact is not yet created
                if (tempContacts.get(contactId) == null) {
                    // If type email, add all detail, else add name and photo (we don't need number)
                    if (mime.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                        tempContacts.put(contactId, new Friend(name, numNo, photo, raw_id));
                    } else {
                        tempContacts.put(contactId, new Friend(name, null, photo, raw_id));
                    }

                } else {

                    if (mime.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                        tempContacts.get(contactId).setPhoneNum(numNo);
                    }
                }
            }
        } finally {
            cursor.close();
            Log.d(TAG, "Cursor closed..");
        }
        long end = System.currentTimeMillis();
        float diffSeconds = (float) ((end - start) / 1000.0);
        Log.d(TAG, tempContacts.size() + " contacts loaded in: " + diffSeconds + "s || " +
                (end - start) + " ms");

        // Convert to ArrayList if you need an arraylist
        mContacts = new ArrayList<>();
        for (Map.Entry<Integer, Friend> friend : tempContacts.entrySet()) {
            mContacts.add(friend.getValue());
        }

        Log.d(TAG, "ArrayList created from contacts");

        // Do whatever you want to do with the loaded contacts
    }

    public ArrayList gettestdata() {
        return mContacts;
    }

    class DataThread extends Thread {
        @Override
        public synchronized void start() {
            loadContacts();
        }
    }

    public void deleteThisItem(String name) {
        cr.delete(
                ContactsContract.RawContacts.CONTENT_URI,
                ContactsContract.Contacts.DISPLAY_NAME + "=?",
                new String[]{name}
        );
    }

//    public void updateThisItem(String name) {
//        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//        int rawContactInsertIndex = ops.size();
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
////                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
//                .build());
//        try {
//            cr.applyBatch(ContactsContract.AUTHORITY, ops);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (OperationApplicationException e) {
//            e.printStackTrace();
//        }
//    }

    public void insertItem(String name, String phoneNum, int rawContactId) {
        ContentValues values = new ContentValues();
        values.clear();
        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        cr.insert(ContactsContract.Data.CONTENT_URI, values);
    }
}
