package com.comtip.tip.tube4uv3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by TipRayong on 3/8/2559.
 */
public class PlayTubeForYou extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    final String YOUTUBE_API_KEY = "AIzaSyB-iYo9CZ0yb13a4esvDeVOZG2zdTqFf0I";

    //windgets
    YouTubePlayerView youtubeView;
    boolean ending = false;

    //ตัวแปร  Bundle รับข้อมูลมาจาก TubeBehavior
    settingPlayerStateChange change = new settingPlayerStateChange();
    String videoSearch;

    //  ใช้กัน วีดีโอหยุดเล่นเอง
    YouTubePlayer yt;
    settingPlaybackEventListener whenVideoStop = new settingPlaybackEventListener();


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.youtube_layout);

        youtubeView = (YouTubePlayerView) findViewById(R.id.youtubeView);

    }


    @Override
    protected void onStart() {
        super.onStart();
        // รับข้อมูลรหัส Video และ ชื่อ Video จาก TubeBehavior
        Bundle bundle = getIntent().getExtras();
        videoSearch = bundle.getString("videoSearch", null);
        if (videoSearch != null) {
            youtubeView.initialize(YOUTUBE_API_KEY, PlayTubeForYou.this);

        } else {
            sendBack();
        }


    }

    // กดปุ่ม back แล้วเรียกเมนู   กด next video กลับ TubeBehavior เพื่อประมวลผลหารายการใหม่มาแสดง
    // กด back to main menu  กลับเมนูหลัก
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertBack = new AlertDialog.Builder(PlayTubeForYou.this);
        alertBack.setTitle("Tube For You");
        alertBack.setPositiveButton("▷ Next Video ▷", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendBack();
            }
        });

        alertBack.setNeutralButton("✘ CLOSE ✘", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //no Action
            }
        });
        alertBack.setNegativeButton("◀ Back to Main Menu ◀", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ending = true;
                sendBack();
            }
        });

        AlertDialog alertB = alertBack.create();
        alertB.show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setFullscreen(true);
        youTubePlayer.loadVideo(videoSearch);
        youTubePlayer.setPlayerStateChangeListener(change);
        youTubePlayer.setPlaybackEventListener(whenVideoStop);
        yt = youTubePlayer;

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, 1).show();
        } else {
            Toast.makeText(this, "Unknown Error", Toast.LENGTH_LONG).show();
        }
        // เจอ Error อะไรก็ตามให้จบการทำงานแล้วส่งกลับ TubeBehavior ทันที
        sendBack();
    }


    // กำหนด PlayerStateChange

    private final class settingPlayerStateChange implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {
            sendBack();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            sendBack();
        }
    }

    // ส่งข้อมูลกลับไปหา TubeBehavior
    public void sendBack() {

        Intent intent = new Intent();
        intent.putExtra("ending", ending);
        setResult(1111, intent);
        finish();
    }

    //  ตั้งไว้ play อย่างเดียวห้ามหยุด
    private final class settingPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {


        }

        @Override
        public void onPaused() {

            yt.play();
        }

        @Override
        public void onStopped() {
            yt.play();
        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    }

}
