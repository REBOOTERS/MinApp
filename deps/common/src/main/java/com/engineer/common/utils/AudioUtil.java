package com.engineer.common.utils;

import static com.engineer.common.utils.AudioRecordHelper.audioFormat;
import static com.engineer.common.utils.AudioRecordHelper.bufferSize;
import static com.engineer.common.utils.AudioRecordHelper.channelConfig;
import static com.engineer.common.utils.AudioRecordHelper.sample;

import android.content.Context;
import android.media.AudioFormat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioUtil {
    private static String sTAG = "AudioRecordHelper";

    public static File convertPcmToWav(Context context, File pcmFile) {
        File wavFile = new File(context.getCacheDir(), "convert.wav");
        if (wavFile.exists()) {
            wavFile.delete();
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(pcmFile);
            fileOutputStream = new FileOutputStream(wavFile);

            long audioByteLen = fileInputStream.getChannel().size();
            long wavByteLen = audioByteLen + 36;

            addWavHeader(fileOutputStream, audioByteLen, wavByteLen, sample, channelConfig, audioFormat);
            byte[] buffer = new byte[bufferSize];
            while (fileInputStream.read(buffer) != -1) {
                fileOutputStream.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wavFile;
    }

    private static void addWavHeader(FileOutputStream fileOutputStream, long audioByteLen,
                                     long wavByteLen, int sampleRateInHz, int channelConfig, int audioFormat) {
        byte[] header = new byte[44];

        // RIFF/WAVE header chunk
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (wavByteLen & 0xff);
        header[5] = (byte) ((wavByteLen >> 8) & 0xff);
        header[6] = (byte) ((wavByteLen >> 16) & 0xff);
        header[7] = (byte) ((wavByteLen >> 24) & 0xff);

        //WAVE
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        // 'fmt ' chunk 4 个字节
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // 4 bytes: size of 'fmt ' chunk（格式信息数据的大小 header[20] ~ header[35]）
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // format = 1 编码方式
        header[20] = 1;
        header[21] = 0;
        // 声道数目
        int channelSize = channelConfig == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
        header[22] = (byte) channelSize;
        header[23] = 0;
        // 采样频率
        header[24] = (byte) (sampleRateInHz & 0xff);
        header[25] = (byte) ((sampleRateInHz >> 8) & 0xff);
        header[26] = (byte) ((sampleRateInHz >> 16) & 0xff);
        header[27] = (byte) ((sampleRateInHz >> 24) & 0xff);
        // 每秒传输速率
        long byteRate = audioFormat * sampleRateInHz * channelSize;
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // block align 数据库对齐单位，每个采样需要的字节数
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        // bits per sample 每个采样需要的 bit 数
        header[34] = 16;
        header[35] = 0;

        //data chunk
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        // pcm字节数
        header[40] = (byte) (audioByteLen & 0xff);
        header[41] = (byte) ((audioByteLen >> 8) & 0xff);
        header[42] = (byte) ((audioByteLen >> 16) & 0xff);
        header[43] = (byte) ((audioByteLen >> 24) & 0xff);

        try {
            fileOutputStream.write(header, 0, 44);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void analyzePcmFile(File pcmFile, int sampleRate, int channels, int bitDepth) {
        // 计算文件大小（字节）
        long fileSize = pcmFile.length();

        // 计算音频时长（秒）

        long totalSamples = fileSize / ((long) bitDepth * channels);
        float duration = (float) totalSamples / sampleRate;

        // 计算数据率（比特率）
        int bitrate = sampleRate * channels * bitDepth;

        // 输出信息
        Log.i(sTAG, "PCM 文件信息:");
        Log.i(sTAG, "文件大小: " + fileSize + " 字节");
        Log.i(sTAG, "采样率: " + sampleRate + " Hz");
        Log.i(sTAG, "声道数: " + channels);
        Log.i(sTAG, "位深度: " + bitDepth + " 位");
        Log.i(sTAG, "音频时长: " + duration + " 秒");
        Log.i(sTAG, "比特率: " + bitrate + " bps");
    }
}
