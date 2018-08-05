package gulajava.speedcepat;

import java.lang.reflect.Field;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.Toast;

import android.content.Intent;


import gulajava.speedcepat.HalAwalBaru;
import gulajava.speedcepat.database.*;
import gulajava.speedcepat.Kecepatan;









public class SetelKecepatan extends Activity {
	
	private Button tombolsimpan;
	private Spinner spinsatuan;
	private ArrayAdapter<String> adaptersatuan;
	private EditText editcepatan;
	private TextView teksbantuancepatan;
	private static String[] arraysatuan = null;
	private ActionBar aksibar = null;
	
	//pesan bantuan dan batas kecepatan
	private static String bantuancepatan = null;
	private static final double maxkph = 180;
	private static final double minkph = 10;
	//private static final double minkph = 1;
	private static final double maxmph = 112;
	private static final double minmph = 6;
	private static final double maxknot = 207;
	private static final double minknot = 12;
	private static double maxspd = 0;
	private static double minspd = 0;
	
	
	//ambil dan simpan data dari database
	private Database database = null;
	private Uri urisimpan = null;
	private static final String[] projeksiDB = {Database.KEY_BARIS,Database.KEY_CEPATMAX,Database.KEY_TIPECEPAT}; 
	private Cursor kursordata = null;
	private double kecepatanbatas,kecepatanbatasbaru = 0;
	private String kecepatanbarustr = null;
	private int tipekecepatan = 0;
	private ContentValues kontenupdate = null;
	
	private Intent intent;
	
	
	
	
	
	
	
	
	//DIPANGGIL PERTAMA KALI
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.setelkecepatan);
		munculMenuAction (SetelKecepatan.this);
		
		aksibar = SetelKecepatan.this.getActionBar();
		aksibar.setDisplayHomeAsUpEnabled(true);
		aksibar.setTitle("Setel Batas Kecepatan");
		
		database = new Database(SetelKecepatan.this);
		database.open();
		urisimpan = Uri.parse(KontenProvider.KONTENURI_UMUM + "/" + Database.NAMA_TABELCEPAT + "/" + Database.BASE_ROWID);
		
		
		
		arraysatuan = SetelKecepatan.this.getResources().getStringArray(R.array.arraycepatan);
		
		tombolsimpan = (Button) findViewById (R.id.tombolsimpanbatas);
		tombolsimpan.setOnClickListener(listenertombol);
		spinsatuan = (Spinner) findViewById (R.id.spinsatuan);
		editcepatan = (EditText) findViewById (R.id.editcepatan);
		teksbantuancepatan = (TextView) findViewById (R.id.teksbatascepatan);
		
		adaptersatuan = new ArrayAdapter<String> (SetelKecepatan.this,android.R.layout.simple_spinner_item,arraysatuan);
		adaptersatuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinsatuan.setAdapter(adaptersatuan);
		spinsatuan.setOnItemSelectedListener(listenerspin);
		
		ambilData();
		
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
			
			intent = new Intent(SetelKecepatan.this,HalAwalBaru.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			SetelKecepatan.this.startActivity(intent);
			SetelKecepatan.this.finish();
			
			return true;
		}
	
		return false;
	}
	
	
	
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		database.close();
	}
	
	
	//ambil data dari database
	private void ambilData() {
		
		kursordata = SetelKecepatan.this.getContentResolver().query(urisimpan, projeksiDB, null, null, null);
		
		if (kursordata != null) {
			kursordata.moveToFirst();
			kecepatanbatas = kursordata.getDouble(kursordata.getColumnIndexOrThrow(Database.KEY_CEPATMAX));
			tipekecepatan = kursordata.getInt(kursordata.getColumnIndexOrThrow(Database.KEY_TIPECEPAT));
		}
		
		switch(tipekecepatan) {
		case Kecepatan.TAG_KMH :
			spinsatuan.setSelection(0);
			break;
		case Kecepatan.TAG_MPH :
			spinsatuan.setSelection(1);
			break;
		case Kecepatan.TAG_KNOT :
			spinsatuan.setSelection(2);
			break;
		}
		
		int kecepatantampil = (int) kecepatanbatas;
		editcepatan.setText("" + kecepatantampil);
		
	}
	
	
	//simpan data ke database
	private void simpanData() {
		
		kecepatanbarustr = editcepatan.getText().toString();
		
		if (kecepatanbarustr.length() > 0) {
			
			kecepatanbatasbaru = Double.valueOf(kecepatanbarustr);
			
			if (kecepatanbatasbaru >= minspd && kecepatanbatasbaru <= maxspd) {
				
				kontenupdate = new ContentValues();
				kontenupdate.put(Database.KEY_CEPATMAX, kecepatanbatasbaru);
				kontenupdate.put(Database.KEY_TIPECEPAT, tipekecepatan);
				SetelKecepatan.this.getContentResolver().update(urisimpan, kontenupdate, null, null);
				kontenupdate.clear();
				Toast.makeText(SetelKecepatan.this, "Batas kecepatan disimpan", Toast.LENGTH_SHORT).show();
				
			}
			else {
				Toast.makeText(SetelKecepatan.this, "Masukkan nilai kecepatan dengan benar", Toast.LENGTH_SHORT).show();
			}			
		}
		else {
			Toast.makeText(SetelKecepatan.this, "Masukkan nilai kecepatan dengan benar", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	
	
	
	
	
	AdapterView.OnItemSelectedListener listenerspin = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int posisi,long arg3) {
			// TODO Auto-generated method stub
			
			switch(posisi) {
			case 0 :				//KMH
				
				//setel pesan bantuan
				bantuancepatan = SetelKecepatan.this.getResources().getString(R.string.bantuanhelpbatas);
				teksbantuancepatan.setText(bantuancepatan);
				
				//setel batas maksimum dan minimum kecepatan
				maxspd = maxkph;
				minspd = minkph;
				tipekecepatan = Kecepatan.TAG_KMH;
				
				break;
					
			case 1 :				//MPH
				
				//setel pesan bantuan
				bantuancepatan = SetelKecepatan.this.getResources().getString(R.string.bantuanhelpbatasmph);
				teksbantuancepatan.setText(bantuancepatan);
				
				//setel batas maksimum dan minimum kecepatan
				maxspd = maxmph;
				minspd = minmph;
				tipekecepatan = Kecepatan.TAG_MPH;
				
				break;
				
			case 2 :				//KNOT
				
				//setel pesan bantuan
				bantuancepatan = SetelKecepatan.this.getResources().getString(R.string.bantuanhelpbatasknot);
				teksbantuancepatan.setText(bantuancepatan);
				
				//setel batas maksimum dan minimum kecepatan
				maxspd = maxknot;
				minspd = minknot;
				tipekecepatan = Kecepatan.TAG_KNOT;
								
				break;
			}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub			
		}
	};
	
	
	
	View.OnClickListener listenertombol = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			sembunyikeyboard (SetelKecepatan.this,v);
			simpanData();
		}
	};
	
	
	
	
	
	
	
	
	
	
	

	//SEMBUNYIKAN KEYBOARD
	private static void sembunyikeyboard (Context context,View view) {
		InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
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
