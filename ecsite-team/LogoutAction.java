package com.internousdev.mimosa.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.mimosa.dao.UserInfoDAO;
import com.internousdev.mimosa.util.CommonUtility;
import com.opensymphony.xwork2.ActionSupport;

public class LogoutAction extends ActionSupport implements SessionAware {

	private Map<String, Object> session;

	//loginIDとsavedLoginIdをsessionに格納。
	public String execute() {

		String result = ERROR;
		CommonUtility commonUtility = new CommonUtility();
		if(!commonUtility.checkSession(session)) {
			return "sessionTimeOut";
		}

		UserInfoDAO userInfoDao = new UserInfoDAO();
		String loginId = String.valueOf(session.get("loginId"));
		boolean savedLoginIdFlg = Boolean.valueOf(String.valueOf(session.get("savedLoginIdFlg")));

		/**ログイン状態ならsessionをクリアし、sevedLoginIdとloginIdを格納する。
		 * 格納した情報はLoginActionのID保存チェックに利用される。
		 */
		int count = userInfoDao.logout(loginId);
		if(count > 0) {
			session.clear();
			session.put("savedLoginIdFlg", savedLoginIdFlg);
			if(savedLoginIdFlg){
				session.put("savedLoginId", loginId);
			}
			result = SUCCESS;
		}
		return result;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}