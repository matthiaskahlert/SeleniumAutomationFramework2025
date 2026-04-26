package base;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import utils.EmailUtils;
import utils.ExtentReportManager;
import utils.Log;

public class BaseTest {

	protected WebDriver driver;
	protected static ExtentReports extent;
	protected ExtentTest test;

	@BeforeSuite
	public void setupReport() {
		extent = ExtentReportManager.getReportInstance();
	}

	@AfterSuite
	public void teardownReport() {
		extent.flush();
		// String reportPath = ExtentReportManager.reportPath;
		// EmailUtils.sendTestReport(reportPath);
	}

	@BeforeMethod
	public void setUp() {

		Log.info("Starting WebDriver...");
		driver = createDriverWithFallback();
		Log.info("Navigating to URL...");
		driver.get("https://admin-demo.nopcommerce.com/login");
	}

	@AfterMethod
	public void tearDown(ITestResult result) {

		if (result.getStatus() == ITestResult.FAILURE) {

			String screenshotPath = ExtentReportManager.captureScreenshot(driver, "LoginFailure");
			test.fail("Test Failed.. Check Screenshot",
					MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
		}

		if (driver != null) {
			Log.info("Closing Browser...");
			driver.quit();
		}
	}

	private String resolveChromeBinary() {
		String envChrome = System.getenv("CHROME_BIN");
		if (envChrome != null && !envChrome.isBlank() && Files.exists(Path.of(envChrome))) {
			return envChrome;
		}

		String[] candidates = {
				"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
				"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe" };

		for (String candidate : candidates) {
			if (Files.exists(Path.of(candidate))) {
				return candidate;
			}
		}

		return null;
	}

	private WebDriver createDriverWithFallback() {
		ChromeOptions options = buildChromeOptions(true);
		try {
			Log.info("Trying Chrome with --headless=new");
			return new ChromeDriver(options);
		} catch (org.openqa.selenium.SessionNotCreatedException ex) {
			Log.info("Chrome startup with --headless=new failed. Retrying with legacy --headless.");
			ChromeOptions fallbackOptions = buildChromeOptions(false);
			return new ChromeDriver(fallbackOptions);
		}
	}

	private ChromeOptions buildChromeOptions(boolean useNewHeadless) {
		ChromeOptions options = new ChromeOptions();
		if (useNewHeadless) {
			options.addArguments("--headless=new");
		} else {
			options.addArguments("--headless");
		}
		options.addArguments("--disable-gpu");
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--no-first-run");
		options.addArguments("--no-default-browser-check");
		options.addArguments("--disable-background-networking");
		options.addArguments("--disable-extensions");
		options.addArguments("--remote-allow-origins=*");
		String tempProfile = "C:\\Windows\\Temp\\jenkins-chrome-profile-" + Instant.now().toEpochMilli();
		options.addArguments("--user-data-dir=" + tempProfile);

		String chromeBinary = resolveChromeBinary();
		if (chromeBinary != null) {
			options.setBinary(chromeBinary);
			Log.info("Using Chrome binary: " + chromeBinary);
		} else {
			Log.info("No explicit Chrome binary found. Falling back to Selenium Manager auto-detection.");
		}

		return options;
	}

}
