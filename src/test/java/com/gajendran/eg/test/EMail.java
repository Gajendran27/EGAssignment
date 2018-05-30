package com.gajendran.eg.test;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EMail {

	public static String appURL = "http://mail.google.com";
	Properties testProp = new Properties();
	private static WebDriver driver;

	@BeforeClass
	public void setUp() throws IOException {
		FileReader reader = new FileReader("testConfig.properties");
		testProp.load(reader);
		//System.setProperty("webdriver.chrome.driver", testProp.getProperty("driverPath"));
		//driver = new ChromeDriver();
		
		System.setProperty("webdriver.gecko.driver", testProp.getProperty("driverPath"));
		driver = new FirefoxDriver();
		
		driver.manage().window().maximize();
		driver.navigate().to(appURL);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		Assert.assertTrue(driver.getTitle().equalsIgnoreCase("GMAIL"), "GMAIL Login");
	}

	@Test
	public void testGMailLoginWithValidCredentials() {
		// Login to gmail using email and password
		// The email and password should be read from a config file
		
		//WebElement GMailLogin = driver.findElement(By.xpath("//a[@id='gmail-sign-in']"));
		//this.waitFluentlyForElement(driver, GMailLogin, 2);
		//GMailLogin.click();
		
		driver.findElement(By.xpath("//*[@id='identifierId']")).sendKeys(testProp.getProperty("username"));
		driver.findElement(By.xpath("//*[@id='identifierNext']")).click();

		WebElement passwordField = driver.findElement(By.xpath("//*[@id='password']/div[1]/div/div[1]/input"));
		this.waitFluentlyForElement(driver, passwordField, 2);
		passwordField.sendKeys(testProp.getProperty("password"));
		
		WebElement loginButton = driver.findElement(By.xpath("//*[@id='passwordNext']"));
		//this.waitFluentlyForElement(driver, loginButton, 10);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		loginButton.click();
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		Assert.assertTrue(driver.getTitle().contains("Inbox"), "Mailbox home Page");

	}

	@Test (dependsOnMethods={"testGMailLoginWithValidCredentials"})
	public void testComposeNewEmailAndSaveToDraft() throws InterruptedException  {
		// Compose a new email and save it to draft
		String toMailId = testProp.getProperty("receipient");
		String emailSubject = testProp.getProperty("emailSubject");
		String emailBody = testProp.getProperty("emailBody");
		Thread.sleep(1000);
		
		driver.findElement(By.cssSelector(".aic .z0 div")).click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		driver.switchTo().activeElement();
		driver.findElement(By.cssSelector(".oj div textarea")).sendKeys(toMailId);
		driver.findElement(By.cssSelector(".aoD.az6 input")).sendKeys(emailSubject);
		driver.findElement(By.cssSelector(".Ar.Au div")).sendKeys(emailBody);

		// Close the email window which will auto-save the email into draft
		driver.findElement(By.xpath("//*[@id=':ms']")).click();
		driver.switchTo().defaultContent();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	}

	@Test (dependsOnMethods={"testComposeNewEmailAndSaveToDraft"})
	public void testPrintUnreadEmailCount() {
		// Print the unread email count in inbox
		String inbox = driver.findElement(By.xpath("//a[contains(@title,'Inbox')]")).getText();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		String unreadInboxMails = inbox.substring(inbox.indexOf("(") + 1, inbox.indexOf(")"));
		System.out.println("Total unread email in the inbox = " + unreadInboxMails);
	}

	@Test (dependsOnMethods={"testPrintUnreadEmailCount"})
	public void testSubjectAndSenderEmailIdOfTheFirstUnreadEmail() {
		// Print the subject line of the first unread email
		List<WebElement> unreadEmails = driver.findElements(By.xpath("//*[@class='zA zE']"));
		System.out.println("Total unread emails are " + unreadEmails.size());
		
		//Click on the first unread email to open and get the subject
		unreadEmails.get(0).click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		String subject = driver.findElement(By.xpath("//h2[@id=':mz']")).getText();
		System.out.println("The subject line of the first unread email is -> " + subject);

		// Print the Sender email address of the first unread email
		String senderEmailId = driver.findElement(By.xpath("//span[@class='go']")).getText();
		System.out.println("The sender email id of the first unread email is -> " + senderEmailId);

	}

	@AfterClass
	public void tearDown() {
		if (driver != null) {
			System.out.println("Closing browser");
			//driver.quit();
		}
	}
	
	
	public WebElement waitFluentlyForElement(WebDriver driver, WebElement element, int timeOutInSeconds) {
		try {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); // nullify implicitlyWait()																
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeOutInSeconds, TimeUnit.SECONDS).pollingEvery(5, TimeUnit.SECONDS)
					.ignoring(NoSuchElementException.class);
			element = (WebElement) wait.until(ExpectedConditions.visibilityOf(element));
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS); // reset implicitlyWait to 20s
																							
			return element; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
