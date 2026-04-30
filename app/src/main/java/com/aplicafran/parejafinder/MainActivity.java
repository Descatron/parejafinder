package com.aplicafran.parejafinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aplicafran.parejafinder.data.AppDatabase;
import com.aplicafran.parejafinder.data.AdminPrivateMessage;
import com.aplicafran.parejafinder.data.ProfileRepository;
import com.aplicafran.parejafinder.data.UserAccount;
import com.aplicafran.parejafinder.ui.AdminFragment;
import com.aplicafran.parejafinder.ui.ChatFragment;
import com.aplicafran.parejafinder.ui.DiscoverFragment;
import com.aplicafran.parejafinder.ui.MatchesFragment;
import com.aplicafran.parejafinder.ui.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        DiscoverFragment.Callbacks,
        MatchesFragment.Callbacks,
        ChatFragment.Callbacks,
        ProfileFragment.Callbacks,
        AdminFragment.Callbacks {
    private SessionManager sessionManager;
    private ProfileRepository repository;
    private LinearLayout layoutAuth;
    private LinearLayout layoutApp;
    private EditText etEmail;
    private EditText etPassword;
    private MatchesFragment matchesFragment;
    private AdminFragment adminFragment;
    private BottomNavigationView bottomNav;
    private TextView tvTopBanner;
    private int pendingChatCandidateId = -1;
    private String pendingChatCandidateName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        repository = new ProfileRepository(AppDatabase.getInstance(this));
        NotificationHelper.ensureChannel(this);
        requestNotificationPermissionIfNeeded();
        layoutAuth = findViewById(R.id.layoutAuth);
        layoutApp = findViewById(R.id.layoutApp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvTopBanner = findViewById(R.id.tvTopBanner);
        Button btnEntrar = findViewById(R.id.btnEntrar);
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.inflateMenu(R.menu.bottom_nav_menu);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_descubrir) {
                openFragment(new DiscoverFragment());
                return true;
            }
            if (item.getItemId() == R.id.nav_matches) {
                if (matchesFragment == null) {
                    matchesFragment = new MatchesFragment();
                }
                openFragment(matchesFragment);
                return true;
            }
            if (item.getItemId() == R.id.nav_perfil) {
                openFragment(new ProfileFragment());
                return true;
            }
            if (item.getItemId() == R.id.nav_admin && isCurrentUserAdmin()) {
                if (adminFragment == null) {
                    adminFragment = new AdminFragment();
                }
                openFragment(adminFragment);
                return true;
            }
            return false;
        });

        btnEntrar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            if (!isValidEmail(email)) {
                Toast.makeText(this, R.string.error_email_requerido, Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, R.string.error_password_requerida, Toast.LENGTH_SHORT).show();
                return;
            }
            loginOrRegisterLocal(email, password);
        });

        if (sessionManager.isLoggedIn() && sessionManager.getEmail() != null && !sessionManager.getEmail().trim().isEmpty()) {
            String email = sessionManager.getEmail();
            UserAccount account = repository.getUserProfile(email);
            if (account != null && account.isBlocked == 1) {
                repository.setUserOnlineStatus(email, false);
                sessionManager.logout();
                Toast.makeText(this, R.string.error_usuario_bloqueado, Toast.LENGTH_SHORT).show();
                showAuthArea();
                parseNotificationIntent(getIntent());
                return;
            }
            sessionManager.saveEmail(email);
            repository.setUserOnlineStatus(email, true);
            showLoggedArea();
            configureAdminMenu();
            bottomNav.setSelectedItemId(R.id.nav_descubrir);
            openPendingChatIfAny();
        } else if (sessionManager.isLoggedIn()) {
            showAuthArea();
        } else {
            showAuthArea();
        }
        parseNotificationIntent(getIntent());
    }

    private void showLoggedArea() {
        layoutAuth.setVisibility(LinearLayout.GONE);
        layoutApp.setVisibility(LinearLayout.VISIBLE);
        updateTopBanner();
    }

    private void showAuthArea() {
        layoutAuth.setVisibility(LinearLayout.VISIBLE);
        layoutApp.setVisibility(LinearLayout.GONE);
    }

    private void updateTopBanner() {
        if (tvTopBanner == null) {
            return;
        }
        tvTopBanner.setText(getString(R.string.banner_bienvenida, getCurrentUsername()));
    }

    private void configureAdminMenu() {
        if (bottomNav == null || bottomNav.getMenu() == null) {
            return;
        }
        bottomNav.getMenu().findItem(R.id.nav_admin).setVisible(isCurrentUserAdmin());
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public String getCurrentUsername() {
        UserAccount profile = repository.getUserProfile(sessionManager.getEmail());
        if (profile != null && profile.displayName != null && !profile.displayName.trim().isEmpty()) {
            return profile.displayName;
        }
        return sessionManager.getDisplayName();
    }

    @Override
    public String getCurrentEmail() {
        return sessionManager.getEmail();
    }

    @Override
    public UserAccount getCurrentUserProfile() {
        return repository.getUserProfile(sessionManager.getEmail());
    }

    @Override
    public boolean updateCurrentUserProfile(String displayName, int age, String city, String bio, String photoUri) {
        return repository.updateUserProfile(sessionManager.getEmail(), displayName, age, city, bio, photoUri);
    }

    @Override
    public List<AdminPrivateMessage> getAdminMessagesForCurrentUser() {
        return repository.getAdminMessagesForUser(sessionManager.getEmail());
    }

    @Override
    public ProfileRepository getRepository() {
        return repository;
    }

    @Override
    public void openChat(int candidateId, String candidateName) {
        openFragment(ChatFragment.newInstance(candidateId, candidateName));
    }

    @Override
    public void notifyNewMessage(int candidateId, String candidateName, String messagePreview, int notificationId) {
        NotificationHelper.showChatNotification(
                this,
                getString(R.string.notification_new_message_title, candidateName),
                messagePreview,
                notificationId,
                candidateId,
                candidateName
        );
    }

    @Override
    public void onNewMatch() {
        if (matchesFragment != null && matchesFragment.isAdded()) {
            matchesFragment.refresh();
        }
    }

    @Override
    public void logout() {
        repository.setUserOnlineStatus(sessionManager.getEmail(), false);
        sessionManager.logout();
        etEmail.setText("");
        etPassword.setText("");
        matchesFragment = null;
        adminFragment = null;
        configureAdminMenu();
        showAuthArea();
    }

    @Override
    public boolean isCurrentUserAdmin() {
        String email = sessionManager.getEmail();
        return ProfileRepository.ADMIN_EMAIL.equalsIgnoreCase(email);
    }

    @Override
    public List<UserAccount> getAllUsers() {
        return repository.getAllUsers();
    }

    @Override
    public boolean updateUserPassword(String email, String newPassword) {
        boolean ok = repository.updatePassword(email, newPassword);
        if (ok) {
            repository.logAdminAction(
                    sessionManager.getEmail(),
                    "PASSWORD_CHANGE",
                    email,
                    "Contrasena actualizada por admin."
            );
        }
        return ok;
    }

    @Override
    public boolean deleteUser(String email) {
        boolean ok = repository.deleteUser(email);
        if (ok) {
            repository.logAdminAction(
                    sessionManager.getEmail(),
                    "DELETE_USER",
                    email,
                    "Usuario eliminado por admin."
            );
        }
        return ok;
    }

    @Override
    public boolean setUserBlocked(String email, boolean blocked) {
        boolean ok = repository.setUserBlocked(email, blocked);
        if (ok) {
            repository.logAdminAction(
                    sessionManager.getEmail(),
                    blocked ? "BLOCK_USER" : "UNBLOCK_USER",
                    email,
                    blocked ? "Usuario bloqueado." : "Usuario desbloqueado."
            );
        }
        return ok;
    }

    @Override
    public boolean sendPrivateMessageToUser(String email, String body) {
        boolean ok = repository.sendAdminPrivateMessage(email, body);
        if (ok) {
            repository.logAdminAction(
                    sessionManager.getEmail(),
                    "PRIVATE_MESSAGE",
                    email,
                    body
            );
        }
        return ok;
    }

    @Override
    public List<com.aplicafran.parejafinder.data.AdminActionLog> getAdminActionLogs() {
        return repository.getAdminActionLogs();
    }

    @Override
    public String exportUsersBackup() {
        return repository.exportUsersBackup();
    }

    @Override
    public int importUsersBackup(String backupRaw) {
        return repository.importUsersBackup(backupRaw);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() >= 6;
    }

    private void loginOrRegisterLocal(String email, String password) {
        ProfileRepository.AuthResult authResult = repository.authenticate(email, password);
        if (authResult == ProfileRepository.AuthResult.LOGIN_SUCCESS) {
            onAuthenticationSuccess(email, false);
            return;
        }
        if (authResult == ProfileRepository.AuthResult.REGISTERED_SUCCESS) {
            onAuthenticationSuccess(email, true);
            return;
        }
        Toast.makeText(this, R.string.error_credenciales_invalidas, Toast.LENGTH_SHORT).show();
    }

    private void onAuthenticationSuccess(String email, boolean justRegistered) {
        UserAccount account = repository.getUserProfile(email);
        if (account != null && account.isBlocked == 1) {
            Toast.makeText(this, R.string.error_usuario_bloqueado, Toast.LENGTH_SHORT).show();
            return;
        }
        repository.setUserOnlineStatus(email, true);
        sessionManager.saveEmail(email);
        if (justRegistered) {
            Toast.makeText(this, R.string.registro_confirmado, Toast.LENGTH_SHORT).show();
        }
        showLoggedArea();
        configureAdminMenu();
        bottomNav.setSelectedItemId(R.id.nav_descubrir);
        openPendingChatIfAny();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
    }

    private void parseNotificationIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        int candidateId = intent.getIntExtra(NotificationHelper.EXTRA_CHAT_CANDIDATE_ID, -1);
        String candidateName = intent.getStringExtra(NotificationHelper.EXTRA_CHAT_CANDIDATE_NAME);
        if (candidateId <= 0 || candidateName == null || candidateName.trim().isEmpty()) {
            return;
        }
        pendingChatCandidateId = candidateId;
        pendingChatCandidateName = candidateName;
        openPendingChatIfAny();
    }

    private void openPendingChatIfAny() {
        if (pendingChatCandidateId <= 0 || pendingChatCandidateName == null || pendingChatCandidateName.trim().isEmpty()) {
            return;
        }
        if (!sessionManager.isLoggedIn()) {
            return;
        }
        openChat(pendingChatCandidateId, pendingChatCandidateName);
        pendingChatCandidateId = -1;
        pendingChatCandidateName = "";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        parseNotificationIntent(intent);
    }
}