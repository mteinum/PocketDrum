package no.teinum.morten;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class PocketDrumActivity extends Activity {
	
	private DrumView drumView;
	private SoundPool sp;
	private Vibrator vibrator;
	private SoundInfo[] sounds;
	
	private SoundInfo create(int id, int vibratorLength){
		return new SoundInfo(sp.load(this, id, 1), vibratorLength);
	}
	
	private AdView adView;
	private SharedPreferences prefs;
	private SharedPreferences.OnSharedPreferenceChangeListener listener;
	private IDrumStrategy drumStrategy;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initializeBannerListener();
        
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        sp = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        
        sounds = new SoundInfo[] {
        		create(R.raw.clap, 10),
        		create(R.raw.crash01, 10),
        		create(R.raw.crash02, 10),
        		create(R.raw.hi_hat_open, 3),
        		create(R.raw.ride, 3),
        		create(R.raw.rim_shot, 5),
        		create(R.raw.tom_medium, 15),
        		create(R.raw.low_tom, 15),
        };
        
        setContentView(R.layout.main);
        
        adView = new AdView(this, AdSize.BANNER, "a14f5913dec3ffb");
 
        
        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
        
     	layout.addView(adView);
 
        AdRequest adRequest = new AdRequest();
        // adRequest.addTestDevice("7D5F6BA997B3B132F2CC599E820DA92D"); // Test Android Device
            
        adView.loadAd(adRequest);
       
        drumView = new DrumView(this);
        layout.addView(drumView);       
        
        drumStrategy = new SlidingDrumStrategy(
        		drumView,
        		sounds,
        		sp,
        		getBaseContext(),
        		vibrator);
    }

	private void initializeBannerListener() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        listener = new SharedPreferences.OnSharedPreferenceChangeListener(){
        	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        	    // Implementation
        		android.util.Log.d("TAG", "key: " + key);
        		
        		if (key.equals("banner")){
        			if (prefs.getBoolean(key, true))
        			{
        				adView.setVisibility(View.VISIBLE);
        			}
        			else {
        				adView.setVisibility(View.GONE);
        			}
        		}
        	  }
        };
        
        prefs.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	protected void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		drumStrategy.Handle(event);
		
		return super.onTouchEvent(event);
	}
}
