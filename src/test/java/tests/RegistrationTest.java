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

        reg.fillForm(name, registeredEmail, registeredPassword);
        reg.clickRegister();

        reg.assertNoInvalidPasswordError();

        LoginPage loginAfter = new LoginPage(driver);
        loginAfter.assertLoginFormVisible();

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

        assertTrue("Ожидали остаться на странице /register при коротком пароле. URL=" + driver.getCurrentUrl(),
                driver.getCurrentUrl().contains("/register"));
    }
}
