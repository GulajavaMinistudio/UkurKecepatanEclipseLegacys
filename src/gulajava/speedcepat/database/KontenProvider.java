package gulajava.speedcepat.database;


import android.content.ContentValues;
import android.content.ContentProvider;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import gulajava.speedcepat.database.Database;




public class KontenProvider extends ContentProvider {
	
	private Database database;
	
	//kode uri matcher untuk hasil mencocokkan uri, jika ada uri yang cocok
	private static final int ID_MULTIDATA = 1;
	private static final int ID_SATUDATA = 2;
	
	//otoritas untuk mengakses konten provider dan untuk manifest
	private static final String OTORITAS = "gulajava.speedcepat.database";
	
	//alamat dasar untuk membedakan antar tipe tabel
	private static final String BASE_PATH_UMUM = "optabelumum";
	
	//uri dasar untuk jenis operasi tabel
	public static final Uri KONTENURI_UMUM = Uri.parse("content://" + OTORITAS + "/" + BASE_PATH_UMUM);
	
	
	//SETEL URI MATCHER UNTUK MENCOCOKKAN URI DAN JENIS OPERASI TABEL
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		//ambil/hapus/update semua data dari tabel. butuh nama tabel , dalam bentuk teks, beri kode /*
		uriMatcher.addURI(OTORITAS, BASE_PATH_UMUM + "/*", ID_MULTIDATA);
		
		//ambil/hapus/update satu data dari tabel butuh nama tabel /* dan id /#
		uriMatcher.addURI(OTORITAS, BASE_PATH_UMUM + "/*" + "/#", ID_SATUDATA);
	};
	
	
	//nilai untuk ambil data
	private static String rowID = null;
	private static String namatabelsatu = null;
	private static Cursor kursorsatu = null;
	
	
	
	
	
	
	

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		
		database = new Database(KontenProvider.this.getContext());
		database.open();
		
		return false;
	}

	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		
		switch(uriMatcher.match(uri)) {
		
		case ID_MULTIDATA :
			
			return null;
			
		case ID_SATUDATA :
			
			namatabelsatu = uri.getPathSegments().get(1); // 0 1 2
			rowID = uri.getPathSegments().get(2);
			kursorsatu = database.ambil1Data(namatabelsatu, projection, rowID);
			
			Log.w("OPERASI DB", "nama tabel " + namatabelsatu + " ,baris " + rowID);
			
			return kursorsatu;
		
		default :
			throw new IllegalArgumentException("Uri tidak diketahui" + uri);
		}
		
	}

	
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		
		switch(uriMatcher.match(uri)) {
		
		case ID_SATUDATA :
			
			namatabelsatu = uri.getPathSegments().get(1); // 0 1 2
			rowID = uri.getPathSegments().get(2);
			
			int updatecount = database.update1Cepatan(namatabelsatu, values, rowID);
			KontenProvider.this.getContext().getContentResolver().notifyChange(uri, null);
			
			Log.w("OPERASI DB", "nama tabel " + namatabelsatu + " ,baris " + rowID);
			
			return updatecount;
		
		default :
			throw new IllegalArgumentException("Uri tidak diketahui" + uri);
		}		
	}

}

















