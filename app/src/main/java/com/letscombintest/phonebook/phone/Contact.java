package com.letscombintest.phonebook.phone;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.Toast;

import com.letscombintest.phonebook.phone.adapter.ContactAdapter;
import com.letscombintest.phonebook.phone.adapter.Friend;
import com.letscombintest.phonebook.phone.fragment.AllFragment;

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
                        tempContacts.put(contactId, new Friend(name, numNo, photo, raw_id,contactId));
                    } else {
                        tempContacts.put(contactId, new Friend(name, null, photo, raw_id,contactId));
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


    public void insertItem(String name, String phoneNum, int rawContactId) {

        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "accountname@gmail.com")
//                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "com.google")
//                .build());
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
//                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,name )
//                .build());
//
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
//                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum )
//                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MAIN)
//                .build());

//        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                .withValue(ContactsContract.Data.MIMETYPE,
//                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
//                .build());


//여기서 실제 입력을 하게 됨
//        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, "basic");
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, "test");
        Uri rawContactUri = cr.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId23 = ContentUris.parseId(rawContactUri);


//등록되었으면 Account 정보 입력
        values.clear();
        values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId23);
        values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        cr.insert(ContactsContract.Data.CONTENT_URI, values);

        values.clear();
        values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId23);
        values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum);
        cr.insert(ContactsContract.Data.CONTENT_URI, values);

//        try {
//            cr.applyBatch(ContactsContract.AUTHORITY, ops);
//
//            Toast.makeText(mContext,"전송",Toast.LENGTH_SHORT).show();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (OperationApplicationException e) {
//            e.printStackTrace();
//        }
        getrawContactId(phoneNum);
    }
    public void update(String name, String phoneNum, int ContactId)
    {
        int id = ContactId;
//        String firstname = name;
        String lastname = name;
        String number = phoneNum;
//        String photo_uri = "android.resource://com.my.package/drawable/default_photo";

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Name
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, lastname);
        ops.add(builder.build());

        // Number
        builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?"+ " AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=?", new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)});
        builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        ops.add(builder.build());


//        // Picture
//        try
//        {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(photo_uri));
//            ByteArrayOutputStream image = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG , 100, image);
//
//            builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
//            builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE});
//            builder.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, image.toByteArray());
//            ops.add(builder.build());
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        // Update
        try
        {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
            AllFragment allFragment = new AllFragment();
            allFragment.refreshList();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void getrawContactId(String phoneNum){
        String[] item = {ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        //"-"삭제
        phoneNum = phoneNum.replace("-", "");
        //폰번호 형식
        String phoneNumberFormatNumber = PhoneNumberUtils.formatNumber(phoneNum);

        //RawContacId를 가져올 where문 - Phone정보 중 매칭된것 하나만 있어도 조건충족, "-"이 들어간 전화번호도 검색.
        String where = ContactsContract.CommonDataKinds.Phone.NUMBER+" IN ('"+phoneNum+"', '"+phoneNumberFormatNumber+"') ";

        //Contact 검색 쿼리
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, item, where, null, null);
        //커서 처음으로 이동
        cursor.moveToFirst();
        int rawContactId =  cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
        getDateFromRawContactId(rawContactId);
    }
    final String[] PROJECTION2 = new String[]{
            ContactsContract.Data.RAW_CONTACT_ID,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.PHOTO_URI,
            ContactsContract.Data.MIMETYPE
    };
    public void getDateFromRawContactId(int rawContactId){
        String mimeType = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
//        String item[] =  new String[] { ContactsContract.RawContacts.Data._ID, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.NUMBER };
        String w= ContactsContract.Data.RAW_CONTACT_ID + "='"
                + rawContactId + "' AND "
                + ContactsContract.Contacts.Data.MIMETYPE + " = '"
                + mimeType + "'";
//        Cursor mCursor = cr.query(ContactsContract.Data.CONTENT_URI, item, w, null, null);
        Cursor mCursor = cr.query(ContactsContract.Data.CONTENT_URI, PROJECTION2, w, null, null);
        final int raw_contact2 = mCursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
        final int idPos2 = mCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        final int namePos2 = mCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
        final int photoPos2 = mCursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
        final int numNoPos2 = mCursor.getColumnIndex(ContactsContract.Data.DATA1);
        final int mimePos2 = mCursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
//        final int numNoPos2 = mCursor.getColumnIndex(ContactsContract.Data.DATA1);
        HashMap<Integer, Friend> tempContacts2 = new LinkedHashMap<>();
//        mCursor.moveToFirst();
        if(mCursor.getCount()  > 0)
        {
//            for(int i=0; i<mCursor.getCount(); i++)
            while (mCursor.moveToNext())
            {
                //데이터를 배열에 저장하시면 됩니다.
//                mCursor.moveToNext();
                int raw_id = mCursor.getInt(raw_contact2);
                int contactId = mCursor.getInt(idPos2);
                String numNo = mCursor.getString(numNoPos2);
                String photo = mCursor.getString(photoPos2);
                String name = mCursor.getString(namePos2);
                String mime = mCursor.getString(mimePos2);

                tempContacts2.put(contactId, new Friend(name, numNo, photo, raw_id,contactId));

            }
        }
        ContactAdapter a = new ContactAdapter(mContext);

        for (Map.Entry<Integer, Friend> friend : tempContacts2.entrySet()) {
//            mContacts.add(friend.getValue());
            a.add(friend.getValue());

        }
    }

}
