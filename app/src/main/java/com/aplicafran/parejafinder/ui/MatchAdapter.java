package com.aplicafran.parejafinder.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicafran.parejafinder.R;
import com.aplicafran.parejafinder.data.MatchWithProfile;
import com.aplicafran.parejafinder.data.UserMatch;

import java.util.ArrayList;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {
    public interface OnMatchClickListener {
        void onClick(MatchWithProfile profile);
    }

    private final List<MatchWithProfile> items = new ArrayList<>();
    private final OnMatchClickListener listener;

    public MatchAdapter(OnMatchClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<MatchWithProfile> matches) {
        items.clear();
        items.addAll(matches);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchWithProfile profile = items.get(position);
        holder.tvNombreEdad.setText(profile.nombre + " - " + profile.edad);
        holder.tvCiudad.setText(profile.ciudad);
        holder.tvIntereses.setText(profile.intereses);
        holder.ivAvatar.setImageResource(getAvatarByCity(profile.ciudad));
        holder.tvBadgeTipo.setText(
                UserMatch.TYPE_SUPER_LIKE.equals(profile.matchType)
                        ? holder.itemView.getContext().getString(R.string.badge_super_like)
                        : holder.itemView.getContext().getString(R.string.badge_like)
        );
        if (profile.unreadCount > 0) {
            holder.tvBadgeTipo.setText(holder.tvBadgeTipo.getText() + " · " +
                    holder.itemView.getContext().getString(R.string.chat_nuevos, profile.unreadCount));
        }
        holder.itemView.setOnClickListener(v -> listener.onClick(profile));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvNombreEdad;
        TextView tvCiudad;
        TextView tvIntereses;
        TextView tvBadgeTipo;

        MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNombreEdad = itemView.findViewById(R.id.tvNombreEdad);
            tvCiudad = itemView.findViewById(R.id.tvCiudad);
            tvIntereses = itemView.findViewById(R.id.tvIntereses);
            tvBadgeTipo = itemView.findViewById(R.id.tvBadgeTipo);
        }
    }

    private int getAvatarByCity(String city) {
        if ("Madrid".equals(city)) {
            return android.R.drawable.ic_menu_camera;
        }
        if ("Barcelona".equals(city)) {
            return android.R.drawable.ic_menu_gallery;
        }
        if ("Valencia".equals(city)) {
            return android.R.drawable.ic_menu_compass;
        }
        if ("Sevilla".equals(city)) {
            return android.R.drawable.ic_menu_mapmode;
        }
        if ("Bilbao".equals(city)) {
            return android.R.drawable.ic_menu_myplaces;
        }
        return android.R.drawable.sym_def_app_icon;
    }
}
