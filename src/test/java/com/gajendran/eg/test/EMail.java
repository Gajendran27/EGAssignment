package com.gajendran.eg.test;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
		System.setProperty("webdriver.chrome.driver", testProp.getProperty("driverPath"));

		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.navigate().to(appURL);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testGMailLoginWithValidCredentials() {
		// Login to gmail using email and password
		// The email and password should be read from a config file
		driver.findElement(By.xpath("//a[@id='gmail-sign-in']")).click();
		driver.findElement(By.xpath("//input[@id='Email']")).clear();
		driver.findElement(By.xpath("//input[@id='Email']")).sendKeys(testProp.getProperty("username"));
		driver.findElement(By.xpath("//input[@id='Passwd']")).clear();
		driver.findElement(By.xpath("//input[@id='Passwd']")).sendKeys(testProp.getProperty("password"));
		driver.findElement(By.xpath("//input[@id='signIn']")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		Assert.assertTrue(driver.getTitle().equalsIgnoreCase("GMAIL"), "GMAIL Login");

	}

	@Test
	public void testComposeNewEmailAndSaveToDraft() throws InterruptedException {
		// Compose a new email and save it to draft
		String toMailId = testProp.getProperty("receipient");
		String emailSubject = testProp.getProperty("emailSubject");
		String emailBody = testProp.getProperty("emailBody");

		driver.findElement(By.xpath("//div[@class='z0']/div")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//td//img[2]")).click();
		driver.findElement(By.className("vO")).sendKeys(toMailId);
		driver.findElement(By.className("aoT")).sendKeys(emailSubject);
		driver.switchTo().frame(driver.findElement(By.xpath("//iframe[@tabindex='1']")));

		// Enter the email body
		WebElement printbody = driver.switchTo().activeElement();
		printbody.sendKeys(emailBody);

		// Close the email window which will auto-save and close the email into
		// draft
		driver.switchTo().defaultContent();
		driver.findElement(By.xpath("//*[@id=':lx']")).click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	}

	@Test
	public void testPrintUnreadEmailCount() {
		// Print the unread email count in inbox
		String inbox = driver.findElement(By.xpath("//a[contains(@title,'Inbox')]")).getText();
		String unreadInboxMails = inbox.substring(inbox.indexOf("(") + 1, inbox.indexOf(")"));
		System.out.println("Total unread email in the inbox = " + unreadInboxMails);
	}

	@Test
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
			driver.quit();
		}
	}

}
