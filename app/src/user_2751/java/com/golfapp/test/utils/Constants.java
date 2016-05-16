package com.golfapp.test.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Created by aman on 11/21/2015.
 */
public class Constants {

    public static final String app_name = "Golf Club Giez";
    public static final String clientId = "2751";

    public static int draw = 7;
    public static int ddraw = 7;
    public static String OLD_LANGUAGE = "golf_app_user_9600_old_language";


    public static final String PROFILE_STRING = "Profile";

    //----APIs URLS----------------------------------------------------------------------------------------------------- LANGUAGE_ID FOR ALL URL REQUIRED
    public static final String webServiceUrl = "http://golfapp.ch/app/api/";
    //Required Fields
    public static final String urlAdvertising = webServiceUrl + "profile";               //clientId
    public static final String urlNews = webServiceUrl + "news";                          //clientId

    public static final String urlNewsDetail = webServiceUrl + "news";
    public static final String removeNotifications = webServiceUrl + "device/notifications_remove";                         //clientId

    public static final String urlCourseData = webServiceUrl + "courses";                        //clientId
    public static final String urlCourseRate = webServiceUrl + "courses/rates";                  //clientId, course_name_id

    public static final String urlHotelData = webServiceUrl + "hotels";                          //clientId
    public static final String urlHotelPackage = webServiceUrl + "hotels/packages";                 //hotelClientId

    public static final String urlRegister = webServiceUrl + "device/register";
    public static final String urlUpcomingEvents = webServiceUrl + "events";                //clientId
    public static final String urlPastEvents = webServiceUrl + "events";                    //clientId

    public static final String urlTeeTime = webServiceUrl + "Get_All_Information_1.php?tag=Get_All_info";               //clientId

    public static final String urlRestaurantData = webServiceUrl + "restaurants";                //clientId
    public static final String urlRestaurantMenu = webServiceUrl + "restaurants/menus";           //restaurentClientId
    public static final String urlRestaurantSugg = webServiceUrl + "restaurants/suggestions";       //restaurentClientId

    public static final String urlProShopData = webServiceUrl + "proshops";                     //clientId
    public static final String urlProShopOffer = webServiceUrl + "proshops/packages";             //proshopClientId

    public static final String urlProDetail = webServiceUrl + "pros";                             //clientId
    public static final String urlProRateOffer = webServiceUrl + "pros/packages"; //proClientId

    public static String SENT_TOKEN_TO_SERVER = "Gcm Registered";
    public static String REG_ID = "Registration Id";
    public static String IS_APPLICATION_VISIBLE = "ON";
    public static String PUSH_RECEIVED = "Push Received";
    public static String RESTAURANTS_PUSH_COUNT = "restaurant_badge_count";
    public static String NEWS_PUSH_COUNT = "news_badge_count";
    public static String PROS_PUSH_COUNT = "pros_badge_count";
    public static String PROSHOP_PUSH_COUNT = "proshop_badge_count";
    public static String HOTEL_PUSH_COUNT = "hotel_badge_count";
    public static String PACKAGE = "Package";
    public static int AD_SERVING_TIME_IN_SEC = 15;
    public static int CLEAR_BACKSTACK_TIME_IN_SECONDS = 10;
    public static final long TASK_KILL_DURATION_IN_MILISECONDS = 10 * 1000;
    public static String APP_START_TIME = "Start Time";
    public static String KILL_BACKSTACK_TIME = "Kill BackStack";

    public static String md5() {
        try {
            DateTimeZone denverTimeZone = DateTimeZone.forID("Europe/Zurich");
            DateTime denverDateTime = new DateTime(denverTimeZone);
            String timeAsString = denverDateTime.toLocalDateTime().toString();
            timeAsString = timeAsString.substring(0, timeAsString.indexOf(":")) + "sacha31" + Constants.clientId;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(timeAsString.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getLanguage() {
        String lang = Locale.getDefault().getLanguage();
        int cc = 2;
        switch (lang) {
//            case "en":
//                cc = 2;
//                break;
            case "fr":
                cc = 2;
                break;
//            case "de":
//                cc = 1;
//                break;
//            case "es":
//                cc = 1;
//                break;
//            case "it":
//                cc = 1;
//                break;
            default:
                cc = 2;
        }

        return cc;
        //set the language of app
    }
}
