package com.comtip.tip.tube4uv3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TipRayong on 24/2/2560 14:11
 * Tube4Uv3
 */
public class BackupControl {
    MainActivity main;
    public Button backupBT = null;

    public BackupControl(MainActivity main) {
        this.main = main;
    }

    public void setupBackupControl() {
        if ((backupBT == null)) {
            return;
        } else {
            backupBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backupAlertDialog();
                }
            });

        }
    }

    private void backupAlertDialog() {
        AlertDialog.Builder alertBackup = new AlertDialog.Builder(main);
        alertBackup.setTitle("✧ Backup ✧");
        // Load Backup
        alertBackup.setNegativeButton("✪ Load ✪", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder alertLoad = new AlertDialog.Builder(main);
                alertLoad.setTitle("✪ Load Backup ✪");
                alertLoad.setNegativeButton("✘ NO ✘", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel
                    }
                });
                alertLoad.setPositiveButton("✔ YES ✔", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        readFromFile();
                    }
                });
                AlertDialog alertL = alertLoad.create();
                alertL.show();

            }
        });

        //Save Backup
        alertBackup.setPositiveButton("✰ Save ✰", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder alertSave = new AlertDialog.Builder(main);
                alertSave.setTitle("✰ Save Backup ✰");
                alertSave.setNegativeButton("✘ NO ✘", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel
                    }
                });
                alertSave.setPositiveButton("✔ YES ✔", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        writeToFile();
                    }
                });
                AlertDialog alertS = alertSave.create();
                alertS.show();
            }
        });
        AlertDialog alertB = alertBackup.create();
        alertB.show();
    }

    private void writeToFile() {
        try {
            // ตรวจสอบว่ามี Folder สำหรับเก็บ Save มั้ย ถ้าไม่มีให้สร้างขึ้น
            File root = new File(Environment.getExternalStorageDirectory(), "tube4uv3save");
            if (!root.exists()) {
                root.mkdirs();
            }

            // กำหนดชื่อไฟล์ และข้อมูลที่จะ save เข้าไป   โดยผ่าน temp ชั่วคราวเพื่อกัน bug ข้อมูลว่างเปล่า ไม่งั้นจะมีปัญหาตอน load กลับ
            String saveFav = "";
            String tempVideo = "";
            String tempPlaylist = "";
            if (main.saveFavVideo.isEmpty()) {
                tempVideo = "empty";
            } else {
                tempVideo = main.saveFavVideo;
            }

            if (main.saveFavPlaylist.isEmpty()) {
                tempPlaylist = "empty";
            } else {
                tempPlaylist = main.saveFavPlaylist;
            }

            saveFav = tempVideo + "✍" + tempPlaylist;

            File saveTitleFile = new File(root, "savefav.txt");
            FileWriter writerTitle = new FileWriter(saveTitleFile);
            writerTitle.append(saveFav);
            writerTitle.flush();
            writerTitle.close();


            Toast.makeText(main, "All Save Done !!!!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Read Files
    private void readFromFile() {

        File checkTitle = new File(Environment.getExternalStorageDirectory(), "tube4uv3save/savefav.txt");
        //ตรวจสอบว่ามีไฟล์อยู่ไหม
        if (checkTitle.exists()) {
            try {
                // อ่านไฟล์กลับ
                String pathTitle = checkTitle.getPath();
                BufferedReader readerTitle = new BufferedReader(new FileReader((pathTitle)));
                String bufferTitle;
                String readTitle = "";
                while ((bufferTitle = readerTitle.readLine()) != null) {
                    readTitle += bufferTitle;
                }

                // เข้ากระบวนการนำข้อมูลที่โหลดกลับมานำไปใช้งาน
                if (!readTitle.isEmpty()) {
                    main.saveFavVideo = readTitle;

                    String[] bufferSave = readTitle.split("\\✍");
                    main.saveFavVideo = bufferSave[0];
                    main.saveFavPlaylist = bufferSave[1];

                    if (main.saveFavVideo.equalsIgnoreCase("empty")) {
                        main.saveFavVideo = "";
                    }

                    if (main.saveFavPlaylist.equalsIgnoreCase("empty")) {
                        main.saveFavPlaylist = "";
                    }

                    Toast.makeText(main, "Load Done !!!!", Toast.LENGTH_SHORT).show();
                    main.recreate();
                } else {
                    Toast.makeText(main, "Data Error !!!!", Toast.LENGTH_SHORT).show();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(main, "Don't have save data in storage", Toast.LENGTH_SHORT).show();
        }
    }


}
