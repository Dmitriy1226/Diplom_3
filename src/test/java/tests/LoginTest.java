package tests;

import org.junit.Test;
import pages.LoginPage;
import pages.MainPage;

public class LoginTest extends BaseUiTest {

    @Test
    public void loginFromMainPageButton() {
        String baseUrl = System.getProperty("baseUrl", "https://stellarburgers.education-services.ru/");
        String email = System.getProperty("userEmail");
        String password = System.getProperty("userPassword");

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
        String email = System.getProperty("userEmail");
        String password = System.getProperty("userPassword");

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
