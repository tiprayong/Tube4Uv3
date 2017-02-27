package com.comtip.tip.tube4uv3;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by TipRayong on 21/2/2560 15:45
 * Tube4Uv3
 * import android.support.v7.app.AlertDialog;
 */
public class SearchWidgetsControl {
    MainActivity main;
    public Button voiceBT = null;
    public Button modeBT = null;
    public EditText searchET = null;
    public Button searchBT = null;

    final String googleapisSearch = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=";
    final String APIkey = "YourKey";
    private String order = "";
    private String typeSearch = "";
    private String querySearch = "";

    public SearchWidgetsControl(MainActivity main) {
        this.main = main;
    }

    public void setupWidgetsControl() {
        if ((voiceBT == null) && (modeBT == null) && (searchET == null) && (searchBT == null)) {
            return;
        } else {

            //ปุ่ม voice  พูดแล้วแปลงเป็น text สำหรับ keyword ใช้ในการค้นหา
            voiceBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main.speechToText();
                }
            });

            //ปุ่ม mode  สำหรับเปลี่ยน mode ค้นหา  video , playlist , live
            modeBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (main.selectMode) {
                        case 0:  // Mode Video
                            main.selectMode = 1;
                            modeBT.setText("List");
                            modeBT.setTextColor(Color.YELLOW);
                            searchBT.setTextColor(Color.YELLOW);
                            voiceBT.setTextColor(Color.YELLOW);
                            break;
                        case 1: //  Mode PlayLisy
                            main.selectMode = 2;
                            modeBT.setText("Live");
                            modeBT.setTextColor(Color.RED);
                            searchBT.setTextColor(Color.RED);
                            voiceBT.setTextColor(Color.RED);
                            break;
                        case 2: // Mode Live
                            main.selectMode = 0;
                            modeBT.setText("Video");
                            modeBT.setTextColor(Color.WHITE);
                            searchBT.setTextColor(Color.WHITE);
                            voiceBT.setTextColor(Color.WHITE);
                            break;
                    }
                }
            });

            //ปุ่ม Search  กดเพื่อสั่ง query ค้นหา youtube
            searchBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchVideoOption(searchET.getText().toString());
                }
            });
        }
    }

    // Option ของปุ่ม Search

    public void searchVideoOption(final String keywords) {
        switch (main.selectMode) {
            case 0:  // ค้นหา Video
                typeSearch = "&type=video&maxResults=50&key=";
                break;
            case 1: //  ค้นหา PlayLisy
                typeSearch = "&type=playlist&maxResults=50&key=";
                break;
            case 2: // ค้นหา Live
                typeSearch = "&eventType=live&type=video&maxResults=50&key=";
                break;
        }

        AlertDialog.Builder alertSearch = new AlertDialog.Builder(main);
        alertSearch.setTitle("Search  " + keywords + " Order By ? ");

        alertSearch.setNegativeButton("☄ New Release ☄", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                order = "&order=date";
                querySearch = googleapisSearch + keywords + order + typeSearch + APIkey;
                main.searchExecute(querySearch);
            }
        });

        if (main.selectMode == 1) { // เฉพาะค้นหา PlayList ปุ่มนี้จะเปลี่ยนเป็นการค้นหาตามจำนวนวิดีโอ
            alertSearch.setNeutralButton("✪ Video Count ✪", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    order = "&order=videoCount";
                    querySearch = googleapisSearch + keywords + order + typeSearch + APIkey;
                    main.searchExecute(querySearch);
                }
            });
        } else {
            alertSearch.setNeutralButton("✪ Popular ✪", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    order = "&order=viewCount";  //order="&order=videoCount";
                    querySearch = googleapisSearch + keywords + order + typeSearch + APIkey;
                    main.searchExecute(querySearch);
                }
            });
        }

        alertSearch.setPositiveButton("★ Normal ★", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                order = "";
                querySearch = googleapisSearch + keywords + order + typeSearch + APIkey;
                main.searchExecute(querySearch);
            }
        });

        AlertDialog alertS = alertSearch.create();
        alertS.show();
    }


}
