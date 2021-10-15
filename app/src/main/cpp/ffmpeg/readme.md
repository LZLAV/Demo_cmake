# FFmpeg

### FFmpeg 命令行
    ffmpeg -s 1280*720 -pix_fmt yuv444p -i excel_01.yuv -pix_fmt yuv420p excel420.yuv -y
    s 用于指出源文件格式
    -pix_fmt 指出像素格式为 yuv444p
    -i 指出输入文件为excel_01.yuv
    第二个-pix_fmt指出输出像素格式 excel420.yuv为输出文件名
    -y为选择覆盖已有文件（即当已有同名文件，则覆盖）

    ffmpeg -s 1280*720 -pix_fmt yuv420p -i excel420.yuv -vcodec libx265 excel265.265 -y
        -vcodec 用于指出编码器 libx265为H265编码器，excel265.265为输出文件名
    
    ffmpeg -i excel265.265 -r 25 -pix_fmt yuv420p  -vcodec libx265 -preset  veryslow -b:v 32k -profile main -crf  32 -strict -2 excel_enc.265 -y
        -r 指出视频每秒帧数
        -preset设置编码速度模式
            preset有如下参数可用：
                ultrafast, superfast, veryfast, faster, fast, medium, slow, slower, veryslow and placebo.
        -b:v 设置码率为32k
        -profile 设置模式为main
        -crf (固定码率因子)设置画面质量（默认为23，一般使用18-28之间的数值）
            264和265中默认的质量/码率控制设置。这个值可以在0到51之间，值越低，质量越好，文件大小越大
        -strict 严格编码，excel_enc.265输出文件名。-strict -2 是实验参数表示使用aac音频编码
    
    ffmpeg -i excel_enc.265 -c:v rawvideo -pix_fmt yuv444p excel_dec.yuv -y
        -c:v 设置解码器 ，excel_dec.yuv输出文件格式


### FFmpeg示例
    init：mediaplayer
        av_register_all();
        avformat_network_init();
        av_log_set_callback(ffmpeg_log_callback);
        av_init_packet(&mFlushPacket);

    open：mediaplayer
        avformat_open_input(&mFormatContext, file, NULL, NULL)
        AVCodec* lenthevc_dec = avcodec_find_decoder_by_name("codecname");
        AVCodec* codec = avcodec_find_decoder(mFormatContext->streams[0]->codec->codec_id);
        avformat_find_stream_info(mFormatContext, NULL)
        
        prepareAudio：
            mAudioStreamIndex : 为音轨 index
            AVStream* stream = mFormatContext->streams[mAudioStreamIndex];
            AVCodecContext* codec_ctx = stream->codec;
            //获取格式
            av_get_sample_fmt_string(buffer, 128, codec_ctx->sample_fmt);
            //获取 codec
            AVCodec* codec = avcodec_find_decoder(codec_ctx->codec_id ? codec_ctx->codec_id : CODEC_ID_MP3)
            //open codec
            avcodec_open2(codec_ctx, codec, NULL)

        prepareVideo：
            AVStream* stream = mFormatContext->streams[mVideoStreamIndex];
            AVCodecContext* codec_ctx = stream->codec;
            AVCodec* codec = avcodec_find_decoder(codec_ctx->codec_id);
            avcodec_open2(codec_ctx, codec, NULL)
            //设置视频多线程(ffmpeg)
            codec_ctx->thread_count = mThreadNumber;
            codec_ctx->thread_type = FF_THREAD_FRAME;
                
    start：MediaPlayer
        av_seek_frame(mFormatContext, mAudioStreamIndex, 0, AVSEEK_FLAG_BACKWARD);
        av_seek_frame(mFormatContext, mVideoStreamIndex, 0, AVSEEK_FLAG_BACKWARD);
            AudioDecode
                AVStream：stream_audio
                onDecoded：audioOutput
            VideoDecode
                AVStream：stream_video
                onDecoded：videoOutput
                    
        startDecoding：
            av_read_frame(mFormatContext, packet)   //音频包入队，视频包入队
            Audio：
                av_frame_alloc();
                while{
                    avcodec_decode_audio4(mStream->codec, mFrame, &gotFrame, packet);
                    解码获取音频帧，然后交给 AudioTrack 播放
                }
            
            video：
                av_frame_alloc();
                while{
                    解码视频包，获取视频帧，然后存入到 FrameQueue 中（显示时做同步处理）
                }

        startRendering：
            视频渲染，同步音频pts