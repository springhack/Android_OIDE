package com.springhack.editor;


import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.webkit.*;
import java.io.IOException;
import java.io.File;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.database.Cursor;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.view.View.*;
import android.widget.SearchView.*;

public class WebActivity extends Activity
{

    private WebView web;
	private Button apk;
	private DownloadManager downloadManager;
	private SharedPreferences prefs;
	private static final String DL_ID = "downloadId";
	private String ttt;
	public String filename = "oide.tar.gz";
	public String filetype = "gz";
	public int count = 0;
	public long[] ids = new long[20];
	public String[] nms = new String[20];
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
		ttt = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/.OIDE";
		downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this); 
		prefs.edit().clear().commit();
		web = (WebView) findViewById(R.id.web);
		apk = (Button) findViewById(R.id.apk);
		web.getSettings().setJavaScriptEnabled(true);
		web.setWebViewClient(new WebViewClient(){
				public boolean shouldOverrideUrlLoading(WebView view,String url){
					if(url.indexOf("tel:")<0){//页面上有数字会导致连接电话
						web.loadUrl(url);
					}
					return true;
				}
			});
        web.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
							web.goBack();
							return true;
						}
					}
					return false;
				}
			});
		
		web.addJavascriptInterface(new Object() {
			public void setFile(String name, String type)
			{
				filename = name;
				filetype = type;
			}
			public void addApk(String name)
			{
				nms[count] = name;
				++count;
			}
		}, "oide");
		apk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				int i;
				for (i=0;i<count;++i)
				{
					try {
						chmod("777", nms[i]);
					} catch (Exception e) {
						//Nothing...
					}
					
					Log.e("file__a__", nms[i]);
					
					Intent tent = new Intent(Intent.ACTION_VIEW);
					tent.setDataAndType(Uri.fromFile(new File(nms[i])), "application/vnd.android.package-archive");
					startActivity(tent);
				}
			}
		});
		web.loadUrl("http://sksks.tk/oide-plugs.html");
		//web.loadUrl("http://192.168.123.6/cmd.php");
		web.setDownloadListener(new MyWebViewDownLoadListener());
    }

	private class MyWebViewDownLoadListener implements DownloadListener{

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {         
			Log.i("tag", "url="+url);         
			Log.i("tag", "userAgent="+userAgent);
			Log.i("tag", "contentDisposition="+contentDisposition);         
			Log.i("tag", "mimetype="+mimetype);
			Log.i("tag", "contentLength="+contentLength);
            //Uri uri = Uri.parse(url);
            //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //startActivity(intent);
			//if(!prefs.contains(DL_ID)) {
				Uri resource = Uri.parse(url); 
				DownloadManager.Request request = new DownloadManager.Request(resource); 
				request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI); 
				request.setAllowedOverRoaming(true); 
				MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
				String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
				request.setMimeType(mimeString);
				request.setShowRunningNotification(true);
				request.setVisibleInDownloadsUi(true);
				request.setDescription("Android OIDE 插件下载……");
				request.setDestinationInExternalPublicDir("/.OIDE/", filename);
				request.setTitle(filename); 
				long id = downloadManager.enqueue(request);
				//prefs.edit().putLong(DL_ID, id).commit(); 
			/**} else { 
				queryDownloadStatus(); 
			}**/
			registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }


	private BroadcastReceiver receiver = new BroadcastReceiver() { 
		@Override 
		public void onReceive(Context context, Intent intent) { 
			if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
			{
				Toast.makeText(WebActivity.this, "Bingo，貌似有下载完成咯~！", Toast.LENGTH_LONG).show();
			}
		}
	};
	private void chmod(String... args) throws IOException {
        String[] cmdline = new String[args.length + 1];
		File ch = new File("/system/bin", "chmod");
        if (ch.exists()) {
			cmdline[0] = "/system/bin/chmod";
			Log.e("dd","::::::bin");
		} else {
			cmdline[0] = "/system/xbin/chmod";
			Log.e("dd","::::::xbin");
		}
        System.arraycopy(args, 0, cmdline, 1, args.length);
        new ProcessBuilder(cmdline).start();
    }
}
