package com.elis.gravitymap.UI;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.elis.gravitymap.GMApplication;
import com.elis.gravitymap.R;
import com.elis.gravitymap.core.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapFragment extends Fragment{
	private View  rootView;
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private GPSTracker gps;
	double lat = 0.0 ,lon = 0.0;
	boolean gpsActive = false;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_map, container, false);
		mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map1);
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.map1, mapFragment).commit();
		gps = new GPSTracker(getActivity());
		
		do {
			if(gps.canGetLocation())
			{
				lat = 41.892900;//gps.getLatitude();
				lon = 12.482500;//gps.getLongitude();
				//Log.d("Latitudine e longitudine", lat )
				gpsActive = true;
			}
			else
				gps.showSettingsAlert();
			
		} while(!gpsActive);
		
		double alt = lookingForAltitude(lat,lon);
		
		map = mapFragment.getMap();
		
		CameraPosition cameraPosition = new CameraPosition.Builder().target(
				new LatLng(lat, lon)).zoom(8).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		
		Toast.makeText(getActivity(), "Altitudine = " + alt+" Gravity: "+ (9.780327*(1+0.0053024*Math.pow(Math.sin(lat),2)-0.0000058*Math.pow(Math.sin(2*lat),2))
				-0.000003086*alt) , Toast.LENGTH_LONG).show();
		Log.d("Altitudine", ""+alt + "Gravity: " + (9.780327*(1+0.0053024*Math.pow(Math.sin(lat),2)-0.0000058*Math.pow(Math.sin(2*lat),2))
				-0.000003086*alt));
		
				
		return rootView;
		
		
		
		
		
		
	}
	
	private double lookingForAltitude(double latitude, double longitude) {
        double result = Double.NaN;
        StringBuilder respStr = new StringBuilder();
        StrictMode.ThreadPolicy policy = new
        		StrictMode.ThreadPolicy.Builder()
        		.permitAll().build();
        		StrictMode.setThreadPolicy(policy);
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String url = "http://maps.googleapis.com/maps/api/elevation/"
                + "xml?locations=" + String.valueOf(latitude)
                + "," + String.valueOf(longitude)
                + "&sensor=true";
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                int r = -1;
				while ((r = instream.read()) != -1)
                    respStr.append((char) r);
                String tagOpen = "<elevation>";
                String tagClose = "</elevation>";
                if (respStr.indexOf(tagOpen) != -1) {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    result = (double)(Double.parseDouble(value));
                }
                instream.close();
            }
        } catch (ClientProtocolException e) {} 
        catch (IOException e) {}

        return result;
    }
}
