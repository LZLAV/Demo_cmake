package com.lzlbuilder.demo.androids.media.video;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * @author liuzuoliang
 * @Description Android MediaCodec 的基本使用
 * @Date    2021-10-05
 * @E-Mail Address：
 */
class AMediaCodec {

    private static final int SUCCESS = 0;
    private static final int ERROR = -4;
    private static int TIMEOUT = 2_000;
    private static final String TAG = AMediaCodec.class.getSimpleName();

    /**
     * 状态
     */
    private boolean isStart = false;        //开始
    private boolean isEndOfInput = false;   //是否输入结束
    private boolean isEnded = false;        //编码器是否停止

    /** 编解码器 */
    protected MediaCodec mediaCodec;

    protected final boolean LOLLIPOP_PLUS_API;

    {
        //5.1 兼容性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LOLLIPOP_PLUS_API = true;
        } else {
            LOLLIPOP_PLUS_API = false;
        }
    }

    /**
     * 配置编解码器
     * @param mediaFormat   媒体相关格式
     * @param mimeType      媒体类型
     * @param CODEC_FLAG    编码还是解码  MediaCodec.CONFIGURE_FLAG_ENCODE
     */
    public void buildMediaCodec(MediaFormat mediaFormat,String mimeType,int CODEC_FLAG)
    {
        try {
            //createByCodecName(codecName);
            mediaCodec = MediaCodec.createEncoderByType(mimeType);
            mediaCodec.configure(mediaFormat, null, null, CODEC_FLAG);
            Log.d(TAG,"----编解码器配置成功----");
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
            if (mediaCodec != null) mediaCodec.release();
        }
    }


    /**
     * 编码
     * @param data  待编码数据
     * @param codecInfo 输入数据的相关信息，pts,isEOF 等
     */
    public void encode(byte[] data,CodecInfo codecInfo)
    {
        int inputFlag = 0;
        //指定超时时间
        int inputIndex = mediaCodec.dequeueInputBuffer(TIMEOUT);
        if (inputIndex >= 0) {
            if (codecInfo.isEOS) {
                //文件结尾，增加结尾标识
                inputFlag = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
            }
        }

        ByteBuffer inputBuff = getInputBuffer(inputIndex);

        inputBuff.clear();
        inputBuff.put(data);
        inputBuff.flip();

        mediaCodec.queueInputBuffer(inputIndex, 0, inputBuff.remaining(), codecInfo.ptsUs, inputFlag);
    }


    /**
     * 获取编码后的数据
     * @param buffer    编码后的数据
     * @param bufferInfo    编码后数据的相关信息
     * @param timeUs    指定超时时间，视频   1s/frameRate，单位微妙
     * @return
     */
    public int getEncodeData(ByteBuffer buffer,MediaCodec.BufferInfo bufferInfo,long timeUs)
    {
        //第二个参数已微秒为单位的timeout时间，负数 永不超时
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, timeUs);
        if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            Log.w(TAG, "encoded buffer ==> INFO_TRY_AGAIN_LATER");
            return MediaCodec.INFO_TRY_AGAIN_LATER;
        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            Log.d(TAG, "encoded ==> INFO_OUTPUT_FORMAT_CHANGED");
            return MediaCodec.INFO_OUTPUT_FORMAT_CHANGED;
        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            Log.d(TAG, "encoded ==> INFO_OUTPUT_BUFFERS_CHANGED");
            return MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED;
        } else if (outputBufferIndex >= 0) {
            ByteBuffer output = getOutputBuffer(outputBufferIndex);

            if (!LOLLIPOP_PLUS_API) {
                output.position(0);
                output.limit(bufferInfo.size);
            }

            buffer.put(output);
            buffer.flip();

            boolean isEOS = false;
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {

            }
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            return SUCCESS;
        }
        return ERROR;
    }

    /**
     * 获取指定 index 的输出缓冲区
     * @param index
     * @return
     */
    protected ByteBuffer getOutputBuffer(int index)
    {
        if (LOLLIPOP_PLUS_API) {
            return mediaCodec.getOutputBuffer(index);
        } else {
            return mediaCodec.getOutputBuffers()[index];
        }
    }

    /**
     * 获取指定的输入缓冲区
     * @param index
     * @return
     */
    protected ByteBuffer getInputBuffer(int index)
    {
        if (LOLLIPOP_PLUS_API) {
            return mediaCodec.getInputBuffer(index);
        } else {
            return mediaCodec.getInputBuffers()[index];
        }
    }

    /**
     * 编解码器停止工作
     */
    public void stop(){

        isStart = false;
        isEndOfInput = false;
        isEnded = false;
        try {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class CodecInfo
    {
        /** 时间戳，μs微秒 为单位 */
        public long ptsUs;
        public boolean isEOS;

        /** true:audio false:video */
        public boolean isAudioSample;
    }

    /******  surface 模式 *****/
    /**
     * 创建 surface，供数据输入测使用
     * @return
     */
    public Surface getInputSurface(){
        if(mediaCodec !=null)
        {
            return mediaCodec.createInputSurface();
        }
        return null;
    }

    /**
     * 指定输入 surface
     * @param surface
     */
    public void setInputSurface(Surface surface)
    {
        if(mediaCodec!=null)
        {
            mediaCodec.setInputSurface(surface);
        }
    }

    /**
     * 指定输出 surface
     * @param surface
     */
    public void setOutputSurface(Surface surface)
    {
        if(mediaCodec!=null)
        {
            mediaCodec.setOutputSurface(surface);
        }
    }




    /**
     * 音频、视频格式
     */
    class AMediaFormat{

        public  void getCommonAudioFormat(){

        }

        public void getCommonVideoFormat(){

        }
    }

}
