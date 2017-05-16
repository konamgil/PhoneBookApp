package com.letscombintest.phonebook.phone.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.letscombintest.phonebook.phone.Contact;
import com.letscombintest.phonebook.phone.adapter.ContactAdapter;
import com.letscombintest.phonebook.phone.adapter.DataAdapter;
import com.letscombintest.phonebook.phone.R;
import com.letscombintest.phonebook.phone.adapter.Friend;

import java.util.ArrayList;

/**
 * Created by konamgil on 2017-05-10.
 */

public class AllFragment extends Fragment {
    private Context context;
//    private DataAdapter mDbHelper;
    private View view;
    private ContactAdapter contactAdapter;
    private static ListView allList;
    private Parcelable state;
    private Contact mContact;
    private ArrayList<Friend> listViewItemList;
    EditText etName;
    EditText etPhone;
    Button btnSuccess;
    Button btnCancel;
    int pos;
    @Override
    public void onResume() {
        super.onResume();
        if (state != null) { // 리스트뷰 상태가 있는 경우
            allList.onRestoreInstanceState(state); // 리스트뷰 스크롤 위치 복구
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        state = allList.onSaveInstanceState(); // 리스트뷰 스크롤 위치 저장
    }


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

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"fab",Toast.LENGTH_SHORT).show();
            }
        });
        allList = (ListView) view.findViewById(R.id.listFavorite);
        allList.getOverScrollMode();

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1, arrayList);
        mContact = new Contact(context);
//        listViewItemList = mContact.gettestdata();
        listViewItemList = new Contact(context).gettestdata();
        contactAdapter = new ContactAdapter(context);

        updateContactAdapter();
        allList.setAdapter(contactAdapter);
        allList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                new BottomSheet.Builder(getActivity(), R.style.MyBottomSheetStyle)
                        .setSheet(R.menu.bottom_sheet)
                        .setTitle("메뉴")
                        .setListener(new BottomSheetListener() {
                            @Override
                            public void onSheetShown(@NonNull BottomSheet bottomSheet) {}
                            @Override
                            public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.editPhoneItem:
                                        Toast.makeText(context,"수정",Toast.LENGTH_SHORT).show();
                                        String update = "수정";
                                        listViewItemList = new Contact(context).gettestdata();

                                        String selectedName = listViewItemList.get(position).getName();
                                        String selectedPhone = listViewItemList.get(position).getPhoneNum();
                                        int raw_contact_id2 = listViewItemList.get(position).getRaw_contact_id();
                                        int contact_id = listViewItemList.get(position).getContact();
                                        pos = position;

                                        showDialog(selectedName,selectedPhone,contact_id,raw_contact_id2);
                                        refreshList();
                                        break;
                                    case R.id.deletePhoneItem:
                                        listViewItemList = new Contact(context).gettestdata();
                                        String name = listViewItemList.get(position).getName();
                                        Toast.makeText(context,"삭제 : " + name,Toast.LENGTH_SHORT).show();
                                        mContact.deleteThisItem(name);
                                        pos=position;
                                        listViewItemList.remove(pos);
                                        refreshList();
                                        break;
                                    case R.id.insertPhoneItem:
                                        int raw_contact_id = listViewItemList.get(position).getRaw_contact_id();
                                        int contact_id2 = listViewItemList.get(position).getContact();
                                        showDialog(raw_contact_id);
                                        refreshList();
                                        break;
                                    default:
                                        break;
                                }
                            }
                            @Override
                            public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int i) {}
                        })
                        .show();
                return true;
            }
        });
    }
    public void updateContactAdapter(){
        ContentResolver cr = context.getContentResolver();
        ContentObserver contentObserver = new ContentObserver( new Handler() ){
            public void onChange( boolean selfChange ){
                super.onChange(selfChange);
                refreshList();
                Toast.makeText(context,"change",Toast.LENGTH_SHORT).show();
            }
        };
        cr.registerContentObserver( ContactsContract.Data.CONTENT_URI, true, contentObserver );
    }
    public void refreshList(){
     ContactAdapter contactAdapter = new ContactAdapter(context);
        allList.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
    }
    //22
    public void showDialog(final String name, String phone, final int contact_id, final int raw_contact_id){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final View innerView = getActivity().getLayoutInflater().inflate(R.layout.dialog_input, null);
        dialog
                .setTitle("입력")
                .setView(innerView);
        etName = (EditText)innerView.findViewById(R.id.etName);
        etPhone = (EditText)innerView.findViewById(R.id.etPhone);

        String name2 = etName.getText().toString();
        String etPhone2 = etPhone.getText().toString();

//        etName.setText(name2);
//        etPhone.setText(etPhone2);

        etName.setText(name);
        etPhone.setText(phone);

        final AlertDialog alertDialog = dialog.create();
        btnCancel = (Button)innerView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        btnSuccess = (Button)innerView.findViewById(R.id.btnSuccess);
        btnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mContact.insertItem(etName.getText().toString(),etPhone.getText().toString(),raw_contact_id);
                mContact.update(etName.getText().toString(), etPhone.getText().toString(), contact_id);
                listViewItemList = new Contact(context).gettestdata();
                listViewItemList.set(pos,new Friend(etName.getText().toString(), etPhone.getText().toString(), null,raw_contact_id, contact_id));
                refreshList();
                Toast.makeText(context,"확인",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
    //insert
    public void showDialog( final int raw_contact_id){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final View innerView = getActivity().getLayoutInflater().inflate(R.layout.dialog_input, null);
        dialog
                .setTitle("입력")
                .setView(innerView);
        etName = (EditText)innerView.findViewById(R.id.etName);
        etPhone = (EditText)innerView.findViewById(R.id.etPhone);


        final AlertDialog alertDialog = dialog.create();
        btnCancel = (Button)innerView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        String uPhone = etPhone.getText().toString();

        btnSuccess = (Button)innerView.findViewById(R.id.btnSuccess);

        btnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, 200);
                mContact.insertItem(etName.getText().toString(), etPhone.getText().toString(),raw_contact_id);

//                listViewItemList.add(listViewItemList.size()-1,new Friend(etName.getText().toString(),etPhone.getText().toString(),null,null));
                refreshList();
                Toast.makeText(context,"확인",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
