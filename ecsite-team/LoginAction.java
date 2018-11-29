package com.internousdev.mimosa.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.mimosa.dao.CartInfoDAO;
import com.internousdev.mimosa.dao.DestinationInfoDAO;
import com.internousdev.mimosa.dao.UserInfoDAO;
import com.internousdev.mimosa.dto.DestinationInfoDTO;
import com.internousdev.mimosa.dto.UserInfoDTO;
import com.internousdev.mimosa.util.CommonUtility;
import com.internousdev.mimosa.util.InputChecker;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport implements SessionAware{

	private String loginId;
	private String password;
	private boolean savedLoginIdFlg;

	private Map<String, Object> session;

	public String execute() {

		List<String> loginIdErrorMessageList = new ArrayList<String>();
		List<String> passwordErrorMessageList = new ArrayList<String>();
		List<String> notExsistsErrorMessageList = new ArrayList<String>();

		session.remove("loginIdErrorMessageList");
		session.remove("passwordErrorMessageList");
		session.remove("notExsistsErrorMessageList");
		session.remove("loginId");

		String result = ERROR;
		CommonUtility commonUtility = new CommonUtility();
		if(!commonUtility.checkSession(session)) {
			return "sessionTimeOut";
		}

		/**ログインID保存が有効ならログインIDを入力済の状態に、
		 * 無効ならログインIDを未入力の状態にする。
		 */
		if(savedLoginIdFlg == true) {
			session.put("savedLoginIdFlg", true);
			session.put("savedLoginId", loginId);
		}else {
			session.put("savedLoginIdFlg", false);
			session.remove("savedLoginId");
		}

		/**
		 * inputChecker を使って入力文字制限をかける。
		 * ログインIDが1文字以上8文字以下の半角英数字のみ、
		 * パスワードが1文字以上16文字以下の半角英数字のみ。
		 */
		InputChecker inputChecker = new InputChecker();
		loginIdErrorMessageList = inputChecker.doCheck("ログインID", loginId, 1, 8, true, false, false, true, false, false, false, false, false);
		passwordErrorMessageList = inputChecker.doCheck("パスワード", password, 1, 16, true, false, false, true, false, false, false, false, false);

		//エラーがあるならメッセージを取得する。
		if(loginIdErrorMessageList.size()!=0) {
			session.put("loginIdErrorMessageList", loginIdErrorMessageList);
		}
		if(passwordErrorMessageList.size()!=0) {
			session.put("passwordErrorMessageList", passwordErrorMessageList);
		}


		//エラーメッセージがあるなら未ログイン状態にする。
		if(loginIdErrorMessageList.size()!=0
		|| passwordErrorMessageList.size()!=0) {
			session.put("logined", 0);
			return result;
		}

		UserInfoDAO userInfoDao = new UserInfoDAO();
		UserInfoDTO userInfoDTO = new UserInfoDTO();
		if(!(userInfoDao.isExistsUserInfo(loginId, password))) {
			notExsistsErrorMessageList.add("パスワードが異なります。");
			session.put("notExsistsErrorMessageList", notExsistsErrorMessageList);
		} else {
			//DBにUserInfoがあるなら、それを取得して格納する。
			if(userInfoDao.login(loginId, password) > 0) {
				userInfoDTO = userInfoDao.getUserInfo(loginId, password);
				//カート情報をtempUserIdから切り替える。
				int count = 0;
				CartInfoDAO cartInfoDao = new CartInfoDAO();
				count = cartInfoDao.linkToLoginId(String.valueOf(session.get("tempUserId")), loginId);
				/**カート画面から遷移してきたことを証明するcartflagが、
				 * sessionにあれば変数に入れsessionから削除する。
				 * なければ変数cartflagに0を代入する。。
				 */
				String cartflag;
				if(session.containsKey("cartflag")) {
					cartflag = session.get("cartflag").toString();
					session.remove("cartflag");
				} else {
					cartflag = "0";
				}

				//cartflagが1かつ、カートに商品が入っていればユーザーの宛先情報を取得する。
				if(cartflag.equals("1") && count > 0) {
					DestinationInfoDAO destinationInfoDao = new DestinationInfoDAO();
					try {
						List<DestinationInfoDTO> destinationInfoDtoList = new ArrayList<DestinationInfoDTO>();
						destinationInfoDtoList = destinationInfoDao.getDestinationInfo(loginId);

						//宛先情報が空ならdestinationInfoListの値をnullにする。
						Iterator<DestinationInfoDTO> iterator = destinationInfoDtoList.iterator();
						if(!(iterator.hasNext())) {
							destinationInfoDtoList = null;
						}
						session.put("destinationInfoDtoList", destinationInfoDtoList);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					//cartflagが1かつ、カートに情報があるなら決済確認画面へ
					result = "settlement";
				//なければホームへ
				} else {
					result = SUCCESS;
				}
			}
			//ログイン状態を表す値をsessionに格納する。
			session.put("loginId", userInfoDTO.getUserId());
			session.put("logined", 1);
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

	public Map<String, Object> getSession() {
		return session;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public boolean isSavedLoginIdFlg() {
		return savedLoginIdFlg;
	}

	public void setSavedLoginIdFlg(boolean savedLoginIdFlg) {
		this.savedLoginIdFlg = savedLoginIdFlg;
	}
}