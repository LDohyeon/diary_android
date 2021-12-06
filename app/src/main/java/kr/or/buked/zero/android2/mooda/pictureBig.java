package kr.or.buked.zero.android2.mooda;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class pictureBig extends AppCompatActivity {

    ImageView picTureXBtn;
    ImageView pictureUriBig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_big);

        pictureUriBig = findViewById(R.id.pictureUriBIg);
        picTureXBtn = findViewById(R.id.x);

        String uriBool =getIntent().getStringExtra("uriBool");

        Uri uri = Uri.parse(uriBool);

        Glide.with(this) .load(uri).override(450, 450) .into(pictureUriBig);


        picTureXBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}