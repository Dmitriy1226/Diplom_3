package api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient extends RestClient {

    private static final String REGISTER = "/api/auth/register";
    private static final String LOGIN = "/api/auth/login";
    private static final String USER = "/api/auth/user";
    private static final String LOGOUT = "/api/auth/logout";

    @Step("API: Регистрация пользователя")
    public Response register(Object body) {
        return given()
                .spec(baseSpec())
                .body(body)
                .when()
                .post(REGISTER);
    }

    @Step("API: Логин пользователя")
    public Response login(Object body) {
        return given()
                .spec(baseSpec())
                .body(body)
                .when()
                .post(LOGIN);
    }

    @Step("API: Удалить пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .spec(authSpec(accessToken))
                .when()
                .delete(USER);
    }
}
