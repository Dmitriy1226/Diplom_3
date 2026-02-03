package tests;

import org.junit.Test;
import pages.LoginPage;
import pages.MainPage;

public class LoginTest extends BaseUiTest {

    @Test
    public void loginFromMainPageButton() {
        String baseUrl = System.getProperty("baseUrl", "https://stellarburgers.education-services.ru/");

        // Берём креды из пользователя, которого создали в BaseUiTest через API
        String email = user.getEmail();
        String password = user.getPassword();

        MainPage main = new MainPage(driver);
        main.open(baseUrl);
        main.goToLogin(baseUrl);

        LoginPage login = new LoginPage(driver);
        login.assertLoginFormVisible();

        login.fillLoginForm(email, password);
        login.submitLogin();
        login.assertLoginSuccess();
    }

    @Test
    public void loginFromAccountButton() {
        String baseUrl = System.getProperty("baseUrl", "https://stellarburgers.education-services.ru/");

        // Берём креды из пользователя, которого создали в BaseUiTest через API
        String email = user.getEmail();
        String password = user.getPassword();

        MainPage main = new MainPage(driver);
        main.open(baseUrl);
        main.goToAccountOrLogin(baseUrl);

        LoginPage login = new LoginPage(driver);
        login.assertLoginFormVisible();

        login.fillLoginForm(email, password);
        login.submitLogin();
        login.assertLoginSuccess();
    }
}
