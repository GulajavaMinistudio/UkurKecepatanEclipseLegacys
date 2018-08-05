package gulajava.speedcepat;


import android.app.Activity;
import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.content.Context;

import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import android.media.MediaPlayer;

import android.view.MenuItem;
import android.view.Menu;
import android.content.Intent;

import gulajava.speedcepat.CekGPSNet;
import gulajava.speedcepat.HalAwalBaru;






public class Kecepatan extends Activity {
	
	
	private TextView tekskmh,teksatuan = null;
	private ImageView gambarstatus = null;
	
	
	//metode baru
	private static String statusGPS = null;
	private static String statusInternet = null;
	private boolean isInternet = false;
	private boolean isNetworkNyala = false;
	private boolean isGPSNyala = false;
	private CekGPSNet cekGpsNet = null;	
	private Location location = null;
	private static Location lokasi = null;
	private static double latitude;
	private static double longitude;
	
	
	// The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute
 
    private LocationManager lokasimanager = null;
	
    private double cepat,cepatkmh,cepatmph,cepatknot = 0;
	private double roundkmh,roundmph,roundknot = 0;
	
	
	//listener
	private LocListenerNetwork locListNetwork = null;
	private LocListenerGPS locListGPS = null;
	//task
	private CepatTask cepatTask;
	
	//aksibar
	private ActionBar aksibar;
	
	
	//pindah tipe kecepatan
	private static int kodeCepatan = 0;
	
	public static final int TAG_KMH = 77;
	public static final int TAG_MPH = 78;
	public static final int TAG_KNOT = 79;
	
	public static final String STR_KMH = "km/jam";
	public static final String STR_MPH = "mil/jam";
	public static final String STR_KNT = "knot";
	public static final String TAG_BATASCEPATAN = "bataskecepatan";
	private static double bataskecepatan = 0;
	
	private Button tombolkmh,tombolmph,tombolknot = null;
	
	//bar kecepatan
    private LayoutParams barparams;
	private LayoutParams barparamsbg = null;
    private LinearLayout layoutbar,layoutbarbg = null;
    private static final double MAX_SPEED = 180;
    private static double intbartampil,intbarbg = 0;
	
	
	private Bundle bundel;
	
	private MediaPlayer mediaplayer = null;
	private TextView teksstatus = null;
	
	private static final String TAG_AMAN = "AMAN";
	private static final String TAG_BAHAYA = "BAHAYA";
	
