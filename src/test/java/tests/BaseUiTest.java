package tests;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BaseUiTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected String userEmail;
    protected String userPassword;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        userEmail = System.currentTimeMillis() + "@test.ru";
        userPassword = "123456";

        driver.manage().window().maximize();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
