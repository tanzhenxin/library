package com.gtcc.library.webserviceproxy;

public class WebServiceInfo {
	public static final String SERVER	= "http://129.223.252.236/gtcclibrary/amfphp/index.php";
	//public static final String SERVER	= "http://192.168.21.1/gtcclibrary/amfphp/index.php";
	
	public static final String PARAMETERS = "parameters";
	public static final String SERVICE_NAME = "serviceName";
	public static final String METHOD_NAME = "methodName";
	
	public static final String LOGIN_SERVICE = "LoginService";
	public static final String LOGIN_METHOD = "Login";
	
	public static final String USER_SERVICE = "UserService";
	public static final String ADD_USER_METHOD = "AddUser";

	public static final int OPERATION_SUCCEED = 0;
	public static final int OPERATION_FAILED = -1;
	public static final int USER_ALREADY_EXISTS = -102;
	public static final int USER_NOT_EXISTS = -103;
	public static final int USER_PASSWORD_WRONG = -104;
}
