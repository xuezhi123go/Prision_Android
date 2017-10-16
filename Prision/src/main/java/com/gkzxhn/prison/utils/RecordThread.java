package com.gkzxhn.prison.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by 方 on 2017/7/28.
 */


public class RecordThread extends Thread {
    static final int frequency = 44100;
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private boolean flag = true;
    @Override
    public void run() {
        // TODO Auto-generated method stub
        int recBufSize = AudioRecord.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding)*2;
        int plyBufSize = AudioTrack.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding)*2;

        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                channelConfiguration, audioEncoding, recBufSize);

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                channelConfiguration, audioEncoding, plyBufSize, AudioTrack.MODE_STREAM);

        byte[] recBuf = new byte[recBufSize];
        audioRecord.startRecording();
        audioTrack.play();
        while(flag){
            int readLen = audioRecord.read(recBuf, 0, recBufSize);
            Log.i("RecordThread", "正在输出本地音频: readLen  : " + readLen);
            Log.i("RecordThread", "正在输出本地音频: recBuf  : " + readLen);
            audioTrack.write(recBuf, 0, readLen);
        }
        Log.i("RecordThread", "run: 停止输出本地音频...");
        audioRecord.stop();
        audioRecord.release();
        audioTrack.stop();
        audioTrack.release();
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
