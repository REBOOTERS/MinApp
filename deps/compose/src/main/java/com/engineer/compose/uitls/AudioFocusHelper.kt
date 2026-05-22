package com.engineer.compose.uitls

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi


class AudioFocusHelper() {
    private var isHasAudioFocus = false
    private var mAudioManager: AudioManager? = null
    private val mHandler = Handler(Looper.getMainLooper())

    private val mOnAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            Log.i(TAG, "onAudioFocusChange = $focusChange ")
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> mHandler.post(Runnable {
                    mListener?.onFocusGain()
                })

                AudioManager.AUDIOFOCUS_LOSS -> mHandler.post(Runnable {
                    mListener?.onFocusLoss()
                })

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> mHandler.post(Runnable {
                    mListener?.onFocusLossTransient()
                })

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mHandler.post(Runnable {
                    mListener?.onFocusLossMayDuck()
                })

                else -> {}
            }
        }

    val volume: Int
        get() = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)

    fun setLowerVolume() {
        Log.i(TAG, "volume lower = " + this.volume)
        if (this.volume <= 1) {
            return
        }
        mAudioManager!!.setStreamVolume(
            AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND
        )
    }

    fun setHighVolume() {
        Log.i(TAG, "volume high = " + this.volume)
        if (this.volume >= mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1) {
            return
        }
        mAudioManager!!.setStreamVolume(
            AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND
        )
    }


    // max
    fun getStreamMaxVolume(context: Context, streamType: Int): Int {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.getStreamMaxVolume(streamType)
    }

    // current
    fun getStreamVolume(context: Context, streamType: Int): Int {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.getStreamVolume(streamType)
    }

    fun setStreamVolume(context: Context, streamType: Int, index: Int) {
        Log.i(TAG, "setStreamVolume streamType = " + streamType + ", index = " + index)
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(streamType, index, 0)
    }

    fun adjustSystemVolume(context: Context, streamType: Int, value: Int) {
        Log.i(TAG, "adjustSystemVolume streamType = " + streamType + ", value = " + value)
        setStreamVolume(context, streamType, value)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isStreamMute(context: Context, streamType: Int): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isStreamMute(streamType)
    }

    private val `object` = Any()

    /**
     * 申请获取音频焦点
     * 如果当前已经有了焦点，不会重复获取
     *
     *
     * 切记：request和abandon要成对调用，因为request超过100次计数未被abandon就会导致后续的request失效，带来混响的问题
     * 建议在页面onDestroy的时候强制调用 [.abandonAudioFocus]
     *
     *
     * @return 是否成功获取到了音频你焦点
     */
    fun requestAudioFocus(context: Context): Boolean {
        synchronized(`object`) {
            if (isHasAudioFocus) {
                return true
            }
            Log.i(TAG, "requestAudioFocus")
            if (mAudioManager == null) {
                mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            }

            val isGranted = mAudioManager!!.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
            if (isGranted == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                setHasAudioFocus(true)
                return true
            }
            return false
        }
    }

    /**
     * 申请获取音频焦点
     * 如果当前已经有了焦点，不会重复获取
     *
     *
     * 切记：request和abandon要成对调用，因为request超过100次计数未被abandon就会导致后续的request失效，带来混响的问题
     * 建议在页面onDestroy的时候强制调用 [.abandonAudioFocus]
     *
     *
     * @return 是否成功获取到了音频你焦点
     */
    fun requestAudioFocus(context: Context, hintDuration: Int): Boolean {
        synchronized(`object`) {
            if (isHasAudioFocus) {
                return true
            }
            Log.i(TAG, "requestAudioFocus")
            try {
                if (mAudioManager == null) {
                    mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
                }
                val isGranted = mAudioManager!!.requestAudioFocus(
                    mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, hintDuration
                )
                if (isGranted == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    setHasAudioFocus(true)
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(TAG, "requestAudioFocus")
            }
            return false
        }
    }

    /**
     * 放弃(释放)音频焦点
     *
     *
     * 切记：request和abandon要成对调用，因为request超过100次计数未被abandon就会导致后续的request失效，带来混响的问题
     * 建议在页面onDestroy的时候强制调用abandonAudioFocus
     *
     */
    fun abandonAudioFocus(context: Context): Boolean {
        synchronized(`object`) {
            Log.i(TAG, "abandonAudioFocus")
            try {
                if (mAudioManager == null) {
                    mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
                }
                setHasAudioFocus(false)
                var isGrated = AudioManager.AUDIOFOCUS_REQUEST_FAILED
                if (mAudioManager != null) {
                    isGrated = mAudioManager!!.abandonAudioFocus(mOnAudioFocusChangeListener)
                }
                return isGrated == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i(TAG, "abandonAudioFocus")
            }
            return false
        }
    }

    fun getHasAudioFocus(): Boolean {
        return isHasAudioFocus
    }

    fun setHasAudioFocus(hasAudioFocus: Boolean) {
        Log.i(TAG, "setHasAudioFocus:" + hasAudioFocus)
        isHasAudioFocus = hasAudioFocus
    }

    private var mListener: OnAudioFocusChangeListener? = null

    fun setFocusListener(listener: OnAudioFocusChangeListener?) {
        mListener = listener
    }

    interface OnAudioFocusChangeListener {
        fun onFocusLoss()

        fun onFocusGain()

        fun onFocusLossTransient()

        fun onFocusLossMayDuck()
    }

    companion object {
        const val TAG = "AudioFocusHelper"
    }
}
