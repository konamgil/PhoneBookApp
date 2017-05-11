package com.letscombintest.phonebook.phone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.letscombintest.phonebook.phone.adapter.DataAdapter;
import com.letscombintest.phonebook.phone.R;

/**
 * Created by konamgil on 2017-05-10.
 */

public class FavoriteFragment extends Fragment {
    private Context context;
    private DataAdapter mDbHelper;
    private View view;

    public static FavoriteFragment newInstance(Context context){
        FavoriteFragment ff = new FavoriteFragment();
        ff.context = context;
        return ff;
    }

    public FavoriteFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.favorite_fragment,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDbHelper = new DataAdapter(context);
        mDbHelper.createDatabase();
        mDbHelper.open();

        GridView gridView = (GridView)view.findViewById(R.id.gridView);
    }
}
