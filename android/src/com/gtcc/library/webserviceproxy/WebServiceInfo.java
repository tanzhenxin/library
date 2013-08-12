package com.gtcc.library.webserviceproxy;

public class WebServiceInfo {
	//public static final String SERVER	= "http://129.223.252.236/gtcclibrary/amfphp/index.php";
	public static final String SERVER	= "http://192.168.21.1/gtcclibrary/amfphp/index.php";
	
	public static final String PARAMETERS = "parameters";
	public static final String SERVICENAME = "serviceName";
	public static final String METHODNAME = "methodName";
	
	public static final String LOGINSERVICE = "LoginService";
	public static final String LOGINMETHOD = "Login";
	
	public static final String USERSERVICE = "UserService";
	public static final String ADDUSERMETHOD = "AddUser";
	public static final String REMOVEUSERMETHOD = "RemoveUser";
	
	
	public static final String OPERATIONSUCCESS = "0";
	
	
	//public static final String LOGIN="login";
	public static final String LOGIN="?serviceName=LoginService&methodName=Login";
	
	public static final String ADDUSER="?serviceName=UserService&methodName=AddUser";
	
	public static final String REMOVEUSER="?serviceName=UserService&methodName=RemoveUser";
	
	public static final String EditUSER="?serviceName=UserService&methodName=EditUser";
}
