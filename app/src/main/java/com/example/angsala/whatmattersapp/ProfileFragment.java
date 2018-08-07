package com.example.angsala.whatmattersapp;

import android.content.Intent;
import android.graphics.Color;
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

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;

import static android.support.constraint.Constraints.TAG;


public class ProfileFragment extends Fragment {

    TextView profileUsername;
    TextView numberOfContacts;
    ProgressBar circleBar;
    TextView txtvPercentage;
    String currentUsername;
    int contactAmount;
    ParseUser user;
    ImageView profile;
    KonfettiView viewKonfetti;
    TextView txtvPercentageExplanation;
    User user1;
    TextView profileStatus;


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
        profileUsername = (TextView) getActivity().findViewById(R.id.profileuserName);
        circleBar = (ProgressBar) getActivity().findViewById(R.id.circleprogressBar);
        txtvPercentage = (TextView) getActivity().findViewById(R.id.txtvPercentage);
        numberOfContacts = (TextView) getActivity().findViewById(R.id.numberOfContacts);
        txtvPercentageExplanation = (TextView) getActivity().findViewById(R.id.txtvPercentExplanation);
        profile = getActivity().findViewById(R.id.ivProfileImage);
        viewKonfetti = getActivity().findViewById(R.id.viewKonfetti);
        profileStatus = getActivity().findViewById(R.id.profileStatus);

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
                if (e == null) {
                    contactAmount = objects.size();
                    if (contactAmount == 1) {
                        numberOfContacts.setText(Integer.toString(contactAmount) + " contact");
                    } else {
                        numberOfContacts.setText(Integer.toString(contactAmount) + " contacts");
                    }
                }

            }
        });
        int x = user.getInt("Friends");
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", currentUsername);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    double uRanking = object.getDouble("UserRanking");
                    circleBar.setProgress((int) uRanking);
                    txtvPercentage.setText(Double.toString(uRanking) + "%");
                    if(uRanking <= 25){
                        txtvPercentageExplanation.setText(R.string.bottom_quarter_global);
                    }

                    else if (uRanking <= 50){
                        txtvPercentageExplanation.setText(R.string.twenty_five_percentile);
                    }

                    else if (uRanking <= 75){
                        txtvPercentageExplanation.setText(R.string.fifty_percentile);
                    }

                    else{
                        txtvPercentageExplanation.setText(R.string.seventy_five_percentile);
                    }


                    String myStatus =object.getString("ProfileStatus");
                    ParseFile img = object.getParseFile("ProfileImage");
                    String imgUrl = "";
                    if (img != null) {
                        imgUrl = img.getUrl();
                        GlideApp.with(getActivity()).load(imgUrl).apply(RequestOptions.circleCropTransform()).into(profile);
                    } else {
                        int id = getResources().getIdentifier("com.example.angsala.whatmattersapp:drawable/" + "instagram_user_filled_24", null, null);
                        profile.setImageResource(id);
                    }
                    //   Glide.with(getActivity()).load(imgUrl).transform
                    profileStatus.setText(myStatus);
                }
            }
        });

        int ranking = user.get("UserRanking") != null ? (Integer) user.get("UserRanking"): 0 ;
        if (ranking >= 70) {
            viewKonfetti.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.RECT, Shape.CIRCLE)
                    .addSizes(new nl.dionsegijn.konfetti.models.Size(12, 5))
                    .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                    .stream(300, 5000L);
        }

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
