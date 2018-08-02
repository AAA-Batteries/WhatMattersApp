package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.angsala.whatmattersapp.model.Contacts;
import com.example.angsala.whatmattersapp.model.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class ProfileFragment extends Fragment {

TextView profileUsername;
TextView numberOfContacts;
ProgressBar circleBar;
TextView txtvPercentage;
String currentUsername;
int contactAmount;
ParseUser user;
ProfileImageHelper helper;
ImageView profile;
User user1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();
        helper = new ProfileImageHelper();
        profileUsername = (TextView) getActivity().findViewById(R.id.profileuserName);
        circleBar = (ProgressBar) getActivity().findViewById(R.id.circleprogressBar);
        txtvPercentage = (TextView) getActivity().findViewById(R.id.txtvPercentage);
        numberOfContacts = (TextView) getActivity().findViewById(R.id.numberOfContacts);
        profile = getActivity().findViewById(R.id.ivProfileImage);
        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        currentUsername = user.getUsername();
        profileUsername.setText(currentUsername);



        //consider making this a local field in user class
        ParseQuery<Contacts> contactQuery = ParseQuery.getQuery(Contacts.class).whereEqualTo("Owner", currentUsername);
        contactQuery.findInBackground(new FindCallback<Contacts>() {
            @Override
            public void done(List<Contacts> objects, ParseException e) {
                contactAmount = objects.size();
                numberOfContacts.setText(Integer.toString(contactAmount) + " contacts");

            }
        });
        int x = user.getInt("Friends");
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", currentUsername);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                double uRanking = object.getDouble("UserRanking");
                circleBar.setProgress((int) uRanking);
                txtvPercentage.setText(Double.toString(uRanking) + "%");
                ParseFile img = object.getParseFile("ProfileImage");
                String imgUrl = "";
                if (img != null){
                    imgUrl = img.getUrl();

                }




                    GlideApp.with(getActivity()).load(imgUrl).apply(RequestOptions.circleCropTransform()).into(profile);
             //   Glide.with(getActivity()).load(imgUrl).transform










            }
        });


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_logout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.logOut:
                Log.d(TAG, "Clicked on logout button");
                user.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
                return true;

            default:
                // If we got here, the user's action was not recognized
                return super.onOptionsItemSelected(item);
        }
    }
}
