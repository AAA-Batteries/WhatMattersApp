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
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.example.angsala.whatmattersapp.model.Chat;
import com.example.angsala.whatmattersapp.model.Contacts;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ListGroupContactsAdapter extends RecyclerView.Adapter<ListGroupContactsAdapter.ViewHolder> {
    List<Contacts> contactsList;
    Context context;

    public ListGroupContactsAdapter(List<Contacts> contactsList) {
        this.contactsList = contactsList;

    }

    @NonNull
    @Override
    public ListGroupContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_grouplistcontact, parent, false);
        return new ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListGroupContactsAdapter.ViewHolder viewHolder, int position) {
        Contacts contacts = contactsList.get(position);

        ParseQuery<ParseUser> recipientQuery = ParseQuery.getQuery(ParseUser.class)
                .whereEqualTo("username", contacts.getContactName());
        recipientQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser recipient, ParseException e) {
                if (e == null) {
                    ParseQuery<Chat> q1 = ParseQuery.getQuery(Chat.class)
                            .whereEqualTo("User1", ParseUser.getCurrentUser())
                            .whereEqualTo("User2", recipient);
                    ParseQuery<Chat> q2 = ParseQuery.getQuery(Chat.class)
                            .whereEqualTo("User2", ParseUser.getCurrentUser())
                            .whereEqualTo("User1", recipient);
                    ArrayList<ParseQuery<Chat>> queries = new ArrayList<>();
                    queries.add(q1);
                    queries.add(q2);
                    ParseQuery<Chat> orQuery= ParseQuery.or(queries);

                    orQuery.getFirstInBackground(new GetCallback<Chat>() {
                        @Override
                        public void done(Chat object, ParseException e) {
                            if (e == null) {
                                if(object.getMessages().size() > 0) {
                                    String lastMessage = object.getMessages().get(0).getBody();
                                    viewHolder.myMessage.setText(lastMessage);
                                }
                            }
                        }
                    });
                }
            }
        });

        String relationship = contacts.getRelationship();
        if (relationship.equals("Friends")) {
            int color = context.getResources().getColor(R.color.Friends);
            GlideApp.with(context).load(R.drawable.friendszz).apply(RequestOptions.circleCropTransform()).into(viewHolder.myRelation);
            viewHolder.myRelation.setColorFilter(color);
            viewHolder.myRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));
        } else if (relationship.equals("Parents")) {
            int color = context.getResources().getColor(R.color.Parents);
            GlideApp.with(context).load(R.drawable.parent_guardian).apply(RequestOptions.circleCropTransform()).into(viewHolder.myRelation);
            viewHolder.myRelation.setColorFilter(color);
            viewHolder.myRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));


        } else if (relationship.equals("Classmates")) {
            int color = context.getResources().getColor(R.color.Classmates);
            GlideApp.with(context).load(R.drawable.bookszz).apply(RequestOptions.circleCropTransform()).into(viewHolder.myRelation);
            viewHolder.myRelation.setColorFilter(color);
            viewHolder.myRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));


        } else if (relationship.equals("Family")) {
            int color = context.getResources().getColor(R.color.Family);
            GlideApp.with(context).load(R.drawable.familyzz).apply(RequestOptions.circleCropTransform()).into(viewHolder.myRelation);
            viewHolder.myRelation.setColorFilter(color);
            viewHolder.myRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));

        } else {
            int color = context.getResources().getColor(R.color.colorAccent);
            GlideApp.with(context).load(R.drawable.classzz).apply(RequestOptions.circleCropTransform()).into(viewHolder.myRelation);
            viewHolder.myRelation.setColorFilter(color);
            viewHolder.myRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));
        }

        String currentUsername = contacts.getContactName();
        ParseQuery<ParseUser> query1 = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", currentUsername);
        query1.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {

                ParseFile img = object.getParseFile("ProfileImage");
                String imgUrl = "";
                if (img != null) {
                    imgUrl = img.getUrl();

                }
                GlideApp.with(context).load(imgUrl).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(viewHolder.contactGroupImage);


            }
        });


        Log.d("adapter", contacts.toString());
        viewHolder.contactGroupImage.setImageResource(R.drawable.ic_launcher_background);
        viewHolder.myContactName.setText(contacts.getContactName());
        Log.d("adapter", viewHolder.myContactName.toString());


    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView contactGroupImage;
        TextView myContactName;
        TextView alertMessage;
        ImageView myRelation;
        TextView myMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactGroupImage = itemView.findViewById(R.id.contactGroupImage);
            myContactName = itemView.findViewById(R.id.myContactName);
            alertMessage = itemView.findViewById(R.id.alertMessage);
            myRelation = itemView.findViewById(R.id.myRelation);
            myMessage = itemView.findViewById(R.id.myMessage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewPosition = getAdapterPosition();
            if (viewPosition != RecyclerView.NO_POSITION) {
                Contacts contact = contactsList.get(viewPosition);
                String contactname = contact.getContactName();
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("Recipient", contactname);
                context.startActivity(chatIntent);
            }


        }
    }
}
