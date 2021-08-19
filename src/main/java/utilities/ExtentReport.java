package utilities;

import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReport  {
	
	static ExtentReports extent;
	static ExtentTest parent;
	static ExtentTest child;	
	private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();
	
	
	
	public void start(){
		try {
			extent = new ExtentReports(); 
			ExtentSparkReporter spark = new ExtentSparkReporter(GlobalKeys.outputDirectory+"/Results.html");
			ExtentSparkReporter sparkFail = new ExtentSparkReporter(GlobalKeys.outputDirectory+"/FailedResults.html")
					  .filter()
					    .statusFilter()
					    .as(new Status[] { Status.FAIL })
					  .apply();
			spark.config().setReportName("Treat Mobile Automation");
			spark.config().setDocumentTitle("Treat Automation Report");				
			extent.attachReporter(spark,sparkFail);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  public static synchronized ExtentTest getTest() 
	  { 
	    return extentTest.get(); 
	  }
	  
	  public void datasetStart(String testDataSet) {
			child = getTest().createNode(testDataSet, testDataSet);
			extentTest.set(child);
			log("info","Test Data source is "+ GlobalKeys.configData.get("TestDataSource"));
		}
	  
	  public void datasetEnd() {	
		 
			extentTest.set(parent);			
		}
	
	public void testScenarioStart(ITestResult result){
		parent = extent.createTest(result.getMethod().getMethodName());
		extentTest.set(parent);
		parent.assignDevice(result.getTestContext().getName());
		System.out.println("Report Started For "+result.getMethod().getMethodName());
	}
	
	public void log(String status, String msg){
		System.out.println(GlobalKeys.outputDirectory + " : "+msg);
		switch(status){
		case "pass" : 
			getTest().pass(msg);
			break;
		case "info" : 
			getTest().info(msg);
			break;
		case "fail" : 
			getTest().fail(msg);
			break;
		case "warn" : 
			getTest().warning(msg);
			break;					
		}
	}
	
	
	public void logJson(String json){
		
		getTest().info(MarkupHelper.createCodeBlock(json, CodeLanguage.JSON));
	}
	
	public void logPerf(String json){
		
		getTest().warning(MarkupHelper.createCodeBlock(json, CodeLanguage.JSON));
	}
	
	public void logXml(String xml){
		getTest().info(MarkupHelper.createCodeBlock(xml));
	}
	
	public void logFailScreenshot(String errMsg,String img){
		getTest()
        .fail(errMsg, MediaEntityBuilder.createScreenCaptureFromBase64String(img).build());
	}
	
	public void logFailException(Throwable throwable){
		getTest()
        .fail(throwable);
	}
	
	public void logScreenshot(String img){
		getTest()
        .info(MediaEntityBuilder.createScreenCaptureFromBase64String(img).build());
	}
	
	public static synchronized  void testScenarioEnd(String name){				
			extent.flush();				
	}
	
	
}
