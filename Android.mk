LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := XOSPDelta
LOCAL_MODULE_TAGS := optional
LOCAL_PRIVILEGED_MODULE := true

LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main)

LOCAL_SDK_VERSION := 23

LOCAL_JNI_SHARED_LIBRARIES := libopendelta
LOCAL_REQUIRED_MODULES := libopendelta

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
