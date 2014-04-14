package com.springhack.editor;


import android.content.Context;
import android.content.pm.PackageManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.Socket;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.springhack.editor.R;
import com.jecelyin.highlight.Highlight;
import com.springhack.util.ColorPicker;
import com.springhack.util.FileBrowser;
import com.springhack.util.FileUtil;
import com.springhack.util.LinuxShell;
import com.springhack.widget.JecEditText;
import com.springhack.widget.JecMenu;
import com.springhack.widget.JecMenu.OnMenuItemSelectedListener;
import com.springhack.widget.SymbolGrid;
import com.springhack.widget.SymbolGrid.OnSymbolClickListener;
import com.springhack.widget.TabHost;
import com.springhack.widget.TabHost.OnTabChangeListener;
import com.springhack.widget.TabHost.OnTabCloseListener;
import com.springhack.widget.TabWidget;

import android.app.Activity;
import android.app.AlertDialog;
/*import android.app.Notification;
 import android.app.NotificationManager;
 import android.app.PendingIntent;*/
import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Environment;

import android.text.Editable;
import android.text.method.TextKeyListener;

import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;
import android.renderscript.*;
import android.view.*;
import android.view.View.*;
import android.content.*;
import jackpal.androidterm.emulatorview.*;
import android.view.GestureDetector.*;


public class Editor extends Activity
{
	@Override
	private JecEditText mj;
	private LinearLayout ly;
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
		ly = (LinearLayout) findViewById(R.id.ed);
		mj = createEditText();
		ly.addView(mj);
		
	}
	
	private JecEditText createEditText()
    {
        JecEditText mEditText = (JecEditText) findViewById(R.id.text_content);
        return mEditText;
    }
}
