package com.experitest.versiononeintegration;

import java.io.FileReader;
import java.util.Properties;

import com.versionone.Oid;
import com.versionone.apiclient.Asset;
import com.versionone.apiclient.Query;
import com.versionone.apiclient.Services;
import com.versionone.apiclient.V1Connector;
import com.versionone.apiclient.interfaces.IAssetType;
import com.versionone.apiclient.interfaces.IAttributeDefinition;
import com.versionone.apiclient.interfaces.IServices;
import com.versionone.apiclient.services.QueryResult;

public class UpdateTestAPI {
//
//	enum TEST_STATUS {
//		PASSED(129), FAILED(155);
//
//		private int value;
//
//		private TEST_STATUS(int value) {
//			this.value = value;
//		}
//	}

	private static final Properties APP_PROPERTIES = new Properties();
	// UtilClass utilClass = new UtilClass();

	public void updateTestInVersionOne(String oId, TEST_STATUS status) throws Exception {
		System.out.println(status.getValue());
		APP_PROPERTIES.load(new FileReader("src/test/resources/app.properties"));
		try {
		String[] userPass = UtilClass.convertBasicAuthToString(APP_PROPERTIES.getProperty("com.cv1.basicauthtoken"));
		V1Connector connector = V1Connector.withInstanceUrl(APP_PROPERTIES.getProperty("com.experitest-cv1.base-url"))
				.withUserAgentHeader("AppName", "1.0").withUsernameAndPassword("admin", "admin")
				// .withUserAgentHeader("AppName", "1.0").withUsernameAndPassword(userPass[0],
				// userPass[1])
				// .withAccessToken(APP_PROPERTIES.getProperty("com.cv1.basicauthtoken"))
				.build();
		System.out.println(status.getValue());
		IServices services = new Services(connector);
		Oid testId = services.getOid("Test:8835");
		IAssetType testAssetType = services.getMeta().getAssetType("Test");
		// services.retrieve(query)
		IAttributeDefinition testStatusAttribute = testAssetType.getAttributeDefinition("Status");
		System.out.println(status.getValue());
		Query query = new Query(testId);
		query.getSelection().add(testStatusAttribute);
		QueryResult result = services.retrieve(query);
		Asset test = (result.getAssets())[0];
		test.setAttributeValue(testStatusAttribute, "TestStatus:" + status.getValue());
		services.save(test);
		System.out.println(test.getAttributes());
		}
		catch(Exception e) {
			System.out.println(e.toString());
		}
		// IAssetType testAssetType = services.getMeta().getAssetType("Test");
		// IAttributeDefinition testAttribute =
		// services.getMeta().getAttributeDefinition("Test");

		// IAssetType testStatusAssetType =
		// services.getMeta().getAssetType("TestStatus");
		// System.out.println(asset.getAttribute(testAttribute).getValues());
		// testStatusAssetType.
		// Asset testStatusAsset = services.
		// testStatusAttribute.
		// asset.setAttributeValue(testStatusAttribute, "Passed");
		// Asset TestStatus =
		// testStatusAttribute
		// System.out.println(member.getAttribute(attributeDefinition));

	}

}
