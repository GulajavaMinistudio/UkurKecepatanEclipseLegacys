package gulajava.speedcepat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.os.AsyncTask;


import gulajava.speedcepat.HalAwalBaru;
import gulajava.speedcepat.database.Database;




public class Loading extends Activity {
	
	
	Runnable splash = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			Loading.this.startActivity(intent);
			Loading.this.finish();
		}
	};
	
	
	private Intent intent;
	private Handler handler;
	private Database database = null;
	private BuatDatabase buatDb = null;
	
	
	
	
	
	
	//DIPANGGIL PERTAMA KALI
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.loading);
		
		
		intent = new Intent(Loading.this,HalAwalBaru.class);
		handler = new Handler();
		
		database = new Database(Loading.this);
		buatDb = new BuatDatabase();
		buatDb.execute();
		
		
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		database.close();
	}
	
	
	
	
	
	
	
	//BACKGROUND TASK MASUKKAN DATA KE DATABASE
	public class BuatDatabase extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			database.open();
			
			return null;
		}
		
		
		protected void onPostExecute(Void unused) {
			super.onPostExecute(unused);
			handler.postDelayed(splash, 1000);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
