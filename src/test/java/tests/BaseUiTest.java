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

    private final ApiUserHelper apiUserHelper = new ApiUserHelper();

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

        // Данные теста: создаём уникального пользователя через API
        user = apiUserHelper.createRandomUser();
        accessToken = apiUserHelper.registerAndGetAccessToken(user);
    }

    @After
    public void tearDown() {
        // После теста удаляем пользователя через API
        apiUserHelper.deleteUser(accessToken);

        if (driver != null) driver.quit();
    }
}
