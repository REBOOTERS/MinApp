package tv.danmaku.ijk.media.player;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * Created on 2022/7/31.
 *
 * @author rookie
 */
public class IjkMediaPlayer implements IMediaPlayer{
    public int dropFrameRate = 0;
    public boolean isLooping;

    @Override
    public void setDisplay(SurfaceHolder sh) {

    }

    public void setDataSource(Context context, Uri uri) {

    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public String getDataSource() {
        return null;
    }

    public void prepareAsync() {
    }

    @Override
    public void start() throws IllegalStateException {

    }

    @Override
    public void stop() throws IllegalStateException {

    }

    public void reset() {
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {

    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public MediaInfo getMediaInfo() {
        return null;
    }

    @Override
    public void setLogEnabled(boolean enable) {

    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public void setAudioStreamType(int streamtype) {

    }

    @Override
    public void setKeepInBackground(boolean keepInBackground) {

    }

    @Override
    public int getVideoSarNum() {
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        return 0;
    }

    @Override
    public void setWakeMode(Context context, int mode) {

    }

    @Override
    public void setLooping(boolean looping) {

    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return new ITrackInfo[0];
    }

    @Override
    public void setSurface(Surface surface) {

    }

    @Override
    public void setDataSource(IMediaDataSource mediaDataSource) {

    }

    @Override
    public void setOnTimedTextListener(tv.danmaku.ijk.media.player.IMediaPlayer.OnTimedTextListener listener) {

    }

    @Override
    public void setOnInfoListener(tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener listener) {

    }

    @Override
    public void setOnErrorListener(tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener listener) {

    }

    @Override
    public void setOnVideoSizeChangedListener(tv.danmaku.ijk.media.player.IMediaPlayer.OnVideoSizeChangedListener listener) {

    }

    @Override
    public void setOnSeekCompleteListener(tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener listener) {

    }

    @Override
    public void setOnBufferingUpdateListener(tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener listener) {

    }

    @Override
    public void setOnCompletionListener(tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener listener) {

    }

    @Override
    public void setOnPreparedListener(tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener listener) {

    }

    public void pause() {
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {

    }

    @Override
    public int getVideoWidth() {
        return 0;
    }

    @Override
    public int getVideoHeight() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {

    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    public void release() {
    }

}
