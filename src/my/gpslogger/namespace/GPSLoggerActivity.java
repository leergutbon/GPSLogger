/* 
 *
 * Data: GPSLoggerActiviy.java Author: Hagen Lauer
 * Date: 15.Dec.11 Version: v0.2
 *
 * This Application here is the "core" of my GPS Logger.
 * As you may have noticed, there is a whole lot of stuff that Android generates.
 * This auto generated content needs some changes here and modifications there,
 * I do not think that it is my job to especially comment these things given by 
 * eclipse, but I will try to mark some changes.
 * 
 * 
 * Now to GPSLoggerActivity.java:
 * It is the Activity itself, meaning: This .java will be executed and contains 
 * the biggest part of the internal logic and functions.
 *
 */





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

public class GPSLoggerActivity extends Activity 
{
	View tv_longitude; /* Text Views declared here */
	View tv_latitude; 
	View tv_altitude;
	View status;
	EditText target; /* Text Bars (inputs) declared */
	EditText descr;
	Button bat; /* finally this is a Button, which i declared */
	FileOutputStream fOut; /* the output stream for the file */
	BufferedOutputStream bOut; /* And a BufferedStream ... always nice to have */ 
	LocationManager lm; /* the Location manager */
	boolean write = false; /* a flag, later in use to determine when I want to write or not */
	
