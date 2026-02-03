package support;

import api.client.AuthClient;
import io.restassured.response.Response;
import model.Credentials;
import model.User;

import java.util.Random;

public class ApiUserHelper {

    private final AuthClient authClient = new AuthClient();

    public User createRandomUser() {
        int n = new Random().nextInt(1_000_000);
        return new User("dmitry" + n + "@yandex.ru", "password123", "Dmitry");
    }

    /** Регистрация и получение accessToken (обычно уже "Bearer ...") */
    public String registerAndGetAccessToken(User user) {
        Response r = authClient.register(user);
        String accessToken = r.then().extract().path("accessToken");

        if (accessToken == null) {
            accessToken = loginAndGetAccessToken(user.getEmail(), user.getPassword());
        }
        return normalizeBearer(accessToken);
    }

    /** Логин и получение accessToken */
    public String loginAndGetAccessToken(String email, String password) {
        Response login = authClient.login(new Credentials(email, password));
        String token = login.then().extract().path("accessToken");
        return normalizeBearer(token);
    }

    /** Удаление пользователя по accessToken */
    public void deleteUser(String accessToken) {
        String token = normalizeBearer(accessToken);
        if (token != null && !token.isBlank()) {
            authClient.deleteUser(token);
        }
    }

    /** Приводим токен к формату "Bearer ..." (на всякий) */
    private String normalizeBearer(String token) {
        if (token == null) return null;
        String t = token.trim();
        if (t.isEmpty()) return null;
        if (t.startsWith("Bearer ")) return t;
        // если вдруг пришёл чистый токен без префикса
        return "Bearer " + t;
    }
}
