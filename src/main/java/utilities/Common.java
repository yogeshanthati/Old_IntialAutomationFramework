package utilities;


import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pages.TreatHomePage;

import org.jsoup.nodes.Document;

public class Common extends ExtentReport{

	protected AppiumDriver<WebElement> driver;	
	
	public void screenShot(String filename){
		String scrPath = GlobalKeys.outputDirectory + "/Screenshots";
		File file = new File(scrPath);
		file.mkdir();
		try {
			Robot robot = new Robot();
			Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage bufferedImage = robot.createScreenCapture(captureSize);
			File outputfile = new File(scrPath + "/" + filename + ".png");
			ImageIO.write(bufferedImage, "png", outputfile);			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	


	
	public void captureScreenShot(WebDriver driver,String filename){
		File scrFile = null;
		String scrPath = GlobalKeys.outputDirectory + "/Screenshots";
		File file = new File(scrPath);
		file.mkdir();     
		try{
			scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile,new File(scrPath + "/" + filename + ".png"));			
		}catch (Exception e){
			e.printStackTrace();

		}finally{
			if (scrFile == null) {
				System.out.println("This WebDriver does not support screenshots");
				return;
			}
		}
	}
	
	
	public String captureScreenShotAsBase64(WebDriver driver){
		String img = null;		    
		try{
			img = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BASE64);	
			return img;
		}catch (Exception e){
			e.printStackTrace();
		}
		if (img == null) {
			System.out.println("This WebDriver does not support screenshots");				
		}
		return null;
		
	}

	public void failed(WebDriver driver, String errMessage){	
		String img = captureScreenShotAsBase64(driver);
		logFailScreenshot(errMessage,img);
	}
	
	public void fail(String errMessage){
		log("fail", errMessage);
	}
	
	public static void failAssert(String errMessage){
		
		Assert.assertFalse(true, errMessage);
	}

	public  void passed(String logMessage){
		log("pass", logMessage);		
	}

	public  void info(String logMessage) {
		log("info", logMessage);
	}
	
	


	/*
	 * public void dataSetEnd(String testDataSet) { child.
	 * .appendChild(GlobalKeys.child); }
	 */

	public  void takeScreenshot(WebDriver driver){		
		String img =  captureScreenShotAsBase64(driver);
		logScreenshot(img);
	}
	
	
	
