package com.sahilhans0605.firebaseusersignup.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.sahilhans0605.firebaseusersignup.dataModel.Messages;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.databinding.ChatItemReceiveBinding;
import com.sahilhans0605.firebaseusersignup.databinding.ChatItemSentBinding;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;


    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_sent, parent, false);
            return new sentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_receive, parent, false);
            return new receiveViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(messages.getSenderId())) {
//            means item is sent
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesArrayList.get(position);

        if (holder.getClass() == sentViewHolder.class) {
            sentViewHolder viewHolder = (sentViewHolder) holder;

            if (messages.getMessage().equals("Image")) {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(messages.getPurl()).placeholder(R.drawable.loadingchatimage).into(viewHolder.binding.image);
            }
            viewHolder.binding.message.setText(messages.getMessage());
        } else {
            receiveViewHolder viewHolder = (receiveViewHolder) holder;
            if (messages.getMessage().equals("Image")) {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(messages.getPurl()).placeholder(R.drawable.placeholder).into(viewHolder.binding.image);

            }

            viewHolder.binding.message.setText(messages.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    public class sentViewHolder extends RecyclerView.ViewHolder {
        ChatItemSentBinding binding;

        public sentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatItemSentBinding.bind(itemView);
        }
    }

    public class receiveViewHolder extends RecyclerView.ViewHolder {

        ChatItemReceiveBinding binding;

        public receiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatItemReceiveBinding.bind(itemView);
        }
    }
}
