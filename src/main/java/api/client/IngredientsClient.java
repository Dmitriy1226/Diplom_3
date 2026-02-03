package api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientsClient extends RestClient {

    private static final String INGREDIENTS = "/api/ingredients";

    @Step("API: Получить список ингредиентов")
    public Response getIngredients() {
        return given()
                .spec(baseSpec())
                .when()
                .get(INGREDIENTS);
    }
}
