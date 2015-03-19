package com.fetal.util;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.SurfaceView;

public class BeatReplayer {
	
	private WavePainter painter;
	private Context context;
	private AudioManager manager;
	private AudioTrack track;
	private File file;
	private FileInputStream stream;
	//音频采样率
	private static int sampleRateInHz = 44100;
	//录制声道
	private static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
	//音频数据格式
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	//缓冲区大小
	private int bufferSizeInBytes = 0;
	//播放状态
	private boolean isPlaying;
	
	public BeatReplayer(Context context, String sound) {
		// TODO Auto-generated constructor stub
		this.context = context;
		bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
		manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		file = new File(sound);
	}
	
	/**
	 * 显示对象
	 * @param surface
	 */
	public void setSurface(SurfaceView surface){
		this.painter = new WavePainter(context, surface);
	}
	
	/**
	 * 绘制胎心频率图
	 * @param beat
	 */
	public void paintBeat(String point, boolean isRepeat){
		painter.drawBeat(point, isRepeat);
	}
	
	/**
	 * 播放胎心
	 */
	public void playBeat(){
		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, AudioTrack.MODE_STREAM);
		track.setPlaybackRate(sampleRateInHz);
		manager.setSpeakerphoneOn(true);
		track.play();
		byte[] buffer = new byte[bufferSizeInBytes];
		isPlaying = true;
		try {
        	stream = new FileInputStream(file);
        	stream.skip(0x2c);
            while(isPlaying && stream.read(buffer) >= 0) {
                track.write(buffer, 0, buffer.length);
            }
            stream.close();
            stream = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
		track.stop();
		track.release();
		track = null;
		manager.setSpeakerphoneOn(false);
	}
	
	/**
	 * 停止播放
	 */
	public void stopPlay(){
		isPlaying = false;
	}
	
	/**
	 * 注销资源
	 */
	public void destroyReplayer(){
		manager = null;
		file = null;
		painter.release();
		painter = null;
	}
}
