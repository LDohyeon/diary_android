package kr.or.buked.zero.android2.mooda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class write extends AppCompatActivity {

    Button pictureBtn;
    Uri fileUri;


    public static final int REQUEST_CODE_MENU2 = 101;
    public static final int REQUEST_CODE_MENU3 = 102;

    Button writeXbtn;

    EditText editText;

    Button btn_selectDate;

    Button writeInsert;
    SQLiteDatabase db;

    int imageIndex = 0;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat= new SimpleDateFormat("yyyy-MM-dd");



    ImageView yellow, green, blue, pink, red, gray;


    ImageView imageView5;

    String uriBool = "none";

    String main;
    static RequestQueue requestQueue;

    ImageView clear, thunderstorm, rain, snow, atmosphere, clouds;
    int weatherNum;


    //GPS
    private static final int REQUEST_CODE_LOCATION = 2;
    double lat = 0;
    double lng = 0;
    LocationManager locationManager;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);


        clear = (ImageView)findViewById(R.id.clear);
        thunderstorm = (ImageView)findViewById(R.id.thunderstorm);
        rain = (ImageView)findViewById(R.id.rain);
        snow = (ImageView)findViewById(R.id.snow);
        atmosphere = (ImageView)findViewById(R.id.atmosphere);
        clouds = (ImageView)findViewById(R.id.clouds);
        imageView5 = (ImageView)findViewById(R.id.imageView5); // ??????
        pictureBtn = findViewById(R.id.pictureBtn); // ?????? uri


        if(requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        getMyLocation();//?????? ??????
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // GPS ??????????????? ??????????????????
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // ???????????? ??????????????? ??????????????????
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("GPSWrite***", "isGPSEnabled=" + isGPSEnabled);
        Log.d("GPSWrite***", "isNetworkEnabled=" + isNetworkEnabled);


        //????????? ?????? ?????? ????????? ????????? ?????? ?????? ????????? ?????? ?????? ????????? ?????? ????????? ????????? ?????????.
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        getLo(locationManager);// ????????? ?????? ???????????? ?????? ?????????





        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    openGallery();
            }
        });




        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), pictureBig.class);
                intent.putExtra("uriBool", uriBool);
                startActivityForResult(intent, REQUEST_CODE_MENU3);

            }
        });



        imageView5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("????????? ??????");
                builder.setMessage("????????? ????????????????");
                //builder.setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        imageView5.setImageResource(0);
                        uriBool = "none";
                    }
                });

                builder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        //???????????? ??????


        editText = findViewById(R.id.editText);


        btn_selectDate = findViewById(R.id.btn_selectDate);
        btn_selectDate.setText(getTime());
        btn_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock(view);
            }
        });




        //???????????? ??????
        yellow = (ImageView)findViewById(R.id.yellow);
        green = (ImageView)findViewById(R.id.green);
        gray = (ImageView)findViewById(R.id.gray);
        red = (ImageView)findViewById(R.id.red);
        pink = (ImageView)findViewById(R.id.pink);
        blue = (ImageView)findViewById(R.id.blue);



        //????????? ???????????? ??????
        String updateDate=getIntent().getStringExtra("updateDate");
        String updateContent = getIntent().getStringExtra("updateContent");
        int updateResId = getIntent().getIntExtra("updateImgResId", 0);
        String updatePictureUri = getIntent().getStringExtra("updatePictureUri");
        int position = getIntent().getIntExtra("position", -1);


        if(updateDate == null)//?????? ????????? ????????? ????????? ??? ?????? ????????? ????????? ?????? ?????? ???
        {
            makeWeatherRequest(lat, lng);//??????
        }
        else//?????? ????????? ????????? ??? ????????? ?????? ?????? ???
        {

            Log.d(updateDate, "btn_selectDate.getText().toString()***");
            Log.d(getTime(), "getTime()***");
            Log.d(updateResId+"", "udpatessss***");

            if(getTime().equals(updateDate))
            {
                //Log.d("????????? ??????", "clcl***");
                makeWeatherRequest(lat, lng);
            }
            if(!getTime().equals(updateDate))
            {
                //Log.d("????????? ?????? ??????", "clcl***");
                //Toast.makeText(getApplicationContext(), "?????? ????????? ?????? ????????????.", Toast.LENGTH_LONG).show();
                weatherNum = -1;

                clear.setVisibility(View.INVISIBLE);
                thunderstorm.setVisibility(View.INVISIBLE);
                rain.setVisibility(View.INVISIBLE);
                snow.setVisibility(View.INVISIBLE);
                atmosphere.setVisibility(View.INVISIBLE);
                clouds.setVisibility(View.INVISIBLE);

            }

            if(updatePictureUri != null) {

                fileUri = Uri.parse(updatePictureUri);

                uriBool = updatePictureUri;

                Glide.with(this) .load(fileUri).override(450, 450) .into(imageView5);

                imageView5.setVisibility(View.VISIBLE);

            }

            editText.setText(updateContent);
            btn_selectDate.setText(updateDate);


            imageIndex= updateResId; //mood

            yellow.setVisibility(View.INVISIBLE);


            if(updateResId == 0)
            {
                yellow.setVisibility(View.VISIBLE);
            }
            if(updateResId == 1)
            {
                green.setVisibility(View.VISIBLE);
            }
            if(updateResId == 2)
            {
                pink.setVisibility(View.VISIBLE);
            }
            if(updateResId == 3)
            {
                red.setVisibility(View.VISIBLE);
            }
            if(updateResId == 4)
            {
                gray.setVisibility(View.VISIBLE);
            }
            if(updateResId == 5)
            {
                blue.setVisibility(View.VISIBLE);
            }


        }

        writeInsert = findViewById(R.id.writeInsert);

        writeInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createDB();
                createTable();

                String content = editText.getText().toString();
                String dates = btn_selectDate.getText().toString();

                int updatePkNum= getIntent().getIntExtra("updatePkNum", -1);

                if(updatePkNum ==  -1)//insert ??????
                {
//                    if(getTime().equals(dates)) //?????? ????????? ?????? ???????????? ??????
//                    {
//                        Calendar ctime = Calendar.getInstance();
//                        int hour = ctime.get(Calendar.HOUR);
//                        int minute = ctime.get(Calendar.MINUTE);
//                        if(hour<10)
//                        {
//                            dates += " 0"+hour+":"+minute;
//                        }
//                        else
//                        {
//                            dates += " "+hour+":"+minute;
//                        }
//                    }
                    

                    insert(dates, imageIndex, content, uriBool, weatherNum);
                }
                else {//?????? ??????
//                    if(getTime().equals(dates)) //?????? ????????? ?????? ???????????? ??????
//                    {
//                        Calendar ctime = Calendar.getInstance();
//                        int hour = ctime.get(Calendar.HOUR);
//                        int minute = ctime.get(Calendar.MINUTE);
//                        dates += "-"+hour+"-"+minute;
//                    }

                    if(getTime().equals(dates))
                    {
                        update(dates, imageIndex, content, uriBool, weatherNum, updatePkNum);
                    }
                    else
                    {
                        update(dates, imageIndex, content, uriBool, updatePkNum);
                    }
                    Log.d("dates***", dates);

                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_CODE_MENU2);
                finish();
            }
        });

        writeXbtn = findViewById(R.id.writeXbtn);

        writeXbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }


