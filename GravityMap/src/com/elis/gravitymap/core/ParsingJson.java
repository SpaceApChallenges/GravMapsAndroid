package com.elis.gravitymap.core;

import java.io.IOException;
import java.util.ArrayList;

import com.elis.gravitymap.model.LocationModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ParsingJson {

	private static final String TAG = "ParsingJson";
	private Gson gson;
	
	//private Reader reader;
	//private InputStream source;
	//private Context context;

	public ParsingJson(/*Context context*/) {
	//	this.context = context;
		this.gson = new Gson();

	}

//	/**
//	 * legge i file json
//	 * 
//	 * @param source
//	 * @throws IOException
//	 */
//	private Reader readJson(String filename) throws IOException {
//		return this.reader = new InputStreamReader(getJson(filename));
//
//	}
//
//	/**
//	 * prende il json dall'assets
//	 * 
//	 * @param filename
//	 * @throws IOException
//	 */
//	private InputStream getJson(String filename) throws IOException {
//
//		return this.source = context.getAssets().open(filename);
//	}

	/**
	 * restituisce la list
	 * 
	 * @return
	 * @throws IOException
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 */
	public LocationModel getListAree(String json)
			throws JsonSyntaxException, JsonIOException, IOException {
		  JsonParser parser = new JsonParser();
		   // JsonArray jArray = parser.parse(json).getAsJsonArray()
		 
		    	LocationModel cse = gson.fromJson( json , LocationModel.class);
		 
		
		return cse;
	}

}
