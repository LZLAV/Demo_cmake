
# C Thread

pthread_create(&mDecodingThread, NULL, startDecoding, NULL)
pthread_join(mThread, NULL)

### 互斥量
pthread_mutex_lock(&mLock)
pthread_mutex_unlock(&mLock)
pthread_mutex_destroy(&mLock)

### 条件变量
pthread_cond_init(&mCondition, NULL)
pthread_cond_wait(&sPlayer->mCondition, &sPlayer->mLock)
pthread_cond_signal(&mCondition)
pthread_cond_destroy(&mCondition)