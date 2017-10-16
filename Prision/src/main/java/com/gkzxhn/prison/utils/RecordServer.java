package com.gkzxhn.prison.utils;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by yun on 2017/7/27.
 */

public class RecordServer extends Service {
    static final int frequency = 44100;//44100;
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int recBufSize, playBufSize;
    AudioRecord audioRecord;
    AudioTrack audioTrack;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(" RecordPlay", "RecordServer  onCreate");

        recBufSize = AudioRecord.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                channelConfiguration, audioEncoding, recBufSize);

        // -----------------------------------------

        playBufSize = AudioTrack.getMinBufferSize(frequency,
                channelConfiguration, audioEncoding);
        audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, frequency,
                channelConfiguration, audioEncoding,
                playBufSize, AudioTrack.MODE_STREAM);
        Log.e(" RecordPlay", "RecordPlayThread().start()");
        new RecordPlayThread().start();// 开一条线程边录边放

        Log.e(" RecordPlay", "RecordPlayThread().start()");
    }

    @Override
    public void onDestroy() {
        Log.e(" RecordPlay", "RecordServer onDestroy()");
        Toast.makeText(getApplicationContext(), "RecordServer onDestroy()", Toast.LENGTH_LONG).show();
        audioRecord.stop();
        audioRecord.release();
        audioTrack.stop();
        audioTrack.release();
        super.onDestroy();
    }

    class RecordPlayThread extends Thread {
        public void run() {
            Log.e(" RecordPlay", "RecordPlayThread  run()");
            try {
                Log.e(" RecordPlay", "RecordPlayThread() run()  try startRecording  play");
                byte[] buffer = new byte[recBufSize];
                audioRecord.startRecording();//开始录制
                audioTrack.play();//开始播放

                while (true) {
                    Log.e(" RecordPlay", "--------------RecordPlaying--------------");
                    //从MIC保存数据到缓冲区
                    int bufferReadResult = audioRecord.read(buffer, 0,
                            recBufSize);

                    byte[] tmpBuf = new byte[bufferReadResult];
                    System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
                    //写入数据即播放
                    audioTrack.write(tmpBuf, 0, tmpBuf.length);
                }
            } catch (Throwable t) {
            }
        }
    }
}