	public static void uploadToQmetry() {
		try {
			if(Util.getConfig("IntegrateQMetry").equals("Yes")) {
				ArrayList<String> testcasesStatus = getTestCasesStatus(GlobalKeys.outputDirectory + "/Results.html");
				createJSONFile(testcasesStatus,GlobalKeys.outputDirectory + "/Results.html");
				sendPostRequest();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String sendPostRequest(){
		Response response = null;
		System.setProperty("javax.net.ssl.trustStore", "./src/test/resources/Config/cacerts");
		String url = Util.getConfig("QMetryURL");
		String APIKey = Util.getConfig("APIKey");
		String PlatFormID = Util.getConfig("PlatFormID");
		String CycleID = Util.getConfig("CycleID");
		String ProjectID = Util.getConfig("ProjectID");
		String ReleaseID = Util.getConfig("ReleaseID");		
		for(int i=0;i<GlobalKeys.executionData.size();i++) {
			if(GlobalKeys.executionData.get(i).get("Execution Flag").trim().equalsIgnoreCase("Yes")) {
				try {
					String testCaseName = GlobalKeys.executionData.get(i).get("TestCase ID");
					String TestSuiteName = GlobalKeys.executionData.get(i).get("TestSuiteName");
					if((TestSuiteName==null) || TestSuiteName.trim().equals("") ) {
						TestSuiteName = Util.getConfig("TestSuiteName");
					}
					
					String TestSuiteId = GlobalKeys.executionData.get(i).get("TestSuiteId");
					if((TestSuiteId==null) || TestSuiteId.trim().equals("") ) {
						TestSuiteId = Util.getConfig("TestSuiteId");
					}
						RestAssured.baseURI = url;
						response = RestAssured.given().urlEncodingEnabled(true)
								.contentType("multipart/form-data")
								.header("apikey",APIKey)
								.header("project",ProjectID)
								.header("Accept", ContentType.JSON.getAcceptHeader())
								.multiPart("testsuiteName", TestSuiteName)
								.multiPart ("testsuiteId",TestSuiteId)
								.multiPart ("projectID", ProjectID)
								.multiPart ("releaseID", ReleaseID)
								.multiPart ("cycleID", CycleID)
								.multiPart ("platformID", PlatFormID)
								.multiPart("entityType","JSON")
								.multiPart ("file", new File(GlobalKeys.outputDirectory + "/"+testCaseName+".json"))
								.post("/rest/import/createandscheduletestresults/1");
					System.out.println("Uploaded the Result successfully for the test case : "+testCaseName);
				} catch (Exception e) {
					System.err.println("Exception Caught in sendPostRequest --> "+e.getMessage());
				}
			}
		}		
		return response.getBody().asString();
	}
	
	public static ArrayList<String> getTestCasesStatus(String ReportFile){

		ArrayList<String> testcasesStatus = new ArrayList<String>();

		try {
			Document htmlFile = Jsoup.parse(new File(ReportFile),"ISO-8859-1");
			
			//retrieve count of total test cases and it's result status			
			int noOfScenarios = htmlFile.body().getElementsByAttributeValueContaining("class", "test-detail").size();
			
			for(int p=0;p<noOfScenarios;p++){
				
				if(htmlFile.body().getElementsByAttributeValueContaining("class", "test-detail").get(p).text().startsWith("MOB_TC")){
					testcasesStatus.add(htmlFile.body().getElementsByAttributeValueContaining("class", "test-detail").get(p).text());
					}
				}

		} catch (Exception e) {
			System.err.println("Exception Caught in getTestCasesStatus --> "+e.getMessage());
		}
		return testcasesStatus;
	}
	
	
	//takes argument as list of test cases status and report file path
		@SuppressWarnings("unchecked")
		public static void createJSONFile(ArrayList<String> testcasesStatus, String ReportFile){
			try  {
				
				LinkedHashMap<String,ArrayList<String>> allTestcases = parseScenarioAndSteps(ReportFile,getNoOfTestcase(ReportFile,testcasesStatus));
				ArrayList<String> ScenarioName = new ArrayList<String>();

				for (HashMap.Entry<String, ArrayList<String>> entry : allTestcases.entrySet()) {
					ScenarioName.add(entry.getKey());
				}

				for(int iterator = 0; iterator<testcasesStatus.size(); iterator++){
					StringBuilder jsonInitialBody = new StringBuilder("[");
					JSONObject testcasesList = new JSONObject(); 
					getTestCaseDetails(jsonInitialBody,ScenarioName.get(iterator),testcasesStatus.get(iterator),ReportFile,allTestcases);
					
					jsonInitialBody.append("}"); jsonInitialBody.append("]");
					testcasesList.put("testcases", jsonInitialBody);
					String jsonString = testcasesList.toString();
					
					FileWriter file = new FileWriter(GlobalKeys.outputDirectory+ "/"+ScenarioName.get(iterator).trim()+".json");
					file.write(jsonString.toString());
					file.flush();
					file.close();
				}
				
			} catch (Exception e) {
				System.err.println("Exception Caught in createJSONFile --> "+e.getMessage());
			}
			
		}
		
		public static void getTestCaseDetails(StringBuilder jsonBody, String tcScenarioName, String tcStatus, String tcResultLink,HashMap<String,ArrayList<String>> StepDescription){
			try {

				jsonBody.append("{");
				jsonBody.append("\"testEntityKey\":" + "\"\",");
				jsonBody.append("\"requirementKey\":" + "\"\",");
				jsonBody.append("\"name\":" +"\""+tcScenarioName+"\",");
				jsonBody.append("\"error\":" + "\"\",");
				jsonBody.append("\"errorTrace\":" + "\"\",");
				jsonBody.append("\"attachments\":" + "[],");
				jsonBody.append("\"steps\": [");

				int count = 1;

				for(int iterator = 0; iterator<StepDescription.get(tcScenarioName).size(); iterator+=2){ 
					
					jsonBody.append("{");
					jsonBody.append("\"name\":" + "\""+""+(String)StepDescription.get(tcScenarioName).get(iterator)+""+"\","); 
					if(iterator >= 1){
						
						jsonBody.append("\"result\":" + "\""+""+(String)StepDescription.get(tcScenarioName).get(iterator+1)+""+"\"");
							
					}	
					else if(iterator < 1){
							
						jsonBody.append("\"result\":" + "\""+""+(String)StepDescription.get(tcScenarioName).get(iterator+1)+""+"\",");
						jsonBody.append("\"actualOutcome\":" + "\"PLEASE REFER TEST STEP COMMENTS FOR DETAILED REPORT \",");
						jsonBody.append("\"comment\":" + "\""+tcResultLink+"\"");
					}
					if(iterator==StepDescription.get(tcScenarioName).size()-2){ //and equals length
						jsonBody.append("}");	
						count++;
					}else
					{
						jsonBody.append("},");
					}	
				} 

				jsonBody.append("]");

			} catch (Exception e) {
				System.err.println("Exception Caught in getTestCaseDetails --> "+e.getMessage());
			}
		}


		public static LinkedHashMap<String,Integer> getNoOfTestcase(String reportFile,ArrayList<String> testcasesStatus) throws IOException{
		Document htmlFile = Jsoup.parse(new File(reportFile),"ISO-8859-1");
		
		int noOfScenarios = testcasesStatus.size();
		int noOfTestCases = htmlFile.body().getElementsByAttributeValueContaining("class", "node").size();
		
		LinkedHashMap<String,Integer> testcasesOfScenarios = new LinkedHashMap<String,Integer>();
		int initialNode = 0;
		
		for(int s=0;s<noOfScenarios;s++){
			
			String scenarioName = testcasesStatus.get(s).split(" ")[0];
			int testcases = 0;
			
			innerTestcase : for(int tc=initialNode;tc<noOfTestCases;tc+=4){
				String testcaseName = htmlFile.body().getElementsByAttributeValueContaining("class", "node").get(tc).text();
				
				if(testcaseName.contains(scenarioName)){
					testcases++;
				}
				if(tc == noOfTestCases - 4){
					testcasesOfScenarios.put(scenarioName, testcases);
					//initialNode = tc;
					break innerTestcase;
				}
				
			}
		}	
		
		return testcasesOfScenarios; 
		
	}
		
		
		public static LinkedHashMap<String,ArrayList<String>> parseScenarioAndSteps(String reportFile, LinkedHashMap<String,Integer> noOfTestcases) throws IOException{

		Document htmlFile = Jsoup.parse(new File(reportFile),"ISO-8859-1");
		
		int noOfSteps = htmlFile.body().getElementsByClass("event-row").size();
		
		LinkedHashMap<String,ArrayList<String>> teststepsOfScenarios = new LinkedHashMap<String,ArrayList<String>>();
		
		int firstStep,StepNumber;
		String exception;
		StepNumber = 0;
		
		try{
			
			
			for (HashMap.Entry<String, Integer> entry : noOfTestcases.entrySet()) {
		   
				firstStep = 0;
				
				ArrayList<String> stepDescription = new ArrayList<String>(); 
				
				inner: for(int itrSteps=StepNumber;itrSteps<noOfSteps;itrSteps++){
					
					if(htmlFile.body().getElementsByClass("event-row").get(StepNumber).text().contains("Test Data source is")){
						firstStep++;
					}
					
					if(firstStep>entry.getValue()){
						teststepsOfScenarios.put(entry.getKey(),stepDescription);
						break inner;
					}
					else if(StepNumber==noOfSteps-1){
						teststepsOfScenarios.put(entry.getKey(),stepDescription);
					}
					
					try{
						exception = htmlFile.body().getElementsByClass("event-row").get(StepNumber).text().split("M ")[1];
						if (!(htmlFile.body().getElementsByClass("event-row").get(StepNumber).text().split("M ")[1].toString().trim().startsWith("base64 img"))){		
							 if (htmlFile.body().getElementsByClass("event-row").get(StepNumber).text().split("M ")[1].toLowerCase().contains("exception")){
								 stepDescription.add("Exception caught");
							 }
							 else{
								 stepDescription.add(htmlFile.body().getElementsByClass("event-row").get(StepNumber).text().split("M ")[1]);
							 }
							 
							 if(htmlFile.body().getElementsByClass("event-row").get(StepNumber).text().split(" ")[0].toString().toLowerCase().contains("fail")){
								 stepDescription.add("fail");
							 }else{
								 stepDescription.add("pass");
							 }
						}
					}
					catch(ArrayIndexOutOfBoundsException ae){
						
					}			
					 
					StepNumber++;

				}

			}
			
		}
		
		catch (Exception e) {
			System.err.println("Exception Caught in parseScenarioAndSteps --> "+e.getMessage());
		}

		return teststepsOfScenarios;
	}

}
