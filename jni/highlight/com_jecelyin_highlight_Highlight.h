/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_jecelyin_highlight_Highlight */

#ifndef _Included_com_jecelyin_highlight_Highlight
#define _Included_com_jecelyin_highlight_Highlight
#ifdef __cplusplus
extern "C" {
#endif

/*jint JNI_OnLoad(JavaVM* vm, void* reserved);*/


/*
 * Class:     com_jecelyin_highlight_Highlight
 * Method:    read_file
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_jecelyin_highlight_Highlight_read_1file
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_jecelyin_highlight_Highlight
 * Method:    jni_parse
 * Signature: (Ljava/lang/String;Ljava/lang/String;)[[I
 */
JNIEXPORT jintArray JNICALL Java_com_jecelyin_highlight_Highlight_jni_1parse
  (JNIEnv *, jclass, jstring, jstring);

#ifdef __cplusplus
}
#endif
#endif
