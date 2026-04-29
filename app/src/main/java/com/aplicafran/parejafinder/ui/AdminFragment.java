package com.aplicafran.parejafinder.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicafran.parejafinder.R;
import com.aplicafran.parejafinder.data.AdminActionLog;
import com.aplicafran.parejafinder.data.UserAccount;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AdminFragment extends Fragment {
    public interface Callbacks {
        boolean isCurrentUserAdmin();
        List<UserAccount> getAllUsers();
        boolean updateUserPassword(String email, String newPassword);
        boolean deleteUser(String email);
        boolean setUserBlocked(String email, boolean blocked);
        boolean sendPrivateMessageToUser(String email, String body);
        List<AdminActionLog> getAdminActionLogs();
        String exportUsersBackup();
        int importUsersBackup(String backupRaw);
    }

    private TextView tvAdminVacio;
    private TextView tvAdminPageInfo;
    private AdminUsersAdapter adapter;
    private Spinner spAdminFilter;
    private Spinner spAdminSort;
    private Button btnAdminPrevPage;
    private Button btnAdminNextPage;
    private EditText etAdminSearch;
    private final List<UserAccount> allUsers = new ArrayList<>();
    private int currentPage = 0;
    private static final int PAGE_SIZE = 8;

    public AdminFragment() {
        super(R.layout.fragment_admin);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvAdminVacio = view.findViewById(R.id.tvAdminVacio);
        Button btnAdminExport = view.findViewById(R.id.btnAdminExport);
        Button btnAdminImport = view.findViewById(R.id.btnAdminImport);
        Button btnAdminLogs = view.findViewById(R.id.btnAdminLogs);
        btnAdminPrevPage = view.findViewById(R.id.btnAdminPrevPage);
        btnAdminNextPage = view.findViewById(R.id.btnAdminNextPage);
        etAdminSearch = view.findViewById(R.id.etAdminSearch);
        tvAdminPageInfo = view.findViewById(R.id.tvAdminPageInfo);
        spAdminFilter = view.findViewById(R.id.spAdminFilter);
        spAdminSort = view.findViewById(R.id.spAdminSort);
        RecyclerView rvAdminUsers = view.findViewById(R.id.rvAdminUsers);
        adapter = new AdminUsersAdapter(new AdminUsersAdapter.Listener() {
            @Override
            public void onEditPassword(UserAccount account) {
                showEditPasswordDialog(account);
            }

            @Override
            public void onDelete(UserAccount account) {
                attemptDelete(account);
            }

            @Override
            public void onToggleBlock(UserAccount account) {
                toggleBlock(account);
            }

            @Override
            public void onPrivateMessage(UserAccount account) {
                showPrivateMessageDialog(account);
            }
        });
        rvAdminUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAdminUsers.setAdapter(adapter);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.filtro_admin_usuarios, android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAdminFilter.setAdapter(filterAdapter);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.sort_admin_usuarios, android.R.layout.simple_spinner_item
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAdminSort.setAdapter(sortAdapter);
        spAdminFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                currentPage = 0;
                applyFilter();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        spAdminSort.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                currentPage = 0;
                applyFilter();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        etAdminSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentPage = 0;
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnAdminPrevPage.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                applyFilter();
            }
        });
        btnAdminNextPage.setOnClickListener(v -> {
            currentPage++;
            applyFilter();
        });
        btnAdminExport.setOnClickListener(v -> showExportDialog());
        btnAdminImport.setOnClickListener(v -> showImportDialog());
        btnAdminLogs.setOnClickListener(v -> showLogsDialog());
        refresh();
    }

    private void refresh() {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null || !callbacks.isCurrentUserAdmin()) {
            return;
        }
        allUsers.clear();
        allUsers.addAll(callbacks.getAllUsers());
        applyFilter();
    }

    private void showEditPasswordDialog(UserAccount account) {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        EditText input = new EditText(requireContext());
        input.setHint(R.string.admin_nueva_password_hint);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        new AlertDialog.Builder(requireContext())
                .setTitle(account.email)
                .setView(input)
                .setPositiveButton(R.string.admin_guardar, (dialog, which) -> {
                    String password = input.getText().toString();
                    if (password.length() < 6) {
                        Toast.makeText(requireContext(), R.string.error_password_requerida, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (callbacks.updateUserPassword(account.email, password)) {
                        Toast.makeText(requireContext(), R.string.admin_password_actualizada, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.admin_cancelar, null)
                .show();
    }

    private void attemptDelete(UserAccount account) {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        if (account.isAdmin == 1) {
            Toast.makeText(requireContext(), R.string.admin_no_eliminar_admin, Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_confirmar_eliminar_titulo)
                .setMessage(getString(R.string.admin_confirmar_eliminar_mensaje, account.email))
                .setPositiveButton(R.string.admin_eliminar_usuario, (dialog, which) -> {
                    if (callbacks.deleteUser(account.email)) {
                        Toast.makeText(requireContext(), R.string.admin_usuario_eliminado, Toast.LENGTH_SHORT).show();
                        refresh();
                    }
                })
                .setNegativeButton(R.string.admin_cancelar, null)
                .show();
    }

    private void showExportDialog() {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        EditText output = new EditText(requireContext());
        output.setMinLines(8);
        output.setText(callbacks.exportUsersBackup());
        output.setFocusable(false);
        output.setTextIsSelectable(true);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_export_titulo)
                .setView(output)
                .setPositiveButton(R.string.cerrar, null)
                .show();
    }

    private void toggleBlock(UserAccount account) {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        boolean blocked = account.isBlocked == 0;
        if (callbacks.setUserBlocked(account.email, blocked)) {
            Toast.makeText(
                    requireContext(),
                    blocked ? R.string.admin_usuario_bloqueado : R.string.admin_usuario_desbloqueado,
                    Toast.LENGTH_SHORT
            ).show();
            refresh();
        }
    }

    private void showPrivateMessageDialog(UserAccount account) {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        EditText input = new EditText(requireContext());
        input.setHint(R.string.admin_mensaje_privado_hint);
        input.setMinLines(4);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.admin_mensaje_privado_titulo, account.email))
                .setView(input)
                .setPositiveButton(R.string.admin_confirmar, (dialog, which) -> {
                    String body = input.getText().toString().trim();
                    if (callbacks.sendPrivateMessageToUser(account.email, body)) {
                        Toast.makeText(requireContext(), R.string.admin_mensaje_enviado, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.admin_cancelar, null)
                .show();
    }

    private void showLogsDialog() {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        List<AdminActionLog> logs = callbacks.getAdminActionLogs();
        StringBuilder body = new StringBuilder();
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        for (AdminActionLog log : logs) {
            body.append(getString(
                    R.string.admin_log_line,
                    log.actionType,
                    log.targetEmail,
                    formatter.format(new Date(log.createdAt)),
                    log.details
            )).append("\n\n");
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_logs_titulo)
                .setMessage(body.length() == 0 ? getString(R.string.admin_logs_vacio) : body.toString().trim())
                .setPositiveButton(R.string.cerrar, null)
                .show();
    }

    private void applyFilter() {
        int selected = spAdminFilter == null ? 0 : spAdminFilter.getSelectedItemPosition();
        String query = etAdminSearch == null ? "" : etAdminSearch.getText().toString().trim().toLowerCase();
        List<UserAccount> filtered = new ArrayList<>();
        for (UserAccount user : allUsers) {
            if (selected == 1 && user.isOnline != 1) {
                continue;
            }
            if (selected == 2 && user.isBlocked != 1) {
                continue;
            }
            if (!query.isEmpty() && (user.email == null || !user.email.toLowerCase().contains(query))) {
                continue;
            }
            filtered.add(user);
        }
        sortUsers(filtered);
        int totalPages = Math.max(1, (int) Math.ceil((double) filtered.size() / PAGE_SIZE));
        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, filtered.size());
        List<UserAccount> page = new ArrayList<>();
        if (from < to) {
            page.addAll(filtered.subList(from, to));
        }
        adapter.submit(page);
        if (tvAdminPageInfo != null) {
            tvAdminPageInfo.setText(getString(R.string.admin_page_info, currentPage + 1, totalPages));
        }
        if (btnAdminPrevPage != null) {
            btnAdminPrevPage.setEnabled(currentPage > 0);
        }
        if (btnAdminNextPage != null) {
            btnAdminNextPage.setEnabled(currentPage < totalPages - 1);
        }
        tvAdminVacio.setVisibility(page.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void sortUsers(List<UserAccount> users) {
        int selected = spAdminSort == null ? 0 : spAdminSort.getSelectedItemPosition();
        if (selected == 1) {
            users.sort((a, b) -> safeEmail(b).compareToIgnoreCase(safeEmail(a)));
            return;
        }
        if (selected == 2) {
            users.sort((a, b) -> Long.compare(b.lastSeenAt, a.lastSeenAt));
            return;
        }
        if (selected == 3) {
            users.sort(Comparator.comparingLong(a -> a.lastSeenAt));
            return;
        }
        users.sort((a, b) -> safeEmail(a).compareToIgnoreCase(safeEmail(b)));
    }

    private String safeEmail(UserAccount account) {
        return account == null || account.email == null ? "" : account.email;
    }

    private void showImportDialog() {
        Callbacks callbacks = getCallbacks();
        if (callbacks == null) {
            return;
        }
        EditText input = new EditText(requireContext());
        input.setHint(R.string.admin_backup_hint);
        input.setMinLines(8);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.admin_import_titulo)
                .setView(input)
                .setPositiveButton(R.string.admin_confirmar, (dialog, which) -> {
                    String raw = input.getText().toString();
                    if (raw.trim().isEmpty()) {
                        Toast.makeText(requireContext(), R.string.admin_backup_vacio, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int imported = callbacks.importUsersBackup(raw);
                    Toast.makeText(requireContext(), getString(R.string.admin_import_resultado, imported), Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .setNegativeButton(R.string.admin_cancelar, null)
                .show();
    }

    private Callbacks getCallbacks() {
        if (requireActivity() instanceof Callbacks) {
            return (Callbacks) requireActivity();
        }
        return null;
    }
}
