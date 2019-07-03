package com.creditsuisse.app;

import org.json.simple.JSONArray;

import com.creditsuisse.beans.LogBean;

public interface Library {
	
	public JSONArray readFile(String fileName);

	public void processFile(JSONArray jsonObject);
	
	public void insertLog(LogBean logBean);

}
