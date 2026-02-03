package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ForgotPasswordPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By forgotHeader =
            By.xpath("//*[self::h2 or self::h1][contains(.,'Восстанов') or contains(.,'Forgot')]");
    private final By loginLink =
            By.xpath("//a[contains(@href,'/login') or contains(.,'Войти') or contains(.,'Log in')]");

    public ForgotPasswordPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Step("Открыта страница восстановления пароля")
    public void waitForOpen() {
        wait.until(d -> d.getCurrentUrl().contains("/forgot-password") || isPresent(forgotHeader));
    }

    @Step("Перейти по ссылке 'Войти' со страницы восстановления пароля")
    public void clickLoginLink() {
        waitForOpen();
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
        scrollIntoView(link);
        link.click();

        // ждём переход на /login
        wait.until(d -> d.getCurrentUrl().contains("/login"));
    }

    private boolean isPresent(By by) {
        return !driver.findElements(by).isEmpty();
    }

    private void scrollIntoView(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        } catch (Exception ignored) {}
    }
}
