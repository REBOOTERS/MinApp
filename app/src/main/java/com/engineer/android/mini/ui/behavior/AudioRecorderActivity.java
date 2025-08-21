package com.engineer.android.mini.ui.behavior;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.engineer.android.mini.R;
import com.engineer.common.utils.AudioRecordHelper;
import com.engineer.common.utils.AudioUtil;
import com.permissionx.guolindev.PermissionX;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AudioRecorderActivity extends AppCompatActivity {
    private static final String TAG = "AudioRecordHelper";

    int count = 0;

    private File wavFile;
    MediaPlayer mediaPlayer;

    private int second = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_audio_record);

        PermissionX.init(this).permissions(android.Manifest.permission.RECORD_AUDIO).request((allGranted, grantedList, deniedList) -> {
            if (allGranted) {
                AudioRecordHelper.INSTANCE.init(AudioRecorderActivity.this);
            }
        });


        TextView tv = findViewById(R.id.progress);
        AudioRecordHelper.INSTANCE.setCallback(value -> runOnUiThread(() -> {
            second++;
            String str = String.format(Locale.getDefault(), "%d seconds %s", second, value);
            tv.setText(str);
        }));

        TextView counter = findViewById(R.id.count);
        counter.setText(getString(R.string.record_count, count));

        counter.setOnClickListener(v -> {
            count = 0;
            second = 0;
            counter.setText(getString(R.string.record_count, count));
        });

        findViewById(R.id.start).setOnClickListener(v -> {
            second = 0;
            AudioRecordHelper.INSTANCE.startRecord(v.getContext());
            count++;
            counter.setText(getString(R.string.record_count, count));
        });

        findViewById(R.id.stop).setOnClickListener(v -> AudioRecordHelper.INSTANCE.stopRecording());

        findViewById(R.id.play).setOnClickListener(v -> {
            try {
                if (wavFile == null || !wavFile.exists()) {
                    String msg = "待播放的文件不存在";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                // 使用MediaPlayer播放音频文件
                mediaPlayer = MediaPlayer.create(AudioRecorderActivity.this, Uri.fromFile(wavFile));
                mediaPlayer.start();
            } catch (Exception e) {
                Log.e("MediaPlayer", "播放音频文件时出错", e);
            }
        });

        findViewById(R.id.play_stop).setOnClickListener(v -> stopPlay());

        findViewById(R.id.convert_to_wav).setOnClickListener(v -> {
            File pcmFile = new File(AudioRecordHelper.INSTANCE.getPcmPath());
            if (!pcmFile.exists()) {
                String msg = "待转换的文件不存在";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            AudioUtil.analyzePcmFile(pcmFile, AudioRecordHelper.sample, AudioRecordHelper.channelConfig, AudioRecordHelper.audioFormat);
            wavFile = AudioUtil.convertPcmToWav(v.getContext(), pcmFile);
        });
        findViewById(R.id.list_all_pcm).setOnClickListener(v -> {
            List<String> pcms = AudioRecordHelper.INSTANCE.getAllPcmList(this);
            StringBuilder sb = new StringBuilder();
            if (pcms != null) {
                Log.i(TAG, "pcm " + pcms);

                TextView tv1 = findViewById(R.id.pcm_list);
                tv1.setText(pcms.toString());
            }


        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 当Activity销毁时，确保停止正在播放的音频，避免内存泄漏
        stopPlay();
    }

    private void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

}
