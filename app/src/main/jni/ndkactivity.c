#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

#include <android/log.h>
#include "bsdiff.h"
#include "bspatch.h"

#define LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"neo",FORMAT,__VA_ARGS__)

JNIEXPORT jstring JNICALL Java_xxx_test_allapplication_activity_NDKActivity_getStringFromC(JNIEnv *env, jclass type) {
    //bsdiff(4,);

    return (*env)->NewStringUTF(env, "123");
}

JNIEXPORT jstring JNICALL
Java_xxx_test_allapplication_activity_NDKActivity_modifyField(JNIEnv *env, jobject jobj) {

//jobj是t对象，JniTest.class
    jclass cls = (*env)->GetObjectClass(env, jobj);
    //jfieldID
    //属性名称，属性签名
    jfieldID fid = (*env)->GetFieldID(env, cls, "key", "Ljava/lang/String;");

    //jason >> super jason
    //获取key属性的值
    //Get<Type>Field
    jstring jstr = (*env)->GetObjectField(env, jobj, fid);
    printf("jstr:%#x\n",&jstr);

    //jstring -> c字符串
    //isCopy 是否复制（true代表赋值，false不复制）
    char *c_str = (*env)->GetStringUTFChars(env,jstr,JNI_FALSE);
    //拼接得到新的字符串
    char text[20] = "先开了口 ";
    strcat(text,c_str);

    //c字符串 ->jstring
    jstring new_jstr = (*env)->NewStringUTF(env, text);

    //修改key
    //Set<Type>Field
    (*env)->SetObjectField(env, jobj, fid, new_jstr);

    printf("new_jstr:%#x\n", &new_jstr);


    return new_jstr;
}

//获取文件大小
long get_file_size(char *path){
    FILE *fp = fopen(path,"rb");
    fseek(fp,0,SEEK_END);
    return ftell(fp);
}

JNIEXPORT void JNICALL
Java_xxx_test_allapplication_activity_NDKActivity_diff__Ljava_lang_String_2Ljava_lang_String_2I(
        JNIEnv *env, jclass type, jstring path_, jstring path_pattern_, jint file_num) {
    const char *path = (*env)->GetStringUTFChars(env, path_, 0);
    const char *path_pattern = (*env)->GetStringUTFChars(env, path_pattern_, 0);

    //得到分割之后的子文件的路径列表
    char **patches = malloc(sizeof(char*) * file_num);
    int i = 0;
    for (; i < file_num; i++) {
        patches[i] = malloc(sizeof(char) * 100);
        //元素赋值
        //需要分割的文件：C://jason/liuyan.png
        //子文件：C://jason/liuyan_%d.png
        sprintf(patches[i], path_pattern, (i+1));
        LOGI("patch path:%s",patches[i]);
    }

    //不断读取path文件，循环写入file_num个文件中
    //	整除
    //	文件大小：90，分成9个文件，每个文件10
    //	不整除
    //	文件大小：110，分成9个文件，
    //	前(9-1)个文件为(110/(9-1))=13
    //	最后一个文件(110%(9-1))=6
    int filesize = get_file_size(path);
    LOGI("源文件文件大小 %d",filesize);
    FILE *fpr = fopen(path,"rb");
    //整除
    if(filesize % file_num == 0){
        //单个文件大小
        int part = filesize / file_num;
        i = 0;
        //逐一写入不同的分割子文件中
        for (; i < file_num; i++) {
            FILE *fpw = fopen(patches[i], "wb");
            int j = 0;
            for(; j < part; j++){
                //边读边写
                fputc(fgetc(fpr),fpw);
            }
            fclose(fpw);
        }
    }
    else{
        //不整除
        int part = filesize / (file_num - 1);
        i = 0;
        //逐一写入不同的分割子文件中
        for (; i < file_num - 1; i++) {
            FILE *fpw = fopen(patches[i], "wb");
            int j = 0;
            for(; j < part; j++){
                //边读边写
                fputc(fgetc(fpr),fpw);
            }
            fclose(fpw);
        }
        //the last one
        FILE *fpw = fopen(patches[file_num - 1], "wb");
        i = 0;
        for(; i < filesize % (file_num - 1); i++){
            fputc(fgetc(fpr),fpw);
        }
        fclose(fpw);
    }

    //关闭被分割的文件
    fclose(fpr);

    //释放
    i = 0;
    for(; i < file_num; i++){
        free(patches[i]);
    }
    free(patches);

    (*env)->ReleaseStringUTFChars(env, path_, path);
    (*env)->ReleaseStringUTFChars(env, path_pattern_, path_pattern);
}

