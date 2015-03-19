package com.fetal.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

	private static final String DATABASE = "fetalmonitor.db";
	private static final int VERSION = 1;
	
	public DatabaseHelper(Context context){
		super(context, DATABASE, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("create table if not exists member" +
				"(" +
				"id integer primary key autoincrement," +
				"remote_id integer," +
				"nickname varchar," +
				"mobile varchar," +
				"weibo varchar," +
				"qq varchar," +
				"thumbnail varchar," +
				"birthday long" +
				")");
		db.execSQL("create table if not exists recordset" +
				"(" +
				"id integer primary key autoincrement," +
				"mid integer," +
				"type integer," +
				"item integer" +
				")");
		db.execSQL("create table if not exists record" +
				"(" +
				"id integer primary key autoincrement," +
				"mid integer," +
				"pregnancy integer," +
				"date varchar," +
				"time varchar," +
				"chart varchar," +
				"tape varchar," +
				"sound varchar," +
				"point varchar," +
				"average integer," +
				"min integer," +
				"max integer," +
				"report integer," +
				"share integer" +
				")");
		db.execSQL("create table if not exists mix" +
				"(" +
				"id integer primary key autoincrement," +
				"mid integer," +
				"date varchar," +
				"time integer," +
				"record varchar," +
				"rfile varchar," +
				"music varchar," +
				"mfile varchar," +
				"sound varchar," +
				"share integer" +
				")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
