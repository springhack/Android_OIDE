package com.springhack.editor;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;


import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;

/**
 * This sample activity demonstrates the use of EmulatorView.
 *
 * This activity also demonstrates how to set up a simple TermSession connected
 * to a local program.  The Telnet connection demonstrates a more complex case;
 * see the TelnetSession class for more details.
 */
public class ResultActivity extends Activity
{
	private EditText result_entry;
    private EmulatorView result_view;
    private TermSession mSession;
	private String path;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

		result_entry = (EditText) findViewById(R.id.result_entry);

        /* Text entry box at the bottom of the activity.  Note that you can
		 also send input (whether from a hardware device or soft keyboard)
		 directly to the EmulatorView. */


        /* Sends the content of the text entry box to the terminal, without
		 sending a carriage return afterwards */


        /**
         * EmulatorView setup.
         */



        EmulatorView view = (EmulatorView) findViewById(R.id.result_view);
        result_view = view;

        /* Let the EmulatorView know the screen's density. */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        view.setDensity(metrics);

        /* Create a TermSession. */
        Intent myIntent = getIntent();
        path = myIntent.getStringExtra("cmd");
		Log.e("dddddd", path);
		//path = "ddd";
        TermSession session;
		session = createLocalTermSession(path);
		session.setDefaultUTF8Mode(true);

        //if (sessionType != null && sessionType.equals("start")) {


		mSession = session;



        /* Attach the TermSession to the EmulatorView. */
        view.attachSession(session);  

        /* That's all you have to do!  The EmulatorView will call the attached
		 TermSession's initializeEmulator() automatically, once it can
		 calculate the appropriate screen size for the terminal emulator. */


		Button result_send = (Button) findViewById(R.id.result_send);
		result_send.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mSession.write(result_entry.getText().toString());
					result_entry.setText("");
					mSession.write("\r");
				}
			});
			
		Button result_exit = (Button) findViewById(R.id.result_finish);
		result_exit.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String busyboxBin = JecEditor.getDataDir(ResultActivity.this) + "/bin/busybox";
					try{
						JecEditor.runCommand(busyboxBin, "pkill", "execrst");
					} catch (Exception e) {
						//xxoo
					}
					if (mSession != null) {
						mSession.finish();
					}
					finish();
				}
			});
		
		result_entry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				public boolean onEditorAction(TextView v, int action, KeyEvent ev) {
					// Ignore enter-key-up events
					if (ev != null && ev.getAction() == KeyEvent.ACTION_UP) {
						return false;
					}
					// Don't try to send something if we're not connected yet
					TermSession session = mSession;
					if (mSession == null) {
						return true;
					}

					Editable e = (Editable) v.getText();
					// Write to the terminal session
					session.write(e.toString());
					session.write('\r');
					TextKeyListener.clear(e);
					return true;
				}
			});
		result_entry.requestFocus();

    }




    @Override
    protected void onResume() {
        super.onResume();

        /* You should call this to let EmulatorView know that it's visible
		 on screen. */
        result_view.onResume();

        result_entry.requestFocus();
    }

    @Override
    protected void onPause() {
        /* You should call this to let EmulatorView know that it's no longer
		 visible on screen. */
        result_view.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        /**
         * Finish the TermSession when we're destroyed.  This will free
         * resources, stop I/O threads, and close the I/O streams attached
         * to the session.
         *
         * For the local session, closing the streams will kill the shell; for
         * the Telnet session, it closes the network connection.
         */

		String busyboxBin = JecEditor.getDataDir(this) + "/bin/busybox";
		try{
			JecEditor.runCommand(busyboxBin, "pkill", "execrst");
		} catch (Exception e) {
			//xxoo
		}

        if (mSession != null) {
            mSession.finish();
        }

        super.onDestroy();
    }

	public void onFinish()
    {
		String busyboxBin = JecEditor.getDataDir(this) + "/bin/busybox";
		try{
			JecEditor.runCommand(busyboxBin, "pkill", "execrst");
		} catch (Exception e) {
			//xxoo
		}
		if (mSession != null) {
            mSession.finish();
        }
        finish();
    }

    /**
     * Create a TermSession connected to a local shell.
     */
    private TermSession createLocalTermSession(String xpath) {
        /* Instantiate the TermSession ... */
        TermSession session = new TermSession();

        /* ... create a process ... */
        String[] execPath = (JecEditor.getDataDir(this) + "/bin/execrst " + xpath).split(" "); 
        ProcessBuilder execBuild =
			new ProcessBuilder(execPath);
        execBuild.redirectErrorStream(true);
        Process exec = null;
        try {
            exec = execBuild.start();
        } catch (Exception e) {
            // handle exception here
        }

        /* ... and connect the process's I/O streams to the TermSession. */
        session.setTermIn(exec.getInputStream());
        session.setTermOut(exec.getOutputStream());

        /* You're done! */
        return session;

    }


}
