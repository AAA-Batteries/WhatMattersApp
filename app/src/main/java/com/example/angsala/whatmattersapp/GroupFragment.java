package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment {
    List<String> groups;
    RecyclerView rvContacts;
    GroupAdapter adapter;
    ParseUser user;
    TextView groupName;
    ImageView groupImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group, container, false);

        groupImage = v.findViewById(R.id.groupImage);
        groupName = v.findViewById(R.id.groupName);

        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvContacts = view.findViewById(R.id.rvContacts);

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.toolbar_groups);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        groups = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            groups.add("");
        }

        groups.set((int) ParseUser.getCurrentUser().get("Parents") - 1, "Parents");
        groups.set((int) ParseUser.getCurrentUser().get("Family") - 1, "Family");
        groups.set((int) ParseUser.getCurrentUser().get("Friends") - 1, "Friends");
        groups.set((int) ParseUser.getCurrentUser().get("Classmates") - 1, "Classmates");
        groups.set((int) ParseUser.getCurrentUser().get("Professors") - 1, "Professors");
        //where we will write the groups?
        writeItems();

        // Initialize contacts
        // Create adapter passing in the sample user data

        //where we will read the groups?
        readItems();
        adapter = new GroupAdapter(groups);

        // Attach the adapter to the recyclerview to populate items

        // Set layout manager to position the items
        rvContacts.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvContacts.setAdapter(adapter);
        Log.d("Groups", groups.toString());
        groupImage = view.findViewById(R.id.groupImage);
        groupName = view.findViewById(R.id.groupName);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.groups_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reprioritize:
                launchPriorityActivity();
                return true;
            default:
                // If we got here, the user's action was not recognized
                return super.onOptionsItemSelected(item);
        }
    }

    //new code for persistence:

    private File getDataFile() {
        return new File(getContext().getFilesDir(), ParseUser.getCurrentUser().getObjectId() + "groups.txt");
    }

    private void readItems() {
        //try-catch block to take care off unhandled exceptions
        try {
            // create the array using the content in the file
            groups = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            //print the error to the console
            e.printStackTrace();
            groups = new ArrayList<>();
        }
    }

    //write the items to the file system
    private void writeItems() {
        try {
            //save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), groups);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing file", e);

        }
    }

    public void launchPriorityActivity() {
        Intent intent = new Intent(getActivity(), PriorityActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        GroupAdapter.relationship = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GroupAdapter.relationship.add("");
        }

        GroupAdapter.relationship.set((int) ParseUser.getCurrentUser().get("Parents") - 1, "Parents");
        GroupAdapter.relationship.set((int) ParseUser.getCurrentUser().get("Family") - 1, "Family");
        GroupAdapter.relationship.set((int) ParseUser.getCurrentUser().get("Friends") - 1, "Friends");
        GroupAdapter.relationship.set((int) ParseUser.getCurrentUser().get("Classmates") - 1, "Classmates");
        GroupAdapter.relationship.set((int) ParseUser.getCurrentUser().get("Professors") - 1, "Professors");
        adapter.notifyDataSetChanged();
    }

}


