package tests;

import org.junit.Assert;
import org.junit.Test;

public class OpenSiteTest extends BaseUiTest {

    @Test
    public void siteShouldOpen() {
        String url = driver.getCurrentUrl();
        String title = driver.getTitle();
        int len = driver.getPageSource() == null ? 0 : driver.getPageSource().length();

        System.out.println("URL: " + url);
        System.out.println("TITLE: " + title);
        System.out.println("HTML length: " + len);

        // Если тут мало — значит сайт не отдался нормально (ошибка/блокировка/заглушка)
        Assert.assertTrue("Страница выглядит пустой, HTML слишком маленький: " + len, len > 5000);
    }
}
