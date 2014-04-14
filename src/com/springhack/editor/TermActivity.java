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


import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;
import java.io.*;

/**
 * This sample activity demonstrates the use of EmulatorView.
 *
 * This activity also demonstrates how to set up a simple TermSession connected
 * to a local program.  The Telnet connection demonstrates a more complex case;
 * see the TelnetSession class for more details.
 */
public class TermActivity extends Activity
{
    private EditText term_edit;
	private EditText term_entry;
    private EmulatorView mEmulatorView;
    private TermSession mSession;
	private LinearLayout ln;
	private String path;
	private String ttt;
	private Process fuck;
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_activity);
		ttt = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/.OIDE";
		term_edit = (EditText) findViewById(R.id.term_edit);
		term_entry = (EditText) findViewById(R.id.term_entry);
		ln = (LinearLayout) findViewById(R.id.term_main);

        /* Text entry box at the bottom of the activity.  Note that you can
           also send input (whether from a hardware device or soft keyboard)
           directly to the EmulatorView. */
        

        /* Sends the content of the text entry box to the terminal, without
           sending a carriage return afterwards */
        

        /**
         * EmulatorView setup.
         */
		 
		 
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			//ln = (LinearLayout) findViewById(R.id.ln);
			ln.setOrientation(0);
		} else {
			//ln = (LinearLayout) findViewById(R.id.ln);
			ln.setOrientation(1);
		}
		 
		 
        EmulatorView view = (EmulatorView) findViewById(R.id.emulatorView);
        mEmulatorView = view;

        /* Let the EmulatorView know the screen's density. */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        view.setDensity(metrics);

        /* Create a TermSession. */
        Intent myIntent = getIntent();
        //path = myIntent.getStringExtra("file");
		path = "ddd";
        TermSession session;
		session = createLocalTermSession();
		session.setDefaultUTF8Mode(true);

        //if (sessionType != null && sessionType.equals("start")) {
       
            
            mSession = session;
			session.write(JecEditor.getDataDir(this) + "/bin/bin/gdb");
            session.write("\r");
			session.write("file " + JecEditor.getDataDir(this) + "/bin/home/temp");
			session.write("\r");
		
        

        /* Attach the TermSession to the EmulatorView. */
        view.attachSession(session);  
		
        /* That's all you have to do!  The EmulatorView will call the attached
           TermSession's initializeEmulator() automatically, once it can
           calculate the appropriate screen size for the terminal emulator. */
		   
		   
		Button term_run = (Button) findViewById(R.id.term_run);
		term_run.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mSession.write("r");
					mSession.write("\r");
				}
			});
		Button term_step = (Button) findViewById(R.id.term_step);
		term_step.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mSession.write("s");
					mSession.write("\r");
				}
			});
		Button term_next = (Button) findViewById(R.id.term_next);
		term_next.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mSession.write("n");
					mSession.write("\r");
				}
			});
		Button term_continue = (Button) findViewById(R.id.term_continue);
		term_continue.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mSession.write("c");
					mSession.write("\r");
				}
			});
		Button term_print = (Button) findViewById(R.id.term_print);
		term_print.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mSession.write("p " + term_entry.getText().toString());
					mSession.write("\r");
				}
			});
		Button term_break = (Button) findViewById(R.id.term_break);
		term_break.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mSession.write("b " + term_entry.getText().toString());
					mSession.write("\r");
				}
			});
		Button term_delete = (Button) findViewById(R.id.term_delete);
		term_delete.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mSession.write("d " + term_entry.getText().toString());
					mSession.write("\r");
				}
			});
		Button term_send = (Button) findViewById(R.id.term_send);
		term_send.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mSession.write(term_edit.getText().toString());
					mSession.write("\r");
				}
			});
    }

	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			//ln = (LinearLayout) findViewById(R.id.ln);
			ln.setOrientation(0);
		} else {
			//ln = (LinearLayout) findViewById(R.id.ln);
			ln.setOrientation(1);
		}
	}
	
	
    @Override
    protected void onResume() {
        super.onResume();

        /* You should call this to let EmulatorView know that it's visible
           on screen. */
        mEmulatorView.onResume();

        term_edit.requestFocus();
    }

    @Override
    protected void onPause() {
        /* You should call this to let EmulatorView know that it's no longer
           visible on screen. */
        mEmulatorView.onPause();

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
			JecEditor.runCommand(busyboxBin, "pkill", "execgdb");
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
			JecEditor.runCommand(busyboxBin, "pkill", "execgdb");
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
    private TermSession createLocalTermSession() {
        /* Instantiate the TermSession ... */
        TermSession session = new TermSession();

        /* ... create a process ... */
        String execPath = JecEditor.getDataDir(this) + "/bin/execgdb";
        ProcessBuilder execBuild =
                new ProcessBuilder(execPath, "/system/bin/sh", "-");
        execBuild.redirectErrorStream(true);
        Process exec = null;
        try {
            exec = execBuild.start();
        } catch (Exception e) {
            // handle exception here
        }
		fuck = exec;

        /* ... and connect the process's I/O streams to the TermSession. */
        session.setTermIn(exec.getInputStream());
        session.setTermOut(exec.getOutputStream());

        /* You're done! */
        return session;

    }
}
