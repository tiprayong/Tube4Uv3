package com.comtip.tip.tube4uv3;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SearchWidgetsControl searchWidgetsControl;
    SearchYoutubeAsyncTask searchYoutubeAsyncTask;
    PresenterWidgetsControl presenterWidgetsControl;
    TubeForYouWidgetsControl tubeForYouWidgetsControl;
    BackupControl backupControl;

    int selectMode = 0; // 0 = Video Mode, 1 = PlayList Mode , 3 = Live Mode  // เลือก Mode ค้นหา
    int sortBy = 0; // 0. first 1 last 2 Alphabet  // รูปแบบจากเรียงข้อมูล

    boolean isPlayListMode = false; // ไว้ใช้ควบคุมประเภทข้อมูลที่ค้นหาและรูปแบบการจัดการใน ListView

    String saveFavVideo = "";  // เก็บข้อมูลวิดีโอโปรด
    String saveFavPlaylist = ""; // เก็บข้อมูล PlayList โปรด
    String searchResult = "";  // เก็บผลลัพธ์ค้นหา

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);   // เรียกใช้ Launcher ตอนเปิดแอพ
        setContentView(R.layout.activity_main);
        loadingShared();
        setupWidgets();

    }

    //บันทึกข้อมูลก่อนปิดแอพ
    @Override
    protected void onPause() {
        super.onPause();
        editor.putString("saveFavVideo", saveFavVideo);
        editor.putString("saveFavPlaylist", saveFavPlaylist);
        editor.commit();
    }

    // โหลดข้อมูลตอนเปิดแอพ
    private void loadingShared() {
        sp = this.getSharedPreferences("Save Mode", Context.MODE_PRIVATE);
        editor = sp.edit();
        saveFavVideo = sp.getString("saveFavVideo", "");
        saveFavPlaylist = sp.getString("saveFavPlaylist", "");
    }

    private void setupWidgets() {
        searchYoutubeAsyncTask = new SearchYoutubeAsyncTask(this);
        searchWidgetsControl = new SearchWidgetsControl(this);
        presenterWidgetsControl = new PresenterWidgetsControl(this);
        backupControl = new BackupControl(this);
        tubeForYouWidgetsControl = new TubeForYouWidgetsControl(this);

        // ส่วน ค้นหา
        searchWidgetsControl.voiceBT = (Button) findViewById(R.id.voiceBT);
        searchWidgetsControl.modeBT = (Button) findViewById(R.id.modeBT);
        searchWidgetsControl.searchET = (EditText) findViewById(R.id.searchET);
        searchWidgetsControl.searchBT = (Button) findViewById(R.id.searchBT);
        searchWidgetsControl.setupWidgetsControl();

        // ส่วน Tube For You
        tubeForYouWidgetsControl.tubeBT = (Button) findViewById(R.id.tubeBT);
        tubeForYouWidgetsControl.setupTubeForYouControl();

        // ส่วน Backup
        backupControl.backupBT = (Button) findViewById(R.id.backupBT);
        backupControl.setupBackupControl();

        // ส่วนการนำเสนอ
        presenterWidgetsControl.favVideoBT = (Button) findViewById(R.id.favVideoBT);
        presenterWidgetsControl.favPlaylistBT = (Button) findViewById(R.id.favPlaylistBT);
        presenterWidgetsControl.videoLV = (ListView) findViewById(R.id.videoLV);
        presenterWidgetsControl.setupWidgetsControl();

        // เริ่มต้นแอพ default อยู่ที่แสดงข้อมูลวิดีโอโปรด
        presenterWidgetsControl.initializeVideoMode();

    }

    // สั่ง Run Asynctask ค้นหา
    public void searchExecute(String query) {
        searchYoutubeAsyncTask.executeSearchVideo(query);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1010) {  // ของ TubeForYou Mode
            //  แสดงข้อมูลวิดีโอที่เคยเล่นใน TubeForYou Mode ในรูปแบบผล Search
            searchResult = data.getStringExtra("tubeHistory");
            if (searchResult == null) {
                searchResult = "";
            }
            tubeForYouWidgetsControl.tubeHistory = searchResult;

            if (!searchResult.isEmpty()) {
                isPlayListMode = false;
                sortBy = 0;
                setupListView(0);
            }

        } else if (requestCode == 77) {

            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchWidgetsControl.searchET.setText(text.get(0));  // พิมพ์ keywords ลง EditText
                searchWidgetsControl.searchVideoOption(searchWidgetsControl.searchET.getText().toString());
            }
        }
    }


    // แปลง voice เป็น text  ทำเฉพาะภาษาไทยเท่านั้น เหตุผลพิมพ์ไทยใน tablet แม่งลำบาก
    public void speechToText() {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "th-TH");
        try {
            startActivityForResult(intent, 77);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Hardware Problem",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    //  การทำงาน แปลง String เป็น ArrayList แล้วโแสดงผลหน้า ListView
    public void setupListView(int type) { // method เชื่อมระหว่าง SearchYoutubeAsyncTask กัย PresenterWidgtsControl
        presenterWidgetsControl.checkListViewType(type);
    }

    // เรียกใช้งานโหมด Tube4U
    public void intentTubeForYou() {
        if (isOnline()) {
            Intent intent = new Intent(this, TubeBehavior.class);
            intent.putExtra("saveFavVideo", saveFavVideo);
            startActivityForResult(intent, 1010);

        } else {
            Toast.makeText(MainActivity.this, "Network Error !!!!", Toast.LENGTH_SHORT).show();
        }
    }

    // ส่งข้อมูลไปให้ PlayYoutube เล่นโหมด PlayList
    public void intentPlayListYoutube(String playlistID, boolean selectShuffle) {

        if (isOnline()) {
            Intent intent = new Intent(this, PlayYoutube.class);
            intent.putExtra("isPlayListMode", isPlayListMode);
            intent.putExtra("selectShuffle", selectShuffle);
            intent.putExtra("playlistID", playlistID);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Network Error !!!!", Toast.LENGTH_SHORT).show();
        }

    }


    // ส่งข้อมูลไปให้ PlayYoutube เล่นโหมด Video
    public void intetntVideoYoutube(boolean isFavVideo, int videoNumber, boolean selectShuffle) {
        if (isOnline()) {
            Intent intent = new Intent(this, PlayYoutube.class);
            intent.putExtra("isPlayListMode", isPlayListMode);
            intent.putExtra("selectShuffle", selectShuffle);
            if (isFavVideo) {
                intent.putExtra("stringData", saveFavVideo);
            } else {
                intent.putExtra("stringData", searchResult);
            }
            intent.putExtra("videoNumber", videoNumber);
            intent.putExtra("sortBy", sortBy);

            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Network Error !!!!", Toast.LENGTH_SHORT).show();
        }
    }

    // ส่วนตรวจสอบว่า Hardware ยังอยู่สถานะ Online หรือไม่ ***สำคัญมาก***
    //////////////////////////////////
    public boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 203.113.24.199"); //Alternate dns server TOT
                int exitValue = ipProcess.waitFor();
                return (exitValue == 0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return false;
        }
    }


}
