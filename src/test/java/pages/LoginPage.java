package pages;

import io.qameta.allure.Step;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By emailInput = By.xpath(
            "//label[contains(.,'Email') or contains(.,'E-mail') or contains(.,'Почта')]/following::input[1]" +
                    " | //input[@name='email' or @type='email']" +
                    " | //input[contains(@placeholder,'Email') or contains(@placeholder,'E-mail') or contains(@placeholder,'Почта')]"
    );

    private final By passwordInput = By.xpath(
            "//input[@name='password' or @type='password']" +
                    " | //label[contains(.,'Пароль') or contains(.,'Password')]/following::input[1]"
    );

    private final By submitButton = By.xpath(
            "//button[@type='submit' and (contains(.,'Войти') or contains(.,'Log in') or contains(.,'Sign in'))]" +
                    " | //button[contains(.,'Войти') or contains(.,'Log in') or contains(.,'Sign in')]"
    );

    private final By registerLink = By.xpath(
            "//a[contains(@href,'/register') or contains(.,'Зарегистр') or contains(.,'Register')]"
    );

    private final By incorrectPasswordError = By.xpath(
            "//*[contains(.,'Некорректный пароль') or contains(.,'Incorrect password')]" +
                    " | //p[contains(@class,'input__error') and (contains(.,'парол') or contains(.,'password'))]"
    );

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Step("Открыть страницу логина")
    public void open(String baseUrl) {
        String url = baseUrl.endsWith("/") ? baseUrl + "login" : baseUrl + "/login";
        driver.get(url);
        waitForPageLoaded();
    }

    @Step("Ожидание загрузки страницы логина (readyState + якоря формы)")
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
                return d.findElements(emailInput).size() > 0
                        || d.findElements(passwordInput).size() > 0
                        || d.findElements(submitButton).size() > 0;
            } catch (Exception e) {
                return false;
            }
        });
    }

    @Step("Проверить что форма логина видима")
    public void assertLoginFormVisible() {
        waitForPageLoaded();

        boolean emailOk = isDisplayed(emailInput);
        boolean passOk = isDisplayed(passwordInput);
        boolean btnOk = isDisplayed(submitButton);

        if (!(emailOk && passOk && btnOk)) {
            Assert.fail("Не вижу форму логина (email/password/button). " +
                    "email=" + emailOk + ", pass=" + passOk + ", button=" + btnOk +
                    ". URL=" + safeUrl());
        }
    }

    // ====== A7: разделили на действие и проверку ======

    @Step("Заполнить форму логина: email={email}")
    public void fillLoginForm(String email, String password) {
        waitForPageLoaded();

        WebElement emailEl = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        emailEl.click();
        emailEl.clear();
        emailEl.sendKeys(email);

        WebElement passEl = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        passEl.click();
        passEl.clear();
        passEl.sendKeys(password);
    }

    @Step("Нажать кнопку 'Войти'")
    public void submitLogin() {
        waitForPageLoaded();
        clickStable(submitButton);
    }

    @Step("Ожидать завершения логина: редирект или ошибка")
    public void waitForLoginResult() {
        boolean finished = wait.until(d -> {
            try {
                String u = d.getCurrentUrl();
                boolean leftLogin = !u.contains("/login");
                boolean hasError = d.findElements(incorrectPasswordError).stream().anyMatch(WebElement::isDisplayed);
                return leftLogin || hasError;
            } catch (Exception e) {
                return false;
            }
        });

        if (!finished) {
            Assert.fail("После нажатия 'Войти' ничего не произошло. URL=" + safeUrl());
        }
    }

    @Step("Проверить успешный логин")
    public void assertLoginSuccess() {
        waitForLoginResult();

        if (safeUrl().contains("/login") && isDisplayed(incorrectPasswordError)) {
            Assert.fail("Логин не удался: отображается ошибка 'Некорректный пароль'. " +
                    "Проверь email/password. URL=" + safeUrl());
        }

        if (safeUrl().contains("/login")) {
            Assert.fail("Остались на странице /login. Логин не выполнен. URL=" + safeUrl());
        }
    }

    @Step("Войти: email={email}")
    public void login(String email, String password) {
        // оставим удобный “комбо”-метод, но теперь он просто вызывает отдельные шаги
        fillLoginForm(email, password);
        submitLogin();
        assertLoginSuccess();
    }

    @Step("Перейти по ссылке 'Зарегистрироваться'")
    public void clickRegisterLink() {
        clickStable(registerLink);
    }

    // ---- helpers ----

    private boolean isDisplayed(By locator) {
        try {
            List<WebElement> els = driver.findElements(locator);
            if (els.isEmpty()) return false;
            for (WebElement el : els) {
                if (el.isDisplayed()) return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

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
        } catch (Exception ignored) { }
    }

    private String safeUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return "<unknown>";
        }
    }
}
