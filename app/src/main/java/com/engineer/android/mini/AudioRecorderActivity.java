package com.engineer.android.mini;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.engineer.common.utils.AudioRecordHelper;
import com.engineer.common.utils.AudioUtil;
import com.permissionx.guolindev.PermissionX;

import java.io.File;

import io.microshow.rxffmpeg.RxFFmpegCommandList;
import io.microshow.rxffmpeg.RxFFmpegInvoke;

public class AudioRecorderActivity extends AppCompatActivity {
    private static final String TAG = "AudioRecordHelper";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_audio_record);

        PermissionX.init(this)
                .permissions(android.Manifest.permission.RECORD_AUDIO)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        AudioRecordHelper.INSTANCE.init(AudioRecorderActivity.this);
                    }
                });


        TextView tv = findViewById(R.id.progress);
        AudioRecordHelper.INSTANCE.setCallback(new AudioRecordHelper.Callback() {
            @Override
            public void progress(@NonNull String value) {
                runOnUiThread(() -> tv.setText(value));
            }
        });

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecordHelper.INSTANCE.startRecord();
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecordHelper.INSTANCE.stopRecording();
            }
        });

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] command = playCommand();


                RxFFmpegInvoke.getInstance().runCommand(command, new RxFFmpegInvoke.IFFmpegListener() {
                    @Override
                    public void onFinish() {
                        Log.d(TAG, "onFinish() called ${Thread.currentThread().name}");
                    }

                    @Override
                    public void onProgress(int progress, long progressTime) {
                        Log.d(TAG, "onProgress() called with: progress = $progress, progressTime = $progressTime");
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(String message) {
                        Log.e(TAG, message);
                    }
                });
            }
        });

        findViewById(R.id.convert_to_wav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File pcmFile = new File(AudioRecordHelper.INSTANCE.getPcmPath());
                AudioUtil.convertPcmToWav(v.getContext(), pcmFile);
            }
        });
    }


    // ffplay -f s16le -ar 16000 -ac 1 -i raw.pcm
    private String[] playCommand() {
        String path = AudioRecordHelper.INSTANCE.getPcmPath();

        RxFFmpegCommandList list = new RxFFmpegCommandList();
        list.append("ffplay");
        list.append("-f");
        list.append("s16le");
        list.append("-ar");
        list.append("16000");
        list.append("-ac");
        list.append("1");
        list.append("-i");
        list.append(path);

        return list.build();
    }

}
