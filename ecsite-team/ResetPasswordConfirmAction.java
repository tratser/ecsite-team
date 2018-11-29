package com.internousdev.mimosa.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.mimosa.dao.UserInfoDAO;
import com.internousdev.mimosa.util.CommonUtility;
import com.internousdev.mimosa.util.InputChecker;
import com.opensymphony.xwork2.ActionSupport;

public class ResetPasswordConfirmAction extends ActionSupport implements SessionAware{

	private String loginId;
	private String password;
	private String newPassword;
	private String reConfirmationPassword;

	private Map<String,Object> session;

	public String execute(){

		List<String> loginIdErrorMessageList= new ArrayList<String>();
		List<String> passwordErrorMessageList= new ArrayList<String>();
		List<String> passwordIncorrectErrorMessageList= new ArrayList<String>();
		List<String> newPasswordErrorMessageList= new ArrayList<String>();
		List<String> reConfirmationPasswordErrorMessageList= new ArrayList<String>();
		List<String> newPasswordIncorrectErrorMessageList= new ArrayList<String>();

		String result=ERROR;
		CommonUtility commonUtility = new CommonUtility();
		if(!commonUtility.checkSession(session)) {
			return "sessionTimeOut";
		}

		session.remove("loginIdErrorMessageList");
		session.remove("passwordErrorMessageList");
		session.remove("passwordIncorrectErrorMessageList");
		session.remove("newPasswordErrorMessageList");
		session.remove("reConfirmationPasswordErrorMessageList");
		session.remove("newPasswordIncorrectErrorMessageList");

		InputChecker inputChecker = new InputChecker();

		loginIdErrorMessageList = inputChecker.doCheck("ログインID", loginId,  1, 8, true, false, false, true, false, false, false, false, false);
		passwordErrorMessageList = inputChecker.doCheck("現在のパスワード", password,  1, 16, true, false, false, true, false, false, false, false, false);
		newPasswordErrorMessageList = inputChecker.doCheck("新しいパスワード", newPassword, 1, 16, true, false, false, true, false, false, false, false, false);
		reConfirmationPasswordErrorMessageList = inputChecker.doCheck("(再確認)", reConfirmationPassword, 1, 16, true, false, false, true, false, false, false, false, false);

		newPasswordIncorrectErrorMessageList = inputChecker.doPasswordCheck(newPassword,reConfirmationPassword);

		if(loginIdErrorMessageList.size()==0
				&& passwordErrorMessageList.size()==0
				&& newPasswordErrorMessageList.size()==0
				&& reConfirmationPasswordErrorMessageList.size()==0
				&& newPasswordIncorrectErrorMessageList.size()==0){

			UserInfoDAO userInfoDAO =new UserInfoDAO();
			ResetPasswordAction action = new ResetPasswordAction();
			if(userInfoDAO.isExistsUserInfo(loginId, password)){
				String concealedPassword = action.concealPassword(newPassword);
				session.put("resetPasswordLoginId",loginId);
				session.put("newPassword", newPassword);
				session.put("concealedPassword", concealedPassword);
				result =SUCCESS;

			}else{
				passwordIncorrectErrorMessageList.add("入力されたパスワードが異なります");
				session.put("passwordIncorrectErrorMessageList", passwordIncorrectErrorMessageList);
			}
		}else{
			session.put("loginIdErrorMessageList", loginIdErrorMessageList);
			session.put("passwordErrorMessageList",passwordErrorMessageList);
			session.put("newPasswordErrorMessageList", newPasswordErrorMessageList);
			session.put("reConfirmationPasswordErrorMessageList", reConfirmationPasswordErrorMessageList);
			session.put("newPasswordIncorrectErrorMessageList",newPasswordIncorrectErrorMessageList);
		}
		return result;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getReConfirmationPassword() {
		return reConfirmationPassword;
	}

	public void setReConfirmationPassword(String reConfirmationPassword) {
		this.reConfirmationPassword = reConfirmationPassword;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}