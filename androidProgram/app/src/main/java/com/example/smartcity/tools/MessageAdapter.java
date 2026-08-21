package com.example.smartcity.tools;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcity.databinding.ItemContainerReceiverBinding;
import com.example.smartcity.databinding.ItemContainerSendMessageBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author :Shangyi Shen
 * UID: u7735222
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final Bitmap receiverAvatar;
    private final List<Message> messageList;
    private final String currentUserId;
    public static final int VIEW_TYPE_SENT=1;
    public static final int VIEW_TYPE_RECEIVED=2;

    public MessageAdapter(Bitmap receiverAvatar, List<Message> messageList, String currentUserId) {
        this.receiverAvatar = receiverAvatar;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        // check the type of ViewHolder
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).setData(message);
        } else if (holder instanceof ReceiverViewHolder) {
            ((ReceiverViewHolder) holder).setData(message, receiverAvatar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            // load layout of sending message
            ItemContainerSendMessageBinding binding = ItemContainerSendMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new MessageViewHolder(binding);
        } else {
            //load layout of receive message
            ItemContainerReceiverBinding binding = ItemContainerReceiverBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ReceiverViewHolder(binding);
        }
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getSender().equals(currentUserId)){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    // ViewHolder is used to save a view reference
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSendMessageBinding binding;

        public MessageViewHolder(@NonNull ItemContainerSendMessageBinding itemContainerSendMessageBinding) {
            super(itemContainerSendMessageBinding.getRoot());
            binding=itemContainerSendMessageBinding;

        }
        void setData(Message message){
            Log.d("MessageAdapter",message.message);
            binding.textMessage.setText(message.message);
            binding.textDate.setText(formatTimestamp(message.timestamp));
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceiverBinding binding;
        ReceiverViewHolder(ItemContainerReceiverBinding itemContainerReceiverBinding){
            super(itemContainerReceiverBinding.getRoot());
            binding=itemContainerReceiverBinding;
        }
        void setData(Message message,Bitmap receiverAvatar){
            binding.textMessage.setText(message.message);
            binding.textDate.setText(formatTimestamp(message.timestamp));
            binding.friendAvatar.setImageBitmap(receiverAvatar);
        }
    }

    public static String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss 'UTC'XXX", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+10"));  // Set the timezone
        return sdf.format(new Date(timestamp));
    }
}
