package com.aplicafran.parejafinder.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicafran.parejafinder.R;
import com.aplicafran.parejafinder.data.CandidateProfile;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    public interface OnProfileClickListener {
        void onClick(CandidateProfile profile);
    }

    private final List<CandidateProfile> items = new ArrayList<>();
    private final OnProfileClickListener listener;

    public ProfileAdapter(OnProfileClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<CandidateProfile> profiles) {
        items.clear();
        items.addAll(profiles);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        CandidateProfile profile = items.get(position);
        holder.tvNombreEdad.setText(profile.nombre + " - " + profile.edad);
        holder.tvCiudad.setText(profile.ciudad);
        holder.tvIntereses.setText(profile.intereses);
        holder.ivAvatar.setImageResource(getAvatarByCity(profile.ciudad));
        holder.itemView.setOnClickListener(v -> listener.onClick(profile));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvNombreEdad;
        TextView tvCiudad;
        TextView tvIntereses;

        ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNombreEdad = itemView.findViewById(R.id.tvNombreEdad);
            tvCiudad = itemView.findViewById(R.id.tvCiudad);
            tvIntereses = itemView.findViewById(R.id.tvIntereses);
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
