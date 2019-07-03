package com.creditsuisse.app;

import org.json.simple.JSONArray;


public class App
{
	// First we need to determine the file to open
	static String fileName = "logsInfo.json";
	    public static void main(String[] args)
	    {
	    	//Instansiate the libray
	    	LibraryIMPL lib = new LibraryIMPL();
	    	
	        //Read the file and create a JSONArray
	    	JSONArray jsonLogArray = lib.readFile(fileName);
	        
	        //Once created, is time to process the JSON File
	        lib.processFile(jsonLogArray);
	    }
	 
    }
