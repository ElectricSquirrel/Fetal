package com.fetal.util;

import java.io.File;

public class MovieMaker {
	
	/**
	 * ����̥�ļ�¼�ļ�
	 * @param timestamp
	 */
	public void toMovie(String tape){
		makeMovie();
		File file = new File("/sdcard/Fetal/tmp/video.flv");
		file.renameTo(new File(tape));
	}
	
    /**
     * �ϳ�̥����Ƶ
     * @return
     */
    public native void makeMovie();

    /**
     * �����
     */
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpeg-jni");
    }
}
