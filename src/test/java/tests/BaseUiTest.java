package tests;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import support.DriverFactory;

import java.time.Duration;

public class BaseUiTest {

    protected WebDriver driver;

    @Before
    public void setUp() {
        driver = DriverFactory.getDriver(System.getProperty("browser", "chrome"));

        // критично для флапа: не наследуем “старые” сессии
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();

        // таймауты на всякий
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        // implicit wait не используем (чтобы не мешал WebDriverWait)
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
