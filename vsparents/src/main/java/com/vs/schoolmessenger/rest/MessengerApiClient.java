//package com.vs.schoolmessenger.rest;
//
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.OkHttpClient;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
///**
// * Created by voicesnap on 3/9/2017.
// */
//
//public class MessengerApiClient {
////    public static String BASE_URL = "http://106.51.127.215:8089/ERP/AppService";
////    public static String BASE_URL = "http://106.51.127.215:8089/ERP/";
////    public static String BASE_URL = "http://45.113.138.248/apk/AppService/";
//
//    //    public static String BASE_URL = "http://45.113.138.248/apk/App_Service_mvc/";
////    public static String BASE_URL = "http://country.voicesnapforschools.com/apk/";
////    public static String BASE_URL = "http://vs3.voicesnapforschools.com/api/ParentsApi/";
////V4
////    public static String BASE_URL ="http://192.168.1.78:8017/api/ParentsApi/";
////    public static String BASE_URL ="http://106.51.127.215:8073/api/ParentsApi/";
//
//    //public static String BASE_URL ="http://vs3.voicesnapforschools.com/api/ParentsApiV4/";
//
//  //  public static String BASE_URL ="http://192.168.0.84:8088/api/ParentsApiV4/";
//
//    //  public static String BASE_URL ="http://192.168.0.84:8085/api/ParentsApiV4/";
//    //  public static String BASE_URL ="http://106.51.127.215:8070/api/ParentsApiV4/";
//
//   public static String BASE_URL ="http://106.51.127.215:8070/api/MergedApi/";
//
//   // public static String BASE_URL ="http://192.168.1.188:8090/api/ParentsApiV4/";
//
//    private static Retrofit retrofit = null;
//
////    public static Retrofit getClient() {
////        if (retrofit==null) {
////            retrofit = new Retrofit.Builder()
////                    .client(getOK_Client())
////                    .baseUrl(BASE_URL)
////                    .addConverterFactory(GsonConverterFactory.create())
////                    .build();
////        }
////        return retrofit;
////    }
////
////    public static OkHttpClient getOK_Client() {
////        OkHttpClient client = new OkHttpClient.Builder()
////                .connectTimeout(300, TimeUnit.SECONDS)
////                .writeTimeout(5, TimeUnit.MINUTES)
////                .readTimeout(5, TimeUnit.MINUTES)
////                .build();
////        return client;
////    }
//
//    public static Retrofit getClient() {
//        retrofit = builder.build();
//        return retrofit;
//    }
//
//    public static OkHttpClient getOK_Client() {
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(300, TimeUnit.SECONDS)
//                .writeTimeout(5, TimeUnit.MINUTES)
//                .readTimeout(5, TimeUnit.MINUTES)
//                .build();
//        return client;
//    }
//
//    private static Retrofit.Builder builder =
//            new Retrofit.Builder()
//                    .client(getOK_Client())
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create());
//
//    public static void changeApiBaseUrl(String newApiBaseUrl) {
//        BASE_URL = newApiBaseUrl;
//
//        builder = new Retrofit.Builder()
//                .client(getOK_Client())
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//        ;
//    }
//}
