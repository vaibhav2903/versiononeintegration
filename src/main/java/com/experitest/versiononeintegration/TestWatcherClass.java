package com.experitest.versiononeintegration;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestWatcherClass extends TestWatcher {
	UpdateTestAPI updateTestAPI = new UpdateTestAPI();
	UtilClass utilClass = new UtilClass();
	CreateDefectAPI defectAPI = new CreateDefectAPI();

	@Override
	protected void failed(Throwable e, Description description) {
		try {
			updateTestAPI.updateTestInVersionOne("", TEST_STATUS.FAILED);
			System.out.println(UtilClass.getProperties().getProperty("com.cv1.createdefect"));
			//clean install -Dcreatedefect="true"
			if("true".equalsIgnoreCase(System.getProperty("createdefect")))
				System.out.println("true");
					//defectAPI.addReportToCV1("", "", "Defect");
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	@Override
	protected void succeeded(Description description) {
		
	}

}
