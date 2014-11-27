
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := soundtouch

LOCAL_SRC_FILES := \
    lib_module_soundtouch_SoundTouch.cpp \
	soundtouch/AAFilter.cpp \
	soundtouch/BPMDetect.cpp \
	soundtouch/cpu_detect_x86.cpp \
	soundtouch/FIFOSampleBuffer.cpp \
	soundtouch/FIRFilter.cpp \
	soundtouch/mmx_optimized.cpp \
	soundtouch/PeakFinder.cpp \
	soundtouch/RateTransposer.cpp \
	soundtouch/SoundTouch.cpp \
	soundtouch/sse_optimized.cpp \
	soundtouch/TDStretch.cpp \

include $(BUILD_SHARED_LIBRARY)
