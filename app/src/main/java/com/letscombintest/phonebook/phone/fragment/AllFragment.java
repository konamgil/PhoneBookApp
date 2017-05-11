package com.letscombintest.phonebook.phone.fragment;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.letscombintest.phonebook.phone.adapter.DataAdapter;
import com.letscombintest.phonebook.phone.R;

import java.util.ArrayList;

/**
 * Created by konamgil on 2017-05-10.
 */

public class AllFragment extends Fragment {
    private Context context;
    private DataAdapter mDbHelper;
    private View view;
    private Button insertContact;

    public static AllFragment newInstance(Context context){
        AllFragment af = new AllFragment();
        af.context = context;
        return af;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.all_fragment,container,false);
        return view;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDbHelper = new DataAdapter(context);
        mDbHelper.createDatabase();
        mDbHelper.open();

        Cursor cursor = mDbHelper.getTestData();
        Cursor cursorNum = mDbHelper.getNameAndPhone();
        ArrayList<String> arrayList = new ArrayList<String>();

        arrayList.clear();
        int count = 0;
        while (cursorNum.moveToNext()){
//            arrayList.add(0,cursor.getString(0));

            arrayList.add(0,cursorNum.getString(0) + "  " + cursorNum.getString(1));
        }
        cursor.close();
        mDbHelper.close();

        ListView allList = (ListView) view.findViewById(R.id.listFavorite);
        allList.getOverScrollMode();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1, arrayList);
        allList.setAdapter(arrayAdapter);

        insertContact = (Button)view.findViewById(R.id.insertContact);
        insertContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, "9999");
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "1-800-GOOG-411");
                values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
                values.put(ContactsContract.CommonDataKinds.Phone.LABEL, "free directory assistance");
//                Uri dataUri = getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
            }
        });
    }
}
