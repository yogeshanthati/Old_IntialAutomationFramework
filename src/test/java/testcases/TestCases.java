package testcases;


import java.lang.reflect.Method;
import java.util.ArrayList;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import base.TestBase;
import pages.TreatHomePage;
import utilities.Common;
import utilities.Data;

@Listeners({ utilities.TestListener.class })
public class TestCases extends Common {

	public static int count = 1;
	private TestBase TestBase;
	private pages.TreatHomePage TreatHomePage;
	public Data data;
	public ArrayList<String> datasets;
	
	
	
	@BeforeMethod
	public void testStart(Method method) {		
		String name = method.getName();
		data = new Data("TestData"); 
		datasets = data.getDataSets(name);		
	}
	
	@Test 
	@Parameters({"selenium.deviceName","selenium.PlatformName","selenium.PlatformVersion"})
	public void MOB_TC001_LogIntoTreatApp(String deviceName,String platformName,String platFormVersion) {
		String strName = new Exception().getStackTrace()[0].getMethodName();
		data.setColIndex(strName);
		for (String dataset : datasets) {      			
			data.setIndex(dataset);
			datasetStart(dataset);		
			TreatHomePage = TestBase.setUp(data);
			TreatHomePage.SignInToApp();
			TestBase.tearDown();
			datasetEnd();
		}
	}
	
	@Test 
	@Parameters({"selenium.deviceName","selenium.PlatformName","selenium.PlatformVersion"})
	public void MOB_TC002_LogIntoTreatApp(String deviceName,String platformName,String platFormVersion) {
		String strName = new Exception().getStackTrace()[0].getMethodName();
		data.setColIndex(strName);
		for (String dataset : datasets) {      			
			data.setIndex(dataset);
			datasetStart(dataset);		
			TreatHomePage = TestBase.setUp(data);
			TreatHomePage.SignInToApp();
			TestBase.tearDown();
			datasetEnd();
		}
	}
	
	
}
