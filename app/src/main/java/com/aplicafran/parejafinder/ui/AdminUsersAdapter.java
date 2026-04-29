package com.aplicafran.parejafinder.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicafran.parejafinder.R;
import com.aplicafran.parejafinder.data.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.AdminUserViewHolder> {
    public interface Listener {
        void onEditPassword(UserAccount account);
        void onDelete(UserAccount account);
        void onToggleBlock(UserAccount account);
        void onPrivateMessage(UserAccount account);
    }

    private final List<UserAccount> items = new ArrayList<>();
    private final Listener listener;

    public AdminUsersAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<UserAccount> users) {
        items.clear();
        items.addAll(users);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new AdminUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {
        UserAccount account = items.get(position);
        holder.tvEmail.setText(holder.itemView.getContext().getString(R.string.admin_usuario, account.email));
        holder.tvRole.setText(account.isAdmin == 1
                ? R.string.admin_rol_admin
                : R.string.admin_rol_usuario);
        if (account.isBlocked == 1) {
            holder.tvStatus.setText(R.string.admin_estado_bloqueado);
        } else if (account.isOnline == 1) {
            holder.tvStatus.setText(R.string.admin_estado_conectado);
        } else {
            holder.tvStatus.setText(R.string.admin_estado_desconectado);
        }
        holder.tvData.setText(holder.itemView.getContext().getString(
                R.string.admin_datos_usuario,
                safe(account.displayName),
                account.age,
                safe(account.city)
        ));
        holder.btnEditPass.setOnClickListener(v -> listener.onEditPassword(account));
        holder.btnDelete.setEnabled(account.isAdmin == 0);
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(account));
        holder.btnBlock.setEnabled(account.isAdmin == 0);
        holder.btnBlock.setText(account.isBlocked == 1
                ? R.string.admin_desbloquear_usuario
                : R.string.admin_bloquear_usuario);
        holder.btnBlock.setOnClickListener(v -> listener.onToggleBlock(account));
        holder.btnPrivateMsg.setOnClickListener(v -> listener.onPrivateMessage(account));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class AdminUserViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail;
        TextView tvRole;
        TextView tvStatus;
        TextView tvData;
        Button btnEditPass;
        Button btnDelete;
        Button btnBlock;
        Button btnPrivateMsg;

        public AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvAdminEmail);
            tvRole = itemView.findViewById(R.id.tvAdminRole);
            tvStatus = itemView.findViewById(R.id.tvAdminStatus);
            tvData = itemView.findViewById(R.id.tvAdminData);
            btnEditPass = itemView.findViewById(R.id.btnAdminEditPass);
            btnDelete = itemView.findViewById(R.id.btnAdminDelete);
            btnBlock = itemView.findViewById(R.id.btnAdminBlock);
            btnPrivateMsg = itemView.findViewById(R.id.btnAdminPrivateMsg);
        }
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }
}
