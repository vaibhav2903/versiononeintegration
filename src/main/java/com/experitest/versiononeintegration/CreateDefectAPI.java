package com.experitest.versiononeintegration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1Connector;
import com.versionone.apiclient.interfaces.IAssetType;
import com.versionone.apiclient.interfaces.IAttributeDefinition;
import com.versionone.apiclient.interfaces.IServices;
import com.versionone.apiclient.services.QueryResult;

public class CreateDefectAPI {

	private static final Properties APP_PROPERTIES = new Properties();
//	String usernameAndPassword = "admin:admin";
//	String userPassCloud = "vaibhav.savala:Dragonballz!1";
//	String authorizationHeaderValue = "Basic "
//			+ java.util.Base64.getEncoder().en	codeToString(usernameAndPassword.getBytes());
//	String authorizationHeaderValueCloud = "Basic "
//			+ java.util.Base64.getEncoder().encodeToString(userPassCloud.getBytes());

//	public String convertToBasicAuth(String user, String pass) {
//		return "Basic " + java.util.Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
//	}
//
//	public String[] convertBasicAuthToString(String token) {
//		String[] tokenn = token.split(" ");
//		byte[] bytesUserPass = java.util.Base64.getDecoder().decode(tokenn[1]);
//		String userPass = new String(bytesUserPass);
//		String[] userpass = userPass.split(":");
//		return userpass;
//	}

	public void addNewAssetWithAttachment(ReportObject object, String errorMessage, String assetType) throws Exception {

		String[] userPass = UtilClass.convertBasicAuthToString(APP_PROPERTIES.getProperty("com.cv1.basicauthtoken"));
		V1Connector connector = V1Connector.withInstanceUrl(APP_PROPERTIES.getProperty("com.experitest-cv1.base-url"))
				.withUserAgentHeader("AppName", "1.0").withUsernameAndPassword("admin", "admin")
				// .withUserAgentHeader("AppName", "1.0").withUsernameAndPassword(userPass[0],
				// userPass[1])
				// .withAccessToken(APP_PROPERTIES.getProperty("com.cv1.basicauthtoken"))
				.build();
		IServices services = new Services(connector);
		// services.get
		Oid projectId = services.getOid(APP_PROPERTIES.getProperty("com.cv1.oid"));
		IAssetType assettype = services.getMeta().getAssetType(assetType);
		Asset newAsset = services.createNew(assettype, projectId);
		IAttributeDefinition nameAttribute = assettype.getAttributeDefinition("Name");
		newAsset.setAttributeValue(nameAttribute, object.name);
		IAttributeDefinition descriptionAttribute = assettype.getDescriptionAttribute();

		if (assetType.equalsIgnoreCase("Defect")) {
			IAttributeDefinition buildAttribute = assettype.getAttributeDefinition("FoundInBuild");
			newAsset.setAttributeValue(buildAttribute,
					((LinkedTreeMap<String, String>) (object.keyValuePairs)).get("appBuildVersion"));

			IAttributeDefinition versionAttribute = assettype.getAttributeDefinition("VersionAffected");
			newAsset.setAttributeValue(versionAttribute,
					((LinkedTreeMap<String, String>) (object.keyValuePairs)).get("appReleaseVersion"));
		}
		newAsset.setAttributeValue(descriptionAttribute, errorMessage + "\n" + object.keyValuePairs);
		services.save(newAsset);
		services.saveAttachment(APP_PROPERTIES.getProperty("com.experitest-cv1.tempfile.location"), newAsset,
				"Attachment for " + newAsset.getOid().toString());
//		IAttributeDefinition createdByDefinition = assettype.getAttributeDefinition("CreatedBy");
//		// createdByAsset = newAsset.getAttribute(createdByDefinition)
//		Query query = new Query(newAsset.getOid());
//		query.getSelection().add(createdByDefinition);
//		QueryResult result = services.retrieve(query);
//		Asset normalAsset = result.getAssets()[0];
//		String oldName = normalAsset.getAttribute(createdByDefinition).getValue().toString();	
		IAttributeDefinition linkDefinition = assettype.getAttributeDefinition("Links");
		Oid assetId = services.getOid(assetType + ":" + newAsset.getOid().getKey());
		Asset newLinkAsset = services.createNew(services.getMeta().getAssetType("Link"), assetId);
		newLinkAsset.setAttributeValue(services.getMeta().getAssetType("Link").getNameAttribute(), "Report URL");
		newLinkAsset.setAttributeValue(services.getMeta().getAssetType("Link").getAttributeDefinition("URL"),
				((LinkedTreeMap<String, String>) (object.keyValuePairs)).get("reportUrl"));
		services.save(newLinkAsset);

	}

