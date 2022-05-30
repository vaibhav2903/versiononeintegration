package com.experitest.versiononeintegration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.experitest.versiononeintegration.CreateDefectAPI.ReportInfo;

public class UtilClass {
	private static final Properties APP_PROPERTIES = new Properties();

	public UtilClass() {
		try {
			APP_PROPERTIES.load(new FileReader("src/test/resources/app.properties"));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public static Properties getProperties() {
		return APP_PROPERTIES;
	}

	public static String convertToBasicAuth(String user, String pass) {
		return "Basic " + java.util.Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
	}

	public static String[] convertBasicAuthToString(String token) {
		String[] tokenn = token.split(" ");
		byte[] bytesUserPass = java.util.Base64.getDecoder().decode(tokenn[1]);
		String userPass = new String(bytesUserPass);
		String[] userpass = userPass.split(":");
		return userpass;
	}

	public static ReportInfo extractProjectAndReportId(String url) {
		int x = 0;
		for (int i = 0; i < 6; i++) {
			x = url.indexOf('/');
			url = url.substring(x + 1);
		}
		x = url.indexOf('/');
		String id = url.substring(0, x);
		url = url.substring(x + 1);
		x = url.indexOf('/');
		url = url.substring(x + 1);
		x = url.indexOf('/');
		String project = url.substring(0, x);
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.id = id;
		reportInfo.project = project;
		System.out.println(id + " ," + project);
		return reportInfo;
	}

	public static void fetchFeed(InputStream is, String fileLocation) {
		File downloadfile = new File(fileLocation);
		FileOutputStream fos = null;
		try {
			byte[] byteArray = IOUtils.toByteArray(is);
			fos = new FileOutputStream(downloadfile);
			fos.write(byteArray);
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			try {
				fos.flush();
				fos.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}

}