	private Intent intent;
	
	
	
	
	
	
	
	
	//KETIKA FRAGMENT DIBUAT
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);	
		setContentView(R.layout.lajukecepatan);
		
		aksibar = Kecepatan.this.getActionBar();
		aksibar.setDisplayHomeAsUpEnabled(true);
		aksibar.setTitle("Besar Kecepatan");
		
		bundel = Kecepatan.this.getIntent().getExtras();
		bataskecepatan = bundel.getDouble(TAG_BATASCEPATAN);
		
		Log.w("BATAS KECEPATAN KECEPATAN.CLS", "batasnya " + bataskecepatan);
		
		
		tombolkmh = (Button) findViewById (R.id.tombolkph);
		tombolkmh.setOnClickListener(listenertombol);
		tombolmph = (Button) findViewById (R.id.tombolmph);
		tombolmph.setOnClickListener(listenertombol);
		tombolknot = (Button) findViewById (R.id.tombolknot);	 	
		tombolknot.setOnClickListener(listenertombol);
		layoutbar = (LinearLayout) findViewById (R.id.barkecepatan);
		layoutbarbg = (LinearLayout) findViewById (R.id.barkecepatanbg);
				
		tekskmh = (TextView) findViewById(R.id.tekskmh);
		tekskmh.setText("0.0");
		teksatuan = (TextView)findViewById(R.id.teksatuan);
		teksstatus = (TextView) findViewById (R.id.teksstatuscepatan);
		//inisialisasi awal
		teksstatus.setText(TAG_AMAN);
		teksstatus.setTextColor(Kecepatan.this.getResources().getColor(R.color.hijaunow));
		
		gambarstatus = (ImageView) findViewById (R.id.gambarstatus);
		gambarstatus.setImageDrawable(Kecepatan.this.getResources().getDrawable(R.drawable.ikonaman));
		
		
		//listener lokasi
		locListNetwork = new LocListenerNetwork();
		locListGPS = new LocListenerGPS();		
		
				
		//inisialisasi media player
		mediaplayer = MediaPlayer.create(Kecepatan.this, R.raw.alertalarm);
		mediaplayer.setOnCompletionListener(listenermedia);
		

		//cek status gps
		cekStatusGPSpeed();

		//setel tipe kecepatan awal
		kodeCepatan = TAG_KMH;	
		setelSatuan(kodeCepatan);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()) {
		
		case android.R.id.home :
			
			intent = new Intent(Kecepatan.this,HalAwalBaru.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			Kecepatan.this.startActivity(intent);
			Kecepatan.this.finish();
			
			return true;
		}
		
		return false;
	}
	
	
	

	
	@Override
	public void onResume() {
		super.onResume();
		cekStatusGPSpeed();
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (lokasimanager != null) {
			lokasimanager.removeUpdates(locListNetwork);
			lokasimanager.removeUpdates(locListGPS);
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
	public void onDestroy() {
		super.onDestroy();
		mediaplayer.stop();
		mediaplayer.release();
	}
	
	
	
	
	
	
	//CEK KONDISI GPS DAN JARINGAN
	private void cekStatusGPSpeed() {
		
		lokasimanager = (LocationManager) Kecepatan.this.getSystemService(Context.LOCATION_SERVICE);
		
		cekGpsNet = new CekGPSNet(Kecepatan.this);
		isInternet = cekGpsNet.cekStatsInternet();
		isNetworkNyala = cekGpsNet.cekStatsNetwork();
		isGPSNyala = cekGpsNet.cekStatsGPS();
		
		statusInternet = cekGpsNet.getKondisiNetwork(isInternet, isNetworkNyala);
		statusGPS = cekGpsNet.getKondisiGPS(isGPSNyala);
		
		Log.w("STATUS GPS INTERNET", "GPS " + statusGPS + " INTERNET " + statusInternet);
		
		if (statusInternet == CekGPSNet.TAG_NYALA) {
			
			lokasimanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES , locListNetwork);
			Log.w("Network", "Network");
			
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
			
			lokasimanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES , locListGPS);
			Log.w("GPS", "GPS");
			
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
	public class LocListenerNetwork implements LocationListener {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			if (location != null) {
				
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				
				if (location.hasSpeed()) {
					
					lokasi = location;
					//jalankan task hitung kecepatan
					cepatTask = new CepatTask();
					cepatTask.execute();
				}
				else {
					//setel kecepatan 0
					tekskmh.setText("0.0");
					cepatkmh = 0;
					cekKecepatanBatas(bataskecepatan, cepatkmh);
				}				
			}
			else {
				//setel kecepatan 0
				tekskmh.setText("0.0");
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
	
	
	//LISTENER LOKASI UNTUK GPS
	public class LocListenerGPS implements LocationListener {

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			if (location != null) {
				
				lokasimanager.removeUpdates(locListNetwork);
				
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				
				if (location.hasSpeed()) {
					
					lokasi = location;
					//jalankan task hitung kecepatan
					cepatTask = new CepatTask();
					cepatTask.execute();
				}
				else {
					//setel kecepatan 0
					tekskmh.setText("0.0");
					cepatkmh = 0;
					cekKecepatanBatas(bataskecepatan, cepatkmh);
				}								
			}
			else {
				tekskmh.setText("0.0");
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
		
		
	//FUNGSI MENGHITUNG KECEPATAN
	private void hitungCepatan(Location lokasis) {
		
		cepat = lokasis.getSpeed();
		
		if (cepat != 0) {
			cepatkmh = (cepat * 3600) / 1000;
			cepatmph = (cepat * 2.2369);	
			cepatknot = (cepat * 1.9438);
			
			roundkmh = (double) Math.round(cepatkmh * 10) / 10;
			roundmph = (double) Math.round(cepatmph * 10) / 10;
			roundknot = (double) Math.round(cepatknot * 10) / 10;
			
			Log.d("KECEPATAN", "" + roundkmh + " " +  roundmph + " " + roundknot);
		}
		else {
			cepatkmh = 0;
			cepatmph = 0;
			cepatknot = 0;
		}
	}



	public class CepatTask extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			hitungCepatan(lokasi);
			
			return null;
		}
		
		
		protected void onPostExecute(Void unused) {
			super.onPostExecute(unused);			
			
			//Toast.makeText(Kecepatan.this, "" + cepatkmh, Toast.LENGTH_SHORT).show();
			//setel nilai kecepatan berdasarkan kode kecepatan , set ke textview
			setelCepatan(kodeCepatan);
			//setel status kecepatan
			cekKecepatanBatas(bataskecepatan, cepatkmh);
		}		
	}
		
	
	
	
	//LISTENER TOMBOL
	View.OnClickListener listenertombol = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch(v.getId()) {
			
			case R.id.tombolkph :
				
				//setel tipe kecepatan
				kodeCepatan = TAG_KMH;				
				//ubah teks satuan
				setelSatuan(kodeCepatan);
				//ubah warna tombol
				tombolkmh.setTextColor(Kecepatan.this.getResources().getColor(R.color.birujam));
				tombolmph.setTextColor(Kecepatan.this.getResources().getColor(R.color.abubg));
				tombolknot.setTextColor(Kecepatan.this.getResources().getColor(R.color.abubg));
				
				break;
				
			case R.id.tombolmph :
				
				//setel tipe kecepatan
				kodeCepatan = TAG_MPH;				
				//ubah teks satuan
				setelSatuan(kodeCepatan);
				//ubah warna tombol
				tombolkmh.setTextColor(Kecepatan.this.getResources().getColor(R.color.abubg));
				tombolmph.setTextColor(Kecepatan.this.getResources().getColor(R.color.birujam));
				tombolknot.setTextColor(Kecepatan.this.getResources().getColor(R.color.abubg));
								
				break;
				
			case R.id.tombolknot :
				
				//setel tipe kecepatan
				kodeCepatan = TAG_KNOT;				
				//ubah teks satuan
				setelSatuan(kodeCepatan);
				//ubah warna tombol
				tombolkmh.setTextColor(Kecepatan.this.getResources().getColor(R.color.abubg));
				tombolmph.setTextColor(Kecepatan.this.getResources().getColor(R.color.abubg));
				tombolknot.setTextColor(Kecepatan.this.getResources().getColor(R.color.birujam));
								
				break;
			}
		}
	};
	
	
	
	//SETEL TEKS SATUAN
	private void setelSatuan(int tipesatuan) {
		
		switch (tipesatuan) {
		
		case TAG_KMH :
			
			teksatuan.setText(STR_KMH);
			
			break;
			
		case TAG_MPH :
			
			teksatuan.setText(STR_MPH);
			
			break;
		
		case TAG_KNOT :
			
			teksatuan.setText(STR_KNT);
			
			break;		
		}
	}
	
	
	//SETEL TEKS KECEPATAN
	private void setelCepatan(int tipecepatan) {
		
		switch(tipecepatan) {
		
		case TAG_KMH :
			
			if (cepatkmh != 0) {
				tekskmh.setText("" + roundkmh);
			}
			else {
				tekskmh.setText("0.0");
			}
								
			break;
					
		case TAG_MPH :
			
			if (cepatmph != 0) {
				tekskmh.setText("" + roundmph);
			}
			else {
				tekskmh.setText("0.0");
			}
						
			break;
		
		case TAG_KNOT :
			
			if (cepatknot != 0) {
				tekskmh.setText("" + roundknot);
			}
			else {
				tekskmh.setText("0.0");
			}
			
			break;		

		}				
		
		//setel ukuran bar
		setelUkuranBar(cepatkmh);
	}
	
	
	
	//SETEL UKURAN BAR
	private void setelUkuranBar(double cepatKMH) {
		
		intbartampil = cepatKMH;
		
		if (intbartampil >= 0 && intbartampil < MAX_SPEED) {
			
			intbarbg = MAX_SPEED - intbartampil;
			
		}
		else if (intbartampil < 0) {
			intbartampil = 0;
			intbarbg = 180;
		}
		else if (intbartampil >= MAX_SPEED) {
			intbartampil = MAX_SPEED;
			intbarbg = 0;
		}
		
		
		//setel ukuran bar
		barparams = (LayoutParams) layoutbar.getLayoutParams();
		barparamsbg = (LayoutParams) layoutbarbg.getLayoutParams();
		barparams.weight = (float) intbartampil;
		barparamsbg.weight = (float) intbarbg;
		
		layoutbar.setLayoutParams(barparams);
		layoutbarbg.setLayoutParams(barparamsbg);		
		
		
	}
	
	
	
	//SETEL PENGINGAT BATAS KECEPATAN
	private void cekKecepatanBatas(double cepatKMHbatas, double cepatKMHSkrg) {
		
		
		if (cepatKMHSkrg < cepatKMHbatas) {
			
			//matikan alarm jika menyala
			matikanAlarm();
			
			//ubah warna grafis jadi hijau
			layoutbar.setBackgroundColor(Kecepatan.this.getResources().getColor(R.color.hijaunow));
						
			//ubah gambar bahaya jadi aman
			gambarstatus.setImageDrawable(Kecepatan.this.getResources().getDrawable(R.drawable.ikonaman));
			
			//ganti teks bahaya jadi aman
			teksstatus.setText(TAG_AMAN);
			teksstatus.setTextColor(Kecepatan.this.getResources().getColor(R.color.hijaunow));
		}
		else {
			
			//nyalakan alarm tanda bahaya
			nyalakanAlarm();
			
			//ubah warna grafis jadi merah
			layoutbar.setBackgroundColor(Kecepatan.this.getResources().getColor(R.color.merahnow));
			
			//ubah gambar aman jadi bahaya
			gambarstatus.setImageDrawable(Kecepatan.this.getResources().getDrawable(R.drawable.ikonbahaya));
			
			//ganti teks aman jadi bahaya
			teksstatus.setText(TAG_BAHAYA);
			teksstatus.setTextColor(Kecepatan.this.getResources().getColor(R.color.merahnow));
		}
	}
	
	
	
	//MATIKAN ALARM TANDA BAHAYA
	private void matikanAlarm() {
		
		if (mediaplayer.isPlaying() == true) {
			//matikan media player
			mediaplayer.pause();
		}
		else {
			//tidak melakukan apa apa
		}				
	}
	
	
	//NYALAKAN ALARM TANDA BAHAYA
	private void nyalakanAlarm() {
		
		if (mediaplayer.isPlaying() == false) {
			//nyalakan alarm bahaya
			mediaplayer.start();
		}
		else {
			//tidak melakukan apa apa
		}		
	}
	
	
	//JIKA MEDIA PLAYER SUDAH SELESAI, CEK APAKAH KECEPATAN MASIH JALAN ATAU TIDAK
	MediaPlayer.OnCompletionListener listenermedia = new MediaPlayer.OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			
			//setel status kecepatan
			cekKecepatanBatas(bataskecepatan, cepatkmh);
			
		}
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
