#include <jni.h>

#include "pcap/pcap_impl.h"

JavaVM* globalJvm = nullptr;
jobject globalListener = nullptr;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    globalJvm = vm;
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT jlong JNICALL
Java_me_spencernold_janus_binding_PacketCapture_pcOpen(JNIEnv* env, jclass clazz) {
    return (jlong) pch_open();
}

extern "C" JNIEXPORT jstring JNICALL
Java_me_spencernold_janus_binding_PacketCapture_pcGetInterfaceName(JNIEnv* env, jclass clazz, jlong handle) {
    pcap_handle* pcap = (pcap_handle*) handle;
    char* interface = pcap->interface;
    return env->NewStringUTF(interface);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_me_spencernold_janus_binding_PacketCapture_pcSend(JNIEnv* env, jclass clazz, jlong handle, jbyteArray data, jint offset, jint length) {
    pcap_handle* pcap = (pcap_handle*) handle;
    jboolean isCopy;
    jbyte* bytes = env->GetByteArrayElements(data, &isCopy);
    if (!bytes)
        return 0;
    jbyte* start = bytes + offset;
    pch_send(pcap, (const unsigned char*) start, (jint) length);
    env->ReleaseByteArrayElements(data, bytes, 0);
    return 1;
}

void listener_function(pcap_handle* pcap, const unsigned char* data, int length) {
    if (!globalJvm || !globalListener)
        return;
    JNIEnv* env = NULL;
    jint res = globalJvm->GetEnv((void**) &env, JNI_VERSION_1_6);
    if (res != JNI_OK)
        return;
    jclass listenerClass = env->GetObjectClass(globalListener);
    jmethodID listenMethod = env->GetMethodID(listenerClass, "listen", "(J[BI)V");
    if (!listenMethod)
        return;
    jbyteArray array = env->NewByteArray((jsize) length);
    env->SetByteArrayRegion(array, 0, length, (const jbyte*) data);
    env->CallVoidMethod(globalListener, listenMethod, (jlong) pcap, array, length);
    env->DeleteLocalRef(array);
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_janus_binding_PacketCapture_pcListen(JNIEnv* env, jclass clazz, jlong handle, jobject listener) {
    if (globalListener)
        env->DeleteGlobalRef(globalListener);
    globalListener = env->NewGlobalRef(listener);
    pch_listen((pcap_handle*) handle, listener_function);
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_janus_binding_PacketCapture_pcIgnore(JNIEnv* env, jclass clazz, jlong handle) {
    pch_ignore((pcap_handle*) handle);
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_janus_binding_PacketCapture_pcClose(JNIEnv* env, jclass clazz, jlong handle) {
    if (globalListener) {
        env->DeleteGlobalRef(globalListener);
        globalListener = NULL;
    }
    pch_close((pcap_handle*) handle);
}