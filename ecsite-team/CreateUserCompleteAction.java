package com.internousdev.mimosa.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.mimosa.dao.CartInfoDAO;
import com.internousdev.mimosa.dao.UserInfoDAO;
import com.internousdev.mimosa.util.CommonUtility;
import com.opensymphony.xwork2.ActionSupport;

public class CreateUserCompleteAction extends ActionSupport implements SessionAware {

	private String familyName;
	private String firstName;
	private String familyNameKana;
	private String firstNameKana;
	private String sex;
	private String email;
	private String createUserLoginId;
	private String password;
	private Map<String, Object> session;

	public String execute() {

		String result = ERROR;
		CommonUtility commonUtility = new CommonUtility();
		if(!commonUtility.checkSession(session)) {
			return "sessionTimeOut";
		}

		//UserInfoDAOのcreateUserメソッドからに新規作成したいユーザーの情報をDBにinsertする。
		UserInfoDAO userInfoDao = new UserInfoDAO();
		int cartCount = 0;
		int insertCount = 0;
		insertCount = userInfoDao.createUser(familyName, firstName, familyNameKana,
				 firstNameKana, sex,  email, createUserLoginId,  password);
		if (insertCount > 0) {

			CartInfoDAO cartInfoDao = new CartInfoDAO();
			cartCount = cartInfoDao.linkToLoginId(String.valueOf(session.get("tempUserId")), createUserLoginId);
			/**カート画面から遷移してきたことを証明するcartflagが、
			 * sessionにあれば変数に入れsessionから削除する。
			 * なければ変数cartflagに0を代入する。
			 */
			String cartflag;
			if(session.containsKey("cartflag")) {
				cartflag = session.get("cartflag").toString();
				session.remove("cartflag");
			} else {
				cartflag = "0";
			}

			if (cartflag.equals("1") && cartCount > 0) {
				session.put("destinationInfoDtoList", null);
				result= "settlement";
			}else {
				result = SUCCESS;
			}

				//ログイン状態を表す値をsessionに格納する。
				session.put("logined", 1);
				session.put("loginId", createUserLoginId);
		}
		return result;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFamilyNameKana() {
		return familyNameKana;
	}

	public void setFamilyNameKana(String familyNameKana) {
		this.familyNameKana = familyNameKana;
	}

	public String getFirstNameKana() {
		return firstNameKana;
	}

	public void setFirstNameKana(String firstNameKana) {
		this.firstNameKana = firstNameKana;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreateUserLoginId() {
		return createUserLoginId;
	}

	public void setCreateUserLoginId(String createUserLoginId) {
		this.createUserLoginId = createUserLoginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}