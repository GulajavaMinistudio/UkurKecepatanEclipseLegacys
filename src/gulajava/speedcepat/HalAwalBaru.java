package gulajava.speedcepat;

import java.lang.reflect.Field;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import android.content.Intent;
import android.database.Cursor;

import gulajava.speedcepat.Tentang;
import gulajava.speedcepat.CekGPSNet;
import gulajava.speedcepat.Kecepatan;
import gulajava.speedcepat.database.*;
import gulajava.speedcepat.SetelKecepatan;



public class HalAwalBaru extends Activity  {
	
	
	private ActionBar aksibar;
	private Button tombolcekecepatan,tombolsetelbatas;
	
	private Intent intent,intentbatascepatan,intententang;
	
	
	
	//cek lokasi
	//metode baru
	private static String statusGPS = null;
	private static String statusInternet = null;
	private boolean isInternet = false;
	private boolean isNetworkNyala = false;
	private boolean isGPSNyala = false;
	private CekGPSNet cekGpsNet = null;
	
	
	
	private Location location = null;
	private static double latitude;
	private static double longitude;
	
	// The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute
 
    private LocationManager lokasimanager = null;
	
	
	private LokasiListenerNetwork locListenerNetwork;
	private LokasiListenerGPS locListenerGPS;
		
	private TextView tekstatusGPS,tekstatuSeluler,tekslokasi = null;
	
	
	//ambil kecepatan dari database
	private Uri urikecepatan = null;
	private static final String[] projeksiDB = {Database.KEY_BARIS,Database.KEY_CEPATMAX,Database.KEY_TIPECEPAT}; 
	private Cursor kursorcepatan = null;
	private static int tipekecepatan = 0;
	private static double kecepatanbatas = 0;
	private Database database = null;
	private String labelcepatan = null;
	private String awalabelcepatan = null;
	private TextView teksbatascepatan = null;
	
