package com.example.twicehomework;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

public class add extends AppCompatActivity {

    private ImageButton btn_back; //返回按钮
    private ImageButton btn_finish;//確認按钮
    private ImageButton btn_clear;//清除按鈕
    private TextView now_time;//當前時間
    private EditText data_information;//儲存數據

    private MyDbHelper myDbHelper;
    private SQLiteDatabase db;
    private ContentValues values;
    private static final String mTablename = "mymemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        btn_back = findViewById(R.id.btn_back);
        data_information = findViewById(R.id.data_information);
        now_time = findViewById(R.id.now_time);
        btn_finish = findViewById(R.id.btn_finish);
        btn_clear = findViewById(R.id.btn_clear);
        myDbHelper  = new MyDbHelper(this);
        //獲取手機當前時間
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        final String str_time = simpleDateFormat.format(date);
        now_time.setText(str_time);
        //返回主頁面
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(add.this,MainActivity.class);
                startActivity(intent);
                queryData();
            }
        });
        //添加備忘錄
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = data_information.getText().toString().trim();
                if (input.isEmpty()){
                    showMsg("請輸入內容");
                }else {
                    addData(str_time.trim(),input);
                    finish();
                    queryData();
                }
            }
        });
        //刪除備忘錄
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });
    }
    private void addData(String now_time,String data_information){
        db = myDbHelper.getWritableDatabase();
        values = new ContentValues();
        values.put("now_time",now_time);
        values.put("information",data_information);
        db.insert(mTablename,null,values);
        showMsg("新增成功");

    }
    private void clearData(){
        db = myDbHelper.getWritableDatabase();
        db.delete(mTablename,null,null);
        String srtResult = "";
        Intent intent = new Intent(add.this,MainActivity.class);
        intent.putExtra("data",srtResult);
        startActivity(intent);
        showMsg("清除成功");
    }
    private void showMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    private void queryData(){
        db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(mTablename,null,null,null,
                null,null,null);
        String srtResult = "";
        while (cursor.moveToNext()){
            srtResult += "\n" + cursor.getString(1);
            srtResult += "\n内容：" + cursor.getString(2);
            srtResult += "\n";
        }
        Intent intent = new Intent(add.this,MainActivity.class);
        intent.putExtra("data",srtResult);
        startActivity(intent);
    }

    class MyDbHelper extends SQLiteOpenHelper{
        public MyDbHelper(@Nullable Context context) {
            super(context, "mymemo.db", null, 2);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + mTablename + "(_id integer primary key autoincrement, " +
                    "now_time varchar(50) unique,information varchar(100) )");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
