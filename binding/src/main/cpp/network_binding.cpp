#include <jni.h>

#include "network/network.h"

extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_janus_binding_Network_getLinkLayerEnd(JNIEnv* env, jclass clazz, jbyteArray data, jint length) {
    jboolean isCopy;
    jbyte* bytes = env->GetByteArrayElements(data, &isCopy);
    if (!bytes)
        return -1;
    return net_get_link_layer_end((unsigned char*) bytes, (int) length);
}

extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_janus_binding_Network_getNetworkLayerEnd(JNIEnv* env, jclass clazz, jbyteArray data, jint length) {
    jboolean isCopy;
    jbyte* bytes = env->GetByteArrayElements(data, &isCopy);
    if (!bytes)
        return -1;
    return net_get_network_layer_end((unsigned char*) bytes, (int) length);
}

extern "C" JNIEXPORT jint JNICALL
Java_me_spencernold_janus_binding_Network_getTransportLayerEnd(JNIEnv* env, jclass clazz, jbyteArray data, jint length) {
    jboolean isCopy;
    jbyte* bytes = env->GetByteArrayElements(data, &isCopy);
    if (!bytes)
        return -1;
    return net_get_transport_layer_end((unsigned char*) bytes, (int) length);
}