package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import base.TestBase;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import utilities.Data;
import utilities.Util;

public class TreatHomePage extends TestBase {


	public TreatHomePage(AppiumDriver<WebElement> driver, Data data) {
		super(driver, data);
	}

	private TestBase page;

	@AndroidFindBy(id = "com.xpresspa.treatmobile.qa:id/login_button")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='Sign In']")
	private MobileElement btnSignInSplashScreen;

	@AndroidFindBy(id = "com.xpresspa.treatmobile.qa:id/edit_email")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name='Email Address']//preceding-sibling:: XCUIElementTypeTextField")
	private MobileElement txtEmailAddress;

	@AndroidFindBy(id = "com.xpresspa.treatmobile.qa:id/edit_password")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name='Password']//following-sibling:: XCUIElementTypeSecureTextField")
	private MobileElement txtPassword;

	@AndroidFindBy(id = "com.xpresspa.treatmobile.qa:id/btn_login")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='Sign In']")
	private MobileElement btnSignIn;

	@AndroidFindBy(id = "com.xpresspa.treatmobile.qa:id/myAccount")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='accountInactive']")
	private MobileElement btnMyAccount;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text='Discover']")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='accountInactive']")
	private MobileElement txtDiscover;

	@AndroidFindBy(id = "com.xpresspa.treatmobile.qa:id/tv_sign_out")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='SIGN OUT']")
	private MobileElement btnSignOut;

	@iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='Not Now']")
	private MobileElement btnNotNow;
	
	@AndroidFindBy(id = "com.xpresspa.treatmobile.qa:id/btn_create_account")
	@iOSXCUITFindBy(xpath = "")
	private MobileElement lnkCreateAccount;
	
	
	public void clickOnSignInbuttonInSplashScreen() {
		
		waitForElement(btnSignInSplashScreen);
		if(isElementPresent(btnMyAccount))
		clickOn(btnSignInSplashScreen, "Sign In Button");
		
		waitForElement(txtEmailAddress);
		if(isElementPresent(txtEmailAddress)) {
			takeScreenshot(driver);	
			
			failed(driver,"User successfully  navigates to signIn Page");
		}
		else {
			failed(driver,"User not anble to navigate to signIn Page");
		}
		
		
	}

	public void SignInToApp() {

		try {
			
			clickOnSignInbuttonInSplashScreen();
			
			enterText(txtEmailAddress, "EmailAddress", data.get("UserName"));

			enterText(txtPassword, "Password", data.get("Password"));

			takeScreenshot(driver);
			clickOn(btnSignIn, "Sign In Button");

			waitForElement(btnNotNow);

			if (isElementPresent(btnNotNow)) {

				clickOn(btnNotNow, "My Account button");
			}

			waitForElement(txtDiscover);

			if (isElementPresent(txtDiscover)) {

				List<WebElement> els1 = driver.findElementsById("com.xpresspa.treatmobile.qa:id/image_view_content");

				Point p1 = els1.get(0).getLocation();

				Point p2 = els1.get(1).getLocation();

				passed("Successfully Navigated To Discover Page");
				takeScreenshot(driver);
				SingOutApp();
			} else {
				failed(driver, "Failed to Navigate To Discover Page");
			}
		} catch (Exception e) {
			failed(driver, "Exception caught while logging in " + e.getMessage());
		}

	}

	public void SingOutApp() {

		waitForElement(btnMyAccount);

		if (isElementPresent(btnMyAccount)) {

			clickOn(btnMyAccount, "My Account button");

			takeScreenshot(driver);

			waitForElement(btnSignOut);

			if (isElementPresent(btnSignOut)) {

				clickOn(btnSignOut, "Sign out button");

				takeScreenshot(driver);

				if (isElementPresent(btnSignInSplashScreen)) {

					passed("User Successfully Logged out From the account");
				} else {
					failed(driver, "User Failed to Log out from the account");
				}
			} else {
				failed(driver, "User Failed to Locate SignOut button");
			}

		} else {
			failed(driver, "User Failed to Locate from the My Account button");
		}

	}

	
	
	
	public CreateAccountPage clickOnCreateAccount() {
		
		
		
		
		return new CreateAccountPage(driver,data);
	}
}
