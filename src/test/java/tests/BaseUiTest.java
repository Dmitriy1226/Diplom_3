package tests;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import support.DriverFactory;

import java.time.Duration;

public abstract class BaseUiTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected String userEmail;
    protected String userPassword;

    @Before
    public void setUp() {
        String browser = System.getProperty("browser", "chrome");

        driver = DriverFactory.getDriver(browser);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        userEmail = System.currentTimeMillis() + "@test.ru";
        userPassword = "123456";

        driver.manage().window().maximize();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
