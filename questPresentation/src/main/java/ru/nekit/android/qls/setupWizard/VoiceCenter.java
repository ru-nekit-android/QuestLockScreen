package ru.nekit.android.qls.setupWizard;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import ru.nekit.android.qls.domain.model.AnswerType;

public class VoiceCenter {

    private MediaRecorder recorder;
    private MediaPlayer mediaPlayer;

    public VoiceCenter() {
    }

    public static String getVoiceFilePath(@NonNull AnswerType type) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                "Voice Recorder" +
                File.separator +
                type.name() +
                ".m4a";
    }

    public void startRecording(@NonNull AnswerType type) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            recorder.setAudioEncodingBitRate(48000);
        } else {
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(64000);
        }
        recorder.setAudioSamplingRate(16000);
        File outputFile = new File(getVoiceFilePath(type));
        outputFile.getParentFile().mkdirs();
        recorder.setOutputFile(outputFile.getAbsolutePath());
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException ignored) {
        }
    }

    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    public boolean recordingIsAlive() {
        return recorder != null;
    }

    public void playVoice(@NonNull AnswerType type) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getVoiceFilePath(type));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException error) {
            stopVoice();
        }
    }

    public void stopVoice() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (IllegalStateException ignore) {
            }
        }
    }
}