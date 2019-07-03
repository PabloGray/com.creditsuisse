package com.creditsuisse.app;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;

import com.creditsuisse.beans.LogBean;

public class LibraryIMPL implements Library{
	
	
	@Override
	public JSONArray readFile(String fileName) {
		//JSON parser object to parse read file
    	Object obj;
    	JSONArray logList = new JSONArray();
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader(fileName))
        {
            //Read JSON file
            obj = jsonParser.parse(reader);
 
            logList =(JSONArray) obj;
            System.out.println(logList);
            System.out.println(logList.getClass());
             
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return logList;
	}
	
	
	@Override
	public void processFile(JSONArray jsonLogArray) {
		
		LogBean bean = new LogBean();

		for (int i=0; i<jsonLogArray.size(); i++) {
			// Iterate over the JSONArray
			JSONObject obj1 = (JSONObject)jsonLogArray.get(i);
			
			// Use a bean to store the values
			bean.setEventId((String) obj1.get("id"));
			bean.setTimeStamp((Long) obj1.get("timestamp"));
			bean.setType((String) obj1.get("type"));
			bean.setHost((String) obj1.get("host"));
			bean.setAlert((String) obj1.get("alert"));
			
			// Variable to store the elapsed time of the log
			Long elapsedTime;
			String id1 = (String) obj1.get("id");
			
			// Iterate over the list again to compare two values
			// I am pretty sure this is not the most efficient method but could not
			// think about other at this point
			for (int j=0;j<jsonLogArray.size();j++) {
				JSONObject obj2 = (JSONObject)jsonLogArray.get(j);
				String id2 = (String) obj2.get("id");
				if(Integer.compare(i, j) == -1 && id1.equalsIgnoreCase(id2)) {
					Long time1 = (Long) obj1.get("timestamp");
					Long time2 = (Long) obj2.get("timestamp");
					elapsedTime = Math.abs(time1 - time2);
					bean.setDuration(Math.abs(time1 - time2));

					if(elapsedTime > 4L) {
						bean.setAlert("Y");
					}
					System.out.println("Process is " + id1);
					System.out.println("Elapsed time is " + elapsedTime.toString() + " ms.");
					
					// Wanted to call the Insert method in the transaction, but with this 
					// implementation is easier on this way
					insertLog(bean);		
				}
			}		
			
		}
         
	}
	@Override
	public void insertLog(LogBean logBean) {

		try
	    {
	      // Create our mysql database connection
	      String myDriver = "org.gjt.mm.mysql.Driver";
	      String myUrl = "jdbc:mysql://localhost/test";
	      
	      // Fill out with user and password
	      String user = "";
	      String password = "";
	      Class.forName(myDriver);
	      Connection conn = DriverManager.getConnection(myUrl, user, password);
	      String sqlQuery;
	      
	      // Build a dynamic SQL Insert query. 
	      
	      String insertColumns = "INSERT INTO LOGSINFO (EVENT_ID, DURATION";
	      String insertValues = "VALUES (" + logBean.getEventId() + ", " + logBean.getDuration();
	      
	      if(logBean.getType() != null && logBean.getHost() != null) {
	    	insertColumns += ", TYPE";
	    	insertValues += ", " + logBean.getType();
	      }
	      
	      if(logBean.getHost() != null) {
	    	  insertColumns += ", HOST";
	    	  insertValues += ", " + logBean.getHost();
	      }
	       if(logBean.getDuration() > 8L) {
	    	   insertColumns += ", HOST";
	    	   insertValues += ", " + logBean.getHost();
	       }
	       
	       if(logBean.getAlert() != null) {
	    	   insertColumns += ", ALERT";
	    	   insertValues += ", " + logBean.getAlert();
	       }
	      
	      sqlQuery = insertColumns + ") " + insertValues + ") ";
	      System.out.println(sqlQuery);
	

	   
	      // Create the java statement
	      Statement st = conn.createStatement();
	      
	      // Execute the query
	      ResultSet rs = st.executeQuery(sqlQuery);
	      
	      // Close the connection to de DB
	      st.close();
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception! ");
	      System.err.println(e.getMessage());
	    }
		
	}	


}
