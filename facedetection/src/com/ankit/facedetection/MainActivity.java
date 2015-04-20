package com.ankit.facedetection;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;

	TextView facedetect;

	DrawingView drawingView;
	Face[] detectedFaces;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		drawingView = new DrawingView(this);
		LayoutParams layoutParamsDrawing = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addContentView(drawingView, layoutParamsDrawing);

		facedetect = (TextView) findViewById(R.id.faces);
	}

	FaceDetectionListener faceDetectionListener = new FaceDetectionListener() {

		@Override
		public void onFaceDetection(Face[] faces, Camera camera) {

			if (faces.length == 0) {
				facedetect.setText(" No Faces Detected! ");
				drawingView.setHaveFace(false);
			} else {
				facedetect.setText(String.valueOf(faces.length)
						+ " Faces detected");
				drawingView.setHaveFace(true);
				detectedFaces = faces;
			}
			drawingView.invalidate();
		}
	};
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (previewing) {
			camera.stopFaceDetection();
			camera.stopPreview();
			previewing = false;
		}

		if (camera != null) {
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();

				camera.startFaceDetection();
				previewing = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera = Camera.open();
		camera.setFaceDetectionListener(faceDetectionListener);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopFaceDetection();
		camera.stopPreview();
		camera.release();
		camera = null;
		previewing = false;
	}

	private class DrawingView extends View {

		boolean haveFace;
		Paint drawingPaint;

		public DrawingView(Context context) {
			super(context);
			haveFace = false;
			drawingPaint = new Paint();
			drawingPaint.setColor(Color.YELLOW);
			drawingPaint.setStyle(Paint.Style.STROKE);
			drawingPaint.setStrokeWidth(3);
		}

		public void setHaveFace(boolean h) {
			haveFace = h;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if (haveFace) {

				int vWidth = getWidth();
				int vHeight = getHeight();

				for (int i = 0; i < detectedFaces.length; i++) {

					int l = detectedFaces[i].rect.left;
					int t = detectedFaces[i].rect.top;
					int r = detectedFaces[i].rect.right;
					int b = detectedFaces[i].rect.bottom;
					int left = (l + 1000) * vWidth / 2000;
					int top = (t + 1000) * vHeight / 2000;
					int right = (r + 1000) * vWidth / 2000;
					int bottom = (b + 1000) * vHeight / 2000;
					canvas.drawRect(left, top, right, bottom, drawingPaint);
				}
			} 
			else 
			{
				canvas.drawColor(Color.TRANSPARENT);
			}
		}
	}
}
