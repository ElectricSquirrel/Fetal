package com.fetal.util;

public class MusicMixer {
	
    /**
     * �ظ�����Ƶ
     * @return
     */
    public native void combiteMusic(int number, String input, String output);
    
    /**
     * �ϳ�̥����Ƶ
     * @return
     */
    public native void makeMusic(String input1, String input2, String output);

    /**
     * �����
     */
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpeg-jni");
    }
}
