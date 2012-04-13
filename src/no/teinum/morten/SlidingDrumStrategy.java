package no.teinum.morten;

import android.content.Context;
import android.media.SoundPool;
import android.os.Vibrator;
import android.view.MotionEvent;

public class SlidingDrumStrategy implements IDrumStrategy {

	private float start_y;
	private DrumView drumView;
	private SoundInfo[] sounds;
	private SoundPool sp;
	private Context context;
	private Vibrator vibrator;

	public SlidingDrumStrategy(DrumView drumView, SoundInfo[] sounds,
			SoundPool sp, Context context, Vibrator vibrator) {
		this.drumView = drumView;
		this.sounds = sounds;
		this.sp = sp;
		this.context = context;
		this.vibrator = vibrator;
	}

	@Override
	public void Handle(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN
				|| event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {

			actionDown(event);
		} else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
			actionMove(event);
		}
	}

	private void actionMove(MotionEvent event) {
		// two rows with pads
		float delta = event.getRawY() - start_y;

		float absLength = Math.abs(delta);

		float max_scale = drumView.getHeight() / 4;

		if (absLength > max_scale)
			absLength = max_scale;

		float rate = (absLength / max_scale);

		if (delta > 0)
			rate *= -1;

		sp.setRate(sounds[drumView.SelectedPad].StreamId, 1 + rate);
	}

	private void actionDown(MotionEvent event) {
		int index = drumView.drumIndexAt(event.getRawX(), event.getRawY());

		start_y = event.getRawY();

		if (index >= 0) {
			if (drumView.SelectedPad != index) {
				drumView.SelectedPad = index;
				drumView.invalidate();
			}

			SoundInfo soundInfo = sounds[index];

			if (soundInfo.StreamId != 0)
				sp.stop(soundInfo.StreamId);

			soundInfo.StreamId = sp.play(soundInfo.SoundId,
					event.getPressure(), event.getPressure(), 1, // priority
					0, // loop
					Settings.getPitch(context));

			if (Settings.getPhysicalFeedback(context))
				vibrator.vibrate(soundInfo.VibratorLength);
		}
	}

}
