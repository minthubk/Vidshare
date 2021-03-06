package com.gm375.vidshare;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;

import com.gm375.vidshare.listenerstuff.DataObjectEvent;
import com.gm375.vidshare.listenerstuff.DataObjectListener;

public class StreamViewer extends Activity implements DataObjectListener,
            MediaPlayer.OnCompletionListener {
    
    private Vidshare vs = null;
    private VideoView mVideoView;
    //private ImageView mLoadingScreen;
    private Stream currentStream = null;
    
    private ConcurrentLinkedQueue<String> dObjFilepaths = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Vidshare.LOG_TAG, "*** StreamViewer *** onCreate() ***");
        
        vs = (Vidshare) getApplication();
        
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.stream_viewer);
        
        dObjFilepaths = new ConcurrentLinkedQueue<String>();
        
        mVideoView = (VideoView) findViewById(R.id.stream_viewer_surface);
        //mLoadingScreen = (ImageView) findViewById(R.id.loading_screen);
        
        mVideoView.setOnCompletionListener(this);
        
        SurfaceHolder holder = mVideoView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public void onStart() {
        super.onStart();
        Log.d(Vidshare.LOG_TAG, "*** StreamViewer *** onStart() ***");
        
        vs.setStreamViewer(this);
        
        String mapKey = getIntent().getStringExtra(VSActivity.STREAM_ID_KEY);
        currentStream = vs.mStreamMap.get(mapKey);
        currentStream.setIsBeingViewed(true);
    }
    
    public void onStop() {
        super.onStop();
        Log.d(Vidshare.LOG_TAG, "*** StreamViewer *** onStop() ***");
        
        currentStream.setIsBeingViewed(false);
        currentStream = null;
        
        vs.setStreamViewer(null);
    }

    @Override
    public void dataObjectAlert(DataObjectEvent dObjEvent) {
        // This is executed whenever a new Data Object (or timeout message) arrives.
        // They *should* arrive in order, so no sequencing should have to happen here.
        // There may be indeterminable delays between each data object though.
        
        Log.d(Vidshare.LOG_TAG, "*** StreamViewer *** DATA OBJECT EVENT! ***");
        
        switch(dObjEvent.getEventType()) {
        
        case DataObjectEvent.EVENT_TYPE_TIMEOUT_REACHED:
            Log.d(Vidshare.LOG_TAG, "*** STREAM VIEWER *** Event: Timeout ***");
            Toast.makeText(getApplicationContext(),
                    "There was a problem with the stream. It timed out.",
                    Toast.LENGTH_LONG).show();
            finish();
            break;
            
        case DataObjectEvent.EVENT_TYPE_NEW_DATA_OBJECT:
            Log.d(Vidshare.LOG_TAG, "*** STREAM VIEWER *** Event: New Data Object ***");
            newDataObject(dObjEvent.getseqNumber(), dObjEvent.getFilepath());
            break;
            
        case DataObjectEvent.EVENT_TYPE_STREAM_ENDED:
            Log.d(Vidshare.LOG_TAG, "*** STREAM VIEWER *** Event: Stream Ended ***");
            finish();
            break;
            
        }
        
    }
    
    private void newDataObject(Integer seqNumber, String filepath) {
        dObjFilepaths.add(filepath);
        if (!mVideoView.isPlaying()) {
            //if (mLoadingScreen.isShown()) {
            //    mLoadingScreen.setVisibility(View.GONE);
            //}
            mVideoView.setVideoPath(dObjFilepaths.poll());
            mVideoView.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(Vidshare.LOG_TAG, "*** STREAM VIEWER *** MediaPlayer onCompletion() ***");
        if (dObjFilepaths.isEmpty()) {
            //mLoadingScreen.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(),
            //        "Loading, please wait...",
            //        Toast.LENGTH_LONG).show();
        } else {
            mVideoView.setVideoPath(dObjFilepaths.poll());
            mVideoView.start();
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            finish();
            break;
        }
        return super.onKeyDown(keyCode, event);
    }
    
}
