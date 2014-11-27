package lib.module.soundtouch;

public final class SoundTouch {
	static {
		System.loadLibrary("soundtouch");
	}
	private static SoundTouch instance = null;

	private long SoundTouch;

	public synchronized static SoundTouch getSoundTouch() {
		if (null == instance) {
			instance = new SoundTouch();
		}

		return instance;
	}

	private SoundTouch() {
		SoundTouch = soundTouchCreate();
	}

	public native long soundTouchCreate();

	public native void soundTouchDestory();

	public native String soundTouchgethVersion();

	public native void setPitchSemiTones(float pitch);

	public native void setTempoChange(float newTempo);

	public native void shiftingPitch(byte[] pcmData, int offset, int length);

	public native int receiveSamples(byte[] pitchData, int bufferLenght);

	public native void soundTouchFlushLastSamples();

	@Override
	protected void finalize() throws Throwable {

		soundTouchDestory();

		super.finalize();
	}
}
