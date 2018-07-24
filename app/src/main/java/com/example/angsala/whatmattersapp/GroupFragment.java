package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
<<<<<<< HEAD
=======
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
>>>>>>> 3fbf6f95dd54e6d86df2b84cd3f9009804c8b33a
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
<<<<<<< HEAD
//
//        // Lookup the recyclerview in activity layout
//        RecyclerView rvContacts = getView().findViewById(R.id.rvContacts);
//
//        // Initialize contacts
//        contacts = (ArrayList) ParseUser.getCurrentUser().get("contacts");
//        // Create adapter passing in the sample user data
//        ContactsAdapter adapter = new ContactsAdapter(contacts);
//        // Attach the adapter to the recyclerview to populate items
//        rvContacts.setAdapter(adapter);
//        // Set layout manager to position the items
//        rvContacts.setLayoutManager(new LinearLayoutManager(this.getContext()));
//        // That's all!
//
//        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
//        StaggeredGridLayoutManager gridLayoutManager =
//                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        // Attach the layout manager to the recycler view
//        rvContacts.setLayoutManager(gridLayoutManager);
=======


        // That's all!

        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
      //  StaggeredGridLayoutManager gridLayoutManager =

        // Attach the layout manager to the recycler view

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
>>>>>>> 3fbf6f95dd54e6d86df2b84cd3f9009804c8b33a
    }

}
