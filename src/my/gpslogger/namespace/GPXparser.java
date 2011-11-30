package my.gpslogger.namespace;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



public class GPXparser {
	
	public static BufferedOutputStream getStream(String filename){
		try {
			return new BufferedOutputStream(new FileOutputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void printHeader(BufferedOutputStream out, String description){
		try {
			//write here
			out.flush();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void printClose(BufferedOutputStream out, String additionals){
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}
