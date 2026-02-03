package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UiWaits {

    public static void waitForDocumentReady(WebDriver driver, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(d -> {
            try {
                return "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState"));
            } catch (Exception e) {
                return false;
            }
        });
    }

    public static void ensureNotDataUrl(WebDriver driver) {
        String url = driver.getCurrentUrl();
        if (url != null && url.startsWith("data:")) {
            throw new IllegalStateException("Открылась пустая страница data:, навигация сорвалась. URL=" + url);
        }
    }
}
