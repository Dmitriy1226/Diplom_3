package tests;

import io.qameta.allure.Description;
import org.junit.Test;
import pages.MainPage;

import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;

public class LoginFromMainPageTest extends BaseUiTest {

    @Test
    @Description("Переход к форме логина по кнопке «Войти в аккаунт» на главной")
    public void loginFromMainPage() {
        driver.get("https://stellarburgers.education-services.ru/");

        MainPage mainPage = new MainPage(driver);
        mainPage.clickLoginButton();

        // Проверяем, что открылась страница логина
        wait.until(urlContains("/login"));
    }
}
