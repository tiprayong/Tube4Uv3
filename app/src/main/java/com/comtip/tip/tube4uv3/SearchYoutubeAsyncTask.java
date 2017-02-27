package com.comtip.tip.tube4uv3;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TipRayong on 21/2/2560 16:32
 * Tube4Uv3
 */
public class SearchYoutubeAsyncTask {
    MainActivity main;
    //ตัวแปรสำหรับสร้าง url  json query Search
    String querySearch = "";

    public SearchYoutubeAsyncTask(MainActivity main) {
        this.main = main;
    }

    public void executeSearchVideo (String query){
        querySearch = query;
        new SearchVideo().execute();
    }

    //Search AsynTask
    private class  SearchVideo  extends AsyncTask<Void,String,Void> {
        String bufferResult = "";
        ProgressDialog pd;
        boolean beforeMode = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            beforeMode =  main.isPlayListMode;  // เก็บค่า mode ก่อนหน้านี้ ไว้ใช้กรณีดึงข้อมูลไม่สำเร็จ

            if(main.selectMode == 1) {
               main.isPlayListMode = true;
            } else {
               main.isPlayListMode = false;
            }

            pd = new ProgressDialog(main,R.style.PdSpinnerTheme);
            pd.setCancelable(false);
            pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            pd.show();
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
                    if(main.isPlayListMode) { // ข้อมูล Gson ค้นหา PlayList
                        GsonSearchPlayListYoutube searchOBJ = gsonSearch.fromJson(searchPage, GsonSearchPlayListYoutube.class);
                        for (int i = 0; i < searchOBJ.getItems().size(); i++) {
                            bufferResult += i+ "➴" +searchOBJ.getItems().get(i).getSnippet().getTitle()
                                    + "➴" +searchOBJ.getItems().get(i).getId().getPlaylistId()+ "✎";
                        }
                    } else { // ข้อมูล Gson ค้นหา Video
                        GsonSearchVideoYoutube searchOBJ = gsonSearch.fromJson(searchPage, GsonSearchVideoYoutube.class);
                        for (int i = 0; i < searchOBJ.getItems().size(); i++) {
                            bufferResult += i+ "➴" +searchOBJ.getItems().get(i).getSnippet().getTitle()
                                    + "➴" +searchOBJ.getItems().get(i).getId().getVideoId()+ "✎";
                        }
                    }

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
              if(!bufferResult.isEmpty()) {
                  main.searchResult =  bufferResult;
                  main.sortBy = 0;
                  main.setupListView(0);
              }  else {
                  // กรณีดึงข้อมูลไม่สำเร็จ  ให้คืนค่า isPlayListMode เป็นค่าเดิมก่อนหน้านี้
                  main.isPlayListMode = beforeMode;

              }
        }
    }

}
