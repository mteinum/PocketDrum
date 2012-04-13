package no.teinum.morten;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class DrumView extends View {
		
	class Scale {
		private float _w;
		private float _h;
				
		public Scale(float w, float h) {
			_w = w; _h = h;
		}
		
		public float getScaledWidth(float value){
			return value * _w;
		}
		
		public float getScaledHeight(float value) {
			return value * _h;
		}
		
		public RectF createScaledRect(RectF source){
			return new RectF(
					getScaledWidth(source.left),
					getScaledHeight(source.top),
					getScaledWidth(source.right),
					getScaledHeight(source.bottom));
		}

	}
	
	
	private Bitmap background;
	private Bitmap scaledBackground;
	
	private Bitmap padLight;
	private Bitmap scaledPadLight;
	
	// original size of the background image
	private static final float original_h = 670;
	private static final float original_w = 1196;

	// original size of the light
	private static final float light_original_h = 13;
	private static final float light_original_w = 54;
	
	// from the left of a pad, how many pixel to the right the light
	// should be drawn.
	private static final float light_offset_x =74;
	private static final float light_offset_y_top = 53;
	private static final float light_offset_y_bottom = 595;
	
	private Scale scale;
	
	private  static final int pad_width = 200;
	private static final int pad_height = 240;
	
	private RectF[] pads;
	private RectF[] scaledPads;
	
	public int SelectedPad;
	
	private void initPads(){
		int[] y_axis = new int[]{ 85, 334 };
		int[] x_axis = new int[]{ 60, 275, 496, 707 };
		
		pads = new RectF[y_axis.length * x_axis.length];
		
		for (int y=0; y < y_axis.length; y++){
			for (int x=0; x < x_axis.length; x++){
				pads[y * x_axis.length + x] = createRect(x_axis[x], y_axis[y]);
			}
		}
	}
	
	public DrumView(Context context) {
		super(context);
		setFocusable(true);
		
		initPads();
				
		background = BitmapFactory.decodeResource(
				getResources(),
				R.drawable.roland_spd30);
		
		padLight = BitmapFactory.decodeResource(
				getResources(),
				R.drawable.padlight);
		
		scaledPads = new RectF[pads.length];
	}
	
	private void createScaledPads() {
		for (int i=0; i < pads.length; i++)
			scaledPads[i] = scale.createScaledRect(pads[i]);
	}
	
	private static RectF createRect(int x, int y) {
		return new RectF(x, y, x + pad_width, y + pad_height);
	}
	
	public int drumIndexAt(float x, float y)
	{
		int[] location = new int[2];
		
		super.getLocationOnScreen(location);
		
		float relative_x = x - location[0];
		float relative_y = y - location[1];
		
		for (int i=0; i < scaledPads.length; i++)
		{
			if (scaledPads[i].contains(relative_x, relative_y))
				return i;
		}
				
		return -1;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		sizeChanged(w, h);
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void sizeChanged(float w, float h){

		scaledBackground = Bitmap.createScaledBitmap(
				background, (int)w, (int)h, true);
		
		scale = new Scale(w / original_w, h / original_h);
		
		scaledPadLight= Bitmap.createScaledBitmap(
				padLight,
				(int)scale.getScaledWidth(light_original_w),
				(int)scale.getScaledHeight(light_original_h),
				true);
		
		createScaledPads();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Paint paint = new Paint();
		
		canvas.drawBitmap(
				scaledBackground,
				0,
				0,
				paint);
		
		RectF current = scaledPads[SelectedPad];
		
		float top;
		
		if (SelectedPad < 4)
		{
			top = light_offset_y_top;
		}
		else{
			top = light_offset_y_bottom;
		}
		
		canvas.drawBitmap(
				scaledPadLight,
				current.left + scale.getScaledWidth(light_offset_x),
				scale.getScaledHeight(top),
				paint);
	}
	
}
