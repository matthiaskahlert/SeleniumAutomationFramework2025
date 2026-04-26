package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.Log;

public class LoginPage {

	private WebDriver driver;
	private WebDriverWait wait;

	@FindBy(id = "Email")
	WebElement usernameTextbox;

	@FindBy(id = "Password")
	WebElement passwordTextbox;

	@FindBy(xpath = "//*[@id=\"main\"]/div/div/div/div[2]/div[1]/div/form/div[3]/button")
	WebElement loginButton;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		PageFactory.initElements(driver, this);
	}

	public void enterUsername(String username) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email")));
			usernameTextbox.clear();
			usernameTextbox.sendKeys(username);
		} catch (TimeoutException ex) {
			String diagnostic = "Email field not visible. Current URL: " + driver.getCurrentUrl() + ", Title: "
					+ driver.getTitle();
			throw new AssertionError(diagnostic, ex);
		}
	}

	public void enterPassword(String password) {

		passwordTextbox.clear();
		passwordTextbox.sendKeys(password);
		// driver.findElement(passwordTextBox).clear();
		// driver.findElement(passwordTextBox).sendKeys(password);
	}

	public void clickLogin() {

		Log.info("Clicking login button..");
		loginButton.click();
		// driver.findElement(loginButton).click();
	}
}
