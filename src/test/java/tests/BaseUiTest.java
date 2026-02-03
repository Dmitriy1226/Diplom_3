package tests;

import model.User;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import support.ApiUserHelper;
import support.DriverFactory;

import java.time.Duration;

public class BaseUiTest {

    protected WebDriver driver;

    protected User user;
    protected String accessToken;

    protected static final String BASE_URL =
            System.getProperty("baseUrl", "https://stellarburgers.education-services.ru/");

    private final ApiUserHelper apiUserHelper = new ApiUserHelper();

    @Before
    public void setUp() {
        driver = DriverFactory.getDriver(System.getProperty("browser", "chrome"));

        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);

        user = apiUserHelper.createRandomUser();
        accessToken = apiUserHelper.registerAndGetAccessToken(user);
    }

    @After
    public void tearDown() {
        apiUserHelper.deleteUser(accessToken);
        if (driver != null) driver.quit();
    }
}
