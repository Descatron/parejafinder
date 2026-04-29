package com.aplicafran.parejafinder.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicafran.parejafinder.R;
import com.aplicafran.parejafinder.data.ChatMessage;
import com.aplicafran.parejafinder.data.ProfileRepository;

import java.util.List;

public class ChatFragment extends Fragment {
    private static final String ARG_CANDIDATE_ID = "candidate_id";
    private static final String ARG_CANDIDATE_NAME = "candidate_name";

    public interface Callbacks {
        String getCurrentUsername();
        ProfileRepository getRepository();
        void notifyNewMessage(int candidateId, String candidateName, String messagePreview, int notificationId);
    }

    private ChatMessageAdapter adapter;
    private int candidateId;
    private String candidateName;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ChatFragment() {
        super(R.layout.fragment_chat);
    }

    public static ChatFragment newInstance(int candidateId, String candidateName) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CANDIDATE_ID, candidateId);
        args.putString(ARG_CANDIDATE_NAME, candidateName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            candidateId = args.getInt(ARG_CANDIDATE_ID, 0);
            candidateName = args.getString(ARG_CANDIDATE_NAME, "");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvChatTitle = view.findViewById(R.id.tvChatTitle);
        TextView tvTyping = view.findViewById(R.id.tvTyping);
        RecyclerView rvChatMessages = view.findViewById(R.id.rvChatMessages);
        EditText etChatMessage = view.findViewById(R.id.etChatMessage);
        Button btnSendChat = view.findViewById(R.id.btnSendChat);

        tvChatTitle.setText(getString(R.string.chat_titulo, candidateName));
        adapter = new ChatMessageAdapter();
        rvChatMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvChatMessages.setAdapter(adapter);

        btnSendChat.setOnClickListener(v -> {
            Callbacks callbacks = getCallbacks();
            if (callbacks == null) {
                return;
            }
            String message = etChatMessage.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }
            callbacks.getRepository().sendMessage(callbacks.getCurrentUsername(), candidateId, "ME", message);
            etChatMessage.setText("");
            refreshMessages();
            tvTyping.setVisibility(View.VISIBLE);
            handler.postDelayed(() -> {
                Callbacks cb = getCallbacks();
                if (cb == null) {
                    return;
                }
                String autoReply = getAutoReply(message);
                cb.getRepository().sendMessage(cb.getCurrentUsername(), candidateId, "MATCH", autoReply);
                tvTyping.setVisibility(View.GONE);
                refreshMessages();
                Toast.makeText(requireContext(), getString(R.string.chat_nuevo_de, candidateName), Toast.LENGTH_SHORT).show();
                cb.notifyNewMessage(candidateId, candidateName, autoReply, (candidateId * 31) + 7);
            }, 1200);
        });

        if (getCallbacks() != null) {
            getCallbacks().getRepository().markConversationRead(getCallbacks().getCurrentUsername(), candidateId);
        }
        refreshMessages();
    }

    private void refreshMessages() {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        List<ChatMessage> messages = callbacks.getRepository().getConversation(callbacks.getCurrentUsername(), candidateId);
        adapter.submit(messages);
        callbacks.getRepository().markConversationRead(callbacks.getCurrentUsername(), candidateId);
    }

    private String getAutoReply(String input) {
        if (input.toLowerCase().contains("hola")) {
            return "Hola! Encantado de hablar contigo.";
        }
        if (input.toLowerCase().contains("plan")) {
            return "Suena bien, podemos organizar algo este finde.";
        }
        return "Me gusta lo que dices :)";
    }

    private Callbacks getCallbacks() {
        if (requireActivity() instanceof Callbacks) {
            return (Callbacks) requireActivity();
        }
        return null;
    }
}
