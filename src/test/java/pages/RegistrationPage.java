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

    // Страница регистрации (хедер/форма) — более надежно, чем ждать конкретно email input
    private final By registerHeader = By.xpath("//*[self::h2 or self::h1][contains(.,'Регистрация')]");
    private final By authForm = By.cssSelector("form");

    // Ошибка про пароль (может отличаться по тексту — сделали мягко)
    private final By invalidPasswordError = By.xpath("//*[contains(.,'Некорректный пароль') or contains(.,'Некорректный') and contains(.,'пароль')]");

    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /** Ждём, что мы реально на странице регистрации и форма готова */
    public void waitForPageLoaded() {
        try {
            // ждём либо URL /register, либо заголовок "Регистрация"
            wait.until(d -> d.getCurrentUrl().contains("/register") || isPresent(registerHeader));

            // форма должна быть в DOM
            wait.until(ExpectedConditions.presenceOfElementLocated(authForm));

            // и должна быть видимой (не всегда, но чаще)
            wait.until(ExpectedConditions.visibilityOfElementLocated(authForm));
        } catch (TimeoutException e) {
            dumpState("waitForPageLoaded() TIMEOUT");
            throw e;
        }
    }

    public void fillForm(String name, String email, String password) {
        waitForPageLoaded();

        WebElement nameInput = findInputByLabel("Имя");
        WebElement emailInput = findInputByLabel("Email");     // на сайте обычно именно "Email"
        WebElement passInput = findInputByLabel("Пароль");

        typeSmart(nameInput, name);
        typeSmart(emailInput, email);
        typeSmart(passInput, password);
    }

    public void clickRegister() {
        // Кнопка "Зарегистрироваться"
        By registerButton = By.xpath("//button[.//span[contains(.,'Зарегистрироваться')] or contains(.,'Зарегистрироваться')]");
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
        // маленькая пауза, чтобы ошибка успела показаться если она есть
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        List<WebElement> errors = driver.findElements(invalidPasswordError);
        if (!errors.isEmpty() && errors.stream().anyMatch(WebElement::isDisplayed)) {
            String text = errors.get(0).getText();
            Assert.fail("Появилась ошибка про пароль: '" + text + "'. Проверь пароль (должен быть >= 6 символов). URL=" + driver.getCurrentUrl());
        }
    }

    // -------------------- helpers --------------------

    /** Находит input, который относится к label с текстом (Имя/Email/Пароль) */
    private WebElement findInputByLabel(String labelText) {
        // 1) Самый частый вариант: label + input рядом в блоке
        By byLabelFollowingInput = By.xpath(
                "//*[self::label or self::p or self::span][normalize-space()='" + labelText + "']" +
                        "/ancestor::*[self::div or self::fieldset][1]//input"
        );

        // 2) Иногда labelText не ровно совпадает (например "E-mail")
        By byContains = By.xpath(
                "//*[self::label or self::p or self::span][contains(.,'" + labelText + "')]" +
                        "/ancestor::*[self::div or self::fieldset][1]//input"
        );

        // 3) fallback: на странице может быть 3 инпута подряд; берём по порядку
        // (имя, email, пароль) — только если не нашли по label
        By allInputs = By.cssSelector("input");

        try {
            WebElement el = firstDisplayed(driver.findElements(byLabelFollowingInput));
            if (el != null) return wait.until(ExpectedConditions.elementToBeClickable(el));

            el = firstDisplayed(driver.findElements(byContains));
            if (el != null) return wait.until(ExpectedConditions.elementToBeClickable(el));

            // fallback по порядку: ищем 3 "нормальных" input-а в форме
            List<WebElement> inputs = driver.findElements(allInputs);
            inputs.removeIf(i -> !i.isDisplayed() || !i.isEnabled());

            if (inputs.size() >= 3) {
                // Имя/Email/Пароль — обычно первые три
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

            // чистим гарантированно
            el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            el.sendKeys(Keys.BACK_SPACE);

            // иногда сайт “глючит” от мгновенного sendKeys — вводим через Actions
            new Actions(driver).sendKeys(el, text).perform();

            // валидация что реально ввелось (если поле тупит — это сразу видно)
            String value = el.getAttribute("value");
            if (value == null || value.trim().isEmpty()) {
                // последняя попытка через JS
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
            // если перекрыто — кликаем JS
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
            } catch (StaleElementReferenceException ignored) {}
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
