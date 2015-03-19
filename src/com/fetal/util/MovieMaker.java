package com.fetal.util;

import java.io.File;

public class MovieMaker {
	
	/**
	 * 生成胎心记录文件
	 * @param timestamp
	 */
	public void toMovie(String tape){
		makeMovie();
		File file = new File("/sdcard/Fetal/tmp/video.flv");
		file.renameTo(new File(tape));
	}
	
    /**
     * 合成胎音视频
     * @return
     */
    public native void makeMovie();

    /**
     * 导入库
     */
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpeg-jni");
    }
}
