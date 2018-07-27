package com.example.angsala.whatmattersapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.angsala.whatmattersapp.model.Message;
import com.example.angsala.whatmattersapp.model.Notification;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    ParseUser user;
    List<Message> notificationList;
    public final String TAG = "NotificationFragment";
    NotificationAdapter adapter;
    RecyclerView rvNotifications;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = ParseUser.getCurrentUser();
        notificationList = new ArrayList<>();

        adapter = new NotificationAdapter(notificationList);

        rvNotifications = (RecyclerView) getActivity().findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvNotifications.setAdapter(adapter);

        //adding the item touch helperI
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvNotifications);
        fetchNotifications();

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchNotifications();
    }


    public void fetchNotifications(){
        //try to get the notifications of the current user
        String username = user.getUsername();
        String i = user.getObjectId();
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class).whereEqualTo("UserReceived",user);
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if(e == null) {
                    if (objects.size() > 0) {
                        Notification mnotifciation = objects.get(0);
                        notificationList.addAll(mnotifciation.getReceived());
                        //hopefully returns a size not null
                        Log.d(TAG, Integer.toString(notificationList.size()));

                        adapter.notifyItemInserted(notificationList.size() - 1);


                    }
                }
                else{
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NotificationAdapter.ViewHolder){
            String name = notificationList.get(viewHolder.getAdapterPosition()).getUserReceived();
        }
        adapter.removeItem(viewHolder.getAdapterPosition());
    }
}
