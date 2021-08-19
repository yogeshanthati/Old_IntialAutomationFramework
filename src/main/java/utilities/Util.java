package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Util {

	public static String am_pm;
	public static String min;
	public static String hr;
	public static String sec;
	public static int yr;
	public static String mon;
	public static String day;

	public static void startup(){
		readAllSheets();
		createOutputDirectory();		
		createTestNgXml();
	}


	public static void createTestNgXml() {		
		int noBrowser = 0;
		ArrayList<String> DeviceName = new ArrayList<String>();
		ArrayList<String> PlatformName = new ArrayList<String>();
		ArrayList<String> PlatformVersion = new ArrayList<String>();

		ArrayList<String> devices = getUsedDeviceInExecution();
		for(HashMap<String, String> map : GlobalKeys.devicesData){
			if(devices.contains(map.get("DeviceName"))){
				DeviceName.add(map.get("DeviceName"));
				PlatformName.add(map.get("Platform Name"));
				PlatformVersion.add(map.get("Platform Version"));
			}
		}

		try{			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element rootElement = doc.createElement("suite");
			doc.appendChild(rootElement);
			rootElement.setAttribute("name", "Suite");
			rootElement.setAttribute("parallel", "tests");

			Element listners = doc.createElement("listners");
			Element listner = doc.createElement("listner");
			listner.setAttribute("class-name", "Utilities.TestListener");
			listners.appendChild(listner);
			rootElement.appendChild(listners);
			int id = 1;
			for (int device = 1; device <= DeviceName.size(); device++){
				String idS = String.valueOf(id++);
				Element test = doc.createElement("test");
				test.setAttribute("name", DeviceName.get(device-1));
				//test.setAttribute("id", idS);

				Element classes = doc.createElement("classes");
				//classes.setAttribute("name", idS);

				Element classs = doc.createElement("class");
				classs.setAttribute("name", "testcases.TestCases");
				//classs.setAttribute("id", idS);

				Element methods = doc.createElement("methods");
				//methods.setAttribute("id", idS);

				Element parameter0 = doc.createElement("parameter");
				parameter0.setAttribute("name", "selenium.deviceName");
				parameter0.setAttribute("value", DeviceName.get(device - 1));

				Element parameter1 = doc.createElement("parameter");
				parameter1.setAttribute("name", "selenium.PlatformName");
				parameter1.setAttribute("value", PlatformName.get(device - 1));


				Element parameter2 = doc.createElement("parameter");
				parameter2.setAttribute("name", "selenium.PlatformVersion");
				parameter2.setAttribute("value", PlatformVersion.get(device - 1));


				rootElement.appendChild(test);
				test.appendChild(parameter0);
				test.appendChild(parameter1);
				test.appendChild(parameter2);								
				classes.appendChild(classs);
				classs.appendChild(methods);
				test.appendChild(classes);
				for (int i = 0; i < GlobalKeys.executionData.size(); i++){
					String value = GlobalKeys.executionData.get(i).get("Execution Flag");
					String execDevice = GlobalKeys.executionData.get(i).get("Device");
					String Keyword = GlobalKeys.executionData.get(i).get("TestCase ID");
					if(value!=null){
						if (value.trim().equalsIgnoreCase("Yes")&&execDevice.trim().equalsIgnoreCase(DeviceName.get(device-1))){
							Element include = doc.createElement("include");
							methods.appendChild(include);						
							include.setAttribute("name", Keyword);
						}else {
							Element exclude = doc.createElement("exclude");
							methods.appendChild(exclude);
							exclude.setAttribute("name", Keyword);
						}
					}
				}
			}


			TransformerFactory tff = TransformerFactory.newInstance();
			Transformer transformer = tff.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource xmlSource = new DOMSource(doc);
			StreamResult outputTarget = new StreamResult(
					"./src/test/resources/Config/testng.xml");
			transformer.transform(xmlSource, outputTarget);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> getUsedDeviceInExecution(){
		ArrayList<String> devices = new ArrayList<String>();
		for(int i=0;i<GlobalKeys.executionData.size();i++){
			String flag = GlobalKeys.executionData.get(i).get("Execution Flag");
			if(flag.trim().equalsIgnoreCase("yes")) {
				String device = GlobalKeys.executionData.get(i).get("Device");
				if(!devices.contains(device)){
					devices.add(device);
				}
			}
		}
		return devices;
	}

	public static void readAllSheets(){
		try {
			List<List<Object>> configData = GetExcelTableInto2DArrayListString("./src/test/resources/TestData/TestConfiguration.xlsx","config");
			GlobalKeys.configData = listToHashMap(configData);
			String execSettingsPath = getConfig("Tc_Settings_Excelpath");
			List<List<Object>> devicesData = GetExcelTableInto2DArrayListString(execSettingsPath,"Devices");
			GlobalKeys.devicesData = listToListOfHashMap(devicesData);
			List<List<Object>> executionData = GetExcelTableInto2DArrayListString(execSettingsPath,"TestCases");
			GlobalKeys.executionData = listToListOfHashMap(executionData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public static String getConfig(String name){
		if(GlobalKeys.configData.get(name).equals(null)){
			return "";
		}else{
			return GlobalKeys.configData.get(name);
		}
	}

	public static HashMap<String , String> listToHashMap(List<List<Object>> list){
		HashMap<String , String> map = new HashMap<>();
		for(int i=0;i<list.size();i++) {
			String str = list.get(i).get(1).toString();
			if(!str.equals("")) {
				map.put(str, list.get(i).get(2).toString());
			}
		}
		return map;
	}

	public static List<HashMap<String , String>> listToListOfHashMap(List<List<Object>> list){
		ArrayList<String> arrString = new ArrayList<>();

		for(int i=0;i<list.get(0).size();i++) {
			arrString.add(list.get(0).get(i).toString());
		}
		List<HashMap<String , String>> listMap = new ArrayList<HashMap<String,String>>();		
		for(int i=1;i<list.size();i++) {
			HashMap<String , String> map = new HashMap<>();
			for(int j=0;j<list.get(i).size();j++) {
				String str = list.get(i).get(j).toString();				
				map.put(arrString.get(j), str);				
			}
			listMap.add(map);
		}
		return listMap;
	}

	public static void createOutputDirectory()
	{
		java.io.File curdir = new java.io.File(".");

		Calendar calendar = new java.util.GregorianCalendar();
		hr = "0" + calendar.get(10);
		hr = hr.substring(hr.length() - 2);
		min = "0" + calendar.get(12);
		min = min.substring(min.length() - 2);
		sec = "0" + calendar.get(13);
		sec = sec.substring(sec.length() - 2);
		yr = calendar.get(1);
		mon = "0" + (calendar.get(2) + 1);
		mon = mon.substring(mon.length() - 2);
		day = "0" + calendar.get(5);
		day = day.substring(day.length() - 2);
		if (calendar.get(9) == 0) {
			am_pm = "AM";
		} else {
			am_pm = "PM";
		}    			
		try{
			String timeStamp = yr + "_" + mon + "_" + day + "_" + hr + "_" + min + "_" + sec + "_" + am_pm;
			
			if(getConfig("ReportPath").trim().equals("")){
	    		GlobalKeys.outputDirectory = curdir.getCanonicalPath() + "\\TestResults\\" + yr + "_" + mon + "_" + day + "_" + hr + "_" + min + "_" + sec + "_" + am_pm;
	    	}else{
	    		String basepath = getConfig("ReportPath").trim();
	    		if(!(getConfig("ReportPath").trim().endsWith("\\")||getConfig("ReportPath").trim().endsWith("/"))){
	    			basepath = basepath + "\\";
	    		}
	    		GlobalKeys.outputDirectory = basepath + yr + "_" + mon + "_" + day + "_" + hr + "_" + min + "_" + sec + "_" + am_pm;
	    	} 					
			GlobalKeys.outputDirectory = GlobalKeys.outputDirectory.replace("\\", "/");
			GlobalKeys.timeStamp = timeStamp;
		}catch (IOException e){
			System.out.println("IO Error while creating Output Directory : " + GlobalKeys.outputDirectory);
		}	
	}


	public static List<List<Object>> GetExcelTableInto2DArrayListString(String excelFile,String sheetName){

		List<List<Object>> OUT = new ArrayList<List<Object>>() ;

		try {
			File myFile = new File(excelFile); 
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(myFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			// Finds the workbook instance for XLSX file 
			XSSFWorkbook myWorkBook = null;
			try {
				myWorkBook = new XSSFWorkbook (fis);
			} catch (IOException e) {
				e.printStackTrace();
			} 

			// Return first sheet from the XLSX workbook 
			XSSFSheet mySheet = myWorkBook.getSheet(sheetName);

			// Get iterator to all the rows in current sheet 
			Iterator<Row> rowIterator = mySheet.iterator(); 

			// Traversing over each row of XLSX file 
			int count=1;
			while (rowIterator.hasNext()) { 
				Row row = rowIterator.next();
				List<Object> InnerArray = new ArrayList();
				// For each row, iterate through each columns 
				Iterator<Cell> cellIterator = row.cellIterator(); 

				while (cellIterator.hasNext()) {

					Cell cell = cellIterator.next(); 				        
					switch (cell.getCellType()) { 
					case STRING :  
						String c = cell.getStringCellValue();
						InnerArray.add(c);
						break; 
					case NUMERIC: 
						int n = (int) cell.getNumericCellValue();
						InnerArray.add(String.valueOf(n));
						break; 
					case BOOLEAN:
						boolean b = cell.getBooleanCellValue();
						InnerArray.add(String.valueOf(b));
						break;
					case BLANK:				            
						InnerArray.add("");
						break; 
					default : 
					} 
				}
				OUT.add(InnerArray);
				count++; 
			}
		} catch (Exception e) {
			System.out.println("Exception caught while reading data from excel "+e.getMessage());
		}

		return OUT;
	}
}
