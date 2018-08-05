package gulajava.speedcepat.database;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import gulajava.speedcepat.Kecepatan;





public class Database {

	
	//membuat database
	public static final String NAMA_DATABASE = "dbcatering";
	private static final int VERSI_DATABASE = 2;
	public final Context konteks;
	public SQLiteDatabase mDb;
	//inisialisasi helper database
	public DatabaseHelper dbHelper = null;
	
	
	//KEY UNTUK TABEL KECEPATAN
	public static final String KEY_BARIS = "_id";
	public static final String KEY_CEPATMAX = "kecepatanmax";
	public static final String KEY_TIPECEPAT = "tipekecepatan";

	public static final String NAMA_TABELCEPAT = "tabelkecepatan";
	public static final String BUAT_TABELCEPAT = "CREATE TABLE " + NAMA_TABELCEPAT + " (" + KEY_BARIS + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CEPATMAX + " TEXT NOT NULL, " + KEY_TIPECEPAT + " TEXT NOT NULL" + ");";                     
	
	public ContentValues kontenawal = null;
	public static final double CEPATAWAL = 70;
	public static final String BASE_ROWID = "1";
	
	
	
	
	
	//BUAT KELAS HELPER UNTUK MEMBUAT DATABASE DAN TABEL
	public class DatabaseHelper extends SQLiteOpenHelper {
		
		public DatabaseHelper (Context context) {
			super(context, NAMA_DATABASE,null,VERSI_DATABASE);			
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(BUAT_TABELCEPAT);
			
			kontenawal = new ContentValues();
			kontenawal.put(KEY_CEPATMAX, CEPATAWAL);
			kontenawal.put(KEY_TIPECEPAT, Kecepatan.TAG_KMH);
			db.insert(NAMA_TABELCEPAT, null, kontenawal);
			kontenawal.clear();
			
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
			Log.w("HAPUS DATABASE", "Melakukan upgrade database, menghapus data dan tabel yang ada dan diganti yang baru");
			onCreate(db);			
		}
	}
	
	
	
	//konstruktor
	public Database (Context context) {
		this.konteks = context;
	}
	
	
	//buka database
	public Database open() throws SQLException {
		
		dbHelper = new DatabaseHelper(konteks);
		mDb = dbHelper.getWritableDatabase();
		return this;
	}
 	
	
	//tutup database
	public void close() {
		dbHelper.close();
	}
	
	
	
//--------------------------------------------------------------------------------------------------------------------------------
	
	
	//FUNGSI DATABASE
	
	//FUNGSI AMBIL SATU DATA DARI TABEL
	public Cursor ambil1Data (String namatabel,String[] kolom, String rowID) {
		
		Cursor kursorambil1 = mDb.query(true, namatabel, kolom, KEY_BARIS + "=" + rowID, null, null, null, null, null);
		
		if (kursorambil1 != null) {
			kursorambil1.moveToFirst();
		}
		
		return kursorambil1;
	}
	
	
	//FUNGSI UPDATE KECEPATAN
	public int update1Cepatan (String namatabel,ContentValues konten,String rowID) {
		
		return mDb.update(namatabel, konten, KEY_BARIS + "=" + rowID, null);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