	//konversi dari mph dan knot ke kmh
	private static final double mphtokph = 1.609;
	private static final double kntokph = 1.852;
	private static double hasilkonv = 0;
	
	
	
	
	
	
	
	
	//DIPANGGIL PERTAMA KALI
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.halamanawalbaru);
		munculMenuAction (HalAwalBaru.this);
		
		awalabelcepatan = HalAwalBaru.this.getResources().getString(R.string.ketbataskecepatan);
		
		isGPSNyala = false;
		isNetworkNyala = false;
		isInternet = false;
		
		aksibar = HalAwalBaru.this.getActionBar();
		aksibar.setTitle("Ukur Kecepatan");
		
		tombolcekecepatan = (Button) findViewById (R.id.tombolcekkecepatan);
		tombolcekecepatan.setOnClickListener(listener);
		tombolsetelbatas = (Button) findViewById (R.id.tombolsetelbatas);
		tombolsetelbatas.setOnClickListener(listener);
		teksbatascepatan = (TextView) findViewById (R.id.teksbatascepatan);
		tekstatusGPS = (TextView) findViewById (R.id.tekstatusGPS);
		tekstatuSeluler = (TextView) findViewById (R.id.tekstatuseluler);
		tekslokasi = (TextView) findViewById (R.id.tekslokasi);
				
		//inisialisasi database
		database = new Database(HalAwalBaru.this);
		database.open();
		urikecepatan = Uri.parse(KontenProvider.KONTENURI_UMUM + "/" + Database.NAMA_TABELCEPAT + "/" + Database.BASE_ROWID);
		ambilData();
		
			
		locListenerNetwork = new LokasiListenerNetwork();
		locListenerGPS = new LokasiListenerGPS();
		
		cekStatusGPS();
	}
	
	
	
	
	//ambil data dari database
	private void ambilData() {
		
		kursorcepatan = HalAwalBaru.this.getContentResolver().query(urikecepatan, projeksiDB, null, null, null);
		
		if (kursorcepatan != null) {
			kursorcepatan.moveToFirst();
			
			kecepatanbatas = kursorcepatan.getDouble(kursorcepatan.getColumnIndexOrThrow(Database.KEY_CEPATMAX));
			tipekecepatan = kursorcepatan.getInt(kursorcepatan.getColumnIndexOrThrow(Database.KEY_TIPECEPAT));
			
		}
		
		//ubah mph atau knot ke kph jika belum bernilai kph , dan disimpan ke dalam hasilkonv
		//berguna untuk batas kecepatan di halaman Kecepatan
		switch (tipekecepatan) {
		case Kecepatan.TAG_KMH :
			
			labelcepatan = Kecepatan.STR_KMH;
			hasilkonv = kecepatanbatas;
			
			break;
		case Kecepatan.TAG_MPH :
			
			labelcepatan = Kecepatan.STR_MPH;
			hasilkonv = (double) Math.round((kecepatanbatas * mphtokph) * 10) / 10;
			
			break;
		case Kecepatan.TAG_KNOT :
			
			labelcepatan = Kecepatan.STR_KNT;
			hasilkonv = (double) Math.round((kecepatanbatas * kntokph) * 10) / 10;
			
			break;
		}
		
		teksbatascepatan.setText(awalabelcepatan + " " + (int) kecepatanbatas + " " + labelcepatan);
		Log.w("NILAI KECEPATAN HASIL KONVERSI", "kecepatan " + hasilkonv);
	}
	
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (lokasimanager != null) {
			lokasimanager.removeUpdates(locListenerNetwork);
			lokasimanager.removeUpdates(locListenerGPS);
			lokasimanager = null;
		}
		
		statusGPS = null;
		statusInternet = null;
		isInternet = false;
		isNetworkNyala = false;
		isGPSNyala = false;
		cekGpsNet = null;
		database.close();
	}
	
	
	
	@Override
	public void onPause() {
		super.onPause();
		if (lokasimanager != null) {
			lokasimanager.removeUpdates(locListenerNetwork);
			lokasimanager.removeUpdates(locListenerGPS);
			lokasimanager = null;
		}
		
		statusGPS = null;
		statusInternet = null;
		isInternet = false;
		isNetworkNyala = false;
		isGPSNyala = false;
		cekGpsNet = null;		
	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		//cek status gps
		cekStatusGPS();
		
		//ambildata
		ambilData();
	}
	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		HalAwalBaru.this.getMenuInflater().inflate(R.menu.halawal, menu);
		
		return true;
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		
		case R.id.action_tentangs :
			
			intententang = new Intent(HalAwalBaru.this,Tentang.class);
			HalAwalBaru.this.startActivity(intententang);
			
			return true;
			
		case R.id.action_keluars :
			
			HalAwalBaru.this.finish();
			
			return true;
		}
	
		
		return false;
	}
	
	
	
	
	View.OnClickListener listener = new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			
			switch(v.getId()) {
			
			case R.id.tombolcekkecepatan :
				
				cekStatusGPS();
				
				if (statusInternet == CekGPSNet.TAG_NYALA && statusGPS == CekGPSNet.TAG_NYALA) {
					lokasimanager.removeUpdates(locListenerNetwork);
					lokasimanager.removeUpdates(locListenerGPS);
					intent = new Intent(HalAwalBaru.this,Kecepatan.class);
					intent.putExtra(Kecepatan.TAG_BATASCEPATAN, hasilkonv);
					
					HalAwalBaru.this.startActivity(intent);
				}
				else {
					Toast.makeText(HalAwalBaru.this, "GPS dan koneksi internet tidak menyala", Toast.LENGTH_SHORT).show();
				}
				
				
				break;
			
			case R.id.tombolsetelbatas :
				
				lokasimanager.removeUpdates(locListenerNetwork);
				lokasimanager.removeUpdates(locListenerGPS);
				intentbatascepatan = new Intent(HalAwalBaru.this,SetelKecepatan.class);
				HalAwalBaru.this.startActivity(intentbatascepatan);
				
				break;
			
			}
			
		}
	};
	
	
	
	
	//CEK KONDISI GPS DAN JARINGAN
	private void cekStatusGPS() {
		
		lokasimanager = (LocationManager) HalAwalBaru.this.getSystemService(Context.LOCATION_SERVICE);
				
		cekGpsNet = new CekGPSNet(HalAwalBaru.this);
		isInternet = cekGpsNet.cekStatsInternet();
		isNetworkNyala = cekGpsNet.cekStatsNetwork();
		isGPSNyala = cekGpsNet.cekStatsGPS();
		
		statusInternet = cekGpsNet.getKondisiNetwork(isInternet, isNetworkNyala);
		statusGPS = cekGpsNet.getKondisiGPS(isGPSNyala);
		
		Log.w("STATUS GPS INTERNET", "GPS " + statusGPS + " INTERNET " + statusInternet);
		
		if (statusInternet == CekGPSNet.TAG_NYALA) {
			
			lokasimanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locListenerNetwork);
			Log.w("Network", "Network");
			
			tekstatuSeluler.setText(CekGPSNet.TAG_NYALA);
			tekstatuSeluler.setTextColor(HalAwalBaru.this.getResources().getColor(R.color.warnanyala));
			
			if (lokasimanager != null) {
				location = lokasimanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					
					Log.w("TAG NETWORK", " " + latitude + " " + longitude);
				}
			}			
		}
		
		
		if (statusGPS == CekGPSNet.TAG_NYALA) {
			
			lokasimanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locListenerGPS);
			Log.d("GPS", "GPS");
			
			tekstatusGPS.setText(CekGPSNet.TAG_NYALA);
			tekstatusGPS.setTextColor(HalAwalBaru.this.getResources().getColor(R.color.warnanyala));
		
			if (lokasimanager != null) {
				location = lokasimanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					
					Log.w("TAG GPS", " " + latitude + " " + longitude);
				}
			}	
		}
	}
	
	
	
	
	
	
	//LISTENER LOKASI UNTUK SINYAL GSM DAN WIFI
	public class LokasiListenerNetwork implements LocationListener {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			
			if (location != null) {
				
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				
				tekslokasi.setText(latitude + ", " + longitude);
			}
			else {
				Toast.makeText(HalAwalBaru.this, "Gagal mengambil lokasi dari jaringan", Toast.LENGTH_SHORT).show();
			}
		}

		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub	
		}
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub	
		}
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub			
		}		
	}
	
	
	
	//LISTENER LOKASI UNTUK GPS
	public class LokasiListenerGPS implements LocationListener {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			
			if (location != null) {
				lokasimanager.removeUpdates(locListenerNetwork);
				
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				tekslokasi.setText(latitude + ", " + longitude);
			}
			else {
				Toast.makeText(HalAwalBaru.this, "Gagal mengambil lokasi dari GPS", Toast.LENGTH_SHORT).show();
			}
			
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub			
		}
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub			
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub	
		}		
	}
	
	
	//MENAMPILKAN MENU ACTION BAR
	private void munculMenuAction (Context context) {
		
		try {
			ViewConfiguration config = ViewConfiguration.get(context);
			Field menuKey = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
		
			if (menuKey != null) {
				menuKey.setAccessible(true);
				menuKey.setBoolean(config, false);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
