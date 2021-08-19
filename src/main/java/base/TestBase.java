package base;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;

import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

//import com.experitest.client.Client;


import utilities.Common;
import utilities.Data;
import utilities.GlobalKeys;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HasOnScreenKeyboard;
import io.appium.java_client.LocksDevice;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSTouchAction;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import pages.TreatHomePage;





/**
 * Base class for all the pages.
 *
 */
public abstract class TestBase extends Common{
	protected static   AppiumDriver<WebElement> driver;	
	protected    Data data;

	/**
	 * Constructor for Page class 
	 * @param browser
	 * @param report
	 */
	protected TestBase(AppiumDriver<WebElement> driver2,Data data) {
		this.driver=driver2;	
		this.data = data;
		PageFactory.initElements(new AppiumFieldDecorator(driver2), this);		
	}

	public static TreatHomePage  setUp(Data data) {
		try {
			String strExecutionEnvironment = GlobalKeys.configData.get("ExecutionEnvironment");
			String deviceName = GlobalKeys.configData.get("DeviceName");
			String paltformName = GlobalKeys.configData.get("PlatFormName");
			String accesskey = GlobalKeys.configData.get("AccessKey");
			String username = GlobalKeys.configData.get("Username");
			String Sauceurl = GlobalKeys.configData.get("Sauceurl");
			String IPAFileName = GlobalKeys.configData.get("IPAFileName");
			String AndroidFileName = GlobalKeys.configData.get("AndroidFileName");

			if (paltformName.trim().contains("iOS")) {

				DesiredCapabilities dc = new DesiredCapabilities();
				dc.setCapability("deviceName", "iphone Sun");
				dc.setCapability("udid", "auto");
				dc.setCapability("platformName", "iOS");
				dc.setCapability("platformVersion", "14.4.2");
				dc.setCapability("bundleId", "com.mutualmobile.treat.dev");
				dc.setCapability("xcodeOrgId", "LJ44QB69J7");
				dc.setCapability("xcodeSigningId", "iPhone Developer");
				dc.setCapability("automationName", "XCUITest");
				driver = new IOSDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), dc);

			} else {
				if (paltformName.trim().equalsIgnoreCase("Android")) {
					DesiredCapabilities dc = new DesiredCapabilities();
					dc.setCapability("deviceName", "RedmiK20pro");
					dc.setCapability("udid", "d015a208");
					dc.setCapability("platformName", "Android");
					dc.setCapability("platformVersion", "11");
					dc.setCapability("noReset", "true");
					dc.setCapability("appPackage", "com.xpresspa.treatmobile.qa");
					dc.setCapability("appActivity", "com.xpresspa.treatmobile.ui.activities.splash.SplashActivity");
					dc.setCapability("skipDeviceInitialization", true);
					dc.setCapability("skipServerInstallation", true);
					URL url = new URL("http://127.0.0.1:4723/wd/hub");
					driver = new AndroidDriver<>(url, dc);
				} else {

					//other config goes here.
				}

			}
			GlobalKeys.driver = driver;
			
			
		} catch (Exception e) {
			failAssert("Unable to launch application in Sauce Labs, Exception is : " + e.getMessage());
		}
	 
		return new TreatHomePage(driver,data);
		
	}

	public static void tearDown() {
		try {

			driver.quit();
		} catch (Exception e) {

			e.printStackTrace();

		}
	}
	

	
	public static void sleep(int i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * Check if the element is present in the page
	 * @param element WebElement need to check
	 * @return True/False
	 */
	protected boolean isElementPresent(WebElement element){
		try{
			if(element.isEnabled()){
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			return false;		
		}
	}


	public void selectByVisisbleValue(WebElement we,String strElemName,String strVisibleValue){
		try{
			if(waitForDropDown(we)){
				Select sel = new Select( we);
				sel.selectByValue(strVisibleValue);
				passed("Selected value -"+strVisibleValue +" from dropdown->"+strElemName);
			}
		}catch(Exception Ex){
			failed(driver,"Unable to select value from the dropdown "+Ex.getMessage());
		}
	}

	/**
	 * Check if the element is present in the page
	 * @param Element locator of type By
	 * @return True/False
	 */
	public boolean isElementPresent(By by){
		try{
			new WebDriverWait(driver, 2).until(ExpectedConditions
					.elementToBeClickable(by));
			if(driver.findElement(by).isDisplayed()){
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			return false;		
		}
	}


	/**
	 * Check if the element is present in the page and report
	 * @param element
	 * @param Name of the Element
	 * @param Name of the page
	 */
	protected void verifyIfElementPresent(WebElement element,String elemName,String pageName) {
		waitForIsClickable(element);
		if(isElementPresent(element)){
			passed(elemName + "Element is displayed in "+pageName+" page" );
		}else{
			failed(driver,elemName + "Element is not displayed in "+pageName+" page" );
		}
	}

	/***
	 * Method to check for an alert for 20 seconds
	 * @param       : Element Name
	 * @return      : 
	 * Modified By  :  
	 ***/

	public boolean isAlertPresent(){
		try{
			WebDriverWait wait = new WebDriverWait(driver, 20);
			wait.until(ExpectedConditions.alertIsPresent());
			return true;
		}catch(Exception e){			
			return false;
		}
	}


	/**
	 * Method to wait for element to load in the page
	 * @param WebElement
	 */
	protected Boolean waitForIsClickable(WebElement we) {
		String str = null;
		try {
			str = we.toString();
			new WebDriverWait(driver, GlobalKeys.elementLoadWaitTime).until(ExpectedConditions
					.elementToBeClickable(we));			
			if(isElementPresent(we)){
				return true;
			}else{
				failed(driver,"Element is not visible after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds");
				return false;
			}			
		} catch (Exception ex) {
			failed(driver,"Element is not visible after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds, : "+str);			
			return false;
		}    	
	}

	/**
	 * Method to wait for element to load in the page
	 * @param WebElement
	 */
	protected Boolean waitForElementPresent(WebElement we) {
		String str = null;
		try {
			str = we.toString();
			new WebDriverWait(driver, GlobalKeys.elementLoadWaitTime).until(ExpectedConditions
					.visibilityOf(we));			
			if(isElementPresent(we)){
				return true;
			}else{
				failed(driver,"Element is not visible after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds");
				return false;
			}			
		} catch (Exception ex) {
			failed(driver,"Element is not visible after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds, : "+str);			
			return false;
		}    	
	}


	/**
	 * Method to wait for element to load in the page
	 * @param WebElement
	 */
	protected Boolean waitForElementPresent(By by) {
		String str = null;
		try {
			str = by.toString();
			new WebDriverWait(driver, GlobalKeys.elementLoadWaitTime).until(ExpectedConditions
					.visibilityOfElementLocated(by));			
			if(isElementPresent(by)){
				return true;
			}else{
				failed(driver,"Element is not visible after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds");
				return false;
			}			
		} catch (Exception ex) {
			failed(driver,"Element is not visible after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds, : "+str);			
			return false;
		}    	
	}



	/**
	 * Method to wait for element to load in the page
	 * @param WebElement
	 */
	protected Boolean waitForIsClickable(By by) {
		String str = null;
		try {
			str = by.toString();
			new WebDriverWait(driver, GlobalKeys.elementLoadWaitTime).until(ExpectedConditions
					.elementToBeClickable(by));			
			if(isElementPresent(by)){
				return true;
			}else{
				failed(driver,"Element is not visible after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds");
				return false;
			}			
		} catch (Exception ex) {
			failed(driver,"Element is not visible after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds, : "+str);			
			return false;
		}    	
	}



	/**
	 * Method to wait for element to load in the page
	 * @param by
	 */
	protected Boolean waitAndSwitchToFrame(By by) {
		String str = null;
		try {
			str = by.toString();
			new WebDriverWait(driver, GlobalKeys.elementLoadWaitTime).until(ExpectedConditions
					.frameToBeAvailableAndSwitchToIt(by));
			return true;
		} catch (Exception ex) {
			failed(driver,"Frame is not displayed after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds, : "+str);			
			return false;
		}    	
	}
	/***
	 * Method to wait for the list of elements to be displayed
	 * @param       : List<WebElement>
	 * @return      : 
	 * Modified By  :  
	 ***/
	public boolean waitForElementList(List<WebElement> elems){
		try{
			WebDriverWait wait = new WebDriverWait(driver, GlobalKeys.elementLoadWaitTime);
			wait.until(ExpectedConditions.visibilityOfAllElements(elems));			
			return true;
		}catch(Exception Ex){
			failed(driver,"Element List is not visible after waiting for "+GlobalKeys.elementLoadWaitTime +" Seconds");
			return false;
		}
	}


	public boolean waitForDropDown(WebElement weDropDown){
		try{
			String str= weDropDown.toString();
			if(waitForIsClickable(weDropDown)){		
				for (int second = 0;; second++) {
					if (second >= 20){
						failed(driver,"Values in dropdown are not loaded after waiting for 20 seconds");
						return false;
					}
					try { 
						Select droplist = new Select(weDropDown);
						if(!droplist.getOptions().isEmpty()){
							return true;						
						}
					} catch (Exception e) {
						failed(driver,"Exception Caught while waiting for dropdown loading,Message is->"+e.getMessage());
						return false;
					}
					sleep(1000);
				}
			}else{
				failed(driver,"Dropdown Element is not visible, Expected Property of DropDown is->"+str);
				return false;
			}
		}catch(Exception ex){
			failed(driver,"Exception Caught while waiting for dropdown loading,Message is->"+ex.getMessage());
			return false;
		}
	}

	/***
	 * Method to click on a link(WebElement button)
	 * @param : WebElement
	 * @param : Element Name
	 ***/
	public void clickOn(WebElement we,String elemName) {		
		try{
			//waitForIsClickable(we);
			String strProp = we.toString();
			if (isElementPresent(we)){
				we.click();				
				passed("Clicked on WebElement-"+ elemName);	
			}else{
				failed(driver,"Unable to click on Element "+elemName+", Element with following property is not displayed->"+strProp);
			}
		}catch (Exception ex) {
			logFailException(ex);
		} 
	}


	/**
	 * Method to click on a link(WebElement link)
	 * @param : WebElement
	 * @param : Element Name
	 */
	protected void jsClick(WebElement we,String elemName) {		
		try{			
			((JavascriptExecutor) driver).executeScript("arguments[0].click();",we);
			passed("Clicked on -"+ elemName +"- Element");			
		}catch (RuntimeException ex) {
			failed(driver,"Uanble to click on Element-"+ elemName +", Exception is->"+ex.getMessage());
		} 
	}


	public String jsGetText(WebElement we){		
		return (String) ((JavascriptExecutor) driver).executeScript(
				"return jQuery(arguments[0]).text();", we);
	}	

	/***
	 * Method to enter text in a textbox
	 * @param : WebElement - Textbox
	 * @param : Text to be entered
	 * @return :
	 ***/
	public boolean enterText(WebElement we,String elemName,String text){
		boolean blnFlag;
		blnFlag = false;
		try{
			//waitForElement(we);
			if(isElementPresent(we)){
				//we.clear();
				we.sendKeys(text);	
				passed("Entered value - "+text+" in the text field- "+ elemName);
				blnFlag = true;
			}else{
				failed(driver,elemName+" element is not present");
			}
		}
		catch (Exception ex) {			
			failed(driver,"Unable to enter text in the text field->"+ elemName);
		} 
		return blnFlag;
	}




	/***
	 * Method to clear text in a textbox
	 * 
	 * @param : Element Name
	 * @return :
	 ***/
	public boolean clearText(WebElement we){
		try{
			waitForIsClickable(we);
			if(isElementPresent(we)){
				we.clear();			
				return true;
			}else{
				failed(driver,"Element is not displayed, Unable to Clear text->");
				return false;
			}
		}catch(RuntimeException ex){
			failed(driver,"Unable to clear text in the text field");
			return false;
		}
	}


	/***
	 * Method to select the checkbox
	 * @param       : cbElement
	 * @return      : 
	 * Modified By  : 
	 ***/
	public boolean selectCheckBox(WebElement cbElement){
		waitForIsClickable(cbElement);
		if (isElementPresent(cbElement)){
			try{
				if (!cbElement.isSelected()){
					cbElement.click();
				}
				passed("Selected the Checkbox Successfully");
				return true;
			}catch (Exception e){
				failed(driver,"Unable to Select the checkbox->"+e.getMessage());
				return false;
			}
		}else{
			failed(driver,"Unable to Select the checkbox(Element is not displayed)");
			return false;
		}
	}


	/***
	 * Method to UnSelect the checkbox
	 * @param       : cbElement
	 * @return      : 
	 * Modified By  : 
	 ***/
	public boolean unSelectCheckBox(WebElement cbElement)
	{
		waitForIsClickable(cbElement);
		if (isElementPresent(cbElement)) {
			try{
				if (cbElement.isSelected()){
					cbElement.click();
				}
				passed("Unchecked the checkbox");
				return true;
			}catch (Exception e){
				failed(driver,"Unable to check the checkbox->"+e.getMessage());
				return false;
			}
		}else{
			failed(driver,"Unable to UnSelect the checkbox(Element is not displayed)");
			return false;
		}
	}





	/**
	 * Method to wait for element to load in the page
	 * @param WebElement
	 * @return true or false
	 */

	protected Boolean waitForElement(WebElement we) {
		try {
			new WebDriverWait(driver, GlobalKeys.elementLoadWaitTime).until(ExpectedConditions.elementToBeClickable(we));
			return true;
		} catch (RuntimeException ex) {
			return false;
		}    	
	}
	
	

	protected Boolean waitForElement(By by) {
		try {
			new WebDriverWait(driver, GlobalKeys.elementLoadWaitTime).until(ExpectedConditions.elementToBeClickable(by));
			return true;
		} catch (RuntimeException ex) {
			return false;
		}    	
	}



	/***
	 * Method to check equality of two mobile elements
	 * @param       : MobileElement1,MobileElement2
	 * @return      :  boolean
	 * Modified By  :
	 ***/


	public boolean AreElementsEquals(MobileElement we1, MobileElement we2) {
		try {
			waitForElement(we1);
			waitForElement(we2);

			if(isElementPresent(we1)&&isElementPresent(we2)) {

				if(we1.equals(we2)) {

					passed("Both the Mobile elements are equal");
					return true;
				}
				else {
					failed(driver,"Both the Mobile elements are not equal");
					return false;
				}


			}
			else {
				failed(driver,"unable to identify both the elements");
				return false;
			}


		}
		catch (Exception e) {
			failed(driver,"Exception Caught while waiting for the elements ->"+e.getMessage());
			return false;
		}
	}


	/***
	 * Method to click Mobile Click On AlertButton
	 * @param       : MobileElement, ElementName
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void ClickOnAlertButton(MobileElement we,String elemName) {
		try{
			if (isElementPresent(we)){
				we.click();
				passed("Successfully clicked on Element-"+ elemName);
			}else{
				failed(driver,"Unable to click on Element "+elemName+", Element is not present");
			}
		}catch (Exception ex) {
			failed(driver,"Uanble to click on Element-"+ elemName +", Exception is->"+ex.getMessage());
		}
	}


	/***
	 * Method to Fetch AlertText
	 * @param       :  MobileElement, ElementName
	 * @return      :  String
	 * Modified By  :
	 ***/

	public String  GetAlertText(MobileElement we,String elemName) {
		try{
			if (isElementPresent(we)){

				String alertTxt=we.getText();
				passed("Successfully get the Alert text as-"+alertTxt);
				return alertTxt;
			}else{
				failed(driver,"Unable to  get the Alert text of "+elemName+", Element is not present");
				return null;
			}
		}catch (Exception ex) {
			failed(driver,"Uanble to click on Element-"+ elemName +", Exception is->"+ex.getMessage());
			return null;
		}
	}



	/***
	 * Method to GetContext
	 * @param       :  none
	 * @return      :  String
	 * Modified By  :
	 ***/

	public String GetContext() {
		try{

			String context=driver.getContext();
			return context;

		}catch (Exception ex) {
			failed(driver,"Uanble to get context Exception is->"+ex.getMessage());
			return null;
		}

	}


	/***
	 * Method to Hide The KeyBoard
	 * @param       : MobileElement
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void hidekeyboard()
	{
		if(isIOS()) {
		try{
			driver.findElementByXPath(String.format("//XCUIElementTypeButton[@name='%s']", "Done")).click();
		}catch (Exception ex) {
			failed(driver,"Unable to hideKeyboard using the Done button, Exception is->"+ex.getMessage());		
		}
		}

	}

	
	public boolean isIOS() {
		if(driver.getPlatformName().toLowerCase().contains("android")){
			System.out.println("Android");
			return false;
		}else {
			System.out.println("IOS");
			return true;
		}
	}

	/***
	 * Method to Double Tap By Coordinates
	 * @param       : int x, int y
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void IOS_DoubleTapByCoordinates(int x,int y) {

		try {
			HashMap<String, Object> args = new HashMap<String, Object> ();
			args.put("x", x);
			args.put("y", y);
			driver.execute("mobile: doubleTap", args);

			passed("Successfully double tap on the Mobile element");

		} catch (Exception e) {

			failed(driver,"Unable to double tap on the Mobile element caught exception "+e.getMessage());

		}

	}


	/***
	 * Method to Double Tap By Coordinates ==============
	 * @param       : int FromX,int FromY,int ToX,int ToY, int Duration
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void IOS_DragFromToForDuration(int FromX,int FromY,int ToX,int ToY, int Duration) {

		try {
			HashMap<String, Object> args = new HashMap<String, Object> ();


			args.put("fromX", FromX);
			args.put("fromY", FromY);
			args.put("toX", ToX);
			args.put("toY", ToY);
			args.put("duration", Duration);

			driver.execute("mobile: dragFromToForDuration", args);

			passed("Successfully dragged");

		} catch (Exception e) {

			failed(driver,"Unable to drag caught Exception As-->"+e.getMessage());
		}


	}


	/***
	 * Method to Tap By Coordinates
	 * @param       : int x,int y
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void IOS_TapByCoordinates(int x,int y) {

		try {
			HashMap<String, Object> args = new HashMap<String, Object> ();
			args.put("x", x);
			args.put("y", y);
			driver.execute("mobile: tap", args);
			passed("Successfully  tap on the Mobile element");

		} catch (Exception e) {

			failed(driver,"Unable to tap on the Mobile element Caught Exception As --->"+e.getMessage());
		}
	}

	/***
	 * Method to Touch And Hold For Duration
	 * @param       : MobileElement,int Duration
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_TouchAndHoldForDuration(MobileElement we,int Duration) {

		try {

			if(isElementPresent(we)){
				HashMap<String, Object> args = new HashMap<String, Object> ();
				args.put("element", ((MobileElement) we).getId());
				args.put("duration", Duration);
				driver.execute("mobile:touchAndHold", args);
				passed("Successfully  mobile is touch and hold");
			}else{
				failed(driver,"Unable to locate the element");
			}
		} catch (Exception e) {

			failed(driver,"Unable to touch and hold Caught Exception -->"+e.getMessage());
		}
	}


	/***
	 * Method to Two Finger Tap
	 * @param       : MobileElement1
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_TwoFingerTap(MobileElement we) {

		try {

			if(isElementPresent(we)){
				HashMap<String, Object> args = new HashMap<String, Object> ();

				args.put("element", ((MobileElement) we).getId());

				driver.execute("mobile: twoFingerTap", args);

				passed("Successfully  tap on the Mobile element by twoFinger");
			}else{
				passed("Element is not present");
			}
		} catch (Exception e) {

			failed(driver,"Unable to tap on the Mobile element by two Finger Caught Exception As-->"+e.getMessage());
		}
	}

	/***
	 * Method to Zoom And Pinch
	 * @param       : int Scale ,int Velocity)
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void IOS_ZoomAndPinch (int Scale ,int Velocity) {

		try {
			HashMap<String, Object> args = new HashMap<String, Object> ();    // scale   Values between 0 and 1 refer to a "pinch"
			//  scale  Values greater than 1 refer to a "zoom
			args.put("velocity", Velocity);

			args.put("scale", Scale);

			driver.execute("mobile: pinch", args);

			passed("Successfully zoom out");

		} catch (Exception e) {

			failed(driver,"Unable to zoom out Caught Exception As-->"+e.getMessage());
		}
	}

	/***
	 * Method to MobileIOSActivateApp
	 * @param       : String bundleID
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void ActivateApp(String bundleID) {
		try {
			driver.activateApp(bundleID);
			passed("This app is activted");

		} catch (Exception e) {

			failed(driver,"This App is not activted ");
		}
	}

	/***
	 * Method to MobileCloseApp
	 * @param       : 
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void CloseApp() {

		try {
			driver.quit();
			passed("Succesfully App is closed");

		} catch (Exception e) {

			failed(driver,"Unable to close the App");
		}
	}

	/***
	 * Method to  Double Tap On Element
	 * @param       : MobileElement
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_DoubleTapOnElement(WebElement ele) {
		try {
			if(isElementPresent(ele))
			{
				IOSTouchAction touch = new IOSTouchAction(driver);
				touch.tap(ElementOption.element(ele)).tap(ElementOption.element(ele)).perform();
				passed("Successfully mobile element is double taped");

			}
			else
			{
				failed(driver,"Unable to tap on Mobile element");
			}

		} catch (Exception e) {

			failed(driver,"Unable to tap on Mobile element Caught Exception As-->"+e.getMessage());
		}
	}

	/***
	 * Method to IsAppInstalled
	 * @param       :  String BundleID
	 * @return      :  boolean
	 * Modified By  :
	 ***/

	public boolean  IsAppInstalled(String BundleID) {
		try {

			boolean value=driver.isAppInstalled(BundleID);
			passed("This App is alredy installed");
			return value;

		} catch (Exception e) {

			failed(driver,"This app is not installed");

		}
		return false;
	}

	/***
	 * Method to MobileIOSLongPressOnElement
	 * @param       : MobileElement
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_LongPressOnElement(MobileElement we) {
		try {

			if(isElementPresent(we))
			{

				IOSTouchAction touch = new IOSTouchAction(driver);
				touch.longPress(ElementOption.element(we)).perform();
				passed("Successfully long pressed on element"+we);
			}
			else
			{
				passed("Element is not present"+we);
			}

			passed("Successfully Mobile element is Long pressed");

		} catch (Exception e) {

			failed(driver,"Unable to Long press on Mobile element");
		}
	}

	/***
	 * Method to Pinch On Element
	 * @param       : MobileElement
	 * @return      :  void
	 * Modified By  :
	 ***/



	public void  IOS_PinchOnElement(MobileElement we,String elemName) {
		try {
			Thread.sleep(4000);
			final int x = we.getLocation().getX() + we.getSize().getWidth() / 2;
			final int y = we.getLocation().getY() + we.getSize().getHeight() / 2;
			final IOSTouchAction finger1= new IOSTouchAction(driver);

			finger1.press(ElementOption.element(we, x, y-100)).moveTo(ElementOption.element(we, x, y-10));

			final IOSTouchAction finger2= new IOSTouchAction(driver);

			finger2.press(ElementOption.element(we, x, y+100)).moveTo(ElementOption.element(we, x, y+10));

			final MultiTouchAction action2= new MultiTouchAction(driver);
			action2.add(finger1).add(finger2).perform();

			passed("Successfully pinched on element "+elemName);

		} catch (Exception e) {

			failed(driver,"Unable to Pinch on the Element, Exception Caught As-->"+e.getMessage());
		}
	}


	/***
	 * Method to MobileIOSQueryAppState
	 * @param       :  String bundleID
	 * @return      :  String 
	 * Modified By  :
	 ***/

	public String QueryAppState(String bundleID ) {
		try {
			String AppState = driver.queryAppState(bundleID).toString();
			passed("AppState is"+AppState);
			return AppState;


		} catch (Exception e) {

			failed(driver,"This is app is not installed");
		}
		return null;

	}


	/***
	 * Method to Scroll By Direction
	 * @param       : String Direction
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void IOS_ScrollByDirection(String Direction) {

		try {
			//test

			HashMap<String, String> scrollObject = new HashMap<String, String>();

			scrollObject.put("direction", Direction);

			driver.execute("mobile:scroll", scrollObject);

			passed("The Mobile App is Successfully scroll "+Direction);

		} catch (Exception e) {

			failed(driver,"The App is not Scroll "+Direction);
		}

	}

	/***
	 * Method to MobileIOSScrollDown
	 * @param       : 
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void  IOS_ScrollDown() {                                            //test

		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			HashMap<String, String> scrollObject = new HashMap<String, String>();
			scrollObject.put("direction", "down");
			js.executeScript("mobile: scroll", scrollObject);

			passed("The Successfully scroll down");

		} catch (Exception e) {

			failed(driver,"Unable to scroll down");
		}

	}

	/***
	 * Method to MobileIOSScrollToView
	 * @param       : MobileElement
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_ScrollToView(MobileElement we) {

		try {
			if(isElementPresent(we))
			{
				String elementID = we.getId();

				HashMap<String, String> scrollObject = new HashMap<String, String>();

				scrollObject.put("element", elementID);
				scrollObject.put("toVisible", "not an empty string");

				driver.execute("mobile:scroll", scrollObject);
				passed(" Successfully scrolled to View");
			}
			else {
				failed(driver,"Element is not present");
			}
		} catch (Exception e) {

			failed(driver,"Unable to scroll Caught Exception As-->"+e.getMessage());
		}
	}


	/***
	 * Method to MobileScrollUP
	 * @param       : 
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void IOS_ScrollUP() {

		try {

			JavascriptExecutor js = (JavascriptExecutor)driver;
			HashMap<String, String> scrollObject = new HashMap<String, String>();

			scrollObject.put("direction", "up");
			js.executeScript("mobile: scroll", scrollObject);

			passed("The Mobile App is Successfully scroll Up");

		} catch (Exception e) {

			failed(driver,"Unable to  scroll Up");
		}
	}

	/***
	 * Method to Select Picker Wheel
	 * @param       : MobileElement we,String Direction
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_SelectPickerWheel(MobileElement we,String Direction) {
		try {
			if(isElementPresent(we))
			{


				HashMap<String, Object> PickerWheelparams = new HashMap<String, Object>();
				PickerWheelparams.put("order", Direction);
				PickerWheelparams.put("offset", 0.15);
				PickerWheelparams.put("element", we.getId());
				driver.execute("mobile: selectPickerWheelValue", PickerWheelparams);

				passed("The Value is selected by picker wheel");
			}
			else
			{
				failed(driver,"This is app is not installed");
			}

		} catch (Exception e) {

			failed(driver,"Exception Caught As-->"+e.getMessage());
		}
	}


	/***
	 * Method to MobileIOSSwipeByDirection
	 * @param       : String Direction
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_SwipeByDirection(String Direction) {

		try {

			HashMap<String, String> scrollObject = new HashMap<String, String>();

			scrollObject.put("direction", Direction);  //Left,Right,Up,Down

			driver.execute("mobile:swipe", scrollObject);

			passed(" Successfully swipe in the direction of "+Direction);

		} catch (Exception e) {

			failed(driver,"unable to swipe up in the direction of"+Direction);
		}

	}



	/***
	 * Method to MobileIOSSwipeDown
	 * @param       : 
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_SwipeDown() {

		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			HashMap<String, String> scrollObject = new HashMap<String, String>();
			scrollObject.put("direction", "down");
			js.executeScript("mobile: swipe", scrollObject);

			passed("successfully swipe down");

		} catch (Exception e) {

			failed(driver,"Unable to swipe down Caught exception as"+e.getMessage());
		}

	}


	/***
	 * Method to MobileIOSSwipeUP
	 * @param       : 
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void IOS_SwipeUP() {

		try {
			JavascriptExecutor js = (JavascriptExecutor)driver;
			HashMap<String, String> scrollObject = new HashMap<String, String>();
			scrollObject.put("direction", "up");
			js.executeScript("mobile: swipe", scrollObject);

			passed("successfully swipe Up");

		} catch (Exception e) {

			failed(driver,"Unable to swipe Up Caught exception as"+e.getMessage());
		}

	}


	/***
	 * Method to SwipeUP
	 * @param       : 
	 * @return      :  void
	 * Modified By  :
	 ***/




	public void TapByCoordinates(int x,int y) {
		try {
			IOSTouchAction action = new IOSTouchAction(driver);
			action.tap(point(x, y)).waitAction(waitOptions(Duration.ofMillis(250))).perform();
			Thread.sleep(2000);
			passed("Successfully Taped By Coordinates--X"+x+"Y"+y);

		} catch (Exception e) {

			failed(driver,"Failed to Tap By coordinates-->X"+x+"Y"+y);
		}

	}

	/***
	 * Method to TapOnElement
	 * @param       : MobileElement we,String elementName
	 * @return      :  void
	 * Modified By  :
	 ***/
	public void IOS_TapOnElement(MobileElement we,String elementName) {
		try {
			if (isElementPresent(we))
			{

				IOSTouchAction touch = new IOSTouchAction(driver);
				touch.tap(ElementOption.element(we)).perform();

				passed("successfully tap on element" +elementName);
			}
			else
			{
				failed(driver,"Unable to tap on element" +elementName);
			}
		} catch (Exception e) {

			failed(driver,"Unable to tap on element Caught Exception As-->" +e.getMessage());
		}
	}

	/***
	 * Method to Terminate App
	 * @param       : String BundleId
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void TerminateApp (String BundleId) {
		try {
			boolean value=(driver).terminateApp(BundleId);

			passed("This App is alredy installed");

		} catch (Exception e) {

			failed(driver,"This is app is not installed");
		}
	}

	/***
	 * Method to  Touch Multiple Times On Element
	 * @param       : MobileElement we, String ElementName,int noOfTimes           
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_TouchAndHoldOnElement(MobileElement we,int duration ) {   //test
		try {

			if(isElementPresent(we))
			{

				IOSTouchAction touch = new IOSTouchAction(driver);
				touch.press(ElementOption.element(we))
				.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(duration))).release().perform();

				TouchAction action = new TouchAction(driver);



				passed("Successfully  TouchAndHoldOnElement "+we);
			}
		} catch (Exception e) {

			failed(driver,"unable to TouchAndHoldOnElement");
		}
	}


	/***
	 * Method to  Touch Multiple Times On Element
	 * @param       : MobileElement we, String ElementName,int noOfTimes           test 32
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_TouchMultipleTimesOnElement(MobileElement we, String ElementName,int noOfTimes) {
		try {
			if(isElementPresent(we))
			{																												//test

				IOSTouchAction touch = new IOSTouchAction(driver);

				for (int i = 0; i < noOfTimes; i++) {
					touch.tap(ElementOption.element(we)).perform();
				}

				passed("Succefully Touch on  MultipleTimesOnElement  "+ElementName);
			}
			else {
				failed(driver,"Unable to Touch on MultipleTimesOnElement "+ElementName );
			}

		} catch (Exception e) {

			failed(driver,"Unable to Touch on MultipleTimesOnElement Caught Exception As--> "+e.getMessage() );
		}
	}


	/***
	 * Method to Zoom On Element
	 * @param       : MobileElement we, String ElementName
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void IOS_ZoomOnElement(MobileElement we, String ElementName) {

		try {

			if(isElementPresent(we))
			{
				Thread.sleep(4000);

				final int x = we.getLocation().getX() + we.getSize().getWidth() / 2;
				final int y = we.getLocation().getY() + we.getSize().getHeight() / 2;


				final IOSTouchAction finger1 = new IOSTouchAction(driver);
				finger1.press(ElementOption.element(we, x, y - 10)).moveTo(ElementOption.element(we, x, y - 100));

				final IOSTouchAction finger2 = new IOSTouchAction(driver);

				finger2.press(ElementOption.element(we, x, y + 10)).moveTo(ElementOption.element(we, x, y + 100));

				final MultiTouchAction action = new MultiTouchAction(driver);

				action.add(finger1).add(finger2).perform();

				passed("Successfully zoom on Element"+ElementName);
			}
			else
			{
				failed(driver,"Unable to Zoom On Elmennt"+ElementName);
			}
		} catch (Exception e) {

			failed(driver,"Unable to Zoom On Elmennt Caught Exception As-->"+e.getMessage());
		}
	}

	/***
	 * Method to check Device Locked or not
	 * @param       : 
	 * @return      :  boolean
	 * Modified By  :
	 ***/

	public boolean IsDeviceLocked () {
		try {

			boolean value = ((LocksDevice) driver).isDeviceLocked();
			passed("Yes This device is Locked");
			return value;


		} catch (Exception e) {

			failed(driver,"No This is not Locked");
		}
		return (Boolean) null;
	}

	/***
	 * Method to Check Element is Displayed or  Not
	 * @param       : MobileElement we,String ElementName
	 * @return      :  boolean
	 * Modified By  :
	 ***/

	public boolean IsElementDisplayed(MobileElement we,String ElementName) {
		try {
			if(isElementPresent(we))
			{	
				boolean element = we.isDisplayed();
				passed("Yes This Element is Displayed");
				return element;

			}

			else {
				failed(driver,"Unable to find the element");
			}

		} catch (Exception e) {

			failed(driver,"This Element is Not displayed Caught Exception As-->"+e.getMessage());
		}
		return (Boolean) null;
	}

	/***
	 * Method to Check Element Enabled or Not
	 * @param       : MobileElement we,String ElementName
	 * @return      :  boolean
	 * Modified By  :
	 ***/

	public boolean IsElementEnabled(MobileElement we,String ElementName) {
		try {

			if(isElementPresent(we))
			{	
				boolean element = we.isEnabled();
				passed(" Element  Enabled  As-->"+element);
				return element;

			}
			else {
				failed(driver,"Unable to find the element");

			}
		} catch (Exception e) {

			failed(driver,"Exception Caught as"+e.getMessage());

		}
		return (Boolean) null;

	}


	/***
	 * Method to Check Element Selected or Not
	 * @param       : MobileElement we,String ElementName
	 * @return      :  boolean
	 * Modified By  :
	 ***/

	public boolean IsElementSelected(MobileElement we,String ElementName) {
		try {

			if(isElementPresent(we))
			{	
				boolean element = we.isSelected();
				passed("Element selected As-->" +element);
				return element;

			}
		} catch (Exception e) {

			failed(driver,"Exception Caught as"+e.getMessage());
		}
		return (Boolean) null;
	}

	/***
	 * Method to Check Is Keyboard Shown
	 * @param       : 
	 * @return      :  boolean
	 * Modified By  :
	 ***/

	public boolean IsKeyboardShown() {
		try {

			boolean value = ((HasOnScreenKeyboard) driver).isKeyboardShown();
			passed("Successfully keyBoard is down ");
			return value;


		} catch (Exception e) {

			failed(driver,"Unable to down the keyBoard");
		}
		return (Boolean) null;
	}

	/***
	 * Method to Lock The Device
	 * @param       : 
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void  LockDevice() {

		try {

			((LocksDevice) driver).lockDevice();
			passed("Successfully This device is Locked");

		} catch (Exception e) {

			failed(driver,"Unable to Lock the device ");
		}

	}




	/***
	 * Method to device Rotate
	 * @param       : String Orientation
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void MobileRotate(String Orientation) {


		try {

			switch (Orientation) {

			case "LANDSCAPE":
				driver.rotate(ScreenOrientation.LANDSCAPE);

				break;
			case "PORTRAIT":

				driver.rotate(ScreenOrientation.PORTRAIT);

				break;

			default:
				info("Invalid Input Entered");

			}
			passed("Succesfully Mobile is Rotated As -->"+Orientation);

		} catch (Exception e) {

			failed(driver,"Exception caught while  Rotate The Mobile as"+e.getMessage());
		}

	}

	/***
	 * Method to  Scroll By Axis
	 * @param       : int FromX,int FromY,int ToX,int ToY
	 * @return      :  void
	 * Modified By  :
	 ***/


	public void ScrollByAxis(int FromX,int FromY,int ToX,int ToY) {

		try {

			TouchAction touch = new TouchAction(driver);

			touch.longPress(PointOption.point(FromX,FromY)).moveTo(PointOption.point(ToX,ToY)).release().perform();

			passed("Successfully Scolled By Uisng Axis");

		} catch (Exception e) {

			failed(driver,"Unable to scrolling");
		}

	}

	/***
	 * Method to  Set  Context
	 * @param       : String ContextName
	 * @return      :  void
	 * Modified By  :
	 ***/

	public void SetContext(String ContextName) {
		try {

			driver.context(ContextName);

			passed("Successfully set the context As "+ContextName);

		} catch (Exception e) {

			failed(driver,"Unable to set the Context");
		}
	}


	/***
	 * Method to  unlock Mobile
	 * @param       : none
	 * @return      :  void
	 * Modified By  :
	 * 
	 ***/

	public void unlockDevice()
	{
		try {

			((LocksDevice) driver).unlockDevice();
			passed("Successfully Device is UnLocked");

		} catch (Exception e) {

			failed(driver,"unable to Unlock The device");
		}

	}

//update
	

	public void scrollByDimension(double XStart, double YStart, double XEnd, double YEnd) {
		Dimension dimension = driver.manage().window().getSize();

		Double scrollWidthStart = dimension.getWidth() * XStart;
		int scrollXStart = scrollWidthStart.intValue();

		Double scrollHeightStart = dimension.getHeight() * YStart;
		int scrollYStart = scrollHeightStart.intValue();

		Double scrollWidthEnd = dimension.getWidth() * XEnd;
		int scrollXEnd = scrollWidthEnd.intValue();

		Double scrollHeightEnd = dimension.getHeight() * YEnd;
		int scrollYEnd = scrollHeightEnd.intValue();

		new TouchAction((PerformsTouchActions) driver).press(PointOption.point(scrollXStart, scrollYStart))
				.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2)))
				.moveTo(PointOption.point(scrollXEnd, scrollYEnd)).release().perform();

	}

	public void SwipeByCoordinates(int scrollXStart, int scrollYStart, int scrollXEnd, int scrollYEnd) {

		try {
			new TouchAction((PerformsTouchActions) driver).press(PointOption.point(scrollXStart, scrollYStart))
					.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2)))
					.moveTo(PointOption.point(scrollXEnd, scrollYEnd)).release().perform();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public String getCurrentDate() {

		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime now = LocalDateTime.now();
			String currentDate = dtf.format(now);
			return currentDate;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void scrollDown() {
		Dimension dimension = driver.manage().window().getSize();

		Double scrollHeightStart = dimension.getHeight() * 0.7;
		int scrollStart = scrollHeightStart.intValue();

		Double scrollHeightEnd = dimension.getHeight() * 0.1;
		int scrollEnd = scrollHeightEnd.intValue();

		new TouchAction((PerformsTouchActions) driver).press(PointOption.point(0, scrollStart))
				.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2))).moveTo(PointOption.point(0, scrollEnd))
				.release().perform();
	}

	public void scrollUp() {
		Dimension dimension = driver.manage().window().getSize();

		Double scrollHeightStart = dimension.getHeight() * 0.2;
		int scrollStart = scrollHeightStart.intValue();

		Double scrollHeightEnd = dimension.getHeight() * 0.7;
		int scrollEnd = scrollHeightEnd.intValue();

		new TouchAction((PerformsTouchActions) driver).press(PointOption.point(0, scrollStart))
				.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2))).moveTo(PointOption.point(0, scrollEnd))
				.release().perform();
	}

	
	
	
}
