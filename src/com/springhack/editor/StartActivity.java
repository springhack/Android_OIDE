/**
 * 文件名：StartActivity.java
 * 创建时间：2012-10-12  下午9:12:11
 * 作者：JERRY
 * Blog ： http://blog.jerry002.com
 */
package com.springhack.editor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class StartActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(StartActivity.this, JecEditor.class);
				startActivity(intent);
				StartActivity.this.finish();
			}
		}, 1000);
        
    }
}
