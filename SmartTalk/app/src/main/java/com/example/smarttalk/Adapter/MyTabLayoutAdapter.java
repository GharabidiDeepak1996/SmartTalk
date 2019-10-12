package com.example.smarttalk.Adapter;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.smarttalk.Fragment.ChatsFragment;
import com.example.smarttalk.Fragment.ContactsFragment;

import java.util.List;

public class MyTabLayoutAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "MyTabLayoutAdapter";
    private List<String> List1;

    public MyTabLayoutAdapter(FragmentManager fm, List<String> List) {
        super( fm );
        Log.d( TAG, "MyTabLayoutAdapter: "+fm );
        Log.d( TAG, "MyTabLayoutAdapter: "+List );
        List1=List;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;
            case 1:
                ContactsFragment contactsFragment=new ContactsFragment();
                return contactsFragment;
                default:
                    return null;
        }
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return List1.get( position );                       //title
    }

    @Override
    public int getCount() {
        return List1.size();
    }
}
