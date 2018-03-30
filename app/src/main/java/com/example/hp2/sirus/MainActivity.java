package com.example.hp2.sirus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vstechlab.easyfonts.EasyFonts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AIListener, SensorEventListener {

    TextToSpeech ts;
    SensorManager sc;
    Sensor sensor;
    SweetAlertDialog s;
    Vibrator vib;
    // TextView t;
    ListView listView;
    ArrayList<Message> arrayList;
    Adapter adapter;
    String f = "";
    AlarmManager alarmManager;
    TimePickerDialog timePickerDialog;
    MediaPlayer mp = new MediaPlayer();
    String name = "";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ContentResolver resolver;
    int scorej = 0, scoreu = 0;
    private AIService aiService;
    String parameterString = "Hi";
    LocationManager locationManager;
    GifImageView imageView;
    EditText editText;
    Result result;
    final ai.api.android.AIConfiguration config = new ai.api.android.AIConfiguration("aec2f5af957b45cd82e1e0078dfe7033",
            AIConfiguration.SupportedLanguages.English,
            ai.api.android.AIConfiguration.RecognitionEngine.System);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        listView = (ListView) findViewById(R.id.listtext);
        arrayList = new ArrayList<>();
        adapter = new Adapter(getApplicationContext(), R.layout.dialog1, arrayList);
        listView.setAdapter(adapter);
        sc = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensor = sc.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sc.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        imageView = (GifImageView) findViewById(R.id.gif);
        editText = (EditText) findViewById(R.id.editTextquery);

        resolver = getContentResolver();

        preferences = getSharedPreferences("name", MODE_PRIVATE);
        editor = preferences.edit();

        if (!(preferences.contains("name"))) {
            name();
        }
        name = preferences.getString("name", "Sir");
        ts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                mp = MediaPlayer.create(MainActivity.this, R.raw.sirusintro);
                mp.start();
            }
        });

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String[] p={"Message","Owner"};
                Cursor c=resolver.query(Util.u1,p,null,null,null);
                if(c!=null||c.getCount()>0){
                    while (c.moveToNext()){
                        arrayList.add(new Message(c.getString(0),c.getInt(1)));
                    }
                    adapter.notifyDataSetChanged();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listView.setSelection(adapter.getCount() - 1);
                super.onPostExecute(aVoid);
            }
        }.execute();

        editText.setTypeface(EasyFonts.droidSerifRegular(MainActivity.this));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    imageView.setImageResource(R.drawable.send);
                } else {
                    imageView.setImageResource(R.drawable.j5);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        aiService = AIService.getService(MainActivity.this, config);
        aiService.setListener(MainActivity.this);
        final AIDataService aiDataService = new AIDataService(config);

        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                if (!isConnected(MainActivity.this)) {
                    s = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
                    s.setTitleText("No Internet Connection");
                    s.setContentText("You need to enable Moblie Data or Wi-Fi to progress further.");
                    s.setConfirmText("WI-Fi");
                    s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivityForResult(intent, 101);

                        }
                    });
                    s.setCancelText("Mobile Data");
                    s.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                            startActivityForResult(intent, 101);

                        }
                    });
                    s.show();
                } else {
                    if (editText.getText().toString().length() > 0) {
                        AIRequest r = new AIRequest();
                        r.setQuery(editText.getText().toString());
                        new AsyncTask<AIRequest, Void, AIResponse>() {

                            @Override
                            protected AIResponse doInBackground(AIRequest... aiRequests) {
                                final AIRequest request = aiRequests[0];
                                try {
                                    final AIResponse response = aiDataService.request(request);
                                    return response;
                                } catch (AIServiceException e) {
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(AIResponse response) {
                                if (response != null) {
                                    result = response.getResult();
                                    parameterString = result.getFulfillment().getSpeech();
                                    ContentValues values = new ContentValues();
                                    values.put("Message", result.getResolvedQuery());
                                    values.put("Owner", 0);
                                    resolver.insert(Util.u1, values);
                                    ContentValues values1 = new ContentValues();
                                    values.put("Message", parameterString);
                                    values.put("Owner", 1);
                                    resolver.insert(Util.u1, values);
                                    new task().execute(result.getResolvedQuery());
                                }
                            }
                        }.execute(r);
                        editText.setText("");
                    } else {
                        try {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.RECORD_AUDIO},
                                        1);
                                return;
                            }

                            aiService.startListening();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        if (preferences.contains("Silent")) {
            if (preferences.getString("Silent", "").equals("true")) {
                menu.getItem(0).setIcon(android.R.drawable.ic_lock_silent_mode);
                menu.getItem(0).setTitle("Silent On");
            } else {
                menu.getItem(0).setIcon(android.R.drawable.ic_lock_silent_mode_off);
                menu.getItem(0).setTitle("Silent Off");
            }
        } else {
            menu.getItem(0).setIcon(android.R.drawable.ic_lock_silent_mode_off);
            menu.getItem(0).setTitle("Silent Off");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            speak("", 1);
        } else if (id == R.id.silent) {
            if (item.getTitle() == "Silent Off") {
                PackageManager pm = MainActivity.this.getPackageManager();
                ComponentName componentName = new ComponentName(MainActivity.this, Ai.class);
                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
                item.setTitle("Silent On");
                editor.putString("Silent", "true");
                editor.commit();
                item.setIcon(android.R.drawable.ic_lock_silent_mode);
            } else {
                PackageManager pm = MainActivity.this.getPackageManager();
                ComponentName componentName = new ComponentName(MainActivity.this, Ai.class);
                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
                item.setTitle("Silent Off");
                editor.putString("Silent", "false");
                editor.commit();
                item.setIcon(android.R.drawable.ic_lock_silent_mode_off);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*int id = item.getItemId();
        Intent i = null;
        if (id == R.id.home) {
            i = new Intent(this, MainActivity.class);
            finish();
        } else if (id == R.id.music) {
            i = new Intent(this, Contacts.class);
        } else if (id == R.id.distance) {
            i = new Intent(this, DistanceCalculation.class);
        } else if (id == R.id.location) {
            i = new Intent(this, LocationFetch.class);
        } else if (id == R.id.social) {
            i = new Intent(this, Social.class);
        } else if (id == R.id.aboutus) {
            i = new Intent(this, AboutUs.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        startActivity(i);
*/
        return true;
    }


    private void speak(String text, int owner) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            arrayList.add(new Message(text, owner));

        } else {
            ts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            arrayList.add(new Message(text, owner));
        }
    }

    void name() {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog);
        Button b = (Button) d.findViewById(R.id.button);
        final EditText editText = (EditText) d.findViewById(R.id.editText);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("name", editText.getText().toString());
                editor.commit();
                d.dismiss();
            }
        });
        d.setCancelable(false);
        d.show();
    }

    void insertdata(String s) {
        Date d = new Date();
        SimpleDateFormat frt = new SimpleDateFormat("M-dd-yyyy hh:mm:ss");
        ContentValues values = new ContentValues();
        values.put(Util.date, frt.format(d).toString());
        values.put(Util.thing, s);
        Uri x = resolver.insert(Util.u, values);
        if (Integer.parseInt(x.getLastPathSegment()) > 0) {
            speak("Sure i ll remember that",1);
        } else {
            speak("Sorry Some Error Occured Please Try Again",1);
        }
        Toast.makeText(MainActivity.this, "Remembered " + x.getLastPathSegment(), Toast.LENGTH_LONG).show();
    }


    String read(String s) {
        StringBuffer buffer = new StringBuffer();
        boolean found = false;
        String[] p = {"Date", "Thing"};
        Cursor c = resolver.query(Util.u, p, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                String x = c.getString(1);
                if (x.contains(s.substring(s.indexOf("is") + 2))) {
                    buffer.append(c.getString(0) + "\n" + c.getString(1) + "\n");
                    found = true;
                }
                if (x.contains(s.substring(s.indexOf("are") + 3))) {
                    buffer.append(c.getString(0) + "\n" + c.getString(1) + "\n");
                    found = true;
                }
            }
        }
        c.close();
        if (!found) {
            buffer.append("Sorry , i don't know about that");
        }
        speak(buffer.toString(),1);
        return buffer.toString();
    }


    void delete(String s) {
        String[] p = {"Date", "Thing"};
        int cr = 0;
        Cursor c = resolver.query(Util.u, p, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                String x = c.getString(1);
                if (x.contains(s.substring(s.indexOf("about") + 6))) {
                    String w = Util.thing + " = '" + x + "'";
                    cr = resolver.delete(Util.u, w, null);
                    break;
                }
            }
        }
        c.close();
        if (cr > 0) {
            speak("Okay " + name + " I'll " + s,1);
        } else {
            speak("I guess you haven't told me " + s.substring(s.indexOf("forget") + 7),1);
        }
    }



    void selfie() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        startActivityForResult(intent, 7623);
    }

    @Override
    public void onResult(AIResponse response) {
        result = response.getResult();
        parameterString = result.getFulfillment().getSpeech();
        ContentValues values = new ContentValues();
        values.put("Message", result.getResolvedQuery());
        values.put("Owner", 0);
        resolver.insert(Util.u1, values);
        ContentValues values1 = new ContentValues();
        values.put("Message", parameterString);
        values.put("Owner", 1);
        resolver.insert(Util.u1, values);
        new task().execute(result.getResolvedQuery());
    }

    @Override
    public void onError(AIError error) {
        //speak("Some Error Occured");
        imageView.setImageResource(R.drawable.j5);
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        imageView.setImageResource(R.drawable.ll);
    }

    @Override
    public void onListeningCanceled() {
        imageView.setImageResource(R.drawable.j5);
    }

    @Override
    public void onListeningFinished() {
        imageView.setImageResource(R.drawable.j5);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor s = sensorEvent.sensor;
        if (s == sensor) {
            final String d;
            float[] v = sensorEvent.values;
            float x = v[0];
            float y = v[1];
            float z = v[2];
            float cal = ((x * x) + (y * y) + (z * z)) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            if (cal > 3) {
                vib.vibrate(500);
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                d = formatter.format(date);
                final StringBuffer buffer = new StringBuffer();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        buffer.append(lat + "," + lon);
                        Geocoder gc = new Geocoder(MainActivity.this);
                        try {
                            List<Address> a = gc.getFromLocation(lat, lon, 5);
                            Address z = a.get(0);
                            for (int i = 0; i < z.getMaxAddressLineIndex(); i++) {
                                buffer.append(z.getAddressLine(i) + ",");
                            }
                            buffer.append("\n");

                            buffer.append(d + "\n I am in Danger Please Help!!! " + " From " + name + " ,Message Sent By Sirus");
                            String num = "+919855981661";
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(num, null, buffer.toString(), null, null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });
                sc.unregisterListener(this, sensor);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    class task extends AsyncTask<String, Integer, String> {
        String se, reminder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... bundle) {

            try {
                if (bundle[0] != null && bundle[0].length() > 0) {
                    se = bundle[0];
                    arrayList.add(new Message(se, 0));

                    if (parameterString.equals("location")) {
                        speak("Okay Let me Fetch it", 1);
                        publishProgress(35);
                    } else if (se.equals("selfie")) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    1);
                            return null;
                        }
                        speak("Smile Please", 1);
                        publishProgress(12);
                    } else if (se.toLowerCase().contains("open")) {
                        String app = se.substring(se.toLowerCase().indexOf("open") + 4).trim();
                        app = app.replaceAll(" ", "");
                        PackageManager packageManager = getPackageManager();
                        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
                        boolean found = false;

                        for (PackageInfo p : packages) {
                            if (p.packageName.contains(app.toLowerCase())) {
                                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(p.packageName);
                                speak("Opening " + app, 1);
                                found = true;
                                startActivity(LaunchIntent);
                                break;
                            }
                        }
                        if (!found) {
                            speak(app + " either not installed or name not correct", 1);
                        }
                    } else if (se.contains("call")) {
                        String namep = se.toLowerCase().substring(se.toLowerCase().indexOf("call") + 5).trim();
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    1);
                            return null;
                        }
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    1);
                            return null;
                        }
                        boolean found = false;
                        ContentResolver resolver = getContentResolver();
                        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                        Cursor c = resolver.query(uri, null, null, null, null);
                        while (c.moveToNext()) {
                            if ((c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))).toLowerCase().contains(namep)) {
                                String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                Intent i = new Intent(Intent.ACTION_CALL);
                                i.setData(Uri.parse("tel:" + phone));
                                speak("Okay " + name, 1);
                                found = true;
                                startActivity(i);
                                break;
                            }
                        }
                        if (!found) {
                            speak("No Contact Found Can U please Specify", 1);
                        }

                    } else if (se.toLowerCase().contains("table of")) {

                        try {
                            if (se.contains(".")) {
                                float num = Float.parseFloat(se.substring(se.indexOf("of") + 3, se.length()));
                                StringBuffer buffer = new StringBuffer();
                                for (int i = 1; i <= 10; i++) {
                                    buffer.append(num + " " + i + " are " + (num * i) + "\n");
                                }
                                speak(buffer.toString(), 1);
                            } else {
                                int num = Integer.parseInt(se.substring(se.indexOf("of") + 3, se.length()));
                                StringBuffer buffer = new StringBuffer();
                                for (int i = 1; i <= 10; i++) {
                                    buffer.append(num + " " + i + " za " + (num * i) + "\n");
                                }
                                speak(buffer.toString(), 1);
                            }
                        } catch (Exception e) {
                            speak("Please Give a Valid Number", 1);
                        }
                    } else if (se.toLowerCase().contains("remember that")) {
                        publishProgress(45);
                        speak("Okay i'll remember that", 1);
                    } else if (se.toLowerCase().contains("forget about")) {
                        publishProgress(55);
                    } else if (se.contains("morning") || se.contains("night") || se.contains("afternoon") || se.contains("evening")) {
                        Date d = new Date();
                        int h = d.getHours();
                        int day = d.getDay();
                        String today = "";
                        switch (day) {
                            case 0:
                                today = "Sunday Enjoy your Holiday";
                                break;
                            case 1:
                                today = "Monday you have to go to college";
                                break;
                            case 2:
                                today = "Tuesday you have to go to college";
                                break;
                            case 3:
                                today = "Wednesday you have to go to college";
                                break;
                            case 4:
                                today = "Thrusday you have to go to college";
                                break;
                            case 5:
                                today = "Friday you have to go to college";
                                break;
                            case 6:
                                today = "Saturday Enjoy your Holiday";
                                break;
                        }
                        if (h < 12) {
                            f = "Good Morning " + name + ",Today is " + today;
                        } else if (h == 12) {
                            f = "Good Afternoon " + name;
                        } else {
                            f = "Good Night " + name;
                        }
                        speak(f, 1);
                    } else if (se.contains("time")) {
                        Calendar d = Calendar.getInstance();
                        SimpleDateFormat frmt = new SimpleDateFormat("hh:mm:ss a");
                        speak(frmt.format(d.getTime()).toString(), 1);
                    } else if (se.contains("+")) {
                        try {
                            int a = Integer.parseInt(se.substring(0, se.indexOf('+')).trim());
                            int b = Integer.parseInt(se.substring(se.indexOf('+') + 1, se.length()).trim());
                            int ans = a + b;
                            speak("The answer is " + ans, 1);
                        } catch (Exception e) {
                            speak("Please Enter A Valid Number", 1);
                        }
                    } else if (se.contains("-")) {
                        try {
                            int a = Integer.parseInt(se.substring(0, se.indexOf('-')).trim());
                            int b = Integer.parseInt(se.substring(se.indexOf('-') + 1, se.length()).trim());
                            int ans = a - b;
                            speak("The answer is " + ans, 1);
                        } catch (Exception e) {
                            speak("Please Enter A Valid Number", 1);
                        }
                    } else if (se.contains("*")) {
                        try {
                            int a = Integer.parseInt(se.substring(0, se.indexOf("*")).trim());
                            int b = Integer.parseInt(se.substring(se.indexOf("*") + 1, se.length()).trim());
                            int ans = a * b;
                            speak("The answer is " + ans, 1);
                        } catch (Exception e) {
                            speak("Please Enter A Valid Number", 1);
                        }
                    } else if (se.contains("/")) {
                        try {
                            int a = Integer.parseInt(se.substring(0, se.indexOf("/")).trim());
                            int b = Integer.parseInt(se.substring(se.indexOf("/") + 1, se.length()).trim());
                            int ans = a / b;
                            speak("The answer is " + ans, 1);
                        } catch (Exception e) {
                            speak("Please Enter A Valid Number", 1);
                        }
                    } else if (parameterString.equals("music")) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1);
                            return null;
                        }

                        speak("Sure " + name, 1);
                        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                        ArrayList<String> arrayList = new ArrayList<>();
                        if (c != null) {
                            while (c.moveToNext()) {
                                arrayList.add(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)));
                            }
                        }
                        c.close();
                        // Intent i = new Intent(MainActivity.this, Music.class);
                        Collections.shuffle(arrayList);
                       // i.putExtra("Path", arrayList);
                       // startActivity(i);
                    } else if (se.toLowerCase().contains("sms")) {
                        String namep = se.toLowerCase().substring(se.toLowerCase().indexOf("sms") + 4, se.toLowerCase().indexOf("that")).trim();
                        boolean found = false;
                        ContentResolver resolver = getContentResolver();
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    1);
                            return null;
                        }
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.SEND_SMS},
                                    1);
                            return null;
                        }
                        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                        Cursor c = resolver.query(uri, null, null, null, null);
                        while (c.moveToNext()) {
                            if ((c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))).toLowerCase().contains(namep)) {
                                String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(phone, null, se.substring(se.indexOf("that") + 4, se.length()), null, null);
                                speak("Okay " + name, 1);
                                found = true;
                                break;
                            }
                            if ((c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE))).toLowerCase().contains(namep)) {
                                String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(phone, null, se.substring(se.indexOf("that") + 4, se.length()), null, null);
                                speak("Okay " + name, 1);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            speak("No Contact Found Can U please Specify", 1);
                        }


                    }
                    else{
                        speak(parameterString,1);
                    }


                }
            } catch (Exception e) {
                //speak("Sorry"+name+" I didnot get that"+" beacause Error: "+e.getMessage(),1);
                // Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
            return "";
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values[0]==10){
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Please Select");
                String [] strings={"EveryDay","Specific Date"};
                builder.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                speak("At What Time ?",1);
                                Calendar c=Calendar.getInstance();
                                int h=c.get(Calendar.HOUR_OF_DAY);
                                int m=c.get(Calendar.MINUTE);
                                TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        Intent intent=new Intent("remind");
                                        intent.putExtra("hour",i);
                                        intent.putExtra("minute",i1);
                                        intent.putExtra("message",name+" it's time to "+reminder);
                                        sendBroadcast(intent);
                                        speak("Okay "+name+" i will remind you",1);
                                    }
                                };
                                TimePickerDialog timePickerDialog=new TimePickerDialog(MainActivity.this,t,h,m,false);
                                timePickerDialog.show();
                                break;
                            case 1:
                                Calendar c1=Calendar.getInstance();
                                DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, final int year, final int month, final int date) {
                                        speak("At What Time ?",1);
                                        Calendar c=Calendar.getInstance();
                                        int h=c.get(Calendar.HOUR_OF_DAY);
                                        int m=c.get(Calendar.MINUTE);
                                        TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                                Intent intent=new Intent("remind");
                                                intent.putExtra("hour",i);
                                                intent.putExtra("minute",i1);
                                                intent.putExtra("message",name+" it's time to "+reminder);
                                                intent.putExtra("date",date);
                                                intent.putExtra("month",month);
                                                intent.putExtra("year",year);
                                                sendBroadcast(intent);
                                                speak("Okay "+name+" i will remind you",1);
                                            }
                                        };
                                        TimePickerDialog timePickerDialog=new TimePickerDialog(MainActivity.this,t,h,m,false);
                                        timePickerDialog.show();


                                    }
                                };
                                DatePickerDialog datePickerDialog=new DatePickerDialog(MainActivity.this,d,c1.get(Calendar.YEAR),c1.get(Calendar.MONTH),c1.get(Calendar.DATE));
                                datePickerDialog.show();
                                break;
                        }
                    }
                });
                builder.create().show();
            }else if(values[0]==15){
                Calendar c=Calendar.getInstance();
                int hh=c.get(Calendar.HOUR_OF_DAY);
                int mm=c.get(Calendar.MINUTE);
                TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Calendar c1=Calendar.getInstance();
                        int h=c1.get(Calendar.HOUR_OF_DAY);
                        int m=c1.get(Calendar.MINUTE);
                        if(i<=h){
                            c1.set(Calendar.DATE,c1.get(Calendar.DATE)+1);
                            c1.set(Calendar.HOUR_OF_DAY, i);
                            c1.set(Calendar.MINUTE, i1);
                            c1.set(Calendar.SECOND, 00);
                            i=i+(23-h);
                            if(i1>=m){
                                i1=i1-m;
                            }
                            else{
                                i=i-1;
                                i1=m-(m-i1);
                            }
                        }else {
                            c1.set(Calendar.HOUR_OF_DAY, i);
                            c1.set(Calendar.MINUTE, i1);
                            c1.set(Calendar.SECOND, 00);
                            i=i-h;
                            if(i1>=m){
                                i1=i1-m;
                            }
                            else{
                                i=i-1;
                                i1=m-(m-i1);
                            }
                        }
                       /* Intent intent=new Intent(MainActivity.this,AlramRing.class);
                        intent.putExtra("message","Wake Up "+name);
                        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),(int)(Math.random()*100),intent,0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,c1.getTimeInMillis(),pendingIntent);
                        speak("Alarm set for "+i+" Hours and "+i1+" minutes from now",1);
                        Toast.makeText(getApplicationContext(),"Alarm set for "+i+" Hours and "+i1+" minutes from now",Toast.LENGTH_LONG).show();
             */       }
                };

                timePickerDialog = new TimePickerDialog(MainActivity.this,t,hh,mm,true);
                timePickerDialog.show();
            }else if(values[0]==35){
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    return ;

                }
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        StringBuffer buffer = new StringBuffer();
                        Geocoder gc = new Geocoder(MainActivity.this);

                        List<Address> a = null;
                        try {
                            a = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                            buffer.append(location.getLatitude()+","+location.getLongitude());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < a.get(0).getMaxAddressLineIndex(); i++) {
                            buffer.append(a.get(i).getAddressLine(i) + ",");
                        }
                        buffer.append("\n");
                        speak(name + " Your Location is " + buffer.toString(),1);
                        //  t.setText(" Your Location is " + buffer.toString());
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                }, Looper.myLooper());
            }else if(values[0]==45){
                insertdata(se);
            }else if(values[0]==55){
                delete(se);
            }else if(values[0]==12){
                selfie();
            }
        }

        @Override
        protected void onPostExecute(String aVoid) {
            // t.setText(se.toUpperCase());
            adapter.notifyDataSetChanged();
            listView.setSelection(adapter.getCount() - 1);
            super.onPostExecute(aVoid);
        }
    }


            public boolean isConnected(Context context) {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();

            if (netinfo != null && netinfo.isConnectedOrConnecting()) {
                android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) {
                    return true;
                } else
                    return false;
            } else
                return false;
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==101){
            s.dismiss();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        sc.unregisterListener(MainActivity.this,sensor);
        super.onDestroy();
    }
    }
