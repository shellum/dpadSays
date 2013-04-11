package com.finalhack.dpadsays;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

//Our thread class that takes care of appropriate delays and the flashing of sequence items
public class Sequencer extends AsyncTask<Integer, Integer, Integer>
{
	//Track associated sequence item data
	private SparseArray<SwitchButton> buttonMap = new SparseArray<SwitchButton>();
	
	//It is nice to have a beginning delay if the app is playing back a sequence
	//But not if the user is trying to match the next sequence item
	private boolean delayBeforeStart = false;
	
	private GameActivity gameActivity;
	
	//Save off the delay info
	public Sequencer(GameActivity context, boolean delayBeforeStart, SparseArray<SwitchButton> buttonMap)
	{
		this.gameActivity = context;
		this.delayBeforeStart = delayBeforeStart;
		this.buttonMap = buttonMap;
	}
	
	@Override
	protected Integer doInBackground(Integer... params)
	{		
		//Wait before playing a full sequence
		if (delayBeforeStart) sleep(2000);
		
		Log.d("Debug-log", "Start loop " + delayBeforeStart);
		
		//For each sequence item...
		for (Integer i : params)
		{
			if (gameActivity.errorOn)
				return null;
			
			Log.d("Debug-log", "playing: " + i);
			//Flash off
			publishProgress(i);
			
	    	MediaPlayer p=MediaPlayer.create(gameActivity, R.raw._19);
	    	if (i==20) p = MediaPlayer.create(gameActivity,  R.raw._20);
	    	if (i==21) p = MediaPlayer.create(gameActivity,  R.raw._21);
	    	if (i==22) p = MediaPlayer.create(gameActivity,  R.raw._22);
	    	p.setOnCompletionListener(new OnCompletionListener() {@Override public void onCompletion(MediaPlayer mp) { mp.release(); }});
	    	p.start();
			
			sleep(175);
			//Flash on
			publishProgress(i);
			sleep(75);
		}
		Log.d("Debug-log", "End loop");

		return null;
	}
	
	//A helper method to clean up the look of sleeps
	private void sleep(long ms)
	{
		try { Thread.sleep(ms); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	//Update item flashes on the UI thread...
	@Override
	protected void onProgressUpdate(Integer... i)
	{
		SwitchButton b = buttonMap.get(i[0]);
		b.change();
	}
	
}
