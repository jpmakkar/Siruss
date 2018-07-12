package com.example.hp2.sirus;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dualcores.swagpoints.SwagPoints;
import com.vstechlab.easyfonts.EasyFonts;

import java.io.IOException;
import java.util.ArrayList;


public class Music extends AppCompatActivity implements View.OnClickListener  {

    ImageButton play,previous,next;
    ImageView imageView;
    int idx=1;
    ArrayList<String> path;
    boolean jplay=true;
    byte[] art;
    MediaPlayer mediaPlayer;
    TextView txtsong;

    MediaMetadataRetriever mediaMetadataRetriever;

    private SwagPoints seekBarProgress;


    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        getSupportActionBar().hide();
        mediaPlayer=new MediaPlayer();

        txtsong=(TextView)findViewById(R.id.textsong);
        txtsong.setTypeface(EasyFonts.caviarDreams(Music.this));

        seekBarProgress=(SwagPoints)findViewById(R.id.seekbar_point);


        seekBarProgress.setOnSwagPointsChangeListener(new SwagPoints.OnSwagPointsChangeListener() {
            @Override
            public void onPointsChanged(SwagPoints swagPoints, int i, boolean b) {
                if(mediaPlayer != null && b){
                    mediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SwagPoints swagPoints) {

            }

            @Override
            public void onStopTrackingTouch(SwagPoints swagPoints) {

            }
        });



        play=(ImageButton)findViewById(R.id.play);
        previous=(ImageButton)findViewById(R.id.previous);
        next=(ImageButton)findViewById(R.id.next);
        imageView=(ImageView) findViewById(R.id.imagesong);
        mediaMetadataRetriever=new MediaMetadataRetriever();
        path=getIntent().getStringArrayListExtra("Path");
        play.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        image();
        play(idx);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play.setImageResource(R.drawable.jpause);
                jplay=true;
                mediaPlayer.reset();
                play(++idx);
                image();
            }
        });


    }



    void play(int id){
        try {
            mediaPlayer.setDataSource(path.get(id));
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void image(){
        mediaMetadataRetriever.setDataSource(path.get(idx));
        primarySeekBarProgressUpdater();
        art=mediaMetadataRetriever.getEmbeddedPicture();
        if(art!=null){
            imageView.setBackground(new BitmapDrawable(BitmapFactory.decodeByteArray(art,0,art.length)));

        }else {
            imageView.setBackgroundResource(R.drawable.music);
        }

        if(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)!=null) {


            StringBuffer b = new StringBuffer();
            b.append("Song :\n" + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) + "\n");
            b.append("Album :\n" + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) + "\n");
            b.append("Artist :\n" + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) + "\n");

            txtsong.setText(b.toString());
        }else {
            txtsong.setText("No Details");
        }


    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch(id){
            case R.id.play:
                if(jplay){
                    play.setImageResource(R.drawable.jplay);
                    jplay=false;
                    mediaPlayer.pause();
                }else{
                    play.setImageResource(R.drawable.jpause);
                    jplay=true;
                    mediaPlayer.start();
                }
                break;

            case R.id.previous:
                if(idx-1<0){
                    Toast.makeText(this,"No Previous",Toast.LENGTH_SHORT).show();
                }else{
                    play.setImageResource(R.drawable.jpause);
                    jplay=true;
                    mediaPlayer.reset();
                    play(--idx);

                    image();
                }
                break;
            case R.id.next:
                if(idx+1>=path.size()){
                    Toast.makeText(this,"No Next",Toast.LENGTH_SHORT).show();
                }else{
                    play.setImageResource(R.drawable.jpause);
                    jplay=true;
                    mediaPlayer.reset();
                    play(++idx);

                    image();
                }
                break;
        }
    }

    private void primarySeekBarProgressUpdater() {

        if (mediaPlayer.isPlaying()) {

            seekBarProgress.setMax((mediaPlayer.getDuration()/1000)+1);
            Music.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBarProgress.setPoints(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }
}
