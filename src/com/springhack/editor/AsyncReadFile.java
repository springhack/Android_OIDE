/**
 *   920 Text Editor is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   920 Text Editor is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with 920 Text Editor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.springhack.editor;

import java.io.File;

import org.mozilla.charsetdetector.CharsetDetector;

import com.jecelyin.highlight.Highlight;
import com.springhack.util.LinuxShell;
import com.springhack.util.TimerUtil;
import com.springhack.widget.JecEditText;
import com.stericson.RootTools.RootTools;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class AsyncReadFile
{
    private final static String TAG = "AsyncReadFile";
    private JecEditor mJecEditor;
    public static final int RESULT_OK = 0;
    public static final int RESULT_FAIL = 1;

    /*
     * private String path = ""; private String encoding = ""; private int
     * lineBreak = 0;
     */

    private static boolean isRoot = false;

    private ProgressDialog mProgressDialog;
    private int mSelStart = 0;
    private int mSelEnd = 0;
    private ReadHandler mReadHandler;

    public AsyncReadFile(final JecEditor mJecEditor, final String path, final String encoding, final int lineBreak, int selStart, int selEnd)
    {
        // 加载文件不算改动，不能有撤销操作
        JecEditor.isLoading = true;
        // mJecEditor.text_content.requestFocus();
        this.mJecEditor = mJecEditor;
        mSelStart = selStart;
        mSelEnd = selEnd;
        isRoot = JecEditor.isRoot;
        mReadHandler  = new ReadHandler(AsyncReadFile.this);
        showProgress();
        
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run()
            {
                Message msg = mReadHandler.obtainMessage();
                Bundle b = new Bundle();
                String enc = encoding;
                try
                {
                    String fileString = path;
                    File file = new File(fileString);
                    fileString = file.getAbsolutePath();

                    String tempFile = JecEditor.TEMP_PATH + "/root_file_buffer.tmp";
                    boolean root = false;
                    if(!file.canRead() && isRoot && RootTools.isAccessGiven())
                    {
                        // 需要Root权限处理
                        //RootTools.sendShell("busybox cp " + LinuxShell.getCmdPath(fileString) + " " + tempFile, 1000);
                        RootTools.copyFile(LinuxShell.getCmdPath(fileString), tempFile, true, true);
                        RootTools.sendShell("busybox chmod 777 " + tempFile, 1000);
                        fileString = tempFile;
                        file = new File(fileString);
                        root = true;
                    }
                    if("".equals(enc))
                        enc = getEncoding(fileString);
                    if("GB18030".equals(enc.toUpperCase()))
                        enc = "GBK";
                    
                    if(file.isFile())
                    {
                        String mData = Highlight.readFile(fileString, enc);
                        if(lineBreak == 2)
                        {// unix
                            mData = mData.replaceAll("\r\n|\r", "\n");
                        }else if(lineBreak == 3)
                        {
                            // CR Only(Macintosh)
                            mData = mData.replaceAll("\r\n|\r", "\r");
                        }
                        if(root)
                        {
                            LinuxShell.execute("rm -rf " + tempFile);
                        }
                        msg.what = RESULT_OK;
                        b.putString("data", mData);
                    } else {
                        msg.what = RESULT_FAIL;
                        b.putString("error", AsyncReadFile.this.mJecEditor.getString(R.string.can_not_open_file));
                    }
                    
                }catch (Exception e)
                {
                    final String errorMsg = e.getMessage();// R.string.exception;
                    msg.what = RESULT_FAIL;
                    b.putString("error", errorMsg);
                }catch (OutOfMemoryError e)
                {//内存不足
                    final String errorMsg = mJecEditor.getString(R.string.out_of_memory);
                    msg.what = RESULT_FAIL;
                    b.putString("error", errorMsg);
                } finally {
                    b.putString("path", path);
                    b.putString("encoding", enc);
                    b.putInt("lineBreak", lineBreak);
                    msg.setData(b);
                    msg.sendToTarget();
                }
            }
        });
        thread.start();
        
    }
    
    private void showProgress()
    {
        mProgressDialog = new ProgressDialog(mJecEditor);
        mProgressDialog.setTitle(R.string.spinner_message);
        mProgressDialog.setMessage(mJecEditor.getText(R.string.loading));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog)
            {
                dismissProgress();
            }
        });
        mProgressDialog.show();
    }
    
    public void dismissProgress()
    {
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
    }
    
    static class ReadHandler extends Handler
    {
        private AsyncReadFile mAsyncReadFile;
        
        public ReadHandler(AsyncReadFile arf)
        {
            super();
            mAsyncReadFile = arf;
        }
        
        @Override
        public void handleMessage(Message msg)
        {
            Bundle b = msg.getData();
            String path = b.getString("path");
            String encoding = b.getString("encoding");
            int lineBreak = b.getInt("lineBreak");
            mAsyncReadFile.dismissProgress();
            if(msg.what == RESULT_OK) {
                mAsyncReadFile.finish(b.getString("data"), path, encoding, lineBreak);
            } else {
                JecEditor.isLoading = false;
                Log.d(TAG, b.getString("error"));
                Toast.makeText(mAsyncReadFile.mJecEditor, b.getString("error"), Toast.LENGTH_LONG).show();
            }
        }
    };

    private void finish(String mData, String path, String encoding, int lineBreak)
    {
        try
        {
            TimerUtil.start();
            JecEditText mEditText = mJecEditor.getEditText();
            mEditText.setText2(mData);
            mData = null;
            mEditText.setTextFinger();
            TimerUtil.stop(TAG + "1");
            // scroll to topprivate
            //mEditText.setSelection(0, 0);
            mEditText.setSelection(mSelStart, mSelEnd);
            mEditText.clearFocus();
            // mJecEditor.text_content.invalidate();
            mEditText.setEncoding(encoding);
            mEditText.setLineBreak(lineBreak);
            mEditText.setPath(path);
            mJecEditor.onLoaded(mSelStart, mSelEnd);
           
        }catch (OutOfMemoryError e)
        {
            Toast.makeText(mJecEditor, R.string.out_of_memory, Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Toast.makeText(mJecEditor, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            JecEditor.isLoading = false;
        }
    }

    private String getEncoding(String path)
    {
        String encoding = CharsetDetector.getEncoding(path).trim().toUpperCase();

        if("".equals(encoding))
        {
            // 默认为utf-8
            encoding = "UTF-8";
        }else if("GB18030".equals(encoding))
        {
            // 转换下,不然无法正确解码
            encoding = "GBK";
        }

        return encoding;
    }
}
