/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.golfapp.test;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.golfapp.test.activities.HotelPackageActivity;
import com.golfapp.test.activities.NewsDetailActivity;
import com.golfapp.test.activities.ProDetailRateOffer;
import com.golfapp.test.activities.ProshopRateOfferActivity;
import com.golfapp.test.activities.RestaurantRatesActivity;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.PrefStore;
import com.golfapp.test.utils.TinyDB;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";
    PrefStore store;
    String tit = "", page = "";
    Intent intent;

    @Override
    public void onMessageReceived(String from, Bundle dat) {
        store = new PrefStore(getApplicationContext());
        String name = null;
        int isApplicationRunning = store.getInt(Constants.IS_APPLICATION_VISIBLE, 0);
        String msg = dat.getString("message");
        JSONObject data = null;
        try {
            data = new JSONObject(msg);
            tit = data.getString("title");
            page = data.getString("page");
            name = data.getString("name");
            int sid = data.getInt("sid");
            int pack = data.getInt("package");
            switch (page) {
                case "Hotel": {
                    String key = GcmConstants.HOTEL + sid;
                    int totalHotelBadgeCount = store.getInt(Constants.HOTEL_PUSH_COUNT, 0);
                    int particularHotelBadgeCont = store.getInt(key + "", 0);                        // previous unread notification for this id
                    if (particularHotelBadgeCont == 0) {                                        // if no previous notification
                        totalHotelBadgeCount = totalHotelBadgeCount + 1;                        // then update the counter.
                        particularHotelBadgeCont = particularHotelBadgeCont + 1;
                        store.setInt(key + "", particularHotelBadgeCont);                            // then save the notification badge for this id.
//                        store.setBoolean(sid + "" , true);
                        TinyDB.getInstance(this).putBoolean(key + "", true);
                        store.setInt(Constants.HOTEL_PUSH_COUNT, totalHotelBadgeCount);         // update total badge count
                        String packageID = store.getString(Constants.PACKAGE + key);
                        if (packageID == null) {
                            packageID = pack + "";
                        } else {
                            packageID = packageID + " , " + pack;
                        }
                        store.setString(Constants.PACKAGE + key, packageID);
                    } else {
                        String packageID = store.getString(Constants.PACKAGE + key);            // get the notifications for which package notifications are there
                        if (!packageID.contains(pack + "")) {                                   // is this package already stored in our local storage
                            totalHotelBadgeCount = totalHotelBadgeCount + 1;                    // if not update total badge count for home screen as this is new notification
                            store.setInt(Constants.HOTEL_PUSH_COUNT, totalHotelBadgeCount);     // update total push counts for home screen
                            particularHotelBadgeCont = particularHotelBadgeCont + 1;            // Update push count for particular hotel
                            store.setInt(key + "", particularHotelBadgeCont);
//                            store.setBoolean(sid + "" , true);
                            TinyDB.getInstance(this).putBoolean(key + "", true);
                            packageID = packageID + " , " + pack;
                            store.setString(Constants.PACKAGE + key, packageID);                // Update the packages that have new notification
                        }
                    }
                    intent = new Intent(this, HotelPackageActivity.class);
                    intent.putExtra("HotelID", sid);
                    intent.putExtra("Name", name);
                    sendNotification(name, tit, sid);
                    break;
                }
                case "Restaurant": {
                    String key = GcmConstants.RESTAURANT + sid;
                    int totalRestaurantBadgeCount = store.getInt(Constants.RESTAURANTS_PUSH_COUNT, 0);
                    int particularRestaurantBadgeCont = store.getInt(key + "", 0);                        // previous unread notification for this id
                    if (particularRestaurantBadgeCont == 0) {                                        // if no previous notification
                        totalRestaurantBadgeCount = totalRestaurantBadgeCount + 1;                        // then update the counter.
                        particularRestaurantBadgeCont = particularRestaurantBadgeCont + 1;
                        store.setInt(key + "", particularRestaurantBadgeCont);                                                   // then save the notification badge for this id.
                        TinyDB.getInstance(this).putBoolean(key + "", true);
                        store.setInt(Constants.RESTAURANTS_PUSH_COUNT, totalRestaurantBadgeCount);         // update total badge count
                        String packageID = store.getString(Constants.PACKAGE + key);
                        if (packageID == null) {
                            packageID = pack + "";
                        } else {
                            packageID = packageID + " , " + pack;
                        }
                        store.setString(Constants.PACKAGE + key, packageID);
                    } else {
                        String packageID = store.getString(Constants.PACKAGE + key);
                        if (!packageID.contains(pack + "")) {
                            totalRestaurantBadgeCount = totalRestaurantBadgeCount + 1;
                            store.setInt(Constants.RESTAURANTS_PUSH_COUNT, totalRestaurantBadgeCount);
                            particularRestaurantBadgeCont = particularRestaurantBadgeCont + 1;
                            TinyDB.getInstance(this).putBoolean(key + "", true);
                            store.setInt(key + "", particularRestaurantBadgeCont);                                                   // then save the notification badge for this id.
                            packageID = packageID + " , " + pack;
                            store.setString(Constants.PACKAGE + key, packageID);
                        }
                    }
                    intent = new Intent(this, RestaurantRatesActivity.class);
                    intent.putExtra("RestaurantID", sid);
                    intent.putExtra("Name", name);
                    sendNotification(name, tit, sid);
                    break;
                }
                case "Pro": {
                    String key = GcmConstants.PROS + sid;
                    int totalProsBadgeCount = store.getInt(Constants.PROS_PUSH_COUNT, 0);
                    int particularProsBadgeCont = store.getInt(key + "", 0);                        // previous unread notification for this id
                    if (particularProsBadgeCont == 0) {                                        // if no previous notification
                        totalProsBadgeCount = totalProsBadgeCount + 1;                        // then update the counter.
                        particularProsBadgeCont = particularProsBadgeCont + 1;
                        store.setInt(key + "", particularProsBadgeCont);                                                   // then save the notification badge for this id.
                        TinyDB.getInstance(this).putBoolean(key + "", true);
                        store.setInt(Constants.PROS_PUSH_COUNT, totalProsBadgeCount);         // update total badge count
                        String packageID = store.getString(Constants.PACKAGE + key);
                        if (packageID == null) {
                            packageID = pack + "";
                        } else {
                            packageID = packageID + " , " + pack;
                        }
                        store.setString(Constants.PACKAGE + key, packageID);
                    } else {
                        String packageID = store.getString(Constants.PACKAGE + key);
                        if (!packageID.contains(pack + "")) {
                            totalProsBadgeCount = totalProsBadgeCount + 1;
                            store.setInt(Constants.PROS_PUSH_COUNT, totalProsBadgeCount);
                            particularProsBadgeCont = particularProsBadgeCont + 1;
                            TinyDB.getInstance(this).putBoolean(key + "", true);
                            store.setInt(key + "", particularProsBadgeCont);                                                   // then save the notification badge for this id.
                            packageID = packageID + " , " + pack;
                            store.setString(Constants.PACKAGE + key, packageID);
                        }
                    }
                    intent = new Intent(this, ProDetailRateOffer.class);
                    intent.putExtra("ProsID", sid);
                    intent.putExtra("Name", name);
                    sendNotification(name, tit, sid);
                    break;
                }
                case "Proshop": {
                    String key = GcmConstants.PROSHOP + sid;
                    int totalProsHopBadgeCount = store.getInt(Constants.PROSHOP_PUSH_COUNT, 0);
                    int particularProsHopBadgeCont = store.getInt(key + "", 0);                        // previous unread notification for this id
                    if (particularProsHopBadgeCont == 0) {                                        // if no previous notification
                        totalProsHopBadgeCount = totalProsHopBadgeCount + 1;                        // then update the counter.
                        particularProsHopBadgeCont = particularProsHopBadgeCont + 1;
                        store.setInt(key + "", particularProsHopBadgeCont);                                                   // then save the notification badge for this id.
                        TinyDB.getInstance(this).putBoolean(key + "", true);
                        store.setInt(Constants.PROSHOP_PUSH_COUNT, totalProsHopBadgeCount);         // update total badge count
                        String packageID = store.getString(Constants.PACKAGE + key);
                        if (packageID == null) {
                            packageID = pack + "";
                        } else {
                            packageID = packageID + " , " + pack;
                        }
                        store.setString(Constants.PACKAGE + key, packageID);
                    } else {
                        String packageID = store.getString(Constants.PACKAGE + key);
                        if (!packageID.contains(pack + "")) {
                            totalProsHopBadgeCount = totalProsHopBadgeCount + 1;
                            store.setInt(Constants.PROSHOP_PUSH_COUNT, totalProsHopBadgeCount);
                            particularProsHopBadgeCont = particularProsHopBadgeCont + 1;
                            TinyDB.getInstance(this).putBoolean(key + "", true);
                            store.setInt(key + "", particularProsHopBadgeCont);                                                   // then save the notification badge for this id.
                            packageID = packageID + " , " + pack;
                            store.setString(Constants.PACKAGE + key, packageID);
                        }
                    }
                    intent = new Intent(this, ProshopRateOfferActivity.class);
                    intent.putExtra("ProShopID", sid);
                    intent.putExtra("Name", name);
                    sendNotification(name, tit, sid);
                    break;
                }
                case "News": {
                    String key = GcmConstants.NEWS + pack;
                    int totalNewsBadgeCount = store.getInt(Constants.NEWS_PUSH_COUNT, 0);
                    int particularNewsBadgeCont = store.getInt(key + "", 0);                        // previous unread notification for this id
                    if (particularNewsBadgeCont == 0) {                                        // if no previous notification
                        totalNewsBadgeCount = totalNewsBadgeCount + 1;                        // then update the counter.
                        particularNewsBadgeCont = particularNewsBadgeCont + 1;
                        store.setInt(key + "", particularNewsBadgeCont);                                                   // then save the notification badge for this id.
                        TinyDB.getInstance(this).putBoolean(key + "", true);
                        store.setInt(Constants.NEWS_PUSH_COUNT, totalNewsBadgeCount);         // update total badge count
                        String packageID = store.getString(Constants.PACKAGE + key);
                        if (packageID == null) {
                            packageID = pack + "";
                        } else {
                            packageID = packageID + " , " + pack;
                        }
                        store.setString(Constants.PACKAGE + key, packageID);
                    } else {
                        String packageID = store.getString(Constants.PACKAGE + key);
                        if (!packageID.contains(pack + "")) {
                            totalNewsBadgeCount = totalNewsBadgeCount + 1;
                            store.setInt(Constants.PROSHOP_PUSH_COUNT, totalNewsBadgeCount);
                            particularNewsBadgeCont = particularNewsBadgeCont + 1;
                            TinyDB.getInstance(this).putBoolean(key + "", true);
                            store.setInt(key + "", particularNewsBadgeCont);                                                   // then save the notification badge for this id.
                            packageID = packageID + " , " + pack;
                            store.setString(Constants.PACKAGE + key, packageID);
                        }
                    }
                    intent = new Intent(this, NewsDetailActivity.class);
                    intent.putExtra("NewsID", pack);
                    sendNotification(name, tit, pack);
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isApplicationRunning == 1)// app is currently running send a broadcast to update the home screen.
            refreshScreen(dat);
        updateBadgeOnAppIcon();

    }

    private void updateBadgeOnAppIcon() {
        int totalNotifications = 0;
        totalNotifications = store.getInt(Constants.NEWS_PUSH_COUNT, 0) + store.getInt(Constants.HOTEL_PUSH_COUNT, 0)
                + store.getInt(Constants.PROSHOP_PUSH_COUNT, 0) + store.getInt(Constants.PROS_PUSH_COUNT, 0) +
                store.getInt(Constants.RESTAURANTS_PUSH_COUNT, 0);              // total notification for the app is sum of all the notification
        ShortcutBadger.with(getApplicationContext()).count(totalNotifications);     // update this on app icon
    }

    private void refreshScreen(Bundle dat) {
        Intent intent = new Intent(Constants.PUSH_RECEIVED);
        intent.putExtras(dat);
        sendBroadcast(intent);
    }

    private void sendNotification(String lin1, String line2, int id) {
        //   Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Push", true);
        int dummyuniqueInt = new Random().nextInt(543254);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, dummyuniqueInt /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.a_notification_icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(line2)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(line2))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }
}
