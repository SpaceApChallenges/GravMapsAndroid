package com.elis.gravitymap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elis.gravitymap.core.GPSTracker;
import com.elis.gravitymap.core.ParsingJson;
import com.elis.gravitymap.model.LocationModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static Double lat, lon, alt;
	public static Marker marker;
	private static GoogleMap map;
	private SearchView search;
	private static NumberFormat nf;
	private static TextView tv1;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				search.clearFocus();
				MainActivity.this
						.getWindow()
						.setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			}
			super.handleMessage(msg);
		}
	};

	public static double lookingForAltitude(double latitude, double longitude) {
		double result = Double.NaN;

		StringBuilder respStr = new StringBuilder();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		String url = "http://maps.googleapis.com/maps/api/elevation/"
				+ "xml?locations=" + String.valueOf(latitude) + ","
				+ String.valueOf(longitude) + "&sensor=true";
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
					result = (double) (Double.parseDouble(value));
				}
				instream.close();
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		if(result < 0) result = 0.0;
		return result;
	}

	public static double calculateGravity(double lat, double alt) {
		return // ((9.80613*(1-0.0026325*Math.pow(Math.cos(2*lat),2)))*1-0.000000392*alt);
		(9.780327 * (1 + 0.0053024 * Math.pow(Math.sin(lat), 2) - 0.0000058 * Math
				.pow(Math.sin(2 * lat), 2)) - 0.000003086 * alt);
	}

	private OnQueryTextListener queryTextListener = new OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String arg0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(), arg0, Toast.LENGTH_SHORT)
					.show();
			String url = "http://maps.googleapis.com/maps/api/geocode/json?address="
					+ arg0.replace(" ", "") + "&sensor=true";
			new HttpAsyncTask().execute(url);

			handler.sendEmptyMessage(1);

			return false;
		}

		@Override
		public boolean onQueryTextChange(String result) {

			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		Log.e("POSITION", position+"");
		try{
		if (position == 0){
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
			
		
		
	}
		if (position == 1){
			
		}
		}catch(Exception e){}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		search = (SearchView) searchItem.getActionView();
		// Toast.makeText(getApplicationContext(), ""+(search==null),
		// Toast.LENGTH_SHORT).show();
		search.setOnQueryTextListener(queryTextListener);
		search.setFocusable(false);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			OnMapClickListener {

		private MarkerOptions markerOpt;
		private View rootView;
		private SupportMapFragment mapFragment;

		private GPSTracker gps;
		boolean gpsActive = false;
		private View view;

		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		 public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		} 
		

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			if(rootView == null) 
				rootView = inflater
						.inflate(R.layout.fragment_map, container, false);
			
			nf = new DecimalFormat("#.#####");
			tv1 = (TextView) rootView.findViewById(R.id.tv1);
			Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
					"euphemia.ttf");
			tv1.setTypeface(tf);
			mapFragment = (SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.map1);
			FragmentTransaction fragmentTransaction = getChildFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.map1, mapFragment).commit();
			gps = new GPSTracker(getActivity());

			do {
				if (gps.canGetLocation()) {
					lat = gps.getLatitude();
					lon = gps.getLongitude();
					// Log.d("Latitudine e longitudine", lat )
					gpsActive = true;
				} else
					gps.showSettingsAlert();

			} while (!gpsActive);

			map = mapFragment.getMap();
			map.setOnMapClickListener(this);

			map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.icon_geoloc_120x120)));

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(lat, lon)).zoom(8).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			tv1.setText(nf.format(calculateGravity(lat,
					lookingForAltitude(lat, lon))));

			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}

		@Override
		public void onMapClick(LatLng point) {
			alt = 0.0;
			lat = point.latitude;
			lon = point.longitude;
			if (marker != null)
				marker.remove();
			alt = lookingForAltitude(point.latitude, point.longitude);
			markerOpt = new MarkerOptions()
					.position(point)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.marker2_40x40));
			map.setInfoWindowAdapter(new InfoWindowAdapter() {
				
				
				@Override
				public View getInfoWindow(Marker arg0) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public View getInfoContents(Marker arg0) {
					Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
							"euphemia.ttf");
		            View v = getActivity().getLayoutInflater().inflate(R.layout.window_layout, null);

		            // Getting reference to the TextView to set latitude
		            TextView tv2 = (TextView) v.findViewById(R.id.tv2);
		            tv2.setTypeface(tf);
		            tv2.setText("Latitude: " + nf.format(lat) + "\nLongitude: "
							+ nf.format(lon) + "\nElevation: " + nf.format(alt));

		            // Returning the view containing InfoWindow contents
		            return v;
				}
			});
			marker = map.addMarker(markerOpt);
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(point.latitude, point.longitude))
					.zoom(8).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			tv1.setText("" + nf.format(calculateGravity(point.latitude, alt)));
			map.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker m) {
					m.showInfoWindow();
					return false;
				}
			});

		}

	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			LocationModel list = null;

			ParsingJson json = new ParsingJson();
			try {
				list = json.getListAree(result);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			lat = (Double) list.getResults().get(0).getGeometry().getLocation()
					.getLat();
			lon = (Double) list.getResults().get(0).getGeometry().getLocation()
					.getLng();
			alt = lookingForAltitude(lat, lon);
			if (marker != null)
				marker.remove();
			marker = map.addMarker(new MarkerOptions().position(
					new LatLng(lat, lon)).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.marker2_40x40)));
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(lat, lon)).zoom(8).build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			tv1.setText(""
					+ nf.format(calculateGravity(lat,alt)));
			map.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker m) {
					m.showInfoWindow();
					return false;
				}
			});

		}

	}
	



}
