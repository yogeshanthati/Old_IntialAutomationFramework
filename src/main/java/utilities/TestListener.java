package utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestListener implements ITestListener 
{
	public ExtentReport extent ; 

@Override
public void onTestStart(ITestResult result) {	  	
	extent.testScenarioStart( result);
}

@Override
public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	
}

@Override
public void onStart(ITestContext context) {
	extent = new ExtentReport();
	extent.start();
}

@Override
public void onFinish(ITestContext context) {
	extent.testScenarioEnd(context.getName());
}

@Override
public void onTestSuccess(ITestResult result) {
	
}

@Override
public void onTestFailure(ITestResult result) {
	
	extent.logFailException(result.getThrowable());
}

@Override
public void onTestSkipped(ITestResult result) {
	System.out.println(result.getSkipCausedBy());
}


}
