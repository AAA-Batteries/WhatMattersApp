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


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {


    Context context;
    List<Contacts> contacts;
    ParseUser user;

    public ContactsAdapter(List<Contacts> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_contact, parent, false);

        return new ViewHolder(postView);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Contacts contact = contacts.get(position);

        ParseQuery<ParseUser> recipientQuery = ParseQuery.getQuery(ParseUser.class)
                .whereEqualTo("username", contact.getContactName());
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
                                if(!object.getMessages().isEmpty()) {
                                    String lastMessage = object.getMessages().get(0).getBody();
                                    viewHolder.tvMessage.setText(lastMessage + "\n" + (object.getMessages().get(0).getRelativeTimeAgo()));
                                    //viewHolder.timeCreated.setText((object.getMessages().get(0).getRelativeTimeAgo()));
                                }
                            }
                        }
                    });
                }
            }
        });


       String relationship = contact.getRelationship();
        if (relationship.equals("Friends")) {
            int color = context.getResources().getColor(R.color.Friends);
            GlideApp.with(context).load(R.drawable.friendszz).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivRelation);
            viewHolder.ivRelation.setColorFilter(color);
            viewHolder.ivRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));

        } else if (relationship.equals("Parents")) {
            int color = context.getResources().getColor(R.color.Parents);
            GlideApp.with(context).load(R.drawable.parent_guardian).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivRelation);
            viewHolder.ivRelation.setColorFilter(color);
            viewHolder.ivRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));

        } else if (relationship.equals("Classmates")) {
            int color = context.getResources().getColor(R.color.Classmates);
            GlideApp.with(context).load(R.drawable.bookszz_border).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivRelation);
            viewHolder.ivRelation.setColorFilter(color);
            viewHolder.ivRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));

        } else if (relationship.equals("Family")) {
            int color = context.getResources().getColor(R.color.Family);
            GlideApp.with(context).load(R.drawable.familyzz).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivRelation);
            viewHolder.ivRelation.setColorFilter(color);
            viewHolder.ivRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));

        } else {
            int color = context.getResources().getColor(R.color.colorAccent);
            GlideApp.with(context).load(R.drawable.classzz_border).apply(RequestOptions.circleCropTransform()).into(viewHolder.ivRelation);
            viewHolder.ivRelation.setColorFilter(color);
            viewHolder.ivRelation.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));

        }
        Boolean flag = contact.getFlag();

        if (flag) {
            viewHolder.flag.setImageResource(R.color.hoist_contact);
            viewHolder.flag.setImageAlpha(50);
            viewHolder.trend.setVisibility(View.VISIBLE);
            GlideApp.with(context).load(R.drawable.trendingdown).into(viewHolder.trend);


        } else {
            viewHolder.flag.setVisibility(View.INVISIBLE);
        }

        String currentUsername = contact.getContactName();

        ParseQuery<ParseUser> query1 = ParseQuery.getQuery(ParseUser.class).whereEqualTo("username", currentUsername);
        query1.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser object, ParseException e) {

                ParseFile img = object.getParseFile("ProfileImage");
                String imgUrl = "";
                if (img != null) {
                    imgUrl = img.getUrl();

                }


                GlideApp.with(context).load(imgUrl).apply(RequestOptions.circleCropTransform()).thumbnail(0.1f).into(viewHolder.contactImage);
                //   Glide.with(getActivity()).load(imgUrl).transform

                String session = object.getString("Session");
                viewHolder.tvSession.setText(session);

                if (session.equals("Online")){
                    int color = context.getResources().getColor(R.color.colorAccent);

                    viewHolder.ivSession.setColorFilter(color);
                } else if (session.equals("Offline")){
                    int color = context.getResources().getColor(R.color.Offline);
                    viewHolder.ivSession.setColorFilter(color);
                } else {
                    int color = context.getResources().getColor(R.color.Sleep_mode);
                    viewHolder.ivSession.setColorFilter(color);
                }


            }
        });


        //be able to use glide

        //viewHolder.contactImage.setImageResource(R.drawable.ic_launcher_background);


        viewHolder.tvUserName.setText(contact.getContactName());
        Log.d("adapter user ranking", viewHolder.tvUserName.toString());




    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView contactImage;
        TextView tvUserName;
        TextView tvMessage;
        ImageView contactColor;
        TextView relationship;
        ImageView flag;

        TextView timeCreated;
        ImageView ivRelation;
        ImageView ivSession;
        TextView tvSession;
        ImageView ivContact;
        ImageView trend;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contactImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);

            flag = itemView.findViewById(R.id.imvFlag);

            ivRelation = itemView.findViewById(R.id.ivRelation);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivSession = itemView.findViewById(R.id.ivSession);
            tvSession = itemView.findViewById(R.id.tvSession);

            ivContact = itemView.findViewById(R.id.ivContact);
            trend = itemView.findViewById(R.id.ivTrend);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewPosition = getAdapterPosition();
            if (viewPosition != RecyclerView.NO_POSITION) {
                Contacts contact = contacts.get(viewPosition);
                String contactname = contact.getContactName();
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("Recipient", contactname);
                context.startActivity(chatIntent);
            }
        }
    }

    public void clear() {
        contacts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Contacts> mcontacts) {
        contacts.addAll(mcontacts);
        notifyDataSetChanged();
    }

}
