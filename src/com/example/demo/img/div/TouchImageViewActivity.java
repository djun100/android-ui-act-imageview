package com.example.demo.img.div;

import android.app.Activity;
import android.os.Bundle;

public class TouchImageViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
        TouchImageView img = new TouchImageView(this); 
        setContentView(img); 
	}
}
