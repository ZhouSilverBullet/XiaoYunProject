package com.sdxxtop.robotproject.utils;

import android.media.MediaPlayer;
import android.os.Environment;

import com.sdxxtop.robotproject.skill.SpeechSkill;

import java.io.File;
import java.io.IOException;

public class ReportMediaPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mPlayer;
    private boolean isStarting;
    private boolean isPause;
    private static ReportMediaPlayer reportMediaPlayer;

    public static ReportMediaPlayer getInstance() {
        if (reportMediaPlayer == null) {
            reportMediaPlayer = new ReportMediaPlayer();
        }
        return reportMediaPlayer;
    }

    private ReportMediaPlayer() {
    }

    public void play() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        if (mPlayer.isPlaying()) {
            return;
        }

        SpeechSkill.getInstance().setPlaying(true);

        if (isPause) {
            mPlayer.start();
            if (reportListener != null) {
                reportListener.onStart();
            }
            return;
        }
        isStarting = mPlayer.isPlaying();
        try {
            File directory = Environment.getExternalStorageDirectory();
            mPlayer.setDataSource(directory + "/bobao.mp3");
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.prepare();
            mPlayer.start();

            if (reportListener != null) {
                reportListener.onStart();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        if (mPlayer != null) {

            SpeechSkill.getInstance().setPlaying(true);
            if (reportListener != null) {
                reportListener.onStart();
            }

            mPlayer.stop();
            try {
                mPlayer.prepare();//stop后下次重新播放要首先进入prepared状态
                mPlayer.seekTo(0);//须将播放时间设置到0；这样才能在下次播放是重新开始，否则会继续上次播放
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isStarting = false;
        SpeechSkill.getInstance().setPlaying(false);
        if (mPlayer != null) {
            mPlayer.release();
        }

        if (reportListener != null) {
            reportListener.onStop();
        }
    }

    public void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
        if (reportListener != null) {
            reportListener.onStop();
        }
        SpeechSkill.getInstance().setPlaying(false);
    }

    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
            isStarting = false;
            isPause = true;
        }
        if (reportListener != null) {
            reportListener.onStop();
        }
        SpeechSkill.getInstance().setPlaying(false);
    }

    public boolean isPause() {
        return isPause;
    }

    public boolean isStarting() {
        return isStarting;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        SpeechSkill.getInstance().playTxt("播报异常");
//            if (mp != null) {
        stop();
        if (mPlayer != null) {
            mPlayer.release();
        }
        if (reportListener != null) {
            reportListener.onStop();
        }
//            }
//        }finally {
//        }
        isStarting = false;
        return false;
    }

    private ReportListener reportListener;

    public void setReportListener(ReportListener reportListener) {
        this.reportListener = reportListener;
    }

    public interface ReportListener {
        void onStart();

        void onStop();
    }
}
