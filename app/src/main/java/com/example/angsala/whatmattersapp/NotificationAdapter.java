package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.angsala.whatmattersapp.model.Contacts;
import com.example.angsala.whatmattersapp.model.Message;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<Message> notificationMessages;


    public NotificationAdapter(List<Message> notifMessages) {
        this.notificationMessages = notifMessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_notifications, parent, false);

        return new NotificationAdapter.ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ViewHolder viewHolder, int position) {

        Message currentMessage = notificationMessages.get(position);
        String username = null;
        try {
            username = currentMessage.getParseUserSender().fetchIfNeeded().getUsername();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String body = currentMessage.getBody();
        viewHolder.txtvName.setText(username);
        viewHolder.txtvBody.setText(body + "  " + currentMessage.getRelativeTimeAgo());
       // viewHolder.timeStamp.setText(currentMessage.getRelativeTimeAgo());

//        if (currentMessage.getUserReceivedPriority() < 3) {
////            viewHolder.txHoistReason.setText(R.string.priority_hoist);
////        } else if (currentMessage.getBuzzwordsDetected()) {
////            viewHolder.txHoistReason.setText(R.string.buzzword_hoist);
////        }
        if (currentMessage.getUserReceivedPriority() < 3 && currentMessage.getBuzzwordsDetected()) {
            viewHolder.txHoistReason.setText("DOUBLE FLAG");

        } else if (currentMessage.getBuzzwordsDetected()) {
            viewHolder.txHoistReason.setText(R.string.buzzword_hoist);
        }
        //this shouldnt run unless the above return false
        else if (currentMessage.getUserReceivedPriority() < 3) {
            viewHolder.txHoistReason.setText(R.string.priority_hoist);
        }

        String currentUsername = currentMessage.getParseUserSender().getUsername();
        ParseQuery<ParseUser> query1 = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", currentUsername);
        query1.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {

                ParseFile img = object.getParseFile("ProfileImage");
                String imgUrl = "";
                if (img != null) {
                    imgUrl = img.getUrl();

                }
                GlideApp.with(context).load(imgUrl).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(viewHolder.imvPicture);


            }
        });

        ParseQuery<Contacts> query = ParseQuery.getQuery(Contacts.class).whereEqualTo("Owner", ParseUser.getCurrentUser().getUsername()).whereEqualTo("ContactName", username);
        query.getFirstInBackground(new GetCallback<Contacts>() {
            @Override
            public void done(Contacts object, ParseException e) {

                if (e == null) {
                    String relationship = object.getString("Relationship");
                    Log.d("Relationship", relationship);
                    if (relationship.equals("Friends")) {
                        int color = context.getResources().getColor(R.color.Friends);
                        GlideApp.with(context).load(R.drawable.friendszz).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivNotiffz);
                        viewHolder.ivNotiffz.setColorFilter(color);
                        viewHolder.ivNotiffz.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));
                    } else if (relationship.equals("Parents")) {
                        int color = context.getResources().getColor(R.color.Parents);
                        GlideApp.with(context).load(R.drawable.parent_guardian).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivNotiffz);
                        viewHolder.ivNotiffz.setColorFilter(color);
                        viewHolder.ivNotiffz.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));
                    } else if (relationship.equals("Classmates")) {
                        int color = context.getResources().getColor(R.color.Classmates);

                        GlideApp.with(context).load(R.drawable.bookszz).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivNotiffz);
                        viewHolder.ivNotiffz.setColorFilter(color);
                        viewHolder.ivNotiffz.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));
                    } else if (relationship.equals("Family")) {
                        int color = context.getResources().getColor(R.color.Family);
                        GlideApp.with(context).load(R.drawable.familyzz).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivNotiffz);
                        viewHolder.ivNotiffz.setColorFilter(color);
                        viewHolder.ivNotiffz.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));
                    } else {
                        int color = context.getResources().getColor(R.color.colorAccent);
                        GlideApp.with(context).load(R.drawable.classzz).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivNotiffz);
                        viewHolder.ivNotiffz.setColorFilter(color);
                        viewHolder.ivNotiffz.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));
                    }


                } else {
                    e.printStackTrace();
            }

            }

        });



    }

    @Override
    public int getItemCount() {
        return notificationMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView txtvName;
        TextView txtvBody;
        ImageView imvPicture;
        public RelativeLayout viewBackground, foreBackground;
        TextView timeStamp;
        TextView txHoistReason;
        ImageView ivNotiffz;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtvName = (TextView) itemView.findViewById(R.id.tvNotificationUsername);
            txtvBody = (TextView) itemView.findViewById(R.id.tvNotificationBody);
            imvPicture = (ImageView) itemView.findViewById(R.id.ivNotification);
            viewBackground = itemView.findViewById(R.id.view_background);
            foreBackground = itemView.findViewById(R.id.view_foreground);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            txHoistReason = itemView.findViewById(R.id.txHoistReason);
            ivNotiffz = itemView.findViewById(R.id.ivNotifzz);


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewPosition = getAdapterPosition();
            if (viewPosition != RecyclerView.NO_POSITION) {
                Message message = notificationMessages.get(viewPosition);
                String recipient = message.getParseUserSender().getUsername();
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("Recipient", recipient);
                context.startActivity(chatIntent);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Toast.makeText(context, "Long clicked", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void removeItem(int position) {
        notificationMessages.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Message messages, int position) {
        notificationMessages.add(position, messages);
        notifyItemInserted(position);
    }

    public void clear() {
        notificationMessages.clear();
        notifyDataSetChanged();
    }
}
