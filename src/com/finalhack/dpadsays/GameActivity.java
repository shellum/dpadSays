package com.finalhack.dpadsays;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity {

	ImageView green;
	ImageView red;
	ImageView blue;
	ImageView yellow;
	
	boolean errorOn = false;
	int current = -1;
	
	List<Integer> soFar = new ArrayList<Integer>();
	SparseArray<SwitchButton> buttonMap = new SparseArray<SwitchButton>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_game);
		
		green = (ImageView)findViewById(R.id.green);
		blue = (ImageView)findViewById(R.id.blue);
		red = (ImageView)findViewById(R.id.red);
		yellow = (ImageView)findViewById(R.id.yellow);
	
		buttonMap.put(19, new SwitchButton(green));
		buttonMap.put(20, new SwitchButton(yellow));
		buttonMap.put(21, new SwitchButton(red));
		buttonMap.put(22, new SwitchButton(blue));
	}
	
	public void playSoFar()
	{
		flash(true, soFar.toArray(new Integer[soFar.size()]));
	}
	
	int getNextRand()
	{
		int next = ((int)(Math.random() * 1000)) % 4 + 19;
		return next;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if (errorOn)
		{
			errorOn = false;
			//TODO:Hide x
		}
		
		System.out.println("Pressed: " + keyCode + ", current:" + current);
		if (current==-1)
		{
			soFar.add(getNextRand());
			playSoFar();
			current = 0;
			return true;
		}
		
		if (keyCode == soFar.get(current))  { flash(false, keyCode); current++;}
		else error();
		
		if (current>=soFar.size())
		{
			soFar.add(getNextRand());
			playSoFar();
			current=0;
		}
		
		int highScoreValue = getHighScore(this);
		if (soFar.size() > highScoreValue)
		{
			highScoreValue = soFar.size();
			setHighScore(soFar.size(), this);
		}
		
		TextView score = (TextView)findViewById(R.id.score);
		score.setText(getString(R.string.score) + soFar.size());

		TextView highScore = (TextView)findViewById(R.id.high_score);
		highScore.setText(getString(R.string.high_score) + highScoreValue);
		
		return true;
	}
	
	public void error()
	{
		System.out.println("error!");
		errorOn = true;
		//TODO:show x
		soFar = new ArrayList<Integer>();
		current = -1;
	}
	
	public void flash(boolean delayBeforeStart, Integer... keyCode)
	{
		Sequencer s = new Sequencer(delayBeforeStart);
		s.execute(keyCode);
	}
	
    public static int getHighScore(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        int highScore = sharedPreferences.getInt("highScore", 0);

        return highScore;
    }
    
    public static void setHighScore(int highScore, Context context)
    {
		Editor sharedPreferencesEditor = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit();
		sharedPreferencesEditor.putInt("highScore", highScore);
		sharedPreferencesEditor.commit();
    }

	
	private class Sequencer extends AsyncTask<Integer, Integer, Integer>
	{
		private boolean delayBeforeStart = false;
		
		public Sequencer(boolean delayBeforeStart)
		{
			this.delayBeforeStart = delayBeforeStart;
		}
		
		@Override
		protected Integer doInBackground(Integer... params)
		{
			if (delayBeforeStart) sleep(2000);
			System.out.println("Start loop " + delayBeforeStart);
			for (Integer i : params)
			{
				System.out.println("playing: " + i);
				publishProgress(i);
				sleep(150);
				publishProgress(i);
				sleep(150);
			}
			System.out.println("End loop");

			return null;
		}
		
		private void sleep(long ms)
		{
			try { Thread.sleep(ms); } catch (InterruptedException e) { e.printStackTrace(); }
		}
		
		@Override
		protected void onProgressUpdate(Integer... i)
		{
			SwitchButton b = buttonMap.get(i[0]);
			b.change();
		}
		
	}
	

}
