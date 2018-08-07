package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
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

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
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
    Button btOpenPopUp;
    TextView tvReadMore, tvCollapse;
    double uRanking;


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
        btOpenPopUp = getActivity().findViewById(R.id.openpopup);
        tvReadMore = getActivity().findViewById(R.id.tvReadMore);
        tvCollapse = getActivity().findViewById(R.id.tvCollapse);

        tvReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvReadMore.setVisibility(View.GONE);
                tvCollapse.setVisibility(View.VISIBLE);
                txtvPercentageExplanation.setMaxLines(Integer.MAX_VALUE);
            }
        });

        tvCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvCollapse.setVisibility(View.GONE);
                tvReadMore.setVisibility(View.VISIBLE);
                txtvPercentageExplanation.setMaxLines(5);
            }
        });






        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        currentUsername = user.getUsername();
        profileUsername.setText(currentUsername);

        btOpenPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                double rank = user.getDouble("UserRanking");
                View popupView = inflater.inflate(R.layout.popup, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                ImageView myGraph = popupView.findViewById(R.id.myGraph);
                Log.d("Ranking", String.valueOf(rank));


                if (uRanking > 90 ){
                    GlideApp.with(getActivity()).load(R.drawable.finalchartz).transform(new RoundedCornersTransformation(50, 10)).into(myGraph);


                } else {
                    GlideApp.with(getActivity()).load(R.drawable.poorchart).transform(new RoundedCornersTransformation(50, 10)).into(myGraph);

                }

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        popupWindow.dismiss();
                    }
                });

                popupWindow.showAsDropDown(btOpenPopUp, 50, -30);

            }
        });






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
                    uRanking = object.getDouble("UserRanking");
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

                    if (uRanking >= 70) {
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
            }
        });

        //uRanking = user.get("UserRanking") != null ? (Integer) user.get("UserRanking"): 0 ;


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
