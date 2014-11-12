#include "lib_module_soundtouch_NativeSoundTouch.h"
#include "log.h"
#include "soundtouch/SoundTouch.h"

static soundtouch::SoundTouch* getTouch(JNIEnv *env, jobject obj) {

	jclass cls = env->GetObjectClass(obj);
	jfieldID fid = env->GetFieldID(cls, "nativeSoundTouch", "J");
	soundtouch::SoundTouch* soundTouch =
			(soundtouch::SoundTouch*) (env->GetLongField(obj, fid));

	return soundTouch;
}

/*
 * Class:     lib_module_soundtouch_NativeSoundTouch
 * Method:    soundTouchCreate
 * Signature: ()J
 */
jlong Java_lib_module_soundtouch_NativeSoundTouch_soundTouchCreate(JNIEnv *,
		jobject) {

	soundtouch::SoundTouch* soundTouch = new soundtouch::SoundTouch();

	const char* verStr = soundTouch->getVersionString();

	soundTouch->setSampleRate(8000);
	soundTouch->setChannels(1);
	//soundTouch->setPitchSemiTones(0.00f);

	soundTouch->setSetting(SETTING_USE_QUICKSEEK, 0);

	soundTouch->setSetting(SETTING_SEQUENCE_MS, 32);
	soundTouch->setSetting(SETTING_SEEKWINDOW_MS, 15);
	soundTouch->setSetting(SETTING_OVERLAP_MS, 8);
	soundTouch->setSetting(SETTING_USE_AA_FILTER, 1);

	return (jlong) soundTouch;
}

/*
 * Class:     lib_module_soundtouch_NativeSoundTouch
 * Method:    soundTouchDestory
 * Signature: ()V
 */
void Java_lib_module_soundtouch_NativeSoundTouch_soundTouchDestory(JNIEnv *env,
		jobject obj) {

	soundtouch::SoundTouch* soundTouch = getTouch(env, obj);

	delete soundTouch;
}

/*
 * Class:     lib_module_soundtouch_NativeSoundTouch
 * Method:    soundTouchgethVersion
 * Signature: ()Ljava/lang/String;
 */
jstring Java_lib_module_soundtouch_NativeSoundTouch_soundTouchgethVersion(
		JNIEnv *env, jobject obj) {

	const char *verStr;

	soundtouch::SoundTouch* soundTouch = getTouch(env, obj);
	verStr = soundTouch->getVersionString();

	// return version as string
	return env->NewStringUTF(verStr);
}

/*
 * Class:     lib_module_soundtouch_NativeSoundTouch
 * Method:    setPitchSemiTones
 * Signature: (D)V
 */
void Java_lib_module_soundtouch_NativeSoundTouch_setPitchSemiTones(JNIEnv *env,
		jobject obj, jfloat jpitch) {

	soundtouch::SoundTouch* soundTouch = getTouch(env, obj);

	float pitch = jpitch;
	soundTouch->setPitchSemiTones(pitch);
}

/*
 * Class:     lib_module_soundtouch_NativeSoundTouch
 * Method:    setTempoChange
 * Signature: (F)V
 */
void Java_lib_module_soundtouch_NativeSoundTouch_setTempoChange(JNIEnv *env,
		jobject obj, jfloat jtempo) {

	soundtouch::SoundTouch* soundTouch = getTouch(env, obj);

	float tempo = jtempo;
	soundTouch->setTempoChange(tempo);

}

/*
 * Class:     lib_module_soundtouch_NativeSoundTouch
 * Method:    shiftingPitch
 * Signature: ([BII)V
 */
void Java_lib_module_soundtouch_NativeSoundTouch_shiftingPitch(JNIEnv *env,
		jobject obj, jbyteArray jarray, jint offset, jint length) {

	soundtouch::SoundTouch* soundTouch = getTouch(env, obj);

	jbyte *data;

	data = env->GetByteArrayElements(jarray, JNI_FALSE);

	//soundtouch::SAMPLETYPE sampleBuffer[length];
	//memcpy(&sampleBuffer, data, length);

	//(16*1)/8=2bytes,length/2=x;x��sample

	soundTouch->putSamples((soundtouch::SAMPLETYPE*) data, length / (16 / 8));

	env->ReleaseByteArrayElements(jarray, data, 0);

}

/*
 * Class:     lib_module_soundtouch_NativeSoundTouch
 * Method:    receiveSamples
 * Signature: ([BI)I
 */
jint Java_lib_module_soundtouch_NativeSoundTouch_receiveSamples(JNIEnv *env,
		jobject obj, jbyteArray jarray, jint jLenght) {

	int receiveSamples = 0;
	int maxReceiveSamples = jLenght / (16 / 8);

	soundtouch::SoundTouch* soundTouch = getTouch(env, obj);

	jbyte *data;

	data = env->GetByteArrayElements(jarray, JNI_FALSE);

	receiveSamples = soundTouch->receiveSamples((soundtouch::SAMPLETYPE*) data,
			maxReceiveSamples);

	//memcpy(data, sampleBuffer, receiveSize);

	env->ReleaseByteArrayElements(jarray, data, 0);

	return receiveSamples;
}

/*
 * Class:     lib_module_soundtouch_NativeSoundTouch
 * Method:    soundTouchFlushLastSamples
 * Signature: ()V
 */
void Java_lib_module_soundtouch_NativeSoundTouch_soundTouchFlushLastSamples(
		JNIEnv *env, jobject obj) {

	soundtouch::SoundTouch* soundTouch = getTouch(env, obj);
	soundTouch->flush();
}

