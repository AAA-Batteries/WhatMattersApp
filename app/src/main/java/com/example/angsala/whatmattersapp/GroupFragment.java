package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.angsala.whatmattersapp.model.Contacts;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.example.angsala.whatmattersapp.LoginActivity.TOAST_CODE;


public class GroupFragment extends Fragment {
    ArrayList<Contacts> contacts;
    RecyclerView rvContacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvContacts = view.findViewById(R.id.rvContacts);
        // Create adapter passing in the sample user data
        GroupAdapter adapter = new GroupAdapter(contacts);
        // Attach the adapter to the recyclerview to populate items

        // Set layout manager to position the items
        rvContacts.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvContacts.setAdapter(adapter);

    }

    public void launchPriorityActivity(View view) {
        Intent intent = new Intent(getActivity(), PriorityActivity.class);
        startActivity(intent);
    }

}
