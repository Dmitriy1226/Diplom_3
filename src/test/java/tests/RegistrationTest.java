package tests;

import org.junit.Test;
import pages.LoginPage;
import pages.MainPage;
import pages.RegistrationPage;

public class RegistrationTest extends BaseUiTest {

    @Test
    public void registrationShouldBeSuccessful() {
        String baseUrl = System.getProperty("baseUrl", "https://stellarburgers.education-services.ru/");

        String email = "test_" + System.currentTimeMillis() + "@mail.ru";
        String password = "123456";
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
        reg.fillForm(name, email, password);
        reg.clickRegister();

        // 5) Проверяем, что не показалась ошибка "Некорректный пароль"
        reg.assertNoInvalidPasswordError();

        // 6) После успешной регистрации возвращает на логин — проверяем форму логина
        LoginPage loginAfter = new LoginPage(driver);
        loginAfter.assertLoginFormVisible();
    }
}
