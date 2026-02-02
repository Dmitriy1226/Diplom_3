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

    public String registerAndGetAccessToken(User user) {
        Response r = authClient.register(user);
        String accessToken = r.then().extract().path("accessToken");

        if (accessToken == null) {
            Response login = authClient.login(new Credentials(user.getEmail(), user.getPassword()));
            accessToken = login.then().extract().path("accessToken");
        }
        return accessToken;
    }

    public void deleteUser(String accessToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            authClient.deleteUser(accessToken);
        }
    }
}
