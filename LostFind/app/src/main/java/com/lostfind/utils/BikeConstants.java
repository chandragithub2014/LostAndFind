/**
 * 
 */
package com.lostfind.utils;

/**
 * @author CHANDRASAIMOHAN
 *
 */
public class BikeConstants {
	public static final String BIKE_PREFS_DATA= "data";
	public static final String BIKE_BOOLEAN_PREFS_DATA= "booldata";
	public static final int STATUSCODE_OK = 200;

    public static final String WEBSERVICE_NETWORK_FAIL = "Network Fail";
	//positions
	public static final int SLIDING_MENU_HOME = 0;
	public static final int SLIDING_MENU_USER_SETTINGS = 1;
	public static final int SLIDING_MENU_REPORT_LOSS = 2;
	public static final int SLIDING_MENU_REPORT_FIND = 3;
	public static final int SLIDING_MENU_SEARCH = 4;
	public static final int SLIDING_MENU_REPORT_HISTORY = 5;
	public static final int SLIDING_MENU_DISCUSSION = 6;
	public static final int SLIDING_MENU_COMMMUNITY = 7;
	public static final int SLIDING_MENU_LOGOUT = 8;



	//Registration Post URL

	public static final String REGISTRATION_POST_SERVICE_URL = "http://52.38.114.74:8000/users";
	public static final String IP_ADDRESS = "http://52.38.114.74:8000";
	public static final String LOGIN_POST_SERVICE_URL = IP_ADDRESS+"/token";
	public static final String SEARCH_GET_SERVICE_URL = IP_ADDRESS+"/items";
	public static final boolean IS_WEB_SERVICE_ENABLED = false;
	public static final String REPORT_POST_SERVICE_URL = IP_ADDRESS+"/items";
	public static final String USER_PROFILE_GET_SERVICE_URL = IP_ADDRESS+"/users/@me";
	public static final String REPORT_HISTORY_GET_SERVICE_URL = IP_ADDRESS+"/history";
	public static final String COMMENT_ITEM_URL = IP_ADDRESS+"/items";
	public static final String RESET_PASSWORD = IP_ADDRESS+"/reset";

}
