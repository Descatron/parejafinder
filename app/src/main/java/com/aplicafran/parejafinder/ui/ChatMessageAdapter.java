package com.aplicafran.parejafinder.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicafran.parejafinder.R;
import com.aplicafran.parejafinder.data.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {
    private final List<ChatMessage> items = new ArrayList<>();

    public void submit(List<ChatMessage> messages) {
        items.clear();
        items.addAll(messages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessage item = items.get(position);
        holder.tvSender.setText("ME".equals(item.sender)
                ? holder.itemView.getContext().getString(R.string.chat_tu)
                : holder.itemView.getContext().getString(R.string.chat_match));
        holder.tvBody.setText(item.body);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvBody;

        ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvBody = itemView.findViewById(R.id.tvBody);
        }
    }
}
