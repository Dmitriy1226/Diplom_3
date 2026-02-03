package tests;

import org.junit.Test;
import pages.LoginPage;
import pages.MainPage;

public class LoginTest extends BaseUiTest {

    @Test
    public void loginFromMainPageButton() {
        MainPage main = new MainPage(driver);
        main.open(BASE_URL);
        main.goToLogin(BASE_URL);

        LoginPage login = new LoginPage(driver);
        login.assertLoginFormVisible();

        login.fillLoginForm(user.getEmail(), user.getPassword());
        login.submitLogin();
        login.assertLoginSuccess();
    }

    @Test
    public void loginFromAccountButton() {
        MainPage main = new MainPage(driver);
        main.open(BASE_URL);
        main.goToAccountOrLogin(BASE_URL);

        LoginPage login = new LoginPage(driver);
        login.assertLoginFormVisible();

        login.fillLoginForm(user.getEmail(), user.getPassword());
        login.submitLogin();
        login.assertLoginSuccess();
    }
}
