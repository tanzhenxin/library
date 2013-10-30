package com.gtcc.library.webserviceproxy;

public class WebServiceInfo {
	//public static final String SERVER		= "http://129.223.252.236/gtcclibrary/amfphp/index.php";
	//public static final String SERVER_IMG	= "http://129.223.252.236/yii/gtcc/images/";
	public static final String SERVER		= "http://129.223.252.139:8080/gtcclibrary/amfphp/index.php";
	public static final String SERVER_IMG	= "http://129.223.252.139:8080/library/images/";
	
	public static final String PARAMETERS = "parameters";
	public static final String SERVICE_NAME = "serviceName";
	public static final String METHOD_NAME = "methodName";
	
	public static final String BOOK_SERVICE = "BookService";
	public static final String BOOK_METHOD_GET_ALL_BOOKS = "GetAllBooks"; 
	public static final String BOOK_METHOD_ADD_BOOKS = "AddBook";
	public static final String BOOK_METHOD_REMOVE_BOOKS = "RemoveBook";
	public static final String BOOK_METHOD_EDIT_BOOKS = "EditBook";
	public static final String BOOK_METHOD_REMOVE_ALL = "RemoveAll";
	public static final String BOOK_METHOD_GET_BOOK_BY_BIANHAO = "GetBookByBianHao"; 
	public static final String BOOK_METHOD_GET_BOOK_BY_ISBN = "GetBookByISBN"; 
	public static final String BOOK_METHOD_GET_ALL_BOOKS_IN_LIST = "GetAllBooksInList"; 
	
	public static final String BORROW_SERVICE = "BorrowService";
	public static final String BORROW_METHOD_GET_ALL_HISTORY = "GetAllHistory"; 
	public static final String BORROW_METHOD_BORROW = "Borrow";
	public static final String BORROW_METHOD_RETURN_BOOK = "ReturnBook";
	public static final String BORROW_METHOD_CHECK_WHETHER_BOOK_IN_BORROW = "checkWhetherBookInBorrow";
	public static final String BORROW_METHOD_GET_BORROW_INFO = "getBorrowInfo";
	public static final String BORROW_METHOD_REMOVE_ALL = "RemoveAll";
	
	public static final String LOGIN_SERVICE = "LoginService";
	public static final String LOGIN_METHOD_LOGIN = "Login";
	
	public static final String USER_SERVICE = "UserService";
	public static final String USER_METHOD_GET_ALL_USERS = "GetAllUsers"; 
	public static final String USER_METHOD_ADD_USER = "AddUser";
	public static final String USER_METHOD_REMOVE_USER = "RemoveUser";
	public static final String USER_METHOD_EDIT_USER = "EditUser";
	public static final String USER_METHOD_REMOVE_ALL_USER = "RemoveAllUser";

	public static final int OPERATION_SUCCEED = 0;
	public static final int OPERATION_FAILED = -1;
	public static final int USER_INVALID = -100;
	public static final int USER_GET_USER_LIST_FAILED = -101;
	public static final int USER_ALREADY_EXISTS = -102;
	public static final int USER_NOT_EXISTS = -103;
	public static final int USER_PASSWORD_WRONG = -104;

    public static final int LOAD_CAPACITY = 15; //load 15 items each time

	public static final int BOOK_CANNOT_GET_BOOK_LIST = -200;
	public static final int BOOK_BIANHAO_ALREADY_EXISTS = -201;
	public static final int BOOK_NO_SUCH_BOOK = -202;
	public static final int BORROW_NO_SUCH_HISTORY = -300;
	public static final int BORROW_NOT_BORROW_ANY_BOOKS = -301;
}
