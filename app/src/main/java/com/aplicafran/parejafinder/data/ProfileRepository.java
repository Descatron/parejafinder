package com.aplicafran.parejafinder.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileRepository {
    public enum AuthResult {
        LOGIN_SUCCESS,
        REGISTERED_SUCCESS,
        INVALID_CREDENTIALS
    }

    public enum MatchResult {
        CREATED,
        UPGRADED_TO_SUPER_LIKE,
        ALREADY_EXISTS
    }

    public static final String ADMIN_EMAIL = "admin@aplicafran.com";
    private static final String ADMIN_DEFAULT_PASSWORD = "admin123";

    private final CandidateProfileDao candidateProfileDao;
    private final UserMatchDao userMatchDao;
    private final UserAccountDao userAccountDao;
    private final ChatMessageDao chatMessageDao;
    private final AdminPrivateMessageDao adminPrivateMessageDao;
    private final AdminActionLogDao adminActionLogDao;

    public ProfileRepository(AppDatabase db) {
        this.candidateProfileDao = db.candidateProfileDao();
        this.userMatchDao = db.userMatchDao();
        this.userAccountDao = db.userAccountDao();
        this.chatMessageDao = db.chatMessageDao();
        this.adminPrivateMessageDao = db.adminPrivateMessageDao();
        this.adminActionLogDao = db.adminActionLogDao();
        seedIfNeeded();
        seedAdminIfNeeded();
    }

    public List<CandidateProfile> search(String currentEmail, String query, String city, int maxAge) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<CandidateProfile> all = candidateProfileDao.getAll();
        List<CandidateProfile> results = new ArrayList<>();
        for (CandidateProfile profile : all) {
            if (profile.email == null || profile.email.equalsIgnoreCase(currentEmail)) {
                continue;
            }
            UserAccount account = userAccountDao.findByEmail(profile.email);
            if (account == null || account.isAdmin == 1 || account.isBlocked == 1) {
                continue;
            }
            boolean textOk = normalizedQuery.isEmpty()
                    || profile.nombre.toLowerCase(Locale.ROOT).contains(normalizedQuery)
                    || profile.intereses.toLowerCase(Locale.ROOT).contains(normalizedQuery);
            boolean cityOk = "Todas las ciudades".equals(city) || profile.ciudad.equals(city);
            boolean ageOk = profile.edad <= maxAge;
            if (textOk && cityOk && ageOk) {
                results.add(profile);
            }
        }
        return results;
    }

    public int countAvailableCandidates(String currentEmail) {
        List<CandidateProfile> all = candidateProfileDao.getAll();
        int count = 0;
        for (CandidateProfile profile : all) {
            if (profile.email == null || profile.email.equalsIgnoreCase(currentEmail)) {
                continue;
            }
            UserAccount account = userAccountDao.findByEmail(profile.email);
            if (account == null || account.isAdmin == 1 || account.isBlocked == 1) {
                continue;
            }
            count++;
        }
        return count;
    }

    public boolean addMatch(String username, CandidateProfile candidateProfile) {
        return addMatch(username, candidateProfile, UserMatch.TYPE_LIKE);
    }

    public boolean addMatch(String username, CandidateProfile candidateProfile, String matchType) {
        return addMatchResult(username, candidateProfile, matchType) != MatchResult.ALREADY_EXISTS;
    }

    public MatchResult addMatchResult(String username, CandidateProfile candidateProfile, String matchType) {
        if (userMatchDao.exists(username, candidateProfile.id) > 0) {
            String existingType = userMatchDao.getType(username, candidateProfile.id);
            if (UserMatch.TYPE_LIKE.equals(existingType) && UserMatch.TYPE_SUPER_LIKE.equals(matchType)) {
                userMatchDao.updateType(username, candidateProfile.id, UserMatch.TYPE_SUPER_LIKE);
                return MatchResult.UPGRADED_TO_SUPER_LIKE;
            }
            return MatchResult.ALREADY_EXISTS;
        }
        userMatchDao.insert(new UserMatch(username, candidateProfile.id, matchType));
        return MatchResult.CREATED;
    }

    public List<MatchWithProfile> getMatches(String username) {
        return userMatchDao.getMatchesForUser(username);
    }

    public AuthResult authenticate(String email, String password) {
        UserAccount account = userAccountDao.findByEmail(email);
        if (account == null) {
            userAccountDao.insert(new UserAccount(
                    email, password, 0, defaultNameFromEmail(email), 18, "", "", "",
                    0, 0, System.currentTimeMillis()
            ));
            syncCandidateProfileFromUser(email);
            return AuthResult.REGISTERED_SUCCESS;
        }
        if (account.isBlocked == 1) {
            return AuthResult.INVALID_CREDENTIALS;
        }
        if (account.password.equals(password)) {
            return AuthResult.LOGIN_SUCCESS;
        }
        return AuthResult.INVALID_CREDENTIALS;
    }

    public boolean isAdmin(String email) {
        UserAccount account = userAccountDao.findByEmail(email);
        return account != null && account.isAdmin == 1;
    }

    public List<UserAccount> getAllUsers() {
        return userAccountDao.getAll();
    }

    public boolean updatePassword(String email, String newPassword) {
        UserAccount account = userAccountDao.findByEmail(email);
        if (account == null) {
            return false;
        }
        account.password = newPassword;
        userAccountDao.update(account);
        return true;
    }

    public boolean deleteUser(String email) {
        UserAccount account = userAccountDao.findByEmail(email);
        if (account == null || account.isAdmin == 1) {
            return false;
        }
        candidateProfileDao.deleteByEmail(email);
        userAccountDao.deleteByEmail(email);
        return true;
    }

    public boolean setUserBlocked(String email, boolean blocked) {
        UserAccount account = userAccountDao.findByEmail(email);
        if (account == null || account.isAdmin == 1) {
            return false;
        }
        account.isBlocked = blocked ? 1 : 0;
        if (blocked) {
            account.isOnline = 0;
            account.lastSeenAt = System.currentTimeMillis();
        }
        userAccountDao.update(account);
        syncCandidateProfileFromUser(email);
        return true;
    }

    public void setUserOnlineStatus(String email, boolean isOnline) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        UserAccount account = userAccountDao.findByEmail(email);
        if (account == null) {
            return;
        }
        account.isOnline = isOnline ? 1 : 0;
        account.lastSeenAt = System.currentTimeMillis();
        userAccountDao.update(account);
    }

    public boolean sendAdminPrivateMessage(String targetEmail, String body) {
        if (targetEmail == null || targetEmail.trim().isEmpty() || body == null || body.trim().isEmpty()) {
            return false;
        }
        UserAccount target = userAccountDao.findByEmail(targetEmail);
        if (target == null) {
            return false;
        }
        adminPrivateMessageDao.insert(new AdminPrivateMessage(targetEmail, body.trim(), System.currentTimeMillis()));
        return true;
    }

    public List<AdminPrivateMessage> getAdminMessagesForUser(String email) {
        return adminPrivateMessageDao.getForUser(email);
    }

    public void logAdminAction(String adminEmail, String actionType, String targetEmail, String details) {
        adminActionLogDao.insert(new AdminActionLog(
                adminEmail == null ? "" : adminEmail,
                actionType == null ? "" : actionType,
                targetEmail == null ? "" : targetEmail,
                details == null ? "" : details,
                System.currentTimeMillis()
        ));
    }

    public List<AdminActionLog> getAdminActionLogs() {
        return adminActionLogDao.getAll();
    }

    public void upsertLocalUserAccount(String email, String password, boolean isAdmin) {
        if (email == null || email.trim().isEmpty() || password == null || password.length() < 6) {
            return;
        }
        UserAccount existing = userAccountDao.findByEmail(email);
        if (existing == null) {
            userAccountDao.insert(new UserAccount(
                    email, password, isAdmin ? 1 : 0, defaultNameFromEmail(email), 18, "", "", "",
                    0, 0, System.currentTimeMillis()
            ));
            syncCandidateProfileFromUser(email);
            return;
        }
        existing.password = password;
        existing.isAdmin = isAdmin ? 1 : existing.isAdmin;
        userAccountDao.update(existing);
        syncCandidateProfileFromUser(email);
    }

    public UserAccount getUserProfile(String email) {
        return userAccountDao.findByEmail(email);
    }

    public boolean updateUserProfile(String email, String displayName, int age, String city, String bio, String photoUri) {
        UserAccount existing = userAccountDao.findByEmail(email);
        if (existing == null) {
            return false;
        }
        existing.displayName = displayName == null ? "" : displayName.trim();
        existing.age = Math.max(18, age);
        existing.city = city == null ? "" : city.trim();
        existing.bio = bio == null ? "" : bio.trim();
        existing.photoUri = photoUri == null ? "" : photoUri.trim();
        userAccountDao.update(existing);
        syncCandidateProfileFromUser(email);
        return true;
    }

    public String exportUsersBackup() {
        List<UserAccount> users = userAccountDao.getAll();
        StringBuilder builder = new StringBuilder();
        for (UserAccount user : users) {
            builder.append(escape(user.email))
                    .append('\t')
                    .append(escape(user.password))
                    .append('\t')
                    .append(user.isAdmin)
                    .append('\t')
                    .append(escape(user.displayName))
                    .append('\t')
                    .append(user.age)
                    .append('\t')
                    .append(escape(user.city))
                    .append('\t')
                    .append(escape(user.bio))
                    .append('\t')
                    .append(escape(user.photoUri))
                    .append('\t')
                    .append(user.isBlocked)
                    .append('\t')
                    .append(user.isOnline)
                    .append('\t')
                    .append(user.lastSeenAt)
                    .append('\n');
        }
        return builder.toString();
    }

    public int importUsersBackup(String backupRaw) {
        if (backupRaw == null || backupRaw.trim().isEmpty()) {
            return 0;
        }
        String[] lines = backupRaw.split("\\r?\\n");
        int processed = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\t");
            if (parts.length < 3) {
                continue;
            }
            String email = unescape(parts[0]);
            String password = unescape(parts[1]);
            int isAdmin;
            try {
                isAdmin = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                continue;
            }
            if (email.isEmpty() || password.length() < 6) {
                continue;
            }
            String displayName = parts.length > 3 ? unescape(parts[3]) : defaultNameFromEmail(email);
            int age = 18;
            if (parts.length > 4) {
                try {
                    age = Integer.parseInt(parts[4]);
                } catch (NumberFormatException ignored) {
                    age = 18;
                }
            }
            String city = parts.length > 5 ? unescape(parts[5]) : "";
            String bio = parts.length > 6 ? unescape(parts[6]) : "";
            String photoUri = parts.length > 7 ? unescape(parts[7]) : "";
            int isBlocked = parts.length > 8 ? parseIntOr(parts[8], 0) : 0;
            int isOnline = parts.length > 9 ? parseIntOr(parts[9], 0) : 0;
            long lastSeenAt = parts.length > 10 ? parseLongOr(parts[10], System.currentTimeMillis()) : System.currentTimeMillis();
            UserAccount existing = userAccountDao.findByEmail(email);
            if (existing == null) {
                userAccountDao.insert(new UserAccount(
                        email,
                        password,
                        isAdmin == 1 ? 1 : 0,
                        displayName,
                        Math.max(18, age),
                        city,
                        bio,
                        photoUri,
                        isBlocked == 1 ? 1 : 0,
                        isOnline == 1 ? 1 : 0,
                        lastSeenAt
                ));
            } else {
                existing.password = password;
                existing.isAdmin = isAdmin == 1 ? 1 : 0;
                existing.displayName = displayName;
                existing.age = Math.max(18, age);
                existing.city = city;
                existing.bio = bio;
                existing.photoUri = photoUri;
                existing.isBlocked = isBlocked == 1 ? 1 : 0;
                existing.isOnline = isOnline == 1 ? 1 : 0;
                existing.lastSeenAt = lastSeenAt;
                userAccountDao.update(existing);
            }
            syncCandidateProfileFromUser(email);
            processed++;
        }
        seedAdminIfNeeded();
        return processed;
    }

    private void seedIfNeeded() {
        // Sin perfiles demo: solo aparecen usuarios reales registrados.
    }

    private void seedAdminIfNeeded() {
        if (userAccountDao.findByEmail(ADMIN_EMAIL) == null) {
            userAccountDao.insert(new UserAccount(
                    ADMIN_EMAIL, ADMIN_DEFAULT_PASSWORD, 1, "Admin", 30, "Madrid",
                    "Administrador de Pareja Finder.", "", 0, 0, System.currentTimeMillis()
            ));
        }
    }

    private String defaultNameFromEmail(String email) {
        int at = email.indexOf("@");
        if (at > 0) {
            return email.substring(0, at);
        }
        return email;
    }

    private void syncCandidateProfileFromUser(String email) {
        UserAccount account = userAccountDao.findByEmail(email);
        if (account == null) {
            return;
        }
        if (account.isAdmin == 1 || account.isBlocked == 1) {
            candidateProfileDao.deleteByEmail(email);
            return;
        }
        CandidateProfile existing = candidateProfileDao.findByEmail(email);
        String intereses = account.bio == null || account.bio.trim().isEmpty()
                ? "Sin bio por ahora"
                : account.bio.trim();
        String city = account.city == null ? "" : account.city.trim();
        String nombre = account.displayName == null || account.displayName.trim().isEmpty()
                ? defaultNameFromEmail(email)
                : account.displayName.trim();
        if (existing == null) {
            candidateProfileDao.insert(new CandidateProfile(email, nombre, Math.max(18, account.age), city, intereses));
            return;
        }
        existing.nombre = nombre;
        existing.edad = Math.max(18, account.age);
        existing.ciudad = city;
        existing.intereses = intereses;
        candidateProfileDao.update(existing);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
    }

    private String unescape(String value) {
        StringBuilder out = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (escaping) {
                if (c == 't') {
                    out.append('\t');
                } else if (c == 'n') {
                    out.append('\n');
                } else {
                    out.append(c);
                }
                escaping = false;
            } else if (c == '\\') {
                escaping = true;
            } else {
                out.append(c);
            }
        }
        if (escaping) {
            out.append('\\');
        }
        return out.toString();
    }

    public List<ChatMessage> getConversation(String username, int candidateId) {
        return chatMessageDao.getConversation(username, candidateId);
    }

    public void sendMessage(String username, int candidateId, String sender, String body) {
        if (body == null || body.trim().isEmpty()) {
            return;
        }
        int isRead = "ME".equals(sender) ? 1 : 0;
        chatMessageDao.insert(new ChatMessage(
                username,
                candidateId,
                sender,
                body.trim(),
                System.currentTimeMillis(),
                isRead
        ));
    }

    public void markConversationRead(String username, int candidateId) {
        chatMessageDao.markConversationAsRead(username, candidateId);
    }

    private int parseIntOr(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private long parseLongOr(String value, long fallback) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
