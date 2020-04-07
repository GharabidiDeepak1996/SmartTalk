package com.example.smarttalk.adapter;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.smarttalk.fragment.ChatsFragment;
import com.example.smarttalk.fragment.ContactsFragment;
import com.example.smarttalk.fragment.ProfileFragment;

import java.util.List;

public class TabLayoutAdapter extends FragmentStatePagerAdapter {
    private List<String> List1;

    public TabLayoutAdapter(FragmentManager fm, List<String> List) {
        super( fm );

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
            case 2:
                ProfileFragment profileFragment=new ProfileFragment();
                return profileFragment;
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
