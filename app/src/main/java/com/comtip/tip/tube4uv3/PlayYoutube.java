package com.comtip.tip.tube4uv3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TipRayong on 12/7/2559.
 */
public class PlayYoutube extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    final String YOUTUBE_API_KEY = "AIzaSyB-iYo9CZ0yb13a4esvDeVOZG2zdTqFf0I";

    //windgets
    YouTubePlayerView youtubeView;

    //ตัวแปร  Bundle รับข้อมูลมาจาก Intent  MainActiviy
    ArrayList<String[]> arrayVideoData = new ArrayList<>();
    String stringVideoData = "";
    String playlistID = "";

    int sortBy = 0; // 0. first 1 last 2 Alphabet
    ArrayList<String> playlist = new ArrayList<>();      // ตัวแปร ArrayList สำหรับสร้าง Playlist เพื่อส่งให้ Youtube เล่น
    boolean selectShuffle = false;  //  boolean เล่น playlist แบบ direct หรือ shuffle
    boolean isPlayListMode = false;  // เป็น video หรือ playlist
    int videoNumber = 0;  // ตำแหน่งวีดีโอลำดับที่เท่าไร

    settingPlaylistEventListener setting = new settingPlaylistEventListener();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.youtube_layout);
        youtubeView = (YouTubePlayerView) findViewById(R.id.youtubeView);

    }


    @Override
    protected void onStart() {
        super.onStart();

        Bundle bundle = getIntent().getExtras();

        isPlayListMode = bundle.getBoolean("isPlayListMode", false);
        selectShuffle = bundle.getBoolean("selectShuffle", false);

        if (isPlayListMode) {  // ของ PlayListMode
            playlistID = bundle.getString("playlistID");
            if (playlistID.isEmpty()) {
                finish();
            } else {
                if (selectShuffle) {
                    //  AsyncTask โหลดข้อมูลทั้งหมดมาทำ Shuffle
                    new GetYoutubePlaylistAllPage().execute();
                } else {
                    youtubeView.initialize(YOUTUBE_API_KEY, PlayYoutube.this);
                }
            }

        } else {  // ของ Video
            stringVideoData = bundle.getString("stringData");
            sortBy = bundle.getInt("sortBy"); // รูปแบบ comparator จัดเรียงข้อมูล
            videoNumber = bundle.getInt("videoNumber");
            if (!stringVideoData.isEmpty()) {
                String[] bufferSetFav = stringVideoData.split("\\✎");
                for (int i = 0; i < bufferSetFav.length; i++) {
                    String[] bufferSetVideo = bufferSetFav[i].split("\\➴");
                    arrayVideoData.add(bufferSetVideo);
                }
                if (selectShuffle) {
                    createPlaylist();
                } else {
                    sortArray();
                }

            } else {
                // กลับหน้าหลัก เพราะไม่มีข้อมูล
                finish();
            }

        }

    }

    private void sortArray() {
        //  Comparator กำหนดรูปแบบการเรียงลำดับ

        switch (sortBy) {

            //First แรกไปล่าสุด
            case 0:
                Collections.sort(arrayVideoData, new Comparator<String[]>() {
                    @Override
                    public int compare(String[] lhs, String[] rhs) {
                        int lS = Integer.parseInt(lhs[0]);
                        int rS = Integer.parseInt(rhs[0]);
                        return ((Integer) lS).compareTo(rS);
                    }
                });
                break;

            //Last  ล่าสุดไปแรก
            case 1:
                Collections.sort(arrayVideoData, Collections.reverseOrder(new Comparator<String[]>() {
                    @Override
                    public int compare(String[] lhs, String[] rhs) {
                        int lS = Integer.parseInt(lhs[0]);
                        int rS = Integer.parseInt(rhs[0]);
                        return ((Integer) lS).compareTo(rS);
                    }
                }));
                break;

            // เรียงลำดับตามตัวอักษร
            case 2:
                Collections.sort(arrayVideoData, new Comparator<String[]>() {
                    @Override
                    public int compare(String[] lhs, String[] rhs) {
                        return (lhs[1].compareTo(rhs[1]));
                    }
                });
                break;
        }

        createPlaylist();

    }

    private void createPlaylist() {  // ของ Video แปลงให้เป็น Playlist เพื่อให้ Youtube เล่นได้
        //  นำข้อมูลใส่ playlist

        for (int i = 0; i < arrayVideoData.size(); i++) {
            if (selectShuffle) {
                playlist.add(arrayVideoData.get(i)[2]);
            } else {
                playlist.add(i, arrayVideoData.get(i)[2]);
            }
        }


        youtubeView.initialize(YOUTUBE_API_KEY, PlayYoutube.this);
    }


    // กดป่ม back เรียกเมนู back to main menu
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertBack = new AlertDialog.Builder(PlayYoutube.this);
        alertBack.setTitle("Back to Main Menu ?");
        alertBack.setPositiveButton("✔ Yes ✔", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertBack.setNegativeButton("✘ No ✘", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //no Action
            }
        });

        AlertDialog alertB = alertBack.create();
        alertB.show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setFullscreen(true);
        youTubePlayer.setPlaylistEventListener(setting); // ****

        if (selectShuffle) {
            Collections.shuffle(playlist);   // shuffle PlayList
            youTubePlayer.loadVideos(playlist);
        } else {
            if (isPlayListMode) { //ของ playlist Mode
                youTubePlayer.loadPlaylist(playlistID);  // Direct Play
            } else {  // ของ Video Mode
                if (videoNumber == 0) {
                    youTubePlayer.loadVideos(playlist);   // Direct Play      need ?
                } else {
                    youTubePlayer.loadVideos(playlist, videoNumber, 0);  // เล่นวีดีโอนี้ก่อนเป็นอันดับแรก
                }
            }

        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, 1).show();
        } else {
            Toast.makeText(this, "Unknown Error", Toast.LENGTH_LONG).show();
        }
        // เจอ Error อะไรก็ตามให้ปิดทันที
        finish();
    }


    // กำหนด PlayListEventListener
    private final class settingPlaylistEventListener implements YouTubePlayer.PlaylistEventListener {

        @Override
        public void onPrevious() {

            if (isOnline()) {
                //Network Status  is OK!!!
            } else {
                finish();
            }
        }

        @Override
        public void onNext() {

            if (isOnline()) {
                //Network Status  is OK!!!
            } else {
                finish();
            }
        }

        @Override
        public void onPlaylistEnded() {

        }
    }

    // โหลดข้อมูล  Playlist จาก Youtube  จะมีกี่ Page  กี่ Video  มีเท่าไรดึงได้หมด

    private class GetYoutubePlaylistAllPage extends AsyncTask<Void, String, Void> {
        // ตัวแปรสำหรับดึง Playlist เพื่อสร้าง Shuffle
        final String googleapis = "https://www.googleapis.com/youtube/v3/playlistItems?";
        String pageToken = "";
        String pageTokenBuffer = "YOUTUBE";
        final String snippet = "part=snippet&playlistId=";
        final String maxResults = "&maxResults=50&key=";
        String queryYTPL = "";
        String playlistPage = "";
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pageTokenBuffer = "YOUTUBE";
            pd = new ProgressDialog(PlayYoutube.this);
            pd.setTitle("กำลังทำการ Shuffle ใน PlayList");
            pd.setMessage("รอสักครู่ . . .");
            pd.setCancelable(false);
            pd.show();

        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            // บอกสถานะชื่อวีดีโอที่กำลังโหลด
            pd.setMessage(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {

            while (pageTokenBuffer != null) {

                queryYTPL = googleapis + pageToken + snippet + playlistID + maxResults + YOUTUBE_API_KEY;

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(queryYTPL).build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        playlistPage = response.body().string();

                        // เข้ากระบวนการใช้ Gson  ดึง Playlist ทั้งหมด
                        Gson gsonYoutube = new Gson();
                        GsonGetPlaylistYoutube obj = gsonYoutube.fromJson(playlistPage, GsonGetPlaylistYoutube.class);

                        //  ใส่ค่าตัวแปรให้ pageToken  สำหรับใช้ในการดึงข้อมูลหน้าต่อไป
                        pageTokenBuffer = obj.getNextPageToken();
                        if (pageTokenBuffer != null) {
                            pageToken = "pageToken=" + pageTokenBuffer + "&";
                        }

                        //  ใส่ข้อมูลชื่อวีดีโอและรหัสวีดีโอ
                        for (int i = 0; i < obj.getItems().size(); i++) {
                            playlist.add(obj.getItems().get(i).getSnippet().getResourceId().getVideoId());
                            // บอกสถานะชื่อวีดีโอที่กำลังโหลด
                            publishProgress(obj.getItems().get(i).getSnippet().getTitle());
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            youtubeView.initialize(YOUTUBE_API_KEY, PlayYoutube.this);
        }
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
    //////////////////////////////////

}
