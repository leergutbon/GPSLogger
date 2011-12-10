package my.gpslogger.namespace;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GPSLoggerActivity extends Activity {
	View tv_longitude;
	View tv_latitude;
	View tv_altitude;
	View status;
	EditText target;
	EditText descr;
	Button bat;// = (Button) this.findViewById(R.id.button);
	FileOutputStream fOut;
	BufferedOutputStream bOut;
	LocationManager lm;
	boolean write = false;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);    
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); // initialize the manager
		tv_longitude = this.findViewById(R.id.longitude);
		tv_latitude = this.findViewById(R.id.latitude);
		tv_altitude = this.findViewById(R.id.altitude);
		status = (TextView) this.findViewById(R.id.status);
		target = (EditText) this.findViewById(R.id.editText1);
		((TextView)status).setText("debugmode");
		try {
			lm.requestLocationUpdates("gps", 600, 1, locationListener); //was 60000
		} catch (Exception e) {
			e.printStackTrace();
		}
		Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (l != null) {
			printSaveLocation(l,false);
		}
	}

	void printSaveLocation(Location l, boolean write) {
		((TextView) tv_longitude).setText("Lon: "+String.valueOf(l.getLatitude()));
		((TextView) tv_latitude).setText("Lat: "+String.valueOf(l.getLongitude()));
		((TextView) tv_altitude).setText("Alt: "+String.valueOf(l.getAltitude()));
		//l.getAltitude();
		if(write){
			try {
				bOut.write(("\t"+"\t"+"<trkpt lat=\""+String.valueOf(l.getLatitude())+"\" lon=\""+String.valueOf(l.getLongitude())+"\" alt=\""+String.valueOf(l.getAltitude())+"\"></trkpt>"+"\n").getBytes());
				bOut.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	void printProlog(){
		descr = (EditText)this.findViewById(R.id.editText2);
		status = (TextView)this.findViewById(R.id.status);
		((TextView)status).setText("PrologWritten!");
		try {
			bOut.write(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"+"\n").getBytes());
			bOut.write(("<gpx creator=\"@Author\" description=\""+descr.getText()+"\" version=\"alpha\">"+"\n").getBytes());
			bOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location l) {
			printSaveLocation(l, write);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			((TextView) tv_latitude).setText("ProviderDisabled");
			((TextView) tv_longitude).setText("ProviderDisabled");
			((TextView) tv_altitude).setText("ProviderDisabled");
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			((TextView) tv_latitude).setText("ProviderEnabled");
			((TextView) tv_longitude).setText("ProviderEnabled");
			((TextView) tv_altitude).setText("ProviderDisabled");
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}
	};
	boolean flicker = false;
	public void myClickHandler(View view){

		switch (view.getId()) {
		case R.id.button: // the exit button!
			if(!flicker){break;} 
			write = false; //show dont write
			status = (TextView)this.findViewById(R.id.status);
			try {
				bOut.write(("\t"+"</trkseq>"+"\n"+"</gpx>"+"\n").getBytes());
				bOut.flush();
				((TextView)status).setText("written!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//finish(); // fraglich, man sollte eigentlich die nächste datei screiben können.
			flicker = false;
			//System.exit(1);
			break;
		case R.id.button1:
			if(flicker){break;} // can only be pressed once
			flicker = true;
			status = (TextView)this.findViewById(R.id.status);
			tv_longitude = this.findViewById(R.id.longitude);
			tv_latitude = this.findViewById(R.id.latitude);
			tv_altitude = this.findViewById(R.id.altitude);
			target = (EditText)this.findViewById(R.id.editText1);
			descr = (EditText)this.findViewById(R.id.editText2);
			String outputfile = target.getText().toString();
			((TextView)status).setText("running, no file created!");
			//LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
			
			try {
				//((TextView)status).setText(Environment.getExternalStorageDirectory().toString());
				File file = Environment.getExternalStorageDirectory();
				File f = new File(file, outputfile+".gpx");
				bOut = new BufferedOutputStream(new FileOutputStream(f));
				//bOut = new BufferedOutputStream(openFileOutput(outputfile+".gpx", MODE_WORLD_READABLE));
				printProlog();
				bOut.write(("\t"+"<trkseg>"+"\n").getBytes()); // mehr an prolog daten
				bOut.flush();
				((TextView)status).setText("running");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			write = true; // show & write
//			try {
//				lm.requestLocationUpdates("gps", 600, 1, locationListener); //was 60000
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//			if (l != null) {
//				printSaveLocation(l,true);
//			}
			break;
		default:
			break;
		}
	}

	public void uglyfy(View view){
		TextView tv = new TextView(this);
		tv.setBackgroundColor(Color.GREEN);
		tv.setText("In case you forgot: Hagen is great.");
		setContentView(tv);
	}

}