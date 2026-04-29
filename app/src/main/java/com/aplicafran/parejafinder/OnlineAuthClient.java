package com.aplicafran.parejafinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OnlineAuthClient {
    public enum Status {
        LOGIN_SUCCESS,
        REGISTER_SUCCESS,
        INVALID_CREDENTIALS,
        NETWORK_ERROR
    }

    public static class AuthResult {
        public final Status status;
        public final String token;
        public final String message;

        public AuthResult(Status status, String token, String message) {
            this.status = status;
            this.token = token == null ? "" : token;
            this.message = message == null ? "" : message;
        }
    }

    private final String baseUrl;

    public OnlineAuthClient(String baseUrl) {
        this.baseUrl = sanitizeBaseUrl(baseUrl);
    }

    public AuthResult loginOrRegister(String email, String password) {
        AuthResult login = request("/api/auth/login", email, password, Status.LOGIN_SUCCESS);
        if (login.status == Status.LOGIN_SUCCESS) {
            return login;
        }
        if (login.status == Status.NETWORK_ERROR) {
            return login;
        }
        return request("/api/auth/register", email, password, Status.REGISTER_SUCCESS);
    }

    private AuthResult request(String path, String email, String password, Status successStatus) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(baseUrl + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            JSONObject payload = new JSONObject();
            payload.put("email", email);
            payload.put("password", password);

            byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(body.length);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(body);
            }

            int code = connection.getResponseCode();
            String response = readResponse(code >= 200 && code < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream());

            if (code >= 200 && code < 300) {
                String token = parseToken(response);
                return new AuthResult(successStatus, token, "");
            }
            if (code == 401 || code == 403 || code == 404) {
                return new AuthResult(Status.INVALID_CREDENTIALS, "", "");
            }
            return new AuthResult(Status.NETWORK_ERROR, "", response);
        } catch (IOException | JSONException e) {
            return new AuthResult(Status.NETWORK_ERROR, "", e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String parseToken(String response) throws JSONException {
        if (response == null || response.trim().isEmpty()) {
            return "";
        }
        JSONObject json = new JSONObject(response);
        if (json.has("token")) {
            return json.optString("token", "");
        }
        if (json.has("accessToken")) {
            return json.optString("accessToken", "");
        }
        if (json.has("jwt")) {
            return json.optString("jwt", "");
        }
        return "";
    }

    private String readResponse(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private String sanitizeBaseUrl(String rawBaseUrl) {
        if (rawBaseUrl == null || rawBaseUrl.trim().isEmpty()) {
            return "https://TU_BACKEND.com";
        }
        String trimmed = rawBaseUrl.trim();
        if (trimmed.endsWith("/")) {
            return trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
