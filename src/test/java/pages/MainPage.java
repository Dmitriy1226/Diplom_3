package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MainPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By constructorTab =
            By.xpath("//*[contains(.,'Конструктор') or contains(.,'Constructor')]");

    private final By loginButton =
            By.xpath("//button[contains(.,'Войти') or contains(.,'Log in') or contains(.,'Sign in')]");

    // Сделал более точный: ссылка на /account или элемент с текстом "Личный кабинет/Account"
    private final By accountButton =
            By.xpath(
                    "//a[contains(@href,'/account')]" +
                            " | //p[contains(.,'Личный Кабинет') or contains(.,'Account')]/ancestor::*[self::a or self::button][1]" +
                            " | //*[(self::a or self::button) and (contains(.,'Личный Кабинет') or contains(.,'Account'))]"
            );

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Step("Открыть главную страницу: {baseUrl}")
    public void open(String baseUrl) {
        driver.get(baseUrl);
        waitForPageLoaded();
    }

    @Step("Ожидание загрузки главной страницы (readyState + якоря)")
    public void waitForPageLoaded() {
        wait.until(d -> {
            try {
                return "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState"));
            } catch (Exception e) {
                return false;
            }
        });

        wait.until(d -> {
            try {
                return d.findElements(constructorTab).size() > 0
                        || d.findElements(loginButton).size() > 0
                        || d.findElements(accountButton).size() > 0;
            } catch (Exception e) {
                return false;
            }
        });
    }

    @Step("Перейти на логин через главную")
    public void goToLogin(String baseUrl) {
        open(baseUrl);
        clickLoginButton();
    }

    @Step("Перейти в Личный кабинет (если не залогинен — попадём на /login)")
    public void goToAccountOrLogin(String baseUrl) {
        open(baseUrl);
        clickAccountButton();

        // ВАЖНО: дождаться реального перехода
        wait.until(d -> {
            try {
                String u = d.getCurrentUrl();
                return u.contains("/login") || u.contains("/account") || u.contains("/profile");
            } catch (Exception e) {
                return false;
            }
        });
    }

    @Step("Нажать кнопку 'Войти' на главной")
    public void clickLoginButton() {
        waitForPageLoaded();
        clickStable(loginButton);
    }

    @Step("Нажать 'Личный кабинет'")
    public void clickAccountButton() {
        waitForPageLoaded();
        clickStable(accountButton);
    }

    // ---- helpers ----

    private void clickStable(By locator) {
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollIntoView(el);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception clickProblem) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    private void scrollIntoView(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        } catch (Exception ignored) {
        }
    }
}
