package com.comtip.tip.tube4uv3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by TipRayong on 25/2/2560 16:41
 * Tube4Uv3
 */
public class TubeForYouWidgetsControl {
    MainActivity main;
    public String tubeHistory = "";
    public Button tubeBT = null;

    public TubeForYouWidgetsControl(MainActivity main) {
        this.main = main;
    }

    public void setupTubeForYouControl() {
        if (tubeBT == null) {
            return;
        } else {
            tubeBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  tubeForYouDialog();
                }
            });
        }

    }

    private void tubeForYouDialog (){

        AlertDialog.Builder  alertTubeForYou = new AlertDialog.Builder(main);
        alertTubeForYou.setTitle("Active TubeForYou Mode ? ");

        // แสดงวิดีโอที่เคยเล่นใน TubeForYou Mode ก่อนหน้านี้
        alertTubeForYou.setNegativeButton("✎ History ✎", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(tubeHistory.isEmpty()) {
                    Toast.makeText(main, "Don't Have History Record !!!.", Toast.LENGTH_SHORT).show();
                } else {
                    main.searchResult = tubeHistory;
                    main.isPlayListMode = false;
                    main.sortBy = 0;
                    main.setupListView(0);
                }

            }
        });


        alertTubeForYou.setNeutralButton("✘ No, Not Now. ✘", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Don't !!!
            }
        });


        // สั่ง Intent  Class TubeBehavior
        alertTubeForYou.setPositiveButton("✔ Yes, Make My Day. ✔", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(main.saveFavVideo.isEmpty())  {
                    Toast.makeText(main, "Data is Empty !! Can't use TubeForYou Mode.", Toast.LENGTH_SHORT).show();
                }else {
                    main.intentTubeForYou();
                }
            }
        });

        AlertDialog alertT = alertTubeForYou.create();
        alertT.show();

    }

}
