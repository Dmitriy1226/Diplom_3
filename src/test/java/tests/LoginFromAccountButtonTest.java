package tests;

import io.qameta.allure.Description;
import org.junit.Test;
import pages.MainPage;

import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;

public class LoginFromAccountButtonTest extends BaseUiTest {

    @Test
    @Description("Переход на страницу логина по кнопке «Личный кабинет»")
    public void loginFromAccountButton() {

        // 1. Открываем главную
        driver.get("https://stellarburgers.education-services.ru/");

        MainPage mainPage = new MainPage(driver);

        // 2. Нажимаем «Личный кабинет»
        mainPage.clickAccountButton();

        // 3. Проверяем, что перешли на страницу логина
        wait.until(urlContains("login"));
    }
}
