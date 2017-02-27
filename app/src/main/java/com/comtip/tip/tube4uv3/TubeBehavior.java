package com.comtip.tip.tube4uv3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TipRayong on 3/8/2559.
 */
public class TubeBehavior extends AppCompatActivity {

    //Array สำหรับแสดงผลลัพธืการ Search  เอาแค่ 50 อันดับแรก
    String[] titleSearch = new String[50];
    String[] videoSearch = new String[50];


    // บันทึก History วิดีโอที่เคยเล่น
    String tubeHistory = "";
    int  indexHistory = 0;

    ArrayList<String> favoriteKey = new ArrayList<>();
    String[] orderRandom = {"","&order=viewCount","&order=date"}; //&order=relevance

    Random random = new Random();
    int  videoRandom = 0;   // สุ่ม keywords,order, ตำแหน่งข้อมูลที่จะส่งให้ youtube เล่น

    boolean ending = false;
    String saveFavVideo = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        saveFavVideo  =  bundle.getString("saveFavVideo","");
        if(saveFavVideo == null) {saveFavVideo = "";}
        if (!saveFavVideo.isEmpty()) {
            // สร้างฐานข้อมูล Keywords จาก Favorite List
            String[] bufferSetFav = saveFavVideo.split("\\✎");
            for (int i = 0; i < bufferSetFav.length; i++) {
                String[] bufferSetVideo = bufferSetFav[i].split("\\➴");
                favoriteKey.add(bufferSetVideo[1]);
            }

            Collections.shuffle(favoriteKey); //  สลับข้อมูล keyword
            searchOption();  // เริ่มการทำงานสุ่มหาเพลง
        }
        else {
            //  กลับหน้า Main เพราะไม่มีข้อมูลพอสร้าง keywords
            backToMain();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1111)     {
            ending = data.getBooleanExtra("ending",false);

            if(ending)  {
                backToMain();
            }
            else {
               searchOption();
            }
        }
    }

    // ส่งข้อมูลกลับ MainActivity
    private void backToMain() {
        Intent intent = new Intent();
        intent.putExtra("tubeHistory",tubeHistory);
        setResult(1010, intent);
        finish();
    }

    // สร้าง Search query URL
    private void searchOption () {
         //  ถ้า  Network ยังปกติ ให้เข้ากระบวนการสร้าง Query Url
        if(isOnline()) {

            new SearchPlaylist().execute();

        } else{
            // กลับหน้าหลัก เพราะ Network มีปัญหา
            backToMain();
        }

    }




    //Search AsynTask
    private class  SearchPlaylist  extends AsyncTask<Void,String,Void> {
        //ตัวแปรสำหรับสร้าง url  json query Search

        ProgressDialog pd;
        String querySearch = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Arrays.fill(titleSearch,null);
            Arrays.fill (videoSearch,null);


            querySearch = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="
                    +favoriteKey.get(random.nextInt(favoriteKey.size()))
                    +orderRandom[random.nextInt(3)]
                    +"&type=video&videoCategoryId=10&maxResults=50&key=YourKey";


            pd = new ProgressDialog(TubeBehavior.this,R.style.PdSpinnerTheme);
            pd.setCancelable(false);
            pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            pd.show();
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pd.setMessage(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(querySearch).build();
            String searchPage = "";

            try {
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    searchPage = response.body().string();
                    Gson gsonSearch = new Gson();
                    GsonSearchVideoYoutube searchOBJ =  gsonSearch.fromJson(searchPage,GsonSearchVideoYoutube.class);


                    for (int i = 0; i < searchOBJ.getItems().size(); i++) {

                            videoSearch[i] = searchOBJ.getItems().get(i).getId().getVideoId();
                            titleSearch[i] = searchOBJ.getItems().get(i).getSnippet().getTitle();
                            publishProgress(titleSearch[i]);

                    }

                     //  random ตำแหน่งข้อมูลที่จะส่งไปให้ youtube เล่น โดยอิงจากขนาดข้อมูลที่ได้ gson
                    videoRandom = random.nextInt(searchOBJ.getItems().size());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();

            // กรองผลลัพธ์ที่ไม่ต้องการ  ถ้าเจอให้เริ่มกระบวนการค้นหาข้อมูลใหม่แต่ต้น
            if(filterVideo(titleSearch[videoRandom])) {
                 searchOption();
            }  else {
                intetntYoutube();
            }
        }
    }

    // ส่งข้อมูลไปให้ PlayTubeForYou
    private void  intetntYoutube () {
        // บันทึก History
        tubeHistory += indexHistory+ "➴" + titleSearch[videoRandom]+ "➴" +videoSearch[videoRandom]+ "✎";
        indexHistory =  indexHistory+1;

        Intent intent = new Intent(this,PlayTubeForYou.class);
        intent.putExtra("videoSearch",videoSearch[videoRandom]);
        startActivityForResult(intent,1111);

    }

    //Filter  กรองผลลัพธ์ที่ไม่ต้องการ
    private static boolean filterVideo (String item){
        String[] filter ={
                "Minecraft",
                "minecraft",
                "Cartoon",
                "cartoon",
                "Let's Play",
                "let's Play",
                "Lesson",
                "lesson"
        };
        for (int i=0;i< filter.length;i++){

            if(item.contains(filter[i])){
                return true;
            }
        }
        return false;
    }


    // ส่วนตรวจสอบว่า Hardware ยังอยู่สถานะ Online หรือไม่ ***สำคัญมาก***
    //////////////////////////////////
    private boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 203.113.24.199"); //Alternate dns server TOT
                int exitValue = ipProcess.waitFor();
                return (exitValue == 0);
            } catch (IOException e) { e.printStackTrace(); }
            catch (InterruptedException e) { e.printStackTrace(); }
            return false;
        }
        else {return false;
        }
    }


}
