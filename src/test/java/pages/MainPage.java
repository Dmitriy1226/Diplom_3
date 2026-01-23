package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MainPage {

    private final WebDriver driver;

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    // Кнопка Войти в аккаунт
    private final By loginButton = By.xpath("//button[text()='Войти в аккаунт']");

    // Кнопка Личный кабинет
    private final By accountButton = By.xpath("//p[text()='Личный Кабинет']");

    @Step("Нажать кнопку Войти в аккаунт")
    public void clickLoginButton() {
        driver.findElement(loginButton).click();
    }

    @Step("Нажать кнопку Личный кабинет")
    public void clickAccountButton() {
        driver.findElement(accountButton).click();
    }
}
