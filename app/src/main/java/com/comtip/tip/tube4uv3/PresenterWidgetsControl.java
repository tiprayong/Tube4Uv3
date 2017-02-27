package com.comtip.tip.tube4uv3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by TipRayong on 22/2/2560 9:54
 * Tube4Uv3
 */
public class PresenterWidgetsControl {
    MainActivity main;
    public Button favPlaylistBT = null;
    public Button favVideoBT = null;
    public ListView videoLV = null;
    //โครงสร้างข้อมูล   0. index  1. Video or Playlist Name  2. id
    ArrayList<String[]> resultArray = new ArrayList<>();
    CustomArrayList adapter;

    // boolean เช็คว่าสร้าง arrayListData หรือยัง   ถ้ายังให้สร้าง ถ้าสร้างแล้วให้ไป method sort
    boolean isPlaylistArrayCreate = false;
    boolean isVideoArrayCreate = false;

    public PresenterWidgetsControl(MainActivity main) {
        this.main = main;
    }

    // ใช้ในเริ่มต้นการทำงานตอนเปิดแอพ
    public void initializeVideoMode() {
        if (!main.saveFavVideo.isEmpty()) {
            main.sortBy = 1;
            main.isPlayListMode = false;
            checkListViewType(2);
        }
    }


    public void setupWidgetsControl() {
        if ((favPlaylistBT == null) && (favVideoBT == null) && (videoLV == null)) {
            return;
        } else {
            favPlaylistBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (main.saveFavPlaylist.isEmpty()) {
                        Toast.makeText(main, "Data is Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        sortByAlertDialog("PlayList Fav.", 1, true);
                    }

                }
            });

            favVideoBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (main.saveFavVideo.isEmpty()) {
                        Toast.makeText(main, "Data is Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        sortByAlertDialog("Video Fav.", 2, false);
                    }
                }
            });

        }
    }

    // Alert Dialog ให้เลือกจะเรียงข้อมูลแบบไหน
    private void sortByAlertDialog(final String favName, final int type, final boolean mode) {

        AlertDialog.Builder alertSort = new AlertDialog.Builder(main);
        alertSort.setTitle(favName + " Sort By ?");
        alertSort.setNeutralButton("⇲ First ⇲", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.searchResult = "";  // เคลียร์ค่า String ของผลลัพธ์ค้นหาก่อนหน้านี้
                main.sortBy = 0;
                main.isPlayListMode = mode;
                checkListViewType(type);

            }
        });


        alertSort.setPositiveButton("⇱ Last Update ⇱", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.searchResult = "";  // เคลียร์ค่า String ของผลลัพธ์ค้นหาก่อนหน้านี้
                main.sortBy = 1;
                main.isPlayListMode = mode;
                checkListViewType(type);
            }
        });

        alertSort.setNegativeButton("Ⓐ ALPHABET Ⓐ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.searchResult = "";  // เคลียร์ค่า String ของผลลัพธ์ค้นหาก่อนหน้านี้
                main.sortBy = 2;
                main.isPlayListMode = mode;
                checkListViewType(type);

            }
        });

        AlertDialog alertS = alertSort.create();
        alertS.show();


    }

    // ตรวจสอบรูปแบบข้อมูลที่จะแสดง มาจากแหล่งไหน Serch หรือ Favorite และรูปแบบใด video หรือ Playlist
    public void checkListViewType(int type) {  // // 0 Search  1  fav PlayList  2  fav Video

        if (main.isPlayListMode) {  // แสดงสถานะที่ปุ่ม fav เพื่อให้รู้ว่าขณะนี้ listview แสดงข้อมูลประเภทไหน
            favPlaylistBT.setTextColor(Color.GREEN);
            favVideoBT.setTextColor(Color.WHITE);
        } else {
            favPlaylistBT.setTextColor(Color.WHITE);
            favVideoBT.setTextColor(Color.GREEN);

        }

        switch (type) {
            case 0: // Search Result
                favPlaylistBT.setText("PlayList Mode");
                favVideoBT.setText("Video Mode");
                isPlaylistArrayCreate = false;
                isVideoArrayCreate = false;
                convertStringToArrayList(main.searchResult);
                break;

            case 1: //  fav PlayList
                favPlaylistBT.setText("♡ PlayList Mode ♡");
                favVideoBT.setText("♡ Video Mode ♡");

                isVideoArrayCreate = false;
                if (isPlaylistArrayCreate) {
                    sortArray();
                } else {
                    isPlaylistArrayCreate = true;
                    convertStringToArrayList(main.saveFavPlaylist);
                }
                break;

            case 2: //  fav Video
                favPlaylistBT.setText("♡ PlayList Mode ♡");
                favVideoBT.setText("♡ Video Mode ♡");
                isPlaylistArrayCreate = false;
                if (isVideoArrayCreate) {
                    sortArray();
                } else {
                    isVideoArrayCreate = true;
                    convertStringToArrayList(main.saveFavVideo);

                }
                break;

        }
    }

    //แปลง String ให้อยู่รูปแบบ ArrayList<Array>
    private void convertStringToArrayList(String rawData) {
        if (!rawData.isEmpty()) {
            resultArray.clear();
            String[] bufferArray = rawData.split("\\✎");

            for (int i = 0; i < bufferArray.length; i++) {
                String[] bufferSet = bufferArray[i].split("\\➴");
                bufferSet[0] = "" + i;
                resultArray.add(bufferSet);
            }
            sortArray();
        }
    }

    // จัดเรียงข้อมูลตามรูปแบบที่ต้องการ
    private void sortArray() { // comparator
        switch (main.sortBy) {
            case 0: // first  เรียงจากแรกไปล่าสุด
                Collections.sort(resultArray, new Comparator<String[]>() {
                    @Override
                    public int compare(String[] lhs, String[] rhs) {
                        int lS = Integer.parseInt(lhs[0]);
                        int rS = Integer.parseInt(rhs[0]);
                        return ((Integer) lS).compareTo(rS);
                    }
                });
                break;

            case 1: // last เรียงจากล่าสุดไปแรก
                Collections.sort(resultArray, Collections.reverseOrder(new Comparator<String[]>() {
                    @Override
                    public int compare(String[] lhs, String[] rhs) {
                        int lS = Integer.parseInt(lhs[0]);
                        int rS = Integer.parseInt(rhs[0]);
                        return ((Integer) lS).compareTo(rS);
                    }
                }));

                break;

            case 2: // Alphabet  เรียงลำดับตามตัวอักษร
                Collections.sort(resultArray, new Comparator<String[]>() {
                    @Override
                    public int compare(String[] lhs, String[] rhs) {

                        return (lhs[1].compareTo(rhs[1]));
                    }
                });

                break;

        }

        listViewControl();
    }


    //  CustomArrayLIst   แสดงข้อมูลใน ListView
    private void listViewControl() {
        if ((videoLV == null)) {
            return;
        } else {
            adapter = new CustomArrayList(main, resultArray);
            videoLV.setAdapter(adapter);
            videoLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (main.isPlayListMode) {
                        playlistMode(resultArray.get(position)[1], resultArray.get(position)[2]);

                    } else {
                        videoMode(resultArray.get(position)[1], resultArray.get(position)[2], position);
                    }

                }
            });

            videoLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    //Delete
                    if (isPlaylistArrayCreate) {
                        //ลบใน PlayList
                        deleteFavorite(position, true);

                    } else if (isVideoArrayCreate) {
                        // ลบใน Video
                        deleteFavorite(position, false);
                    } else {
                        // Search  ลบไม่ได้
                        Toast.makeText(main, "Can't Delete in Search Result !!!", Toast.LENGTH_SHORT).show();

                    }

                    return false;
                }
            });

        }
    }

    // การกระทำในกรณีอยู่ใน PlayList Mode
    private void playlistMode(final String title, final String id) {
        AlertDialog.Builder alertPlayList = new AlertDialog.Builder(main);
        alertPlayList.setTitle("PlayList : " + title);
        if ((!isPlaylistArrayCreate)) { //กรณีอยู่หน้า Search
            alertPlayList.setNegativeButton("♡ Favorite ♡", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Favorite PlayList
                    addFavorite(title, id, true);
                }
            });
        }

        alertPlayList.setPositiveButton("⇆ Shuffle ⇆", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.intentPlayListYoutube(id, true);
            }
        });

        alertPlayList.setNeutralButton("⇉ Direct ⇉", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.intentPlayListYoutube(id, false);
            }
        });


        AlertDialog alert = alertPlayList.create();
        alert.show();


    }


    // การกระทำในกรณีอยู่ใน Video Mode
    private void videoMode(final String title, final String id, final int position) {
        AlertDialog.Builder alertDB = new AlertDialog.Builder(main);
        alertDB.setTitle(title);

        if ((!isVideoArrayCreate)) { //กรณีอยู่หน้า Search
            alertDB.setNegativeButton("♡ Favorite ♡", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //  favorite  video
                    addFavorite(title, id, false);
                }
            });
        }

        alertDB.setPositiveButton("⇆ Shuffle ⇆", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.intetntVideoYoutube(isVideoArrayCreate, 0, true);
            }
        });

        alertDB.setNeutralButton("⇉ Play This Video ⇉", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                main.intetntVideoYoutube(isVideoArrayCreate, position, false);
            }
        });

        AlertDialog alert = alertDB.create();
        alert.show();

    }


    // หาขนาด Array เพื่อใช้เป็น index ตำแหน่งสุดท้ายของชุดข้อมูล
    private int sizeArray(String rawData) {
        int indexFav = 0;
        if (!rawData.isEmpty()) {
            String[] bufferArray = rawData.split("\\✎");
            indexFav = bufferArray.length;
        }
        return indexFav;
    }

    // add favorite
    private void addFavorite(final String title, final String id, final boolean isFavoritePlaylist) {

        final AlertDialog.Builder alertSaveFav = new AlertDialog.Builder(main);
        if (isFavoritePlaylist) {
            alertSaveFav.setTitle(" Add PlayList Favorite : " + title);
        } else {
            alertSaveFav.setTitle(" Add Video Favorite : " + title);
        }
        alertSaveFav.setNegativeButton("✘ NO ✘", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel
            }
        });
        alertSaveFav.setPositiveButton("✔ YES ✔", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Add Favorite
                String tempFavSave = "";
                if (isFavoritePlaylist) {
                    tempFavSave = main.saveFavPlaylist;
                } else {
                    tempFavSave = main.saveFavVideo;
                }

                String bufferTextSave = sizeArray(tempFavSave) + "➴" + title + "➴" + id;
                tempFavSave += bufferTextSave + "✎";

                main.sortBy = 1;
                if (isFavoritePlaylist) {
                    main.saveFavPlaylist = tempFavSave;
                    checkListViewType(1);
                    Toast.makeText(main, "✔✔✔ Favorite PlayList " + title + " ✔✔✔", Toast.LENGTH_SHORT).show();
                } else {
                    main.saveFavVideo = tempFavSave;
                    checkListViewType(2);
                    Toast.makeText(main, "✔✔✔ Favorite Video " + title + " ✔✔✔", Toast.LENGTH_SHORT).show();
                }

            }
        });
        AlertDialog alertSF = alertSaveFav.create();
        alertSF.show();

    }


    //Delete  Favorite

    public void deleteFavorite(int position, final boolean deleteFavPlayList) {
        final String delData = resultArray.get(position)[0] + "➴" + resultArray.get(position)[1] + "➴" + resultArray.get(position)[2] + "✎";

        AlertDialog.Builder alertDeletee = new AlertDialog.Builder(main);
        alertDeletee.setTitle("Delete " + resultArray.get(position)[1] + " ?");
        alertDeletee.setPositiveButton("✔ Yes ✔", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete in stringData
                if (deleteFavPlayList) {
                    main.saveFavPlaylist = main.saveFavPlaylist.replace(delData, "");
                    if (main.saveFavPlaylist.isEmpty()) {
                        main.recreate(); // ถ้าข้อมูลถูกลบหมดเกลี้ยงให้สั่ง recreate
                    } else {
                        convertStringToArrayList(main.saveFavPlaylist);
                    }
                } else {
                    main.saveFavVideo = main.saveFavVideo.replace(delData, "");
                    if(main.saveFavVideo.isEmpty()) {// ถ้าข้อมูลถูกลบหมดเกลี้ยงให้สั่ง recreate
                        main.recreate();
                    }else {
                        convertStringToArrayList(main.saveFavVideo);
                    }
                }


            }
        });


        alertDeletee.setNegativeButton("✘ No ✘", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //no Action
            }
        });

        AlertDialog alertD = alertDeletee.create();
        alertD.show();

    }


}
