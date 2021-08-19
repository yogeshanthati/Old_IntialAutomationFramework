package utilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.ios.IOSDriver;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.Select;


public class GlobalKeys
  extends Common
{
  public static MobileDriver<WebElement> driver;    
  public static int textLoadWaitTime; 
  public static int elementLoadWaitTime; 
  public static int implicitlyWaitTime;
  public static int pageLoadWaitTime = 0;
  public static final ArrayList<String> testCaseNames = new ArrayList();
  public static ArrayList<String> testCaseDataSets = new ArrayList();
  public static boolean testCaseExecutionStatus = false;  
  public static String outputDirectory;
  public static HashMap<String , String> configData = new HashMap<String , String>();
  public static List<HashMap<String , String>> executionData = new ArrayList<HashMap<String , String>>();
  public static List<HashMap<String , String>> devicesData = new ArrayList<HashMap<String , String>>();
  public static int failureNo;
  public static String timeStamp;
}
