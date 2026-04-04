#include <jni.h>

#include "firewall/firewall.h"

extern "C" JNIEXPORT jlong JNICALL
Java_me_spencernold_janus_binding_NativeFirewall_fwStart(JNIEnv* env, jclass clazz, jint protocol, jint port, jint action) {
    return (jlong) fw_start(protocol, port, action);
}

extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_janus_binding_NativeFirewall_fwRule(JNIEnv* env, jclass clazz, jlong handle, jint action, jlong network, jlong broadcast) {
    return fw_write_rule((firewall_t*) handle, action, (uint32_t) network, (uint32_t) broadcast);
}

extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_janus_binding_NativeFirewall_fwCommit(JNIEnv* env, jclass clazz, jlong handle) {
    return fw_commit((firewall_t*) handle);
}

extern "C" JNIEXPORT void JNICALL
Java_me_spencernold_janus_binding_NativeFirewall_fwStop(JNIEnv* env, jclass clazz, jlong handle) {
    fw_stop((firewall_t*) handle);
}