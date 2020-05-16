package mediaplayer.example.com.test;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    public TextView songName,startTimeField,endTimeField;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private ImageButton playButton,pauseButton;
    public static int oneTimeOnly = 0;
    private ImageView backgroundImage;
    private AudioManager mAudioManager;
    private int originalVolume ,maxVolume,songPosn;
    private float volumeStep;
    private ContentResolver musicResolver ;
    private Uri musicUri;
    private Cursor musicCursor ;
    private ArrayList<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songList = new ArrayList<Song>();
        setContentView(R.layout.activity_main);
        songName = (TextView)findViewById(R.id.textView4);
        startTimeField =(TextView)findViewById(R.id.textView1);
        endTimeField =(TextView)findViewById(R.id.textView2);
        seekbar = (SeekBar)findViewById(R.id.seekBar1);
        //playButton = (ImageButton)findViewById(R.id.imageButton1);
        //pauseButton = (ImageButton)findViewById(R.id.imageButton2);
        musicResolver = getContentResolver();
        musicUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        //Log.d("Music uri  : "+musicUri,"mydebug");
        musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //Log.d(""+musicCursor,"mydebug");

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        backgroundImage = (ImageView)findViewById(R.id.imageView1);
        setListeners(backgroundImage);
        songName.setText("song.mp3");
        songPosn=0;
//        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        seekbar.setClickable(false);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        originalVolume =  mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeStep = (float) 0.1;
      //  Log.d("Intial ORiginal and max volumes : "+ originalVolume +"  "+maxVolume + "  Volume step : " +volumeStep ,"mydebug");


        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        for(int i=0;i<songList.size();i++)
             Log.d(""+songList.get(i).getTitle(),"mydebug");
        //pauseButton.setEnabled(false);

    }

    int i = 0;
    private void setListeners(ImageView background) {

        background.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {

                finish();
                return false;
            }
        });
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    i++;
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            i = 0;
                        }
                    };

                    if (i == 1) {
                        //Single click
                        handler.postDelayed(r, 250);
                    } else if (i == 2) {
                        //Double click
                        i = 0;
                        if(mediaPlayer.isPlaying()){
                            pause();
                        }else{
                            play();
                        }
                    }


                }


        });

        background.setOnTouchListener(new MyTouchListener(this.getApplicationContext()) {
            public boolean onSwipeTop() {

                    volumeStep+=(float) 0.1;
                    if(volumeStep>(float)1.1){
                        volumeStep = (float) 0.1;
                    }
                    originalVolume += (int)volumeStep;


                if(originalVolume>maxVolume)
                    originalVolume = maxVolume;

                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
               // Log.d("Volume : " +originalVolume + "   Volume step : " + volumeStep , "mydebug");
               // Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                return true;
            }

            public boolean onSwipeBottom() {

                    volumeStep+=(float) 0.1;
                    if(volumeStep>(float)1.1){
                        volumeStep = (float) 0.1;
                    }
                    originalVolume -= (int)volumeStep;
                if(originalVolume<0)
                    originalVolume = 0;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                //Log.d("Volume : " +originalVolume+ "   Volume step : "  + volumeStep, "mydebug");
                //Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                return true;
            }

            public boolean onSwipeRight() {
                songPosn--;
                play();
                return true;
            }
            public boolean onSwipeLeft() {
                songPosn++;
                play();
                return true;
            }

            public boolean onScrollRight(){
                rewind();
                return true;
            }

            public boolean onScrollLeft(){
                forward();
                return true;
            }
        });
    }

    public void play(){

        mediaPlayer.reset();
        //get song
        Song playSong = songList.get(songPosn);
        //get id
        long currSong = playSong.getID();
        //set uri

        //Log.d("current song and Pos ","mydebug");
        Log.d(playSong.getTitle()+"  "+songPosn,"mydebug");
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                currSong);

        //Log.d(" URI : "+trackUri,"mydebug");
        try{
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);

          //  Log.d("Prepaing async","mydebug");
        }
        catch(Exception e){
            e.printStackTrace();
            //Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mediaPlayer.getCurrentPosition()>0){
                    mp.reset();
                    songPosn++;
                    play();
                }
            }
        });
        Toast.makeText(getApplicationContext(), "Playing sound",
                Toast.LENGTH_SHORT).show();
        mediaPlayer.start();

        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();
        if(oneTimeOnly == 0){
            seekbar.setMax((int) finalTime);
            oneTimeOnly = 1;
        }

        endTimeField.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) finalTime)))
        );
        startTimeField.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
        );
        seekbar.setProgress((int)startTime);
        myHandler.postDelayed(UpdateSongTime,100);
       // pauseButton.setEnabled(true);
       // playButton.setEnabled(false);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            startTimeField.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    public void pause(){
        Toast.makeText(getApplicationContext(), "Pausing sound",
                Toast.LENGTH_SHORT).show();

        mediaPlayer.pause();
       // pauseButton.setEnabled(false);
        //playButton.setEnabled(true);
    }

    public void forward(){
        int temp = (int)startTime;
        if((temp+forwardTime)<=finalTime){
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Cannot jump forward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void rewind(){
        int temp = (int)startTime;
        if((temp-backwardTime)>0){
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Cannot jump backward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}