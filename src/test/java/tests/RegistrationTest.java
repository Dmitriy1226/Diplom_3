package tests;

import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;
import pages.MainPage;
import pages.RegistrationPage;

import java.time.Duration;

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

        // 3) Ждём, что реально ушли на /register
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/register"));

        // 4) Регистрация
        RegistrationPage reg = new RegistrationPage(driver);
        reg.fillForm(name, email, password);
        reg.clickRegister();

        // 5) Проверяем, что не показалась ошибка "Некорректный пароль"
        reg.assertNoInvalidPasswordError();

        // 6) После успешной регистрации обычно возвращает на логин — проверяем форму логина
        LoginPage loginAfter = new LoginPage(driver);
        loginAfter.assertLoginFormVisible();
    }
}
