package Screenshot;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;

public class Functional {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://demo.dealsdray.com/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            usernameField.sendKeys("prexo.mis@dealsdray.com");
            driver.findElement(By.name("password")).sendKeys("prexo.mis@dealsdray.com");
            driver.findElement(By.xpath("//button[text()='Login']")).click();

            WebElement ordersSection = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Order']")));
            ordersSection.click();

            WebElement orderButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, '/orders')]")));
            orderButton.click();

            WebElement addBulkOrderButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Add Bulk Orders')]")));
            addBulkOrderButton.click();

            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='file']")));
            fileInput.sendKeys("C:\\Users\\91703\\Downloads\\demo-data.xlsx");

            WebElement importButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Import']")));
            importButton.click();

            WebElement validateDataButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Validate Data']")));
            validateDataButton.click();

            // Wait for the alert to be present and handle it
            try {
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                String alertText = alert.getText();
                System.out.println("Alert text: " + alertText);
                alert.accept(); // This clicks the OK button on the alert
            } catch (TimeoutException e) {
                System.out.println("No alert present after waiting");
            }

            // Wait for the page to stabilize after closing the alert
            Thread.sleep(2000);

            // Take a screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImg = ImageIO.read(screenshot);

            // Save the screenshot
            File destFile = new File("C:\\Users\\91703\\eclipse-workspace\\Assignment\\validation_result.png");
            ImageIO.write(fullImg, "png", destFile);
            System.out.println("Screenshot saved successfully at: " + destFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
