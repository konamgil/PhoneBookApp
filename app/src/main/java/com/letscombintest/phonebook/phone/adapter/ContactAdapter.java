package com.letscombintest.phonebook.phone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.letscombintest.phonebook.phone.Contact;
import com.letscombintest.phonebook.phone.R;

import java.util.ArrayList;

/**
 * Created by jisun on 2017-05-14.
 */

public class ContactAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Friend> listViewItemList = new ArrayList<Friend>() ;
    public ContactAdapter() {;}

    public ContactAdapter(Context context) {
        this.context = context;
        Contact mContact= new Contact(context);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listViewItemList = mContact.gettestdata();
    }

    @Override
    public int getCount() {return listViewItemList.size();}

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_item, parent, false);
        }
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.ivPhoto) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.tvName) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.tvPhoneNumber) ;

        Friend frienditem = listViewItemList.get(position);

        if(frienditem.getPhoto() == null){
            Glide.with(context).load(R.mipmap.user).into(iconImageView);
        } else {
            Glide.with(context).load(frienditem.getPhoto()).into(iconImageView);
        }
        titleTextView.setText(frienditem.getName());
        descTextView.setText(frienditem.getPhoneNum());

        return convertView;
    }
    public void add(Friend item){
        listViewItemList.add(item);
        notifyDataSetChanged();
    }
}
