package com.example.angsala.whatmattersapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.angsala.whatmattersapp.model.Message;
import com.parse.ParseException;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<Message> notificationMessages;


    public NotificationAdapter(List<Message> notifMessages) {this.notificationMessages = notifMessages;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_notifications, parent, false);

        return new NotificationAdapter.ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder viewHolder, int position) {
    Message currentMessage = notificationMessages.get(position);
        String username = null;
        try {
            username = currentMessage.getParseUserSender().fetchIfNeeded().getUsername();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String body = currentMessage.getBody();
    viewHolder.txtvName.setText(username);
    viewHolder.txtvBody.setText(body);
    viewHolder.timeStamp.setText(currentMessage.getRelativeTimeAgo());
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtvName = (TextView) itemView.findViewById(R.id.tvNotificationUsername);
            txtvBody = (TextView) itemView.findViewById(R.id.tvNotificationBody);
            imvPicture = (ImageView) itemView.findViewById(R.id.ivNotification);
            viewBackground = itemView.findViewById(R.id.view_background);
            foreBackground = itemView.findViewById(R.id.view_foreground);
            timeStamp = itemView.findViewById(R.id.timeStamp);


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int viewPosition = getAdapterPosition();
            if (viewPosition != RecyclerView.NO_POSITION){
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

    public void removeItem(int position){
        notificationMessages.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Message messages, int position){
        notificationMessages.add(position, messages);
        notifyItemInserted(position);
    }
}
