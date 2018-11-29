package com.internousdev.mimosa.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.mimosa.util.CommonUtility;
import com.opensymphony.xwork2.ActionSupport;

public class GoLoginAction extends ActionSupport implements SessionAware{

	private Map<String, Object> session;

	public String execute() {

		CommonUtility commonUtility = new CommonUtility();
		if(!commonUtility.checkSession(session)) {
			return "sessionTimeOut";
		}

	//画面遷移時にエラーメッセージを空にする。
		session.put("loginIdErrorMessageList", "");
		session.put("passwordErrorMessageList", "");
		session.put("notExsistsErrorMessageList", "");
		return SUCCESS;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
