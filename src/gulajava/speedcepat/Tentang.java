package gulajava.speedcepat;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;

import gulajava.speedcepat.HalAwalBaru;




public class Tentang extends Activity {
	
	
	private ActionBar aksibar;
	private Intent intent;
	
	
	
	
	//DIPANGGIL PERTAMA KALI
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.tentangbaru);
		
		aksibar = Tentang.this.getActionBar();
		aksibar.setDisplayHomeAsUpEnabled(true);
		
		intent = new Intent(Tentang.this,HalAwalBaru.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);		
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
	
		return true;
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		
		case android.R.id.home :
			
			Tentang.this.startActivity(intent);
			Tentang.this.finish();
			
			return true;
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
