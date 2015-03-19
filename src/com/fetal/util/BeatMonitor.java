package com.fetal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteOrder;
import java.util.List;

import com.fetal.base.ApplicationBase;
import com.fetal.bean.RecordBean;
import com.fetal.service.MovieService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;

public class BeatMonitor {
	
	private Context context;
	private AudioRecord recorder;
	private AudioManager manager;
	private AudioTrack track;
	private SurfaceView surface;
	private short MaxAmplitude = 0;
	private WavePainter painter;
	private Handler saveHandler;
	private List<Integer> beating;
	private RecordBean record;
	//音频源
	private int audioSource = MediaRecorder.AudioSource.DEFAULT;
	//音频采样率
	private static int sampleRateInHz = 44100;
	//录制声道
	private static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
	//音频数据格式
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	//缓冲区大小
	private int bufferSizeInBytes = 0;
	//录制状态
	private boolean isRecording = true;
	//写入状态
	private boolean isWriting = false;
	//是否正确保存
	private boolean isSaving = false;
	//裸音频数据文件
	private static final String AudioName = "/sdcard/Fetal/tmp/tmp.raw";
	//裸音频资源
	private FileOutputStream resource = null;
	
	public BeatMonitor(Context context){
		this.context = context;
		initWidget();
	}
	
	/**
	 * 显示对象
	 * @param surface
	 */
	public void setSurface(SurfaceView surface){
		this.surface = surface;
		this.painter = new WavePainter(context, surface);
	}
	
