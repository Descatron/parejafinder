package com.aplicafran.parejafinder.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.aplicafran.parejafinder.R;
import com.aplicafran.parejafinder.data.AdminPrivateMessage;
import com.aplicafran.parejafinder.data.UserAccount;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {
    public interface Callbacks {
        String getCurrentUsername();
        String getCurrentEmail();
        UserAccount getCurrentUserProfile();
        boolean updateCurrentUserProfile(String displayName, int age, String city, String bio, String photoUri);
        List<AdminPrivateMessage> getAdminMessagesForCurrentUser();
        void logout();
    }

    private String selectedPhotoUri = "";
    private ImageView ivFotoPerfil;
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) {
                    return;
                }
                selectedPhotoUri = uri.toString();
                if (ivFotoPerfil != null) {
                    ivFotoPerfil.setImageURI(uri);
                }
            });

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvSaludo = view.findViewById(R.id.tvSaludo);
        TextView tvEmailPerfil = view.findViewById(R.id.tvEmailPerfil);
        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        Button btnSeleccionarFoto = view.findViewById(R.id.btnSeleccionarFoto);
        EditText etNombrePerfil = view.findViewById(R.id.etNombrePerfil);
        EditText etEdadPerfil = view.findViewById(R.id.etEdadPerfil);
        EditText etCiudadPerfil = view.findViewById(R.id.etCiudadPerfil);
        EditText etBioPerfil = view.findViewById(R.id.etBioPerfil);
        Button btnGuardarPerfil = view.findViewById(R.id.btnGuardarPerfil);
        Button btnMensajesAdmin = view.findViewById(R.id.btnMensajesAdmin);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        if (getActivity() instanceof Callbacks) {
            Callbacks callbacks = (Callbacks) getActivity();
            tvSaludo.setText(getString(R.string.perfil_saludo, callbacks.getCurrentUsername()));
            tvEmailPerfil.setText(getString(R.string.perfil_email, callbacks.getCurrentEmail()));
            UserAccount profile = callbacks.getCurrentUserProfile();
            if (profile != null) {
                etNombrePerfil.setText(profile.displayName);
                etEdadPerfil.setText(String.valueOf(profile.age));
                etCiudadPerfil.setText(profile.city);
                etBioPerfil.setText(profile.bio);
                selectedPhotoUri = profile.photoUri == null ? "" : profile.photoUri;
                if (!selectedPhotoUri.isEmpty()) {
                    try {
                        ivFotoPerfil.setImageURI(Uri.parse(selectedPhotoUri));
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), R.string.perfil_error_foto, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            btnSeleccionarFoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
            btnGuardarPerfil.setOnClickListener(v -> {
                String displayName = etNombrePerfil.getText().toString().trim();
                String ageText = etEdadPerfil.getText().toString().trim();
                String city = etCiudadPerfil.getText().toString().trim();
                String bio = etBioPerfil.getText().toString().trim();
                int age = 18;
                try {
                    if (!ageText.isEmpty()) {
                        age = Integer.parseInt(ageText);
                    }
                } catch (NumberFormatException ignored) {
                    age = 18;
                }
                if (displayName.isEmpty()) {
                    displayName = callbacks.getCurrentUsername();
                }
                boolean updated = callbacks.updateCurrentUserProfile(displayName, age, city, bio, selectedPhotoUri);
                Toast.makeText(
                        requireContext(),
                        updated ? R.string.perfil_guardado_ok : R.string.perfil_error_guardado,
                        Toast.LENGTH_SHORT
                ).show();
            });
            btnMensajesAdmin.setOnClickListener(v -> showAdminInbox(callbacks));
            btnCerrarSesion.setOnClickListener(v -> {
                callbacks.logout();
                Toast.makeText(requireContext(), R.string.sesion_cerrada, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showAdminInbox(Callbacks callbacks) {
        List<AdminPrivateMessage> messages = callbacks.getAdminMessagesForCurrentUser();
        StringBuilder body = new StringBuilder();
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        for (AdminPrivateMessage message : messages) {
            body.append("• ")
                    .append(message.body)
                    .append("\n")
                    .append(formatter.format(new Date(message.createdAt)))
                    .append("\n\n");
        }
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_inbox_titulo)
                .setMessage(body.length() == 0 ? getString(R.string.admin_inbox_vacio) : body.toString().trim())
                .setPositiveButton(R.string.cerrar, null)
                .show();
    }
}
