package ru.perm.trubnikov.gps2sms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

  class DBHelper extends SQLiteOpenHelper {

	private String defSmsMsg;
	  
    public DBHelper(Context context) {
      // конструктор суперкласса
      super(context, "rupermtrubnikovgps2smsDB", null, 2);
      defSmsMsg = context.getString(R.string.default_sms_msg);
    }

    public long getSettingsParamInt(String param) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor c = db.query("settings", null, "param = '"+param+"'", null, null, null, null);
    	
    	if (c.moveToFirst()) {
            int idx = c.getColumnIndex("val_int");
            Long r = c.getLong(idx);
            return r;
		}
    	
    	return 0;
    }
    
    public String getSettingsParamTxt(String param) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor c = db.query("settings", null, "param = '"+param+"'", null, null, null, null);
    	
    	if (c.moveToFirst()) {
            int idx = c.getColumnIndex("val_txt");
            String r = c.getString(idx);
            return r;
		}
    	
    	return "";
    }
    
    public void setSettingsParamInt(String param, long val) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
	    cv.put("val_int", val);
	    db.update("settings", cv, "param = ?", new String[] { param });
    }
    
    public void setSettingsParamTxt(String param, String val) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
	    cv.put("val_txt", val);
	    db.update("settings", cv, "param = ?", new String[] { param });
    }
    
    public String getPhone() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor c = db.query("phone", null, "_id=1", null, null, null, null);
    	
    	if (c.moveToFirst()) {
            int idx = c.getColumnIndex("phone");
            String phone = c.getString(idx);
            return phone;
		}
    	
    	return "";
    }

    
    public String getName() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor c = db.query("contact", null, "_id=1", null, null, null, null);
    	
    	if (c.moveToFirst()) {
            int idx = c.getColumnIndex("contact");
            String contact = c.getString(idx);
            return contact;
		}
    	
    	return "";
    }

    
    public String getSmsMsg() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor c = db.query("msg", null, "_id=1", null, null, null, null);
    	
    	if (c.moveToFirst()) {
            int idx = c.getColumnIndex("msg");
            String msg = c.getString(idx);
            return msg;
		}
    	
    	return "";
    }    

    public String getSlot(int id, String col) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor c = db.query("slots", null, "_id="+id, null, null, null, null);
    	
    	if (c.moveToFirst()) {
            int idx = c.getColumnIndex(col);
            String val = c.getString(idx);
            return val;
		}
    	
    	return "";
    }
    
    public void setSlot(int id, String name, String phone) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
	    cv.put("name", name);
	    cv.put("phone", phone);
	    //Log.d("gps", "save! " + name + " " + phone + " " + id);
	    db.update("slots", cv, "_id = ?", new String[] { Integer.toString(id) });
    }
    
    
    @Override
    public void onCreate(SQLiteDatabase db) {
      
      ContentValues cv = new ContentValues();

      // номер телефона для отправки SMS
      db.execSQL("create table phone ("
              + "_id integer primary key," 
              + "phone text"
              + ");");
      
      // Договорились, что телефон хранится в таблице с _id=1
      cv.put("_id", 1);
      cv.put("phone", ""); // без "+7" !!!
      db.insert("phone", null, cv);  
      
      db.execSQL("create table contact ("
              + "_id integer primary key," 
              + "contact text"
              + ");");
      
      // Договорились, что хранится в таблице с _id=1
      cv.clear();
      cv.put("_id", 1);
      cv.put("contact", ""); 
      db.insert("contact", null, cv);  
     
      
      db.execSQL("create table msg ("
              + "_id integer primary key," 
              + "msg text"
              + ");");
      
      // Договорились, что хранится в таблице с _id=1
      cv.clear();
      cv.put("_id", 1);
      cv.put("msg", defSmsMsg);
      db.insert("msg", null, cv);
      
      Upgrade_1_to_2(db);
      
    }

    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    	ContentValues cv = new ContentValues();
    	
    	if (oldVersion == 1 && newVersion == 2) {
    		Upgrade_1_to_2(db);
    	}
    	
    }
    
    
    public void Upgrade_1_to_2(SQLiteDatabase db) {
    	
    	ContentValues cv = new ContentValues();
    	
    	// Появилось с БД версии 2
        // таблица настроек
        db.execSQL("create table settings ("
                + "_id integer primary key autoincrement," 
                + "param text,"
                + "val_txt text,"
                + "val_int integer"
                + ");");
        
        cv.clear();
        cv.put("param", "sendvia"); // Посылать СМС/Навител
        cv.put("val_txt", "");
        cv.put("val_int", 1);
        db.insert("settings", null, cv);
        
        // Слоты контактов, plain phone - нулевой слот
        db.execSQL("create table slots ("
                + "_id integer primary key," 
                + "name text,"
                + "phone text"
                + ");");
        
        cv.clear();
        cv.put("_id", 0);
        cv.put("name", ""); 
        cv.put("phone", "");
        db.insert("slots", null, cv);
        
        cv.clear();
        cv.put("_id", 1);
        cv.put("name", ""); 
        cv.put("phone", "");
        db.insert("slots", null, cv);
        
        cv.clear();
        cv.put("_id", 2);
        cv.put("name", ""); 
        cv.put("phone", "");
        db.insert("slots", null, cv);
        
        cv.clear();
        cv.put("_id", 3);
        cv.put("name", ""); 
        cv.put("phone", "");
        db.insert("slots", null, cv);
              
    };
    
    
  }