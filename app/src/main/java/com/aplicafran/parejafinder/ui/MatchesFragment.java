package com.aplicafran.parejafinder.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicafran.parejafinder.R;
import com.aplicafran.parejafinder.data.MatchWithProfile;
import com.aplicafran.parejafinder.data.UserMatch;

import java.util.ArrayList;
import java.util.List;

public class MatchesFragment extends Fragment {
    public interface Callbacks {
        String getCurrentUsername();
        com.aplicafran.parejafinder.data.ProfileRepository getRepository();
        void openChat(int candidateId, String candidateName);
    }

    private MatchAdapter adapter;
    private TextView tvMatchesTotal;
    private TextView tvVacioMatches;
    private Spinner spFiltroTipo;

    public MatchesFragment() {
        super(R.layout.fragment_matches);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvMatchesTotal = view.findViewById(R.id.tvMatchesTotal);
        tvVacioMatches = view.findViewById(R.id.tvVacioMatches);
        spFiltroTipo = view.findViewById(R.id.spFiltroTipo);
        RecyclerView rvMatches = view.findViewById(R.id.rvMatches);
        adapter = new MatchAdapter(match -> {
            Callbacks callbacks = getCallbacks();
            if (callbacks != null) {
                callbacks.openChat(match.candidateId, match.nombre);
            }
        });
        rvMatches.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMatches.setAdapter(adapter);
        ArrayAdapter<CharSequence> filtroAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.filtro_tipo_matches,
                android.R.layout.simple_spinner_item
        );
        filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltroTipo.setAdapter(filtroAdapter);
        spFiltroTipo.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                refresh();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        refresh();
    }

    public void refresh() {
        Callbacks callbacks = getCallbacks();
        if (callbacks != null) {
            List<MatchWithProfile> matches = callbacks.getRepository().getMatches(callbacks.getCurrentUsername());
            List<MatchWithProfile> filtered = applyTypeFilter(matches);
            adapter.submit(filtered);
            tvMatchesTotal.setText(getString(R.string.matches_total, filtered.size()));
            tvVacioMatches.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private List<MatchWithProfile> applyTypeFilter(List<MatchWithProfile> source) {
        int selected = spFiltroTipo == null ? 0 : spFiltroTipo.getSelectedItemPosition();
        if (selected == 0) {
            return source;
        }

        String type = selected == 1 ? UserMatch.TYPE_LIKE : UserMatch.TYPE_SUPER_LIKE;
        List<MatchWithProfile> filtered = new ArrayList<>();
        for (MatchWithProfile item : source) {
            if (type.equals(item.matchType)) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private Callbacks getCallbacks() {
        if (requireActivity() instanceof Callbacks) {
            return (Callbacks) requireActivity();
        }
        return null;
    }
}
