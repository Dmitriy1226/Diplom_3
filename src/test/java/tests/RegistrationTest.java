package tests;

import org.junit.After;
import org.junit.Test;
import pages.LoginPage;
import pages.MainPage;
import pages.RegistrationPage;
import support.ApiUserHelper;

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
    }

    @Test
    public void registrationShouldBeSuccessful() {
        String baseUrl = BASE_URL; // берём из BaseUiTest (ты уже вынес в константу)

        registeredEmail = "test_" + System.currentTimeMillis() + "@mail.ru";
        registeredPassword = "123456";
        String name = "Test User";

        MainPage main = new MainPage(driver);
        main.open(baseUrl);

        // 1) Переходим на логин
        main.goToLogin(baseUrl);

        // 2) С логина кликаем "Зарегистрироваться"
        LoginPage login = new LoginPage(driver);
        login.clickRegisterLink();

        // 3) Ждём открытия страницы регистрации (в Page Object)
        RegistrationPage reg = new RegistrationPage(driver);
        reg.waitForOpen();

        // 4) Регистрация
        reg.fillForm(name, registeredEmail, registeredPassword);
        reg.clickRegister();

        // 5) Проверяем, что не показалась ошибка "Некорректный пароль"
        reg.assertNoInvalidPasswordError();

        // 6) После успешной регистрации возвращает на логин — проверяем форму логина
        LoginPage loginAfter = new LoginPage(driver);
        loginAfter.assertLoginFormVisible();

        // 7) (опционально) заранее получим токен, чтобы @After точно удалил
        registeredAccessToken = api.loginAndGetAccessToken(registeredEmail, registeredPassword);
    }
}