//    /*
//     * String??? Bitmap????????? ??????
//     * */
//    public Bitmap StringToBitmap(String encodedString) {
//        try {
//            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            return bitmap;
//        } catch (Exception e) {
//            e.getMessage();
//            return null;
//        }
//    }
//    /*
//     * Bitmap??? String????????? ??????
//     * */
//    public String BitmapToString(Bitmap bitmap) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
//        byte[] bytes = baos.toByteArray();
//        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
//        return temp;
//    }

    public void openGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, 102);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 102)
        {
            if(resultCode == RESULT_OK) // ????????? ???????????? ???
            {
                fileUri = data.getData();
                //Log.d(fileUri+"", "data***");
                uriBool = fileUri.toString();
                Glide.with(this) .load(fileUri).override(450, 450) .into(imageView5);
                imageView5.setVisibility(View.VISIBLE);
            }
            if(resultCode == RESULT_CANCELED ) // ????????? ?????? ??? ?????? ?????? ?????? ???
            {
                finish();
            }

        }
    }




    public void createDB()
    {
        db = openOrCreateDatabase("mooda", MODE_PRIVATE, null);
        Log.d("db ??????", "write***");
    }

    public void createTable()
    {
        String sql = "create table if not exists mooda " +
                "(num integer primary key autoincrement, " +
                "dates date, mood integer, content text, pictureUri text, weather integer);";

        db.execSQL(sql);
    }

    void insert(String dates, int mood, String content, String pictureUri, int weatherNums)
    {
        String sql = "insert into mooda(dates, mood, content, pictureUri, weather) values('"
                + dates + "', " + mood + ", '" + content +"', '"+pictureUri +"', "+ weatherNums +");";

        db.execSQL(sql);
    }
    void update(String dates, int mood, String content, String pictureUri, int weatherNums ,int updatePkNum)
    {
        String sql = "update mooda set dates = '"+dates+"', mood = "+mood+", content = '"
                +content+"', pictureUri = '"+pictureUri+"', weather = "+ weatherNums +" where num = "+updatePkNum;

        db.execSQL(sql);
    }
    void update(String dates, int mood, String content, String pictureUri, int updatePkNum)
    {
        String sql = "update mooda set dates = '"+dates+"', mood = "+mood+", content = '"
                +content+"', pictureUri = '"+pictureUri+"' where num = "+updatePkNum;

        db.execSQL(sql);
    }



    public void onButtonClicked(View view)
    {
        changeImage();
    }


    private void changeImage()
    {

        if(imageIndex == 0)
        {
            yellow.setVisibility(View.INVISIBLE);
            green.setVisibility(View.VISIBLE);
        }
        if(imageIndex == 1)
        {
            green.setVisibility(View.INVISIBLE);
            pink.setVisibility(View.VISIBLE);
        }
        if(imageIndex == 2)
        {
            pink.setVisibility(View.INVISIBLE);
            red.setVisibility(View.VISIBLE);
        }
        if(imageIndex == 3)
        {
            red.setVisibility(View.INVISIBLE);
            gray.setVisibility(View.VISIBLE);
        }
        if(imageIndex == 4)
        {
            gray.setVisibility(View.INVISIBLE);
            blue.setVisibility(View.VISIBLE);
        }
        if(imageIndex == 5)
        {
            blue.setVisibility(View.INVISIBLE);
            yellow.setVisibility(View.VISIBLE);
            imageIndex = -1;

        }
        imageIndex++;

    }






    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        //Log.d(mFormat.format(mDate)+"", "clock***");
        return mFormat.format(mDate);
    }

    public void clock(View view)
    {
        if(view == btn_selectDate)
        {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);


            //Log.d("hour***", mYear +" , "+hour+", "+ minute);




            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            if(dayOfMonth < 10)
                            {
                                String strDayOfMonth = "0"+dayOfMonth;
                                btn_selectDate.setText(year+"-" + (month+1) + "-" + strDayOfMonth);

                                if(getTime().equals(btn_selectDate.getText().toString()))
                                {
                                    //Log.d("????????? ??????", "clcl***");
                                    makeWeatherRequest(lat, lng);
                                }
                                if(!getTime().equals(btn_selectDate.getText().toString()))
                                {
                                    //Log.d("????????? ?????? ??????", "clcl***");
                                    Toast.makeText(getApplicationContext(), "?????? ??? ?????? ????????? ?????? ????????????.", Toast.LENGTH_LONG).show();
                                    weatherNum = -1;

                                    clear.setVisibility(View.INVISIBLE);
                                    thunderstorm.setVisibility(View.INVISIBLE);
                                    rain.setVisibility(View.INVISIBLE);
                                    snow.setVisibility(View.INVISIBLE);
                                    atmosphere.setVisibility(View.INVISIBLE);
                                    clouds.setVisibility(View.INVISIBLE);
                                }
                            }
                            else if(dayOfMonth >= 10)
                            {
                                btn_selectDate.setText(year+"-" + (month+1) + "-" + dayOfMonth);

                                if(getTime().equals(btn_selectDate.getText().toString()))
                                {
                                    //Log.d("????????? ??????", "clcl***");
                                    makeWeatherRequest(lat, lng);
                                }
                                if(!getTime().equals(btn_selectDate.getText().toString()))
                                {
                                    //Log.d("????????? ?????? ??????", "clcl***");
                                    Toast.makeText(getApplicationContext(), "?????? ????????? ?????? ????????????.", Toast.LENGTH_LONG).show();
                                    weatherNum = -1;

                                    clear.setVisibility(View.INVISIBLE);
                                    thunderstorm.setVisibility(View.INVISIBLE);
                                    rain.setVisibility(View.INVISIBLE);
                                    snow.setVisibility(View.INVISIBLE);
                                    atmosphere.setVisibility(View.INVISIBLE);
                                    clouds.setVisibility(View.INVISIBLE);
                                }
                            }

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }



    }

    //????????? ???



    //??????

    public void makeWeatherRequest(double lat, double lng)
    {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+ lat +"&lon="+lng+"&appid=9429a8a51df3d29e1ea25e60417ba76a";

        if(lat == 0)//?????? ?????? ??? ????????? ????????? ?????? ?????? ?????? ????????? ????????????.
        {
            url = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=9429a8a51df3d29e1ea25e60417ba76a";
        }


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //println("?????? - > " + response);

                Log.d(response,"response***");

                String json = response;
                try {
                    JSONObject jsonObject = new JSONObject(json);

                    String weather = jsonObject.getString("weather");

                    JSONArray jarray = new JSONArray(weather);
                    for(int i=0; i < jarray.length(); i++){
//                    for(int i=0; i < 1; i++){
                        JSONObject jObject = jarray.getJSONObject(i);  // JSONObject ??????
                        main = jObject.getString("main");


                        Log.d(main,"main***");
                        Log.d(jarray.length()+"","main***");

                        if(main.equals("Clear"))
                        {
                            clear.setVisibility(View.VISIBLE);
                            weatherNum = 0;
                        }
                        if(main.equals("Thunderstorm"))
                        {
                            thunderstorm.setVisibility(View.VISIBLE);
                            weatherNum = 1;
                        }
                        if(main.equals("Rain")  || main.equals("Drizzle"))
                        {
                            rain.setVisibility(View.VISIBLE);
                            atmosphere.setVisibility(View.INVISIBLE);
                            weatherNum = 2;

                            break;//?????? ????????? ?????? ????????? ?????? ????????? ??????.
                        }
                        if(main.equals("Snow"))
                        {
                            snow.setVisibility(View.VISIBLE);
                            weatherNum = 3;
                        }
                        if(main.equals("Atmosphere")  || main.equals("Mist")  || main.equals("Smoke")  || main.equals("Haze")  ||
                        main.equals("Dust")  || main.equals("Fog")  || main.equals("Sand")  || main.equals("Ash")
                        || main.equals("Squall") || main.equals("Tornado"))
                        {
                            atmosphere.setVisibility(View.VISIBLE);
                            weatherNum = 4;
                        }
                        if(main.equals("Clouds"))
                        {
                            clouds.setVisibility(View.VISIBLE);
                            weatherNum = 5;
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("?????? - > " + error, "error***");
                    }
                }
        )
        {
            protected Map<String, String> getParms() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };


        request.setShouldCache(false);
        requestQueue.add(request);


    }

    private void getMyLocation() {//?????? ??????
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("??????????????? ????????? ???????????????"   , "log***");
            
            //?????? ??????
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);

            return;//?????? ?????? ??????
        }
        else {
            Log.d("??????????????? ???????????? ????????????"   , "log***");

        }
    }

    private void getLo(LocationManager locationManager) { //GPS
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("??????????????? ????????? ???????????????"   , "log***");

            //?????? ?????? ?????? ?????? getMyLocation() ???????????? ?????? ????????? ?????? ?????? ?????????????????? ?????? ????????? ??????. ????????? ?????? ?????? ????????? ??? ?????? ??? ?????????.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);

            return;//?????? ?????? ??????
        }
        else {
            Log.d("??????????????? ???????????? ????????????"   , "log***");

        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            lng = lastKnownLocation.getLatitude();
            lat = lastKnownLocation.getLatitude();
            Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);

            //Toast.makeText(this, lng + ", latitude=" + lat, Toast.LENGTH_LONG).show();
        }
    }



    

}






















