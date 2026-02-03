package tests;

import org.junit.Test;
import pages.ForgotPasswordPage;
import pages.LoginPage;
import pages.MainPage;
import pages.RegistrationPage;

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

    @Test
    public void loginFromRegistrationPageLink() {
        LoginPage login = new LoginPage(driver);
        login.open(BASE_URL);
        login.assertLoginFormVisible();

        // /login -> /register
        login.clickRegisterLink();

        // /register -> /login (ссылка "Войти")
        RegistrationPage reg = new RegistrationPage(driver);
        reg.waitForOpen();
        reg.clickLoginLink();

        // логинимся
        login.assertLoginFormVisible();
        login.fillLoginForm(user.getEmail(), user.getPassword());
        login.submitLogin();
        login.assertLoginSuccess();
    }

    @Test
    public void loginFromForgotPasswordPageLink() {
        LoginPage login = new LoginPage(driver);
        login.open(BASE_URL);
        login.assertLoginFormVisible();

        // /login -> /forgot-password
        login.clickForgotPasswordLink();

        // /forgot-password -> /login (ссылка "Войти")
        ForgotPasswordPage forgot = new ForgotPasswordPage(driver);
        forgot.waitForOpen();
        forgot.clickLoginLink();

        // логинимся
        login.assertLoginFormVisible();
        login.fillLoginForm(user.getEmail(), user.getPassword());
        login.submitLogin();
        login.assertLoginSuccess();
    }
}
