package com.finalhack.dpadsays;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Util {
	
	//Get the high score from shared preferences
    public static int getHighScore(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        int highScore = sharedPreferences.getInt("highScore", 0);

        return highScore;
    }
    
    //Set the high score in shared preferences
    public static void setHighScore(int highScore, Context context)
    {
		Editor sharedPreferencesEditor = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit();
		sharedPreferencesEditor.putInt("highScore", highScore);
		sharedPreferencesEditor.commit();
    }
}
