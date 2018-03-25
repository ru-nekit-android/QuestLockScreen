package ru.nekit.android.qls.setupWizard;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

public class VoiceCenter {

    private MediaRecorder mRecorder;
    private MediaPlayer mMediaPlayer;

    public VoiceCenter() {
    }

    public static String getVoiceFilePath(@NonNull Type type) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                "Voice Recorder" +
                File.separator +
                type.getName() +
                ".m4a";
    }

    public void startRecording(@NonNull Type type) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            mRecorder.setAudioEncodingBitRate(48000);
        } else {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioEncodingBitRate(64000);
        }
        mRecorder.setAudioSamplingRate(16000);
        File outputFile = new File(getVoiceFilePath(type));
        outputFile.getParentFile().mkdirs();
        mRecorder.setOutputFile(outputFile.getAbsolutePath());
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException ignored) {
        }
    }

    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public boolean recordingIsAlive() {
        return mRecorder != null;
    }

    public void playVoice(@NonNull Type type) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getVoiceFilePath(type));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException error) {
            stopVoice();
        }
    }

    public void stopVoice() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (IllegalStateException ignore) {
            }
        }
    }

    public enum Type {

        RIGHT("right"),
        WRONG("wrong");

        private String mName;

        Type(@NonNull String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }
}