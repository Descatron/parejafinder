package com.aplicafran.parejafinder.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicafran.parejafinder.R;
import com.aplicafran.parejafinder.data.CandidateProfile;
import com.aplicafran.parejafinder.data.ProfileRepository;
import com.aplicafran.parejafinder.data.UserMatch;

import java.util.List;

public class DiscoverFragment extends Fragment {
    public interface Callbacks {
        String getCurrentUsername();
        String getCurrentEmail();
        ProfileRepository getRepository();
        void onNewMatch();
    }

    private ProfileAdapter adapter;
    private EditText etBusqueda;
    private Spinner spCiudad;
    private SeekBar sbEdad;
    private TextView tvEdad;
    private TextView tvVacio;

    public DiscoverFragment() {
        super(R.layout.fragment_discover);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        etBusqueda = view.findViewById(R.id.etBusqueda);
        spCiudad = view.findViewById(R.id.spCiudad);
        sbEdad = view.findViewById(R.id.sbEdad);
        tvEdad = view.findViewById(R.id.tvEdad);
        tvVacio = view.findViewById(R.id.tvVacio);
        Button btnFiltrar = view.findViewById(R.id.btnFiltrar);
        Button btnLimpiar = view.findViewById(R.id.btnLimpiar);
        RecyclerView rvPerfiles = view.findViewById(R.id.rvPerfiles);

        adapter = new ProfileAdapter(this::showProfileDialog);
        rvPerfiles.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPerfiles.setAdapter(adapter);

        ArrayAdapter<CharSequence> ciudadesAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.ciudades_filtro, android.R.layout.simple_spinner_item);
        ciudadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCiudad.setAdapter(ciudadesAdapter);

        sbEdad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvEdad.setText(getString(R.string.edad_maxima, progress + 18));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnFiltrar.setOnClickListener(v -> applyFilters());
        btnLimpiar.setOnClickListener(v -> {
            etBusqueda.setText("");
            spCiudad.setSelection(0);
            sbEdad.setProgress(22);
            applyFilters();
        });

        applyFilters();
        return view;
    }

    private void applyFilters() {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        List<CandidateProfile> results = callbacks.getRepository().search(
                callbacks.getCurrentEmail(),
                etBusqueda.getText().toString(),
                String.valueOf(spCiudad.getSelectedItem()),
                sbEdad.getProgress() + 18
        );
        adapter.submit(results);
        if (results.isEmpty()) {
            int available = callbacks.getRepository().countAvailableCandidates(callbacks.getCurrentEmail());
            tvVacio.setText(available == 0 ? R.string.sin_usuarios_disponibles : R.string.sin_resultados);
            tvVacio.setVisibility(View.VISIBLE);
        } else {
            tvVacio.setVisibility(View.GONE);
        }
    }

    private void showProfileDialog(CandidateProfile profile) {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        String detail = getString(R.string.detalle_perfil, profile.nombre, profile.edad, profile.ciudad, profile.intereses);
        new AlertDialog.Builder(requireContext())
                .setTitle(profile.nombre)
                .setMessage(detail)
                .setNegativeButton(R.string.accion_descartar, (d, w) ->
                        Toast.makeText(requireContext(), getString(R.string.descartar_confirmado, profile.nombre), Toast.LENGTH_SHORT).show())
                .setPositiveButton(R.string.accion_me_interesa, (d, w) -> {
                    ProfileRepository.MatchResult result = callbacks.getRepository().addMatchResult(
                            callbacks.getCurrentUsername(), profile, UserMatch.TYPE_LIKE);
                    if (result == ProfileRepository.MatchResult.CREATED) {
                        Toast.makeText(requireContext(), getString(R.string.match_confirmado, profile.nombre), Toast.LENGTH_SHORT).show();
                        callbacks.onNewMatch();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.match_ya_existia, profile.nombre), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton(R.string.accion_super_like, (d, w) -> {
                    ProfileRepository.MatchResult result = callbacks.getRepository().addMatchResult(
                            callbacks.getCurrentUsername(), profile, UserMatch.TYPE_SUPER_LIKE);
                    if (result == ProfileRepository.MatchResult.CREATED) {
                        Toast.makeText(requireContext(), getString(R.string.super_like_confirmado, profile.nombre), Toast.LENGTH_SHORT).show();
                        callbacks.onNewMatch();
                    } else if (result == ProfileRepository.MatchResult.UPGRADED_TO_SUPER_LIKE) {
                        Toast.makeText(requireContext(), getString(R.string.super_like_upgrade_confirmado, profile.nombre), Toast.LENGTH_SHORT).show();
                        callbacks.onNewMatch();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.super_like_ya_existia, profile.nombre), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private Callbacks getCallbacks() {
        if (requireActivity() instanceof Callbacks) {
            return (Callbacks) requireActivity();
        }
        return null;
    }
}