	String convertkeyValuePairs(Object keyValuePairs) {
		HashMap<String, String> map = new HashMap<String, String>();
		System.out.println(keyValuePairs.toString());
		JSONObject jObject = new JSONObject(keyValuePairs);

		Iterator<?> keys = jObject.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			String value = jObject.getString(key);
			map.put(key, value);

		}

		StringBuilder mapAsString = new StringBuilder("");
		for (String key : map.keySet()) {
			mapAsString.append(key + "=" + map.get(key) + "\n ");
		}
		return mapAsString.toString();
	}

	public void addReportToCV1(String url, String errorMessage, String assetType) throws Exception {
		APP_PROPERTIES.load(new FileReader("src/test/resources/app.properties"));
		ReportInfo info = UtilClass.extractProjectAndReportId(url);
		ReportObject rj = getReportInfo(info.id, info.project);
		addNewAssetWithAttachment(rj, errorMessage, assetType);
	}

//	public void addReportToCV1(List<String> reportIds, String projectName, String assetType) throws Exception {
//		APP_PROPERTIES.load(new FileReader("src/test/resources/app.properties"));
//		for (String reportId : reportIds) {
//			ReportObject rj = getReportInfo(reportId, projectName);
//			addNewAssetWithAttachment(rj, assetType);
//		}
//	}

	public ReportObject getReportInfo(String id, String project) {
		Client client = ClientBuilder.newClient();
		StringBuilder url = new StringBuilder(
				APP_PROPERTIES.getProperty("com.experitest.base-url") + "/reporter/api/tests/" + id);
		if (!project.isEmpty()) {
			url.append("/project/" + project);
		}
		Response response = client.target(url.toString()).request(MediaType.APPLICATION_JSON)
				.header("Authorization", APP_PROPERTIES.getProperty("com.experitest.basicauthtoken")).get();
		String json = response.readEntity(String.class);
		System.out.println(json);
		Gson g = new Gson();
		ReportObject obj = g.fromJson(json, ReportObject.class);
		getAttachment(id, project);
		return obj;
	}

	static class ReportObject {
		Long id;
		String name;
		String status;
		boolean success;
		Object keyValuePairs;

		@Override
		public String toString() {
			return "ReportObject [id=" + id + ", name=" + name + ", status=" + status + ", success=" + success
					+ ", keyValuePairs=" + keyValuePairs + "]";
		}

	}

	public void getAttachment(String id, String project) {
		// id = "44187";
		Client client = ClientBuilder.newClient();
		Response response = client
				.target(APP_PROPERTIES.getProperty("com.experitest.base-url") + "/reporter/api/" + id + "/attachments")
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", APP_PROPERTIES.getProperty("com.experitest.basicauthtoken")).get();

		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			InputStream is = response.readEntity(InputStream.class);
			UtilClass.fetchFeed(is,APP_PROPERTIES.getProperty("com.experitest-cv1.tempfile.location"));
			IOUtils.closeQuietly(is);
		}
	}

//	private void fetchFeed(InputStream is) {
//		File downloadfile = new File(APP_PROPERTIES.getProperty("com.experitest-cv1.tempfile.location"));
//		FileOutputStream fos = null;
//		try {
//			byte[] byteArray = IOUtils.toByteArray(is);
//			fos = new FileOutputStream(downloadfile);
//			fos.write(byteArray);
//		} catch (Exception e) {
//			System.out.println(e.toString());
//		} finally {
//			try {
//				fos.flush();
//				fos.close();
//			} catch (Exception e) {
//				System.out.println(e.toString());
//			}
//		}
//	}

	static class ReportInfo {
		String id;
		String project;
	}

}
