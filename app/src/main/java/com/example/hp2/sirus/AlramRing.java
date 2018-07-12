package com.example.hp2.sirus;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AlramRing extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    ImageButton stop;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alram_ring);
        textView=(TextView)findViewById(R.id.textView4);
        stop=(ImageButton) findViewById(R.id.buttonStop);

        textView.setText(getIntent().getStringExtra("message").toString());
        if(textView.getText().toString().toLowerCase().contains("wake up")){
            mediaPlayer=MediaPlayer.create(this,R.raw.alarm);
        }else{
            mediaPlayer=MediaPlayer.create(this,R.raw.alarm2);
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                finish();
            }
        });

    }


}