	/* Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState); /*Android specific construct, see class Activity*/
		setContentView(R.layout.main); /*this here sets the Content as given in the R.Layout, view the Layout folder*/
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); /* initialize the manager */
		tv_longitude = this.findViewById(R.id.longitude); /* assign an element to the declared types */
		tv_latitude = this.findViewById(R.id.latitude);   
		tv_altitude = this.findViewById(R.id.altitude);
		status = (TextView) this.findViewById(R.id.status); /* assigning, cast is needed due the return value is View */
		target = (EditText) this.findViewById(R.id.editText1);
		((TextView)status).setText("debugmode"); /*this is where the lowes view comes from*/
		try 
		{
			/*
			 * Now we start using what is so great about Android:
			 * The Managers.
			 * We request Location updates
			 * From provider "gps", with two rates, 600 is the time, 1 is the change
			 * and locationListener (defined below) as the reciever to handle these
			 * changes (Locations as we think in objects)
			 * 
			 * Surrounded by a try catch block, but this should work correctly anyways
			 */
			lm.requestLocationUpdates("gps", 600, 1, locationListener); /*was 60000*/
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		/*Manually getting a Location from the Manager, since the Listener might not be able to get them
		 *(if there is no data, the last known Location is given, listener wont recognize that) 
		 */
		Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (l != null) 
		{
			printSaveLocation(l,false); /*false is the flag, indicating that the app should not write this to a file
			 							 *but on the screen instead
			 							 */
		}
	}

	void printSaveLocation(Location l, boolean write) 
	{ /*Give it a Location and the flag*/
		((TextView) tv_longitude).setText("Lon: "+String.valueOf(l.getLatitude())); /*set the values to the display*/
		((TextView) tv_latitude).setText("Lat: "+String.valueOf(l.getLongitude()));
		((TextView) tv_altitude).setText("Alt: "+String.valueOf(l.getAltitude()));
		//l.getAltitude();
		if(write)
		{ /*flag is true means write to file*/
			try 
			{
				/*
				 * Write the file in the .gpx format, one line per write
				 */
				bOut.write(("\t"+"\t"+"<trkpt lat=\""+String.valueOf(l.getLatitude())+"\" lon=\""+String.valueOf(l.getLongitude())+"\" alt=\""+String.valueOf(l.getAltitude())+"\"></trkpt>"+"\n").getBytes());
				bOut.flush();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/*
	 * This Method is called once per "file" open
	 * it basicly creates the header of a .gpx file
	 * and adding out description tag to it
	 */
	void printProlog()
	{
		descr = (EditText)this.findViewById(R.id.editText2); /*assign this input field*/
		status = (TextView)this.findViewById(R.id.status); /*can be used as an output for our file*/
		((TextView)status).setText("PrologWritten!"); /*short debug info, will not be displayed for too long ...*/
		try 
		{
			/*
			 * write the following text
			 */
			bOut.write(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"+"\n").getBytes());
			bOut.write(("<gpx creator=\"@Author\" description=\""+descr.getText()+"\" version=\"alpha\">"+"\n").getBytes());
			bOut.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	/*
	 * The Location listener needs to be implemented on creation
	 * We need to specify the Methods:
	 * 
	 * onLocationChanged():
	 * 	Most important Method for us! Is called whenever the Manager recognizes changes
	 * 
	 * onProviderEnabled() / onProviderDisabled:
	 * 	can be used to display the user that he maybe hasn't or has enabled his gps device
	 * 
	 * onStatusChanged():
	 * 	not used, can be useful if you want to do anything with the status of the Listener/Manager
	 *
	 */
	private final LocationListener locationListener = new LocationListener() 
	{
		public void onLocationChanged(Location l) 
		{
			printSaveLocation(l, write); /* if the location is changed: display and write it */
		}

		@Override
		public void onProviderDisabled(String arg0) 
		{
			((TextView) tv_latitude).setText("ProviderDisabled"); /* its very self explaining isnt it? */
			((TextView) tv_longitude).setText("ProviderDisabled");
			((TextView) tv_altitude).setText("ProviderDisabled");
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) 
		{
			((TextView) tv_latitude).setText("ProviderEnabled"); /* This text will only shortly or even never appear
			 													  * since you will get a location changed as soon as
			 													  * the provider is enabled
			 													  */
			((TextView) tv_longitude).setText("ProviderEnabled");
			((TextView) tv_altitude).setText("ProviderDisabled");
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) 
		{
			// TODO Auto-generated method stub
			/*UNUSED*/

		}
	};
	boolean flicker = false; /* the magic flicker, it is just used to synch the record and stop buttons */
	/*
	 * the click handler being called ... on click
	 * responds to the two buttons record and stop
	 * for more class info, please view the api
	 * Android is almost overloaded with information
	 * and classes
	 */
	public void myClickHandler(View view)
	{
		/*switch determins which button has been pressed*/
		switch (view.getId()) 
		{
		case R.id.button: /* the stop button! */
			if(!flicker)
			{
				break;
			} /* flick of the switch ...*/
			write = false; /*show dont write, important for the listener*/
			status = (TextView)this.findViewById(R.id.status); /*set this as an output*/
			try 
			{
				bOut.write(("\t"+"</trkseq>"+"\n"+"</gpx>"+"\n").getBytes()); /*write file end of the .gpx*/
				bOut.flush();
				((TextView)status).setText("written!"); /*self speaking*/
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//finish(); // fraglich, man sollte eigentlich die nächste datei screiben können.
			flicker = false; /* allow the press of the record button*/
			//System.exit(1);
			break;
		case R.id.button1:
			if(flicker)
			{
				break;
			} /* can only run if the record is stopped / initialized */
			flicker = true; /*allow the press of the stop button*/
			status = (TextView)this.findViewById(R.id.status);
			tv_longitude = this.findViewById(R.id.longitude); /* assign the output textfields */
			tv_latitude = this.findViewById(R.id.latitude);
			tv_altitude = this.findViewById(R.id.altitude);
			target = (EditText)this.findViewById(R.id.editText1); /* assign the input textfields*/
			descr = (EditText)this.findViewById(R.id.editText2);
			String outputfile = target.getText().toString(); /* the string to be used in the filestream */
			((TextView)status).setText("running, no file created!"); /* debug info */
			//LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
			
			try 
			{
				//((TextView)status).setText(Environment.getExternalStorageDirectory().toString());
				File file = Environment.getExternalStorageDirectory(); /* very important, wont save anything to sd without it*/
				File f = new File(file, outputfile+".gpx"); /* append the .gpx to the sd card directory */
				bOut = new BufferedOutputStream(new FileOutputStream(f)); /* creat the output stream */
				//bOut = new BufferedOutputStream(openFileOutput(outputfile+".gpx", MODE_WORLD_READABLE));
				printProlog(); /* just print the .gpx header */
				bOut.write(("\t"+"<trkseg>"+"\n").getBytes()); /* open the track segment block */
				bOut.flush();
				((TextView)status).setText("running"); /* once more a debug info */
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			write = true; /* set write to true, and it will be written */
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
	
	/*
	 * Just playing with a different view here, could be cool in the future
	 */
	public void uglyfy(View view)
	{
		TextView tv = new TextView(this);
		tv.setBackgroundColor(Color.GREEN);
		tv.setText("Test");
		setContentView(tv);
	}

}