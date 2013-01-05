package com.finalhack.dpadsays;

import android.view.View;
import android.widget.ImageView;

public class SwitchButton
{
	public ImageView view;
	public boolean isGone = false;
	
	public SwitchButton(ImageView view)
	{
		this.view = view;
	}
	
	public void change()
	{
		if (isGone) unFlash();
		else flash();
	}
	
	public void flash()
	{
		view.setVisibility(View.INVISIBLE);
		isGone = true;
	}
	
	public void unFlash()
	{
		view.setVisibility(View.VISIBLE);
		isGone = false;
	}
}