package support;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public static WebDriver getDriver(String browser) {
        ChromeOptions options = new ChromeOptions();

        if ("yandex".equalsIgnoreCase(browser)) {
            // путь к Yandex browser.exe
            String yandexBinary = System.getProperty("yandex.binary");
            if (yandexBinary == null || yandexBinary.isBlank()) {
                throw new IllegalStateException(
                        "Для Yandex Browser задай путь к browser.exe через -Dyandex.binary=\"C:\\\\...\\\\browser.exe\""
                );
            }
            options.setBinary(yandexBinary);

            // ВАЖНО: драйвер должен совпадать с версией Chromium у Яндекс.Браузера
            // По умолчанию ставим 142 (как у тебя в логах), но можно переопределить параметром:
            // -Dbrowser.version=142
            String browserVersion = System.getProperty("browser.version", "142");
            WebDriverManager.chromedriver().browserVersion(browserVersion).setup();

        } else {
            // обычный Chrome — можно брать актуальный драйвер
            WebDriverManager.chromedriver().setup();
        }

        return new ChromeDriver(options);
    }
}
