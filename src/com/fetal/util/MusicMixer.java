package com.fetal.util;

public class MusicMixer {
	
    /**
     * ÷ÿ∏¥∂Ã“Ù∆µ
     * @return
     */
    public native void combiteMusic(int number, String input, String output);
    
    /**
     * ∫œ≥…Ã•“Ù“Ù∆µ
     * @return
     */
    public native void makeMusic(String input1, String input2, String output);

    /**
     * µº»Îø‚
     */
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpeg-jni");
    }
}
