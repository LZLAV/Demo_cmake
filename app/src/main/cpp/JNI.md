# JNI

### 获取JavaVM
    获取 JVM(全局)
    static JavaVM *gVM;
    jint JNI_OnLoad(JavaVM* vm, void* reserved){
        gVM = vm;
        if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
            LOGE("GetEnv failed! \n");
            return JNI_ERR;
        }
    }

### env
    获取 env，并附加到当前线程
    gVM->GetEnv((void**) &env, JNI_VERSION_1_4);
    gVM->AttachCurrentThread(&env, NULL);
    gVM->DetachCurrentThread()

### 异常
    抛出异常
    env->ThrowNew(exceptionClass, msg)

### 回调 Java
    反射回调Java
    jclass clazz = env->FindClass("全类名");
    env->GetStaticMethodID(clazz,"方法名","参数签名");

##### 注意
    方法名ID 静态变量，类对象存储为全局变量（避免重复获取影响性能）


### String与 const char*
    String -->const char*
        const char *pathStr = env->GetStringUTFChars(path, NULL);
            特别说明，该函数第二个参数，标识字符是否进行了拷贝(内部抉择是否拷贝)
        env->ReleaseStringUTFChars(path, pathStr);

### array
    int *nativeParams = (int*)env->GetIntArrayElements(params, NULL);
    env->ReleaseIntArrayElements(params, nativeParams, 0);
        //函数原型
        void Release<PrimitiveType>ArrayElements(JNIEnv *env,ArrayType array, NativeType *elems, jint mode);
        特别说明，该函数第三个参数 mode 标识数组将进行如何处理，有三个值可选：
            0               拷贝回内容并释放elem 缓存
            JNI_COMMIT      拷贝回内存但不释放 elem 缓存
            JNI_ABORT       释放缓存而不拷贝回可能的改变
    jsize len = (*env)->GetArrayLength(env, intArr);

### C与C++区别
#### C
可直接强制转换
const long *str;
str = (*env)->GetStringUTFChars(env, prompt, &isCp);



#### C++
强制转换受限
const long *str;    //不可强制转换成其他类型
const char *str;
str = env->GetStringUTFChars(prompt, &isCp);
