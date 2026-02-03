package tests;

import org.junit.After;
import org.junit.Test;
import pages.LoginPage;
import pages.MainPage;
import pages.RegistrationPage;
import support.ApiUserHelper;

import static org.junit.Assert.assertTrue;

public class RegistrationTest extends BaseUiTest {

    private final ApiUserHelper api = new ApiUserHelper();

    private String registeredEmail;
    private String registeredPassword;
    private String registeredAccessToken;

    @After
    public void cleanRegisteredUser() {
        // удаляем пользователя, которого создали в этом тесте через UI
        if (registeredAccessToken == null
                && registeredEmail != null
                && registeredPassword != null) {
            registeredAccessToken = api.loginAndGetAccessToken(registeredEmail, registeredPassword);
        }

        api.deleteUser(registeredAccessToken);

        // чтобы следующий тест случайно не унаследовал данные
        registeredEmail = null;
        registeredPassword = null;
        registeredAccessToken = null;
    }

    private RegistrationPage openRegisterForm(String baseUrl) {
        MainPage main = new MainPage(driver);
        main.open(baseUrl);
        main.goToLogin(baseUrl);

        LoginPage login = new LoginPage(driver);
        login.clickRegisterLink();

        RegistrationPage reg = new RegistrationPage(driver);
        reg.waitForOpen();
        return reg;
    }

    @Test
    public void registrationShouldBeSuccessful() {
        String baseUrl = BASE_URL; // берём из BaseUiTest

        registeredEmail = "test_" + System.currentTimeMillis() + "@mail.ru";
        registeredPassword = "123456";
        String name = "Test User";

        RegistrationPage reg = openRegisterForm(baseUrl);

        // Регистрация
        reg.fillForm(name, registeredEmail, registeredPassword);
        reg.clickRegister();

        // Не должно быть ошибки про пароль
        reg.assertNoInvalidPasswordError();

        // После успешной регистрации возвращает на логин — проверяем форму логина
        LoginPage loginAfter = new LoginPage(driver);
        loginAfter.assertLoginFormVisible();

        // заранее получим токен, чтобы @After точно удалил
        registeredAccessToken = api.loginAndGetAccessToken(registeredEmail, registeredPassword);
    }

    @Test
    public void registrationShouldFail_whenPasswordIsTooShort() {
        String baseUrl = BASE_URL;

        registeredEmail = "test_" + System.currentTimeMillis() + "@mail.ru";
        registeredPassword = "12345"; // < 6 символов
        String name = "Test User";

        RegistrationPage reg = openRegisterForm(baseUrl);

        reg.fillForm(name, registeredEmail, registeredPassword);
        reg.clickRegister();

        // Тут наоборот: ошибка ДОЛЖНА появиться
        // (В твоём PageObject есть только assertNoInvalidPasswordError(),
        // поэтому проверим через URL + простой assert, и добавим в RegistrationPage отдельный метод на шаге 2.1 ниже)
        assertTrue("Ожидали остаться на странице /register при коротком пароле. URL=" + driver.getCurrentUrl(),
                driver.getCurrentUrl().contains("/register"));
    }
}
