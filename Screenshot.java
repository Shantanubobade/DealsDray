package Screenshot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.Dimension;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.time.Duration;
import java.util.List; 

public class Screenshot {
    private static final int[][] MOBILE_RESOLUTIONS = {{360, 640}, {414, 896}, {375, 667}};
    private static final int[][] DESKTOP_RESOLUTIONS = {{1920, 1080}, {1366, 768}, {1536, 864}};

    public static void main(String[] args) {
    	//System.setProperty("webdriver.chrome.driver", "C:\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        System.setProperty("webdriver.gecko.driver", "C:\\geckodriver-v0.35.0-win64\\geckodriver.exe");

        try {
            captureScreenshots();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void captureScreenshots() throws Exception {
        WebDriver driver = null;
        try {
            driver = new FirefoxDriver();
            //driver=new ChromeDriver();
            String sitemapUrl = "https://www.getcalley.com/page-sitemap.xml";
            driver.get(sitemapUrl);
            System.out.println("Sitemap Page Title: " + driver.getTitle());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("a")));

            List<WebElement> allLinks = driver.findElements(By.tagName("a"));
            
            String startUrl = "https://www.getcalley.com/";
            int startIndex = -1;
            
            for (int i = 0; i < allLinks.size(); i++) {
                if (allLinks.get(i).getAttribute("href").equals(startUrl)) {
                    startIndex = i;
                    break;
                }
            }
            
            if (startIndex == -1) {
                System.out.println("Starting URL not found in sitemap.");
                return;
            }
            
            for (int i = startIndex; i < startIndex + 5 && i < allLinks.size(); i++) {
                driver.get(sitemapUrl); // Return to sitemap page
                wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("a")));
                allLinks = driver.findElements(By.tagName("a")); // Refresh the list of links
                
                WebElement link = allLinks.get(i);
                String linkUrl = link.getAttribute("href");
                System.out.println("Processing link: " + linkUrl);
                
                captureDesktopScreenshots(driver, linkUrl, i - startIndex);
                captureMobileScreenshots(linkUrl, i - startIndex);
            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static void captureDesktopScreenshots(WebDriver driver, String url, int linkIndex) throws Exception {
        for (int[] resolution : DESKTOP_RESOLUTIONS) {
            driver.manage().window().setSize(new Dimension(resolution[0], resolution[1]));
            driver.get(url);
           // Thread.sleep(000);  // Wait 

            File screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(String.format("desktop_screenshot_%d_%dx%d.png", linkIndex, resolution[0], resolution[1]));
            FileUtils.copyFile(screenshotFile, destinationFile);
            System.out.println("Desktop screenshot saved: " + destinationFile.getAbsolutePath());
        }
    }

    private static void captureMobileScreenshots(String url, int linkIndex) throws Exception {
        for (int[] resolution : MOBILE_RESOLUTIONS) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--width=" + resolution[0]);
            options.addArguments("--height=" + resolution[1]);
            options.addArguments("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1");

            WebDriver mobileDriver = new FirefoxDriver(options);
            try {
                mobileDriver.get(url);
               // Thread.sleep(5000);  // Wait for page to load

                File screenshotFile = ((TakesScreenshot)mobileDriver).getScreenshotAs(OutputType.FILE);
                File destinationFile = new File(String.format("mobile_screenshot_%d_%dx%d.png", linkIndex, resolution[0], resolution[1]));
                FileUtils.copyFile(screenshotFile, destinationFile);
                System.out.println("Mobile screenshot saved: " + destinationFile.getAbsolutePath());
            } finally {
                mobileDriver.quit();
            }
        }
    }
}
