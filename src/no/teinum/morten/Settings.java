package no.teinum.morten;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	public static boolean getPhysicalFeedback(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("physicalfeedback", true);
	}

	public static boolean getBanner(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("banner", true);
	}

	public static float getPitch(Context context){
		return Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(context)
				.getString("pitch", "1.0"));
	}

}
