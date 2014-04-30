package com.neha.pivotpowergeniusswitch;

public class Constants {
	public static final String SERVER_URL = "https://winkapi.quirky.com/";
	public static final String AUTH_REQUEST = "oauth2/token";
	public static final String DEVICES_REQUEST = "users/me/wink_devices";
	public static final String OUTLETS_REQUEST = "outlets/";

	public static final String HEADER_AUTH = "Authorization";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	
	public static final String PARAM_CLIENT_ID = "client_id";
	public static final String PARAM_CLIENT_SECRET = "client_secret";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_GRANT_TYPE = "grant_type";

	public static final String PARAM_ACCESS_TOKEN = "access_token";

	public static final String PARAM_DATA = "data";
	public static final String PARAM_POWERSTRIP_ID = "powerstrip_id";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_OUTLETS = "outlets";
	public static final String PARAM_OUTLET_ID = "outlet_id";
	public static final String PARAM_POWERED = "powered";
	
	public static final String FILE_AUTH_PREFS = "AuthorizationPrefs";
	
	public static final String INTENT_DATA_POWERSTRIP = "powerstrip_list";
	public static final String INTENT_DATA_OUTLET = "outlet_list";
	
	public static final int OUTLET_ACTIVITY_REQUEST_CODE = 1000;
	public static final String EXTRA_POWERSTRIP = "extra_powerstrip";
	
}