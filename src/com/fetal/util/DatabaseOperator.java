package com.fetal.util;

import com.fetal.bean.MemberBean;
import com.fetal.bean.MixBean;
import com.fetal.bean.RecordBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseOperator {
	private DatabaseHelper helper;
	private SQLiteDatabase db;
	
	public DatabaseOperator(Context context){
		helper = new DatabaseHelper(context);
		db = helper.getWritableDatabase();
	}
	
	/**
	 * 添加记录集
	 */
	public void addRecordset(int mid, int type, int id){
		db.beginTransaction();
		try {
			db.execSQL(
				"insert into recordset values(null, ?, ?, ?)", 
				new Object[]{
						mid, type, id
				}
			);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 获取所有记录集
	 * @return
	 */
	public Cursor showAllRecordset(int mid){
		String table = "recordset";
		String[] columns = new String[]{"type", "item"};
		String selection = "mid = ?";
		String[] selectionArgs = {String.valueOf(mid)};
		String groupBy = null;
		String having = null;
		String orderBy = "id desc";
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	/**
	 * 添加胎心记录
	 * @param record
	 */
	public void addRecord(RecordBean record){
		db.beginTransaction();
		try {
			db.execSQL(
				"insert into record values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)", 
				new Object[]{
					record.getMid(),
					record.getPregnancy(),
					record.getDate(),
					record.getTime(),
					record.getChart(),
					record.getTape(),
					record.getSound(),
					record.getPoint(),
					record.getAverageBeat(),
					record.getMinBeat(),
					record.getMaxBeat(),
					record.getReport()
				}
			);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 获取所有胎心记录
	 * @param mid
	 * @return
	 */
	public Cursor showAllRecord(int mid){
		String table = "record";
		String[] columns = new String[]{"id", "pregnancy", "chart", "average", "report", "date"};
		String selection = "mid = ?";
		String[] selectionArgs = {String.valueOf(mid)};
		String groupBy = null;
		String having = null;
		String orderBy = "id desc";
		return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	/**
	 * 获取指定胎心记录
	 * @param id
	 * @return
	 */
	public RecordBean getOneRecord(int id){
		String table = "record";
		String[] columns = new String[]{"id", "pregnancy", "date", "time", "chart", "tape", "sound", "point", "average", "min", "max", "report"};
		String selection = "id = ?";
		String[] selectionArgs = {String.valueOf(id)};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		RecordBean record = new RecordBean();
		while (cursor.moveToNext()) {
			record.setDate(cursor.getLong(cursor.getColumnIndex("date")));
			record.setAverageBeat(cursor.getInt(cursor.getColumnIndex("average")));
			record.setChart(cursor.getString(cursor.getColumnIndex("chart")));
			record.setId(cursor.getInt(cursor.getColumnIndex("id")));
			record.setMaxBeat(cursor.getInt(cursor.getColumnIndex("max")));
			record.setMinBeat(cursor.getInt(cursor.getColumnIndex("min")));
			record.setPregnancy(cursor.getInt(cursor.getColumnIndex("pregnancy")));
			record.setReport(cursor.getInt(cursor.getColumnIndex("report")));
			record.setTape(cursor.getString(cursor.getColumnIndex("tape")));
			record.setSound(cursor.getString(cursor.getColumnIndex("sound")));
			record.setPoint(cursor.getString(cursor.getColumnIndex("point")));
			record.setTime(cursor.getInt(cursor.getColumnIndex("time")));
		}
		cursor.close();
		return record;
	}
	
	/**
	 * 获取最新胎心记录
	 * @return
	 */
	public RecordBean getLastRecord(){
		Cursor cursor = db.rawQuery("select * from record order by id desc limit 1", null);
		RecordBean record = new RecordBean();
		while (cursor.moveToNext()) {
			record.setDate(cursor.getLong(cursor.getColumnIndex("date")));
			record.setAverageBeat(cursor.getInt(cursor.getColumnIndex("average")));
			record.setChart(cursor.getString(cursor.getColumnIndex("chart")));
			record.setId(cursor.getInt(cursor.getColumnIndex("id")));
			record.setMaxBeat(cursor.getInt(cursor.getColumnIndex("max")));
			record.setMinBeat(cursor.getInt(cursor.getColumnIndex("min")));
			record.setPregnancy(cursor.getInt(cursor.getColumnIndex("pregnancy")));
			record.setReport(cursor.getInt(cursor.getColumnIndex("report")));
			record.setTape(cursor.getString(cursor.getColumnIndex("tape")));
			record.setPoint(cursor.getString(cursor.getColumnIndex("point")));
			record.setSound(cursor.getString(cursor.getColumnIndex("sound")));
			record.setTime(cursor.getInt(cursor.getColumnIndex("time")));
		}
		cursor.close();
		return record;
	}
	
	/**
	 * 获取最新胎心记录ID
	 * @return
	 */
	public int getLastRecordId(){
		Cursor cursor = db.rawQuery("select id from record order by id desc limit 1", null);
		int id = 0;
		while (cursor.moveToNext()) {
			id = cursor.getInt(cursor.getColumnIndex("id"));
		}
		cursor.close();
		return id;
	}
	
	/**
	 * 标注分享
	 * @param id
	 */
	public void shareRecord(int id) {
		db.beginTransaction();
		try {
			db.execSQL(
				"update record set share = 1 where id = ?", 
				new Object[]{id}
			);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 添加混音记录
	 */
	public void addMix(MixBean mix){
		db.beginTransaction();
		try {
			db.execSQL(
				"insert into mix values(null, ?, ?, ?, ?, ?, ?, ?, ?, 0)", 
				new Object[]{
					mix.getMid(),
					mix.getDate(),
					mix.getTime(),
					mix.getRecord(),
					mix.getRfile(),
					mix.getMusic(),
					mix.getMfile(),
					mix.getSound()
				}
			);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 标注分享
	 * @param id
	 */
	public void shareMix(int id) {
		db.beginTransaction();
		try {
			db.execSQL(
				"update mix set share = 1 where id = ?", 
				new Object[]{id}
			);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 获取指定混音记录
	 * @param id
	 * @return
	 */
	public MixBean getOneMix(int id){
		String table = "mix";
		String[] columns = new String[]{"id", "mid", "date", "time", "record", "music", "rfile", "mfile", "sound"};
		String selection = "id = ?";
		String[] selectionArgs = {String.valueOf(id)};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		MixBean mix = new MixBean();
		while (cursor.moveToNext()) {
			mix.setDate(cursor.getLong(cursor.getColumnIndex("date")));
			mix.setTime(cursor.getInt(cursor.getColumnIndex("time")));
			mix.setId(cursor.getInt(cursor.getColumnIndex("id")));
			mix.setMid(cursor.getInt(cursor.getColumnIndex("mid")));
			mix.setRecord(cursor.getString(cursor.getColumnIndex("record")));
			mix.setMusic(cursor.getString(cursor.getColumnIndex("music")));
			mix.setSound(cursor.getString(cursor.getColumnIndex("sound")));
			mix.setRfile(cursor.getString(cursor.getColumnIndex("rfile")));
			mix.setMfile(cursor.getString(cursor.getColumnIndex("mfile")));
		}
		cursor.close();
		return mix;
	}
	
	/**
	 * 获取最新混音记录
	 * @return
	 */
	public MixBean getLastMix(){
		Cursor cursor = db.rawQuery("select * from mix order by id desc limit 1", null);
		MixBean mix = new MixBean();
		while (cursor.moveToNext()) {
			mix.setDate(cursor.getLong(cursor.getColumnIndex("date")));
			mix.setTime(cursor.getInt(cursor.getColumnIndex("time")));
			mix.setId(cursor.getInt(cursor.getColumnIndex("id")));
			mix.setMid(cursor.getInt(cursor.getColumnIndex("mid")));
			mix.setRecord(cursor.getString(cursor.getColumnIndex("record")));
			mix.setMusic(cursor.getString(cursor.getColumnIndex("music")));
			mix.setRfile(cursor.getString(cursor.getColumnIndex("rfile")));
			mix.setMfile(cursor.getString(cursor.getColumnIndex("mfile")));
			mix.setSound(cursor.getString(cursor.getColumnIndex("sound")));
		}
		cursor.close();
		return mix;
	}
	
	/**
	 * 获取最新混音记录ID
	 * @return
	 */
	public int getLastMixId(){
		Cursor cursor = db.rawQuery("select id from mix order by id desc limit 1", null);
		int id = 0;
		while (cursor.moveToNext()) {
			id = cursor.getInt(cursor.getColumnIndex("id"));
		}
		cursor.close();
		return id;
	}
	
	/**
	 * 添加用户
	 * @param member
	 */
	public void addMember(MemberBean member) {
		db.beginTransaction();
		try {
			db.execSQL(
				"insert into member values(null, 0, ?, ?, null, null, null, ?)", 
				new Object[]{
					member.getNickname(),
					member.getMobile(),
					member.getBirthday()
				}
			);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 更新用户
	 * @param member
	 */
	public void modifyMember(MemberBean member) {
		db.beginTransaction();
		try {
			db.execSQL(
				"update member set nickname = ?, birthday = ?, mobile = ? where id = ?", 
				new Object[]{
					member.getNickname(),
					member.getMobile(),
					member.getBirthday(),
					member.getId()
				}
			);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 更新远程用户ID
	 * @param member
	 */
	public void modifyMemberRemoteId(MemberBean member) {
		db.beginTransaction();
		try {
			db.execSQL(
				"update member set remote_id = ? where id = ?", 
				new Object[]{
					member.getRemoteId(),
					member.getId()
				}
			);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 更新用户头像
	 * @param member
	 */
	public void modifyMemberThumbnail(MemberBean member) {
		db.beginTransaction();
		try {
			db.execSQL(
				"update member set thumbnail = ? where id = ?", 
				new Object[]{
					member.getThumbnail(),
					member.getId()
				}
			);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 获取用户列表
	 * @return
	 */
	public Cursor showAllMember() {
		return db.rawQuery("select * from member", null);
	}
	
	/**
	 * 获取指定用户信息
	 * @param id
	 * @return
	 */
	public MemberBean getOneMember(int id) {
		String table = "member";
		String[] columns = new String[]{"id", "nickname", "birthday", "mobile", "qq", "weibo"};
		String selection = "id = ?";
		String[] selectionArgs = {String.valueOf(id)};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		MemberBean member = new MemberBean();
		while (cursor.moveToNext()) {
			member.setId(cursor.getInt(cursor.getColumnIndex("id")));
			member.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
			member.setBirthday(cursor.getLong(cursor.getColumnIndex("birthday")));
			member.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
			member.setQQ(cursor.getString(cursor.getColumnIndex("qq")));
			member.setWeibo(cursor.getString(cursor.getColumnIndex("weibo")));
		}
		cursor.close();
		return member;
	}
	
	public int getMemberRemoteId(int id) {
		String table = "member";
		String[] columns = new String[]{"remote_id"};
		String selection = "id = ?";
		String[] selectionArgs = {String.valueOf(id)};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		int remoteId = 0;
		while (cursor.moveToNext()) {
			remoteId = cursor.getInt(cursor.getColumnIndex("remote_id"));
		}
		cursor.close();
		return remoteId;
	}
	
	/**
	 * 清除数据库句柄
	 */
	public void closeDatabase(){
		db.close();
	}
}
