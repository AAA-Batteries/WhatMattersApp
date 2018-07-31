package com.example.angsala.whatmattersapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.angsala.whatmattersapp.model.Message;

public class SentMessageHolder  extends RecyclerView.ViewHolder{
    TextView messageText, timeText;
    ImageView profileImage;
    public SentMessageHolder(@NonNull View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
    }

    void bind (Message message){
        messageText.setText(message.getBody());

        timeText.setText(message.getRelativeTimeAgo());
    }
}
