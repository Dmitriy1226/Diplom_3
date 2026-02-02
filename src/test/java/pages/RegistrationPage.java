package pages;

import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class RegistrationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Страница регистрации (хедер/форма)
    private final By registerHeader = By.xpath("//*[self::h2 or self::h1][contains(.,'Регистрация')]");
    private final By authForm = By.cssSelector("form");

    // Ошибка про пароль
    private final By invalidPasswordError = By.xpath(
            "//*[contains(.,'Некорректный пароль') or (contains(.,'Некорректный') and contains(.,'пароль'))]"
    );

    // Кнопка "Зарегистрироваться"
    private final By registerButton = By.xpath("//button[.//span[contains(.,'Зарегистрироваться')] or contains(.,'Зарегистрироваться')]");

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /** Ждём, что мы реально на странице регистрации и форма готова */
    public void waitForOpen() {
        try {
            // ждём либо URL /register, либо заголовок "Регистрация"
            wait.until(d -> d.getCurrentUrl().contains("/register") || isPresent(registerHeader));

            // форма должна быть в DOM и видимой
            wait.until(ExpectedConditions.presenceOfElementLocated(authForm));
            wait.until(ExpectedConditions.visibilityOfElementLocated(authForm));
        } catch (TimeoutException e) {
            dumpState("waitForOpen() TIMEOUT");
            throw e;
        }
    }

    public void fillForm(String name, String email, String password) {
        waitForOpen();

        WebElement nameInput = findInputByLabel("Имя");
        WebElement emailInput = findInputByLabel("Email");
        WebElement passInput = findInputByLabel("Пароль");

        typeSmart(nameInput, name);
        typeSmart(emailInput, email);
        typeSmart(passInput, password);
    }

    public void clickRegister() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(registerButton));
            clickSmart(btn);
        } catch (TimeoutException e) {
            dumpState("clickRegister() TIMEOUT");
            throw e;
        }
    }

    /** После клика на регистрацию: убеждаемся, что НЕ появилась ошибка пароля */
    public void assertNoInvalidPasswordError() {
        // ждём короткое время: если ошибка должна появиться, она появится быстро
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.presenceOfElementLocated(invalidPasswordError));
        } catch (TimeoutException ignored) {
            // ошибки нет — ок
        }

        List<WebElement> errors = driver.findElements(invalidPasswordError);
        if (!errors.isEmpty() && errors.stream().anyMatch(WebElement::isDisplayed)) {
            String text = errors.get(0).getText();
            Assert.fail("Появилась ошибка про пароль: '" + text + "'. Проверь пароль (должен быть >= 6 символов). URL=" + driver.getCurrentUrl());
        }
    }

    // -------------------- helpers --------------------

    /** Находит input, который относится к label с текстом (Имя/Email/Пароль) */
    private WebElement findInputByLabel(String labelText) {
        By byLabelFollowingInput = By.xpath(
                "//*[self::label or self::p or self::span][normalize-space()='" + labelText + "']" +
                        "/ancestor::*[self::div or self::fieldset][1]//input"
        );

        By byContains = By.xpath(
                "//*[self::label or self::p or self::span][contains(.,'" + labelText + "')]" +
                        "/ancestor::*[self::div or self::fieldset][1]//input"
        );

        By allInputs = By.cssSelector("input");

        try {
            WebElement el = firstDisplayed(driver.findElements(byLabelFollowingInput));
            if (el != null) return wait.until(ExpectedConditions.elementToBeClickable(el));

            el = firstDisplayed(driver.findElements(byContains));
            if (el != null) return wait.until(ExpectedConditions.elementToBeClickable(el));

            // fallback по порядку: Имя/Email/Пароль — обычно первые три
            List<WebElement> inputs = driver.findElements(allInputs);
            inputs.removeIf(i -> !i.isDisplayed() || !i.isEnabled());

            if (inputs.size() >= 3) {
                if (labelText.equals("Имя")) return inputs.get(0);
                if (labelText.equals("Email")) return inputs.get(1);
                if (labelText.equals("Пароль")) return inputs.get(2);
            }

            dumpState("findInputByLabel(" + labelText + ") NOT FOUND");
            Assert.fail("Не нашёл input для поля '" + labelText + "'. URL=" + driver.getCurrentUrl());
            return null;
        } catch (TimeoutException e) {
            dumpState("findInputByLabel(" + labelText + ") TIMEOUT clickable");
            throw e;
        }
    }

    private void typeSmart(WebElement el, String text) {
        try {
            scrollIntoView(el);
            clickSmart(el);

            el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            el.sendKeys(Keys.BACK_SPACE);

            new Actions(driver).sendKeys(el, text).perform();

            String value = el.getAttribute("value");
            if (value == null || value.trim().isEmpty()) {
                jsSetValue(el, text);
            }

        } catch (Exception e) {
            dumpState("typeSmart() ERROR");
            throw e;
        }
    }

    private void clickSmart(WebElement el) {
        try {
            scrollIntoView(el);
            el.click();
        } catch (ElementClickInterceptedException | JavascriptException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    private void scrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    private void jsSetValue(WebElement el, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles:true}));",
                el, value
        );
    }

    private WebElement firstDisplayed(List<WebElement> list) {
        for (WebElement el : list) {
            try {
                if (el != null && el.isDisplayed()) return el;
            } catch (StaleElementReferenceException ignored) {
            }
        }
        return null;
    }

    private boolean isPresent(By by) {
        return !driver.findElements(by).isEmpty();
    }

    private void dumpState(String reason) {
        System.out.println("=== DEBUG (" + reason + ") ===");
        System.out.println("URL: " + driver.getCurrentUrl());
        System.out.println("TITLE: " + driver.getTitle());
        System.out.println("HTML length: " + driver.getPageSource().length());
        System.out.println("=============================");
    }
}
