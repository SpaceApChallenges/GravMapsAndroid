package com.elis.gravitymap;
import android.app.Application;
import android.content.Context;


public class GMApplication extends Application {
	
	private static Context context;
	
	public void onCreate() {
		super.onCreate();
		GMApplication.context = getApplicationContext();
	}

	public static Context getContext(){
		return context;
	}
	
	
}