	/**
	 * 初始化
	 */
	public void initWidget(){
		bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
		bufferSizeInBytes = Math.max(
				bufferSizeInBytes, 
				AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
				);
		manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		try {
			checkProjectPath("Fetal");
			checkProjectPath("Fetal/tmp");
			File file = new File(AudioName);
			if (file.exists()) {
				file.delete();
			}
			resource = new FileOutputStream(file);// 建立一个可存取字节的文件
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入音频
	 */
	public void readRecording(){
		recorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
		
//		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, AudioTrack.MODE_STREAM);
//		track.setPlaybackRate(sampleRateInHz);
//		manager.setSpeakerphoneOn(true);//边录边放
		manager.setBluetoothScoOn(true);
//		track.play();
		
		recorder.startRecording();
		byte[] buffer = new byte[bufferSizeInBytes];
		//判断CPU类型
		boolean bBigEnding = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? true : false;
		while (isRecording) {
			recorder.read(buffer, 0, bufferSizeInBytes);
			if (isWriting) {
				writeRecording(buffer);
			}
			playRecording(buffer, bBigEnding);
		}
		recorder.stop();
		recorder.release();
		recorder = null;
//		track.stop();
//		track.release();
//		track = null;
		manager.setSpeakerphoneOn(false);
		if (isWriting && isSaving) {//给裸数据加上头文件
			try {
				resource.close();// 关闭写入流
			} catch (IOException e) {
				e.printStackTrace();
			}
			//插入记录裤
			isWriting = false;
			saveRecord();
			Message msg = Message.obtain();
			msg.what = 1;
			saveHandler.sendMessage(msg);
		}
		painter = null;
	}
	
	/**
	 * 保存胎心记录
	 */
	private void saveRecord(){
		//文件
		copyWaveFile(AudioName, "/sdcard/Fetal/tmp/sound.wav");
		copyWaveFile(AudioName, record.getSound());
		painter = new WavePainter(context);
		painter.createBeatBitmap(record.getChart(), beating);
		Intent intent = new Intent(context, MovieService.class);
		intent.putExtra("tape", record.getTape());
		context.startService(intent);
		//数据库
		DatabaseOperator db = new DatabaseOperator(context);
		db.addRecord(record);
		db.addRecordset(record.getMid(), 1, db.getLastRecordId());
		db.closeDatabase();
		db = null;
	}
	
	/**
	 * 绘制波形图
	 * @param wdata
	 */
	public void paintWave(short[] wdata){
		painter.drawWave(wdata);
	}
	
	/**
	 * 绘制胎心频率图
	 * @param beat
	 */
	public void paintBeat(int beat){
		if (painter != null) {
			painter.drawBeat(beat, isWriting);
		}
	}
	
	/**
	 * 写入录音文件
	 * @param buffer
	 */
	private void writeRecording(byte[] buffer){
		if (AudioRecord.ERROR_INVALID_OPERATION != buffer.length) {
			try {
				resource.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 开始录音
	 */
	public void goWriting(Handler saveHandler){
		this.saveHandler = saveHandler;
		isWriting = true;
	}
	
	/**
	 * 非保存退出
	 */
	public void stopMonitor(){
		isRecording = false;
	}
	
	/**
	 * 停止监护
	 */
	public void stopMonitor(int stopwatch, List<Integer> beating, int pregnancy){
		if (isWriting) {
			//生成用户文件夹
			SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
			int id = sp.getInt("id", 0);
			checkProjectPath("Fetal/" + id);
			//赋值
			long current = System.currentTimeMillis();
			String file = Long.toString(current);
			String tape = "/sdcard/Fetal/" + id + "/" + file + ".flv";
			String chart = "/sdcard/Fetal/" + id + "/" + file + ".png";
			String sound = "/sdcard/Fetal/" + id + "/" + file + ".wav";
			String point = "";
			record = new RecordBean();
			record.setMid(id);
			record.setChart(chart);
			record.setDate(current);
			record.setPregnancy(pregnancy);
			record.setTape(tape);
			record.setSound(sound);
			record.setTime(stopwatch);
			this.beating = beating;
			int min = 0, max = 0, avg = 0, total = 0;
			int size = beating.size();
			for (int i = 0; i < size; i++) {
				int value = beating.get(i);
				total += value;
				if (value > max) {
					max = value;
				}
				if (min == 0 && value != 0) {
					min = value;
				}
				if (value < min && value > 0) {
					min = value;
				}
				point += value + ";";
			}
			total = total / size;
			for (int i = 0; i < size; i++) {
				int value = beating.get(i);
				if (value != min && value != max) {
					avg += value - total;
				}
			}
			avg = total + (avg / size);
			record.setMaxBeat(max);
			record.setMinBeat(min);
			record.setAverageBeat(avg);
			record.setPoint(point);
			if (avg <= 120 && avg >= 160) {
				record.setReport(1);
			} else if (avg > 160) {
				record.setReport(2);
			} else {
				record.setReport(3);
			}
			isSaving = true;
		}
		isRecording = false;
	}
	
	/**
	 * 返回监护状态
	 * @return
	 */
	public boolean isMonitoring(){
		return isRecording;
	}
	
	/**
	 * 返回录制状态
	 * @return
	 */
	public boolean isWriting(){
		return isWriting;
	}
	
	/**
	 * 绘制波形图
	 * @param buffer
	 * @param painter
	 */
	private void playRecording(byte[] buffer, boolean bBigEnding){
		short[] data = Bytes2Shorts(buffer, bBigEnding);
		for (int i = 0; i < data.length; i++) {
			if (data[i] < 0) {
				data[i] = 0;
			}
			setMaxAmplitude(data[i]);
		}
		data = null;
//		track.write(buffer, 0, buffer.length);
	}
	
	/** 
	 * 设置最大波幅
	 * @param data
	 */
	private void setMaxAmplitude(short data) {
		if (MaxAmplitude < data) {
			MaxAmplitude = data;
		}
	}
	
	/**
	 * 获取间隔内最大波幅
	 * @return
	 */
	public int getMaxAmplitude() {
		short result = (short) (MaxAmplitude);
		MaxAmplitude = 0;
		return (int) (result);
	}
	
	/**
	 * 升级版数据转换
	 * @param buf
	 * @param bBigEnding
	 * @return
	 */
	private short[] Bytes2Shorts(byte[] buf, boolean bBigEnding) {
        byte bLength = 2;
        short[] s = new short[buf.length / bLength];
        byte[] temp = new byte[bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                temp[jLoop] = buf[iLoop * bLength + jLoop];
            }
            s[iLoop] = getShort(temp, bBigEnding);
        }
        temp = null;
        return s;
    }
	
	/**
	 * 获取转换结果
	 * @param buf
	 * @param bBigEnding
	 * @return
	 */
	public short getShort(byte[] buf, boolean bBigEnding) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > 2) {
            throw new IllegalArgumentException("byte array size > 2 !");
        }
        short r = 0;
        if (bBigEnding) {
            for (int i = 0; i < buf.length; i++) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        } else {
            for (int i = buf.length - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        }
        return r;
    }
	
	/**
	 * 这里得到可播放的音频文件
	 * @param inFilename
	 * @param outFilename
	 */
	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = sampleRateInHz;
		int channels = 2;
		long byteRate = 16 * sampleRateInHz * channels / 8;
		byte[] data = new byte[bufferSizeInBytes];
		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);
			while (in.read(data) != -1) {
				out.write(data);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		in = null;
		out = null;
	}

	/**
	 * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
	 * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
	 * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
	 * 自己特有的头文件。
	 */
	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (channels * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
		header = null;
	}
	
	/**
	 * 生成项目文件夹
	 * @param filename 文件名称
	 */
	public boolean checkProjectPath(String path) {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				String sdcard = Environment.getExternalStorageDirectory().getPath();
				path = sdcard + "/" + path;
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdir();
				}
				dir = null;
				return true;
			} else {
				Log.e("failed", "create dir failed");
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
}
