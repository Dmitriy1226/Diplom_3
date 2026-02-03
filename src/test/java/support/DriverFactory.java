package support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    public static WebDriver getDriver(String browser) {
        if ("yandex".equalsIgnoreCase(browser)) {
            throw new IllegalStateException("Пока запускаем только Chrome. Для Yandex вернёмся после стабилизации.");
        }

        ChromeOptions options = new ChromeOptions();

        System.out.println("### DriverFactory USED ###");
        System.out.println("### browser param = " + browser + " ###");
        options.addArguments("--lang=ru");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1280,900");

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // Часто нужно на новых Chrome/Driver
        options.addArguments("--remote-allow-origins=*");

        // Чистый профиль
        try {
            Path profileDir = Files.createTempDirectory("sb-profile-");
            options.addArguments("--user-data-dir=" + profileDir.toAbsolutePath());
        } catch (Exception ignored) {
        }

        // prefs: отключаем восстановление сессии и прочие "помощники"
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("translate.enabled", false);

        // 4 = открыть новую вкладку (не восстанавливать)
        prefs.put("session.restore_on_startup", 4);

        // убрать “Chrome управляется организацией” это не снимет, но подсказки отключит
        prefs.put("browser.show_home_button", false);

        options.setExperimentalOption("prefs", prefs);

        return new ChromeDriver(options);
    }
}
