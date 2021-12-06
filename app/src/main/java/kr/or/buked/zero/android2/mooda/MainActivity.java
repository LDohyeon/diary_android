package kr.or.buked.zero.android2.mooda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button btnYearMonthPicker;
    String calendars = null;

    PersonAdapter adapterSelect;
    PersonAdapter adapter;

    SQLiteDatabase db;
    Button writeBtn;

    public static final int REQUEST_CODE_MENU = 101;    //write 용
    private static final int REQUEST_CODE_LOCATION = 2; // 위치 권한 용



    RecyclerView mainRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnYearMonthPicker = findViewById(R.id.btn_year_month_picker);


        getMyLocation();// 위치 구하기 위한 권한 처리
        

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        Calendar c1 = Calendar.getInstance();

        String strToday = sdf.format(c1.getTime());

        btnYearMonthPicker.setText(strToday);

        btnYearMonthPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                pd.setListener(d);

                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");


            }
        });

        //여기까지 like를 위한 날짜


        mainRecyclerView = findViewById(R.id.mainRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mainRecyclerView.setLayoutManager(layoutManager);

        adapter = new PersonAdapter();

        adapterSelect = mainSelect(adapter, strToday);

        mainRecyclerView.setAdapter(adapterSelect);



        int counts = selectCount(strToday);//insert나 그냥 왔을 때는 가장 최근 글을 보여준다.

        int position = getIntent().getIntExtra("position", -1);

        if(position == -1)//insert나 그냥 왔을 때
        {
            mainRecyclerView.scrollToPosition(counts-1);
        }
        else//수정했을 때
        {
            mainRecyclerView.scrollToPosition(position);
        }


        writeBtn = findViewById(R.id.writeBtn);

        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), write.class);
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });

    }


    //like를 위한 날짜
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            //Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);

            if(monthOfYear <10)
            {
                calendars = year + "-0" + monthOfYear;
            }
            else
            {
                calendars = year + "-" + monthOfYear;
            }

            Log.d(calendars, "calendars***");

        }
    };


    public PersonAdapter mainSelect(PersonAdapter adapter, String strToday)
    {
        createDB();
        createTable();

        //String sql = "select * from mooda order by dates ASC;";

        String sql = "select * from mooda where dates like '%"+ strToday +"%' order by dates ASC";

        Cursor cursor = db.rawQuery(sql, null);

        if(cursor.getCount()<=0)
        {
            Toast.makeText(this, "조회 결과가 없습니다.", Toast.LENGTH_LONG).show();
            //Log.d("select null ", "select***");
            return adapter;
        }
        while(cursor.moveToNext())
        {
            int num = cursor.getInt(cursor.getColumnIndex("num"));
            String dates = cursor.getString(cursor.getColumnIndex("dates"));
            int mood = cursor.getInt(cursor.getColumnIndex("mood"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String pictureUri = cursor.getString(cursor.getColumnIndex("pictureUri"));
            int weather = cursor.getInt(cursor.getColumnIndex("weather"));


            Log.d(weather+"", "weather***");

            adapter.addItem(new Person(num, dates, mood, content, pictureUri, weather));
        }

        return adapter;
    }

    public int selectCount(String strToday)
    {
        createDB();
        createTable();

        String sql = "select num from mooda where dates like '%"+ strToday +"%' order by dates ASC";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();

        int count = cursor.getCount();

        return count;

    }



    void createDB()
    {
        db = openOrCreateDatabase("mooda", MODE_PRIVATE, null);
        //Log.d("db 생성", "write***");
    }

    void createTable()
    {
        String sql = "create table if not exists mooda " +
                "(num integer primary key autoincrement, " +
                "dates date, mood integer, content text, pictureUri text, weather integer);";

        db.execSQL(sql);
       // Log.d("table 생성", "write***");
    }
    void dropTable()
    {
        String sql = "drop table mooda;";
        db.execSQL(sql);
    }

    private void getMyLocation() {//위치 권한
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("사용자에게 권한을 요청해야함"   , "log***");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);

            return;
        }
        else {
            Log.d("사용자에게 권한요청 안해도됨"   , "log***");

        }
    }


}























