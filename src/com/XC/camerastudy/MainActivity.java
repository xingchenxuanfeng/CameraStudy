package com.XC.camerastudy;

import java.io.IOException;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	private final String TAG = "Preview";

	class Preview extends ViewGroup implements PreviewCallback, Callback {

		Preview(Context context) {
			super(context);

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = mSurfaceView.getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
		}
		 public  void setCameraDisplayOrientation(Activity activity,
		         int cameraId, android.hardware.Camera camera) {
		     android.hardware.Camera.CameraInfo info =
		             new android.hardware.Camera.CameraInfo();
		     android.hardware.Camera.getCameraInfo(cameraId, info);
		     int rotation = activity.getWindowManager().getDefaultDisplay()
		             .getRotation();
		     int degrees = 0;
		     switch (rotation) {
		         case Surface.ROTATION_0: degrees = 0; break;
		         case Surface.ROTATION_90: degrees = 90; break;
		         case Surface.ROTATION_180: degrees = 180; break;
		         case Surface.ROTATION_270: degrees = 270; break;
		     }

		     int result;
		     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
		         result = (info.orientation + degrees) % 360;
		         result = (360 - result) % 360;  // compensate the mirror
		     } else {  // back-facing
		         result = (info.orientation - degrees + 360) % 360;
		     }
		     camera.setDisplayOrientation(result);
		 }

		public void surfaceCreated(SurfaceHolder holder) {
			Log.i(TAG, "surfaceCreated");
			try {
				if (mCamera != null) {
					setCameraDisplayOrientation(MainActivity.this,0,mCamera);
					mCamera.setPreviewDisplay(holder);
					Log.i(TAG, "setPreviewDisplay");
				}
			} catch (IOException exception) {
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// Surface will be destroyed when we return, so stop the preview.
			if (mCamera != null) {
				mCamera.stopPreview();
				Log.i(TAG, "stopPreview");
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			mCamera.startPreview();
			Log.i(TAG, "startPreview");
		}

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {

		}

	}

	Preview mPreview;
	Camera mCamera;
	SurfaceView mSurfaceView;
	SurfaceHolder mHolder;
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.iv);
		mSurfaceView = (SurfaceView) findViewById(R.id.sv);
		mPreview = new Preview(this);
		Log.i(TAG, "onCreate");

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		mCamera = Camera.open();
		Log.i(TAG, "Camera.open");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		mCamera.takePicture(null, null, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				imageView.setImageBitmap(bitmap);
				mCamera.startPreview();
			}
		});

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onClick(View v) {

	}

}
