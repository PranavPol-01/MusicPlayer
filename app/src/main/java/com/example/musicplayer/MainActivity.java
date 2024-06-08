package com.example.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button btnPlayPause, btnNext, btnPrevious;
    private TextView tvTrackInfo;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private int[] audioFiles = {R.raw.song1, R.raw.song2};
    private int currentTrackIndex = 0;
    private Timer timer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        tvTrackInfo = findViewById(R.id.tv_track_info);
        seekBar = findViewById(R.id.seekBar);

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    pauseMusic();
                } else {
                    playMusic();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMusic();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMusic();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, audioFiles[currentTrackIndex]);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextMusic();
                }
            });
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            startSeekBarUpdate();
        } else if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startSeekBarUpdate();
        }
        isPlaying = true;
        btnPlayPause.setText("Pause");
        tvTrackInfo.setText("Playing song " + (currentTrackIndex + 1));
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopSeekBarUpdate();
        }
        isPlaying = false;
        btnPlayPause.setText("Play");
    }

    private void nextMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentTrackIndex = (currentTrackIndex + 1) % audioFiles.length;
        playMusic();
    }

    private void previousMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentTrackIndex = (currentTrackIndex - 1 + audioFiles.length) % audioFiles.length;
        playMusic();
    }

    private void startSeekBarUpdate() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        }, 0, 1000);
    }

    private void stopSeekBarUpdate() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopSeekBarUpdate();
    }
}
