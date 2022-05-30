package com.experitest.versiononeintegration;

public class RunClass {

	public static void main(String[] args) {
		// CreateDefectAPI api = new CreateDefectAPI();
		// UtilClass clas = new UtilClass();
		// api.getAttachment("");
		try {
			// api.getReportInfo("","");
			// api.t();
			// System.out.println(api.convertToBasicAuth("admin", "admin"));
//			System.out.println(api.convertToBasicAuth("vaibhav.savala", "Dragonballz!1"));
			// api.addReportToCV1("44533", "", "Defect");
			// api.extractProjectAndReportId("https://uscloud.experitest.com/reporter/#/test/44513/project/Default/");
//			String[] x = api.convertBasicAuthToString("Basic YWRtaW46YWRtaW4=");
//			System.out.println(x[1] +" "+x[0]);
			UpdateTestAPI api = new UpdateTestAPI();
			//api.updateTestInVersionOne("","");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

}