JNIEXPORT void JNICALL
Java_xxx_test_allapplication_activity_NDKActivity_patch__Ljava_lang_String_2ILjava_lang_String_2(
        JNIEnv *env, jclass type, jstring path_pattern_, jint file_num, jstring merge_path_) {
    const char *path_pattern = (*env)->GetStringUTFChars(env, path_pattern_, 0);
    const char *merge_path = (*env)->GetStringUTFChars(env, merge_path_, 0);

//得到分割之后的子文件的路径列表
    char **patches = malloc(sizeof(char*) * file_num);
    int i = 0;
    for (; i < file_num; i++) {
        patches[i] = malloc(sizeof(char) * 100);
        //元素赋值
        //需要分割的文件：C://jason/liuyan.png
        //子文件：C://jason/liuyan_%d.png
        sprintf(patches[i], path_pattern, (i+1));
        LOGI("patch path:%s",patches[i]);
    }

    FILE *fpw = fopen(merge_path,"wb");
    //把所有的分割文件读取一遍，写入一个总的文件中
    i = 0;
    for(; i < file_num; i++){
        //每个子文件的大小
        int filesize = get_file_size(patches[i]);
        FILE *fpr = fopen(patches[i], "rb");
        int j = 0;
        for (; j < filesize; j++) {
            fputc(fgetc(fpr),fpw);
        }
        fclose(fpr);
    }
    fclose(fpw);

    //释放
    i = 0;
    for(; i < file_num; i++){
        free(patches[i]);
    }
    free(patches);

    (*env)->ReleaseStringUTFChars(env, path_pattern_, path_pattern);
    (*env)->ReleaseStringUTFChars(env, merge_path_, merge_path);
}

JNIEXPORT void JNICALL//生成差分包
Java_xxx_test_allapplication_activity_NDKActivity_bisdiff(JNIEnv *env, jclass type,
                                                          jstring oldPath_, jstring newPath_,
                                                          jstring patch_path_) {
    const char *oldPath = (*env)->GetStringUTFChars(env, oldPath_, 0);
    const char *newPath = (*env)->GetStringUTFChars(env, newPath_, 0);
    const char *patch_path = (*env)->GetStringUTFChars(env, patch_path_, 0);
    char *argv[4];
    argv[0] = "";
    argv[1] = (char *) oldPath;
    argv[2] = (char *) newPath;
    argv[3] = (char *) patch_path;
    bsdiff(4,argv);

    (*env)->ReleaseStringUTFChars(env, oldPath_, oldPath);
    (*env)->ReleaseStringUTFChars(env, newPath_, newPath);
    (*env)->ReleaseStringUTFChars(env, patch_path_, patch_path);
}

JNIEXPORT void JNICALL//合并差分包
Java_xxx_test_allapplication_activity_NDKActivity_bspatch(JNIEnv *env, jclass type,
                                                          jstring oldPath_, jstring newPath_,
                                                          jstring patch_path_) {
    const char *oldPath = (*env)->GetStringUTFChars(env, oldPath_, 0);
    const char *newPath = (*env)->GetStringUTFChars(env, newPath_, 0);
    const char *patch_path = (*env)->GetStringUTFChars(env, patch_path_, 0);

    char *argv[4];
    argv[0] = "";
    argv[1] = (char *) oldPath;
    argv[2] = (char *) newPath;
    argv[3] = (char *) patch_path;
    bspatch(4,argv);
    (*env)->ReleaseStringUTFChars(env, oldPath_, oldPath);
    (*env)->ReleaseStringUTFChars(env, newPath_, newPath);
    (*env)->ReleaseStringUTFChars(env, patch_path_, patch_path);
}
