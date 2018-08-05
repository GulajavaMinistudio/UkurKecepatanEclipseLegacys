package gulajava.speedcepat;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;




public class CekGPSNet {
	
	Context konteks = null;
	LocationManager lokasimanager = null;
	ConnectivityManager conmanager = null;
	NetworkInfo netinfo = null;
	boolean isInternet = false;
	boolean isNetworkNyala = false;
	boolean isGPSNyala = false;
	
	public static String TAG_NYALA = "NYALA";
	public static String TAG_MATI = "MATI";
	public String statusNetwork = "MATI";
	public String statusGPS = "MATI";
	
	
	
	
	public CekGPSNet(Context conteks) {
		this.konteks = conteks;
				
		isInternet = false;
		isNetworkNyala = false;
		isGPSNyala = false;
		statusNetwork = "MATI";
		statusGPS = "MATI";
		
	
		lokasimanager = (LocationManager) konteks.getSystemService(Context.LOCATION_SERVICE);
		conmanager = (ConnectivityManager) konteks.getSystemService(Context.CONNECTIVITY_SERVICE);
		
	}
	
	
	
	
	
	
	
	
	
	//cek status internet dan balikkan nilainya
	public boolean cekStatsInternet() {
		
		netinfo = conmanager.getActiveNetworkInfo();
		
		if (netinfo != null) {
			isInternet = netinfo.isConnected();
		}
		else {
			isInternet = false;
		}
		
		Log.w("TAG INTERNET CLASS", isInternet + "");
		
		return isInternet;
	}
	
	
	
	//cek status jaringan, apakah hidup atau ga
	public boolean cekStatsNetwork() {
		
		try {
			isNetworkNyala = lokasimanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			isNetworkNyala = false;
		}
		
		Log.w("TAG NETWORK CLASS", isNetworkNyala + "");
		
		return isNetworkNyala;
	}
	
	
	//cek status gps apakah hidup atau ga
	public boolean cekStatsGPS() {
		
		try {
			isGPSNyala = lokasimanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			isGPSNyala = false;
		}
		
		return isGPSNyala;
	}
	
	
	
	//setel status jaringan berdasarkan kondisi internet dan jaringan
	public String getKondisiNetwork(boolean statInet,boolean statNetw) {
		
		if (statInet == true && statNetw == true) {
			statusNetwork = TAG_NYALA;
		}
		else if (statInet == false && statNetw == false) {
			statusNetwork = TAG_MATI;
		}
		else {
			statusNetwork = TAG_MATI;
		}
		
		return statusNetwork;
	}
	
	
	//setel status gps berdasarkan kondisi gps
	public String getKondisiGPS(boolean statGPS) {
		
		if (statGPS == false) {
			statusGPS = TAG_MATI;
		}
		else {
			statusGPS = TAG_NYALA;
		}
		
		return statusGPS;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
