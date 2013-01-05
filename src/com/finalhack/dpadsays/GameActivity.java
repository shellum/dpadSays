package com.finalhack.dpadsays;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends Activity {

	//UI elements
	private ImageView green;
	private ImageView red;
	private ImageView blue;
	private ImageView yellow;
	private LinearLayout errorBox;
	private LinearLayout instructionsBox;
	
	//Tracking
	private boolean errorOn = false;
	private boolean instructionsOn = true;
	private int current = -1;
	
	//Randomly generated sequence so far
	private List<Integer> soFar = new ArrayList<Integer>();
	//Mapping between color buttons and their keycode/flash status/etc data
	private SparseArray<SwitchButton> buttonMap = new SparseArray<SwitchButton>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_game);
		
		//Hook up all our UI views
		green = (ImageView)findViewById(R.id.green);
		blue = (ImageView)findViewById(R.id.blue);
		red = (ImageView)findViewById(R.id.red);
		yellow = (ImageView)findViewById(R.id.yellow);
		errorBox = (LinearLayout)findViewById(R.id.error_box);
		instructionsBox = (LinearLayout)findViewById(R.id.intro_box);
		
		green.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {	onKeyUp(19, null); }});
		yellow.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) { onKeyUp(20, null); }});
		red.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) { onKeyUp(21, null); }});
		blue.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) { onKeyUp(22, null); }});
		
		//Setup a lookup map for keycodes to UI
		buttonMap.put(19, new SwitchButton(green));
		buttonMap.put(20, new SwitchButton(yellow));
		buttonMap.put(21, new SwitchButton(red));
		buttonMap.put(22, new SwitchButton(blue));
	}
	
	//Play the generated sequence back to the user
	public void playSoFar()
	{
		flash(true, soFar.toArray(new Integer[soFar.size()]));
	}
	
	//Pick a valid but random next sequence item
	int getNextRand()
	{
		int next = ((int)(Math.random() * 1000)) % 4 + 19;
		return next;
	}

	
	
	//If the user is entering what they remember...
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		super.onKeyUp(keyCode, event);
		
		if (keyCode <19 || keyCode > 22) return false;
		
		//Are instructions currently displayed?
		//If so, get rid of them
		if (instructionsOn) instructionsBox.setVisibility(View.GONE);

		//Is an error message currently displayed?
		if (errorOn)
		{
			//Get rid of it
			errorOn = false;
			errorBox.setVisibility(View.INVISIBLE);
		}
		
		
		if (BuildConfig.DEBUG) Log.d("Debug-log: ", "Pressed: " + keyCode + ", current:" + current);
		
		//If a game is not in progress and this is not a first shot at remembering a sequence...
		if (current==-1)
		{
			//Generate a sequence item
			soFar.add(getNextRand());
			//Play back the sequence, and set the game to in progress
			playSoFar();
			current = 0;
			return true;
		}
		 
		//If the game is in progress, make sure the user is remembering the correct next sequence item
		if (keyCode == soFar.get(current))  { flash(false, keyCode); current++;}
		//If they get it wrong...
		else error();
		
		//If the user has entered the right sequence, and is at the end of the sequence...
		if (current>=soFar.size())
		{
			//Extend the sequence
			soFar.add(getNextRand());
			playSoFar();
			current=0;
		}
		
		//Update the high score if the user has passed the old high score
		int highScoreValue = Util.getHighScore(this);
		if (soFar.size() > highScoreValue)
		{
			highScoreValue = soFar.size();
			Util.setHighScore(soFar.size(), this);
		}
		
		//Update scoring in the UI
		TextView score = (TextView)findViewById(R.id.score);
		score.setText(getString(R.string.score) + soFar.size());
		TextView highScore = (TextView)findViewById(R.id.high_score);
		highScore.setText(getString(R.string.high_score) + highScoreValue);
		
		return true;
	}

	//If the user entered a wrong sequence item...
	public void error()
	{
		if (BuildConfig.DEBUG) Log.d("Debug-log:", "error!");
		//Show an error message and take the game out of progress
		errorOn = true;
		errorBox.setVisibility(View.VISIBLE);
		soFar = new ArrayList<Integer>();
		current = -1;
	}
	
	//Flash one or more items in sequence
	public void flash(boolean delayBeforeStart, Integer... keyCode)
	{
		//Do it on an AsyncTask
		Sequencer s = new Sequencer(this, delayBeforeStart, buttonMap);
		s.execute(keyCode);
	}



}
