package com.internousdev.mimosa.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.mimosa.dao.MCategoryDAO;
import com.internousdev.mimosa.dto.MCategoryDTO;
import com.internousdev.mimosa.util.CommonUtility;
import com.opensymphony.xwork2.ActionSupport;

public class HomeAction extends ActionSupport implements SessionAware{

	private List<MCategoryDTO> mCategoryDtoList=new ArrayList<MCategoryDTO>();
	private Map<String,Object> session;

	public String execute(){

		session.remove("cartflag");

		if(!(session.containsKey("loginId"))&& !(session.containsKey("tempUserId"))){
			CommonUtility commonUtility=new CommonUtility();
			session.put("tempUserId",commonUtility.getRamdomValue());
		}

		if(!session.containsKey("logined")){
			session.put("logined",0);
		}

		if(!session.containsKey("mCategoryList")){
			MCategoryDAO mCategoryDao=new MCategoryDAO();
			mCategoryDtoList=mCategoryDao.getMCategoryList();
			session.put("mCategoryDtoList",mCategoryDtoList);
		}

		return SUCCESS;
	}

	public List<MCategoryDTO> getmCategoryDtoList(){
		return mCategoryDtoList;
	}

	public void setCategoryDtoList(List<MCategoryDTO> mCategoryDtoList){
		this.mCategoryDtoList=mCategoryDtoList;
	}

	public Map<String,Object> getSession(){
		return session;
	}

	public void setSession(Map<String,Object>session){
		this.session=session;
	}
}