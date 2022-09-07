package com.vs.schoolmessenger.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vs.schoolmessenger.assignment.AssignmentViewClass;
import com.vs.schoolmessenger.model.CircularDates;
import com.vs.schoolmessenger.model.DatesModel;
import com.vs.schoolmessenger.model.HolidayModel;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.VideoModelClass;

import java.util.ArrayList;

/**
 * Created by voicesnap on 5/5/2018.
 */

public class SqliteDB extends SQLiteOpenHelper {
    public Context con;
    public static final String DATABASE_NAME = "School.db";
    private static final int DATABASE_VERSION = 1;
    public Context context;
    public static final String COUNT_DETAILS = "Count_Details";
    public static final String VOICE_MESSAGES = "Voice_Messages";
    public static final String TEXT_MESSAGES = "Text_Messages";
    public static final String ATTEDANCE = "Attedance_Messages";
    public static final String EXAM_TEST = "Exam_Test";
    public static final String EMERGENCY_VOICE = "Emergency_voice";
    public static final String EVENTS = "Events";
    public static final String NOTICE_BOARD = "Notice_Board";
    public static final String DOCUMENTS = "Documents";
    public static final String HOMEWORK = "Homework";
    public static final String CHILD_DETAILS = "Child_details";
    public static final String IMAGES = "IMAGES";
    public static final String HOLIDAYS = "HOLIDAYS";
    public static final String VIDEOS = "VIDEOS";
    public static final String ASSIGNMENT = "ASSIGNMENT";

    public static final String DAY_BY_DAY_VOICE_LIST = "Voice_List";
    public static final String DAY_BY_DAY_TEXT_LIST = "Text_List";
    public static final String VOICE_HOME_WORK_LIST = "Voice_Home_Work_list";
    public static final String TEXT_HOME_WORK_LIST = "Text_Home_Work_list";


    private ArrayList<DatesModel> Dates = new ArrayList<>();
    private ArrayList<DatesModel> text_Dates = new ArrayList<>();
    private ArrayList<DatesModel> Attedance_list = new ArrayList<>();
    private ArrayList<HolidayModel> Holidays_List = new ArrayList<>();
    private ArrayList<MessageModel> Exam_Test = new ArrayList<>();
    private ArrayList<MessageModel> emergency_voice = new ArrayList<>();
    private ArrayList<Profiles> Child_details = new ArrayList<>();
    private ArrayList<MessageModel> events = new ArrayList<>();
    private ArrayList<MessageModel> Notice = new ArrayList<>();
    private ArrayList<MessageModel> images = new ArrayList<>();
    private ArrayList<VideoModelClass> Videos_list = new ArrayList<>();
    private ArrayList<AssignmentViewClass> Assignment_list = new ArrayList<>();

    private ArrayList<CircularDates> Homework = new ArrayList<>();
    private ArrayList<MessageModel> documents = new ArrayList<>();

    private ArrayList<MessageModel> Daybyday_voice_list = new ArrayList<>();
    private ArrayList<MessageModel> Daybyday_text_list = new ArrayList<>();
    private ArrayList<MessageModel> Voice_Homework_list = new ArrayList<>();
    private ArrayList<MessageModel> Text_Homework_list = new ArrayList<>();


    //VIDEOS COLUMN

    public static final String V_ID = "VideoId";
    public static final String V_CREATED_BY = "CreatedBy";
    public static final String V_CREATED_ON = "CreatedOn";
    public static final String V_TITLE = "Title";
    public static final String V_DESCRIPTION = "Description";
    public static final String V_VIMEO_URL = "VimeoUrl";
    public static final String V_VIMEO_ID = "VimeoId";
    public static final String V_DETAIL_ID = "DetailID";
    public static final String V_IS_APPROVED = "IsAppViewed";
    public static final String V_IFRAME = "Iframe";

    //ASSIGNMENT_COLUMN

    public static final String A_ID = "AssignmentId";
    public static final String A_TYPE = "Type";
    public static final String A_SUBJECT = "Subject";
    public static final String A_TITLE = "Title";
    public static final String A_DATE = "Date";
    public static final String A_TIME = "Time";
    public static final String A_SUBMITTED_COUNT = "SubmittedCount";
    public static final String A_SENT_BY = "SentBy";
    public static final String A_ISAPPREAD = "isAppRead";
    public static final String A_END_DATE = "EndDate";
    public static final String A_DETAIL_ID = "DeTailId";


    //COUNT_DETAILS COLUMNS
    public static final String CHILD_ID = "CHILD_ID";
    public static final String PDF_COUNT = "PDF";
    public static final String IMAGE_COUNT = "IMAGE";
    public static final String EMERGENCY_COUNT = "EMERGENCYVOICE";
    public static final String VOICE_COUNT = "VOICE";
    public static final String SMS_COUNT = "SMS";
    public static final String NOTICEBOARD_COUNT = "NOTICEBOARD";
    public static final String EVENTS_COUNT = "EVENTS";
    public static final String HOMEWORK_COUNT = "HOMEWORK";
    public static final String EXAM_COUNT = "EXAM";

    //HOLIDAYS Column
    public static final String HOLIDAY_DATE = "H_Date";
    public static final String HOLIDAY_REASON = "H_Reason";
    //VOICE_MESSAGES COLUMNS

    public static final String DAY = "Day";
    public static final String DATE = "Date";
    public static final String UNREAD_COUNT = "UnreadCount";

    //TEXT_MESSGES COLUMNS


    public static final String TEXT_DAY = "Day";
    public static final String TEXT_DATE = "Date";
    public static final String TEXT_UNREAD_COUNT = "UnreadCount";

    //EXAM_TEST_COLUMNS

    public static final String EXAM_ID = "ExamId";
    public static final String EXAMINATION_NAME = "ExaminationName";
    public static final String EXAMINATION_SYLLABUS = "ExaminationSyllabus";
    public static final String SUBJECTS_FOR_EXAM = "SubjectsForExam";
    public static final String EXAM_READ_STATUS = "msgReadStatus";
    public static final String EXAM_MSG_DATE = "msgDate";
    public static final String EXAM_MSG_TIME = "msgTime";

    //EMEGENCY_VOICE MESSAGES

    public static final String EMERGENCY_MESSAGE_ID = "MessageId";
    public static final String EMERGENCY_SUBJECT = "Subject";
    public static final String EMERGENCY_URL = "URL";
    public static final String EMERGENCY_APPREADSTATUS = "AppReadStatus";
    public static final String EMERGENCY_DATE = "Date";
    public static final String EMERGENCY_TIME = "Time";
    public static final String EMERGENCY_DESCRIPTION = "Description";

    //CHILD_DETAILS_COLUMN
    public static final String CHILD_NAME = "ChildName";
    public static final String CHILD_DetailS_ID = "ChildID";
    public static final String CHILD_ROLL_NUMBER = "RollNumber";
    public static final String CHILD_STANDARD_NAME = "StandardName";
    public static final String CHILD_SECTION_NAME = "SectionName";
    public static final String CHILD_SCHOOL_ID = "SchoolID";
    public static final String CHILD_SCHOOL_NAME = "SchoolName";
    public static final String CHILD_SCHOOL_CITY = "SchoolCity";
    public static final String CHILD_SCHOOL_LOGO = "SchoolLogoUrl";
    public static final String CHILD_BOOK_ENABLE = "BookEnable";
    public static final String CHILD_BOOK_LINK = "BookLink";



    //EVENTS COLUMN

    public static final String EVENT_STATUS = "Status";
    public static final String EVENT_TITLE = "EventTitle";
    public static final String EVENT_CONTENT = "EventContent";
    public static final String EVENT_DATE = "EventDate";
    public static final String EVENT_TIME = "EventTime";
    public static final String EVENT_APP_READ_STATUS = "AppReadStatus";
    public static final String EVENT_DESCRIPTION = "Description";

    //NOTICE_BOARD COLUMNS



    public static final String NOTICE_STATUS = "Status";
    public static final String NOTICE_TITLE = "NoticeBoardTitle";
    public static final String NOTICE_Content = "NoticeBoardContent";
    public static final String NOTICE_DATE = "Date";
    public static final String NOTICE_DAY = "Day";
    public static final String NOTICE_APP_READ_STATUS = "AppReadStatus";
    public static final String NOTICE_DESCRIPTION = "Description";

    //DOCUMENTS_COLUMNS


    public static final String DOCUMENT_MSG_ID = "MessageID";
    public static final String DOCUMENT_SUBJECT = "Subject";
    public static final String DOCUMENT_URL = "URL";
    public static final String DOCUMENT_APPREAD_STATUS = "AppReadStatus";
    public static final String DOCUMENT_DATE = "Date";
    public static final String DOCUMENT_TIME = "Time";
    public static final String DOCUMENT_DESC = "Description";

    //HomeWork_columns

    public static final String HOME_WORK_DATE = "HomeworkDate";
    public static final String HOME_WORK_DAY = "HomeworkDay";
    public static final String HOME_WORK_VOICE_COUNT = "VoiceCount";
    public static final String HOMEWORK_VOICE_UNREAD = "voice_unread";
    public static final String HOME_WORK_TEXT_COUNT = "TextCount";
    public static final String HOME_WORK_TEXT_UNREAD = "text_unread";


    //Images columns
    public static final String IMAGE_LIST_ID = "ID";
    public static final String IMAGE_SUBJECT_LIST = "Subject";
    public static final String IMAGE_URL_LIST = "URL";
    public static final String IMAGE_APPREADSTATUS_LIST = "AppReadStatus";
    public static final String IMAGE_DATE_LIST = "Date";
    public static final String IMAGE_TIME_LIST = "Time";
    public static final String IMAGE_DESCRIPTION_LIST = "Description";

    //Daybyday_voice_list



    public static final String VOICE_LIST_ID = "ID";
    public static final String SUBJECT_LIST = "Subject";
    public static final String URL_LIST = "URL";
    public static final String APPREADSTATUS_LIST = "AppReadStatus";
    public static final String DATE_LIST = "Date";
    public static final String TIME_LIST = "Time";
    public static final String DESCRIPTION_LIST = "Description";

//DaybyDay_text_list
    public static final String VOICE_TEXT_LIST_ID = "ID";
    public static final String SUBJECT_TEXT_LIST = "Subject";
    public static final String URL_TEXT_LIST = "URL";
    public static final String APPREADSTATUS_TEXT_LIST = "AppReadStatus";
    public static final String DATE_TEXT_LIST = "Date";
    public static final String TIME_TEXT_LIST = "Time";
    public static final String DESCRIPTION_TEXT_LIST = "Description";

//Voice_homework_list

    public static final String HOME_WORK_VOICE_ID = "HomeworkID";
    public static final String HOME_WORK_VOICE_SUBJECT = "HomeworkSubject";
    public static final String HOMEWORK_VOICE_CONTENT = "HomeworkContent";


    public static final String HOME_WORK_VOICE_APPREADSTATUS = "AppreadStatus";
    public static final String HOME_WORK_VOICE_DATE = "date";
    public static final String HOMEWORK_VOICE_TIME = "time";

    public static final String HOMEWORK_VOICE_TITLE = "HomeworkTitle";

//text_homework_list
    public static final String HOME_WORK_TEXT_ID = "HomeworkID";
    public static final String HOME_WORK_TEXT_SUBJECT = "HomeworkSubject";
    public static final String HOMEWORK_TEXT_CONTENT = "HomeworkContent";

    public static final String HOME_WORK_TEXT_APPREADSTATUS = "AppreadStatus";
    public static final String HOME_WORK_TEXT_DATE = "date";
    public static final String HOMEWORK_TEXT_TIME = "time";

    public static final String HOMEWORK_TEXT_TITLE = "HomeworkTitle";


    public SqliteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
        con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // TODO Auto-generated method stub

        sqLiteDatabase.execSQL(
                "create table " + COUNT_DETAILS +
                        "("
                        + "CHILD_ID text unique primary key," +
                        " PDF text," +
                        "IMAGE text," +
                        "EMERGENCYVOICE text," +
                        "VOICE text," +
                        "SMS text," +
                        "NOTICEBOARD text," +
                        "EVENTS text," +
                        "HOMEWORK text," +
                        "EXAM text" +
                        ")"

        );

        sqLiteDatabase.execSQL(
                "create table " + VOICE_MESSAGES +
                        "("
                        + " Day text," +
                        "Date text," +
                        "UnreadCount text" +
                        ")"

        );

        sqLiteDatabase.execSQL(
                "create table " + HOLIDAYS +
                        "("
                        + " H_Date," +
                        "H_Reason" +
                        ")"

        );

        sqLiteDatabase.execSQL(
                "create table " + TEXT_MESSAGES +
                        "("
                        + " Day text," +
                        "Date text," +
                        "UnreadCount text" +
                        ")"

        );
        sqLiteDatabase.execSQL(
                "create table " + ATTEDANCE +
                        "("
                        + " Day text," +
                        "Date text," +
                        "UnreadCount text" +
                        ")"

        );

        sqLiteDatabase.execSQL(
                "create table " + EXAM_TEST +
                        "("
                        + " ExamId text unique primary key," +
                        "ExaminationName text," +
                        "ExaminationSyllabus text," +
                        "SubjectsForExam text," +
                        "msgReadStatus text," +
                        "msgDate text," +
                        "msgTime text" +
                        ")"

        );

        sqLiteDatabase.execSQL(
                "create table " + EMERGENCY_VOICE +
                        "("
                        + " MessageId text unique primary key," +
                        "Subject text," +
                        "URL text," +
                        "AppReadStatus text," +
                        "Date text," +
                        "Time text," +
                        "Description text" +
                        ")"

        );


        sqLiteDatabase.execSQL(
                "create table " + CHILD_DETAILS +
                        "("
                        + " ChildID text unique primary key," +
                        "ChildName text," +
                        "RollNumber text," +
                        "StandardName text," +
                        "SectionName text," +
                        "SchoolID text," +
                        "SchoolName text," +
                        "SchoolCity text," +
                        "SchoolLogoUrl text," +
                        "BookEnable text," +
                        "BookLink text" +
                        ")"

        );



        sqLiteDatabase.execSQL(
                "create table " + VIDEOS +
                        "("
                        + " VideoId text unique primary key," +
                        "CreatedBy text," +
                        "CreatedOn text," +
                        "Title text," +
                        "Description text," +
                        "VimeoUrl text," +
                        "VimeoId text," +
                        "DetailID text," +
                        "IsAppViewed text," +
                        "Iframe text" +
                        ")"

        );



        sqLiteDatabase.execSQL(
                "create table " + ASSIGNMENT +
                        "("
                        + " AssignmentId text unique primary key," +
                        "Type text," +
                        "Subject text," +
                        "Title text," +
                        "Date text," +
                        "Time text," +
                        "SubmittedCount text," +
                        "SentBy text," +
                        "isAppRead text," +
                        "EndDate text," +
                        "DeTailId text" +
                        ")"

        );

        sqLiteDatabase.execSQL(
                "create table " + EVENTS +
                        "("
                        + " Status text," +
                        "EventTitle text," +
                        "EventContent text," +
                        "EventDate text," +
                        "EventTime text," +
                        "AppReadStatus text," +
                        "Description text" +
                        ")"

        );


        sqLiteDatabase.execSQL(
                "create table " + NOTICE_BOARD +
                        "("
                        + " Status text," +
                        "NoticeBoardTitle text," +
                        "NoticeBoardContent text," +
                        "Date text," +
                        "Day text," +
                        "AppReadStatus text," +
                        "Description text" +
                        ")"

        );

        sqLiteDatabase.execSQL(
                "create table " + DOCUMENTS +
                        "("
                        + " MessageID text," +
                        "Subject text," +
                        "URL text," +
                        "AppReadStatus text," +
                        "Date text," +
                        "Time text," +
                        "Description text" +
                        ")"

        );



        sqLiteDatabase.execSQL(
                "create table " + HOMEWORK +
                        "("
                        + " HomeworkDate text," +
                        "HomeworkDay text," +
                        "VoiceCount text," +
                        "voice_unread text," +
                        "TextCount text," +
                        "text_unread text" +
                        ")"

        );


        sqLiteDatabase.execSQL(
                "create table " + DAY_BY_DAY_VOICE_LIST +
                        "("
                        + " ID text," +
                        "Subject text," +
                        "URL text," +
                        "AppReadStatus text," +
                        "Date text," +
                        "Time text," +
                        "Description text" +
                        ")"

        );

        sqLiteDatabase.execSQL(
                "create table " + DAY_BY_DAY_TEXT_LIST +
                        "("
                        + " ID text," +
                        "Subject text," +
                        "URL text," +
                        "AppReadStatus text," +
                        "Date text," +
                        "Time text," +
                        "Description text" +
                        ")"

        );

        sqLiteDatabase.execSQL(
                "create table " + VOICE_HOME_WORK_LIST +
                        "("
                        + " HomeworkID text," +
                        "HomeworkSubject text," +
                        "HomeworkContent text," +
                        "AppReadStatus text," +
                        "Date text," +
                        "Time text," +
                        "HomeworkTitle text" +
                        ")"

        );


        sqLiteDatabase.execSQL(
                "create table " + TEXT_HOME_WORK_LIST +
                        "("
                        + " HomeworkID text," +
                        "HomeworkSubject text," +
                        "HomeworkContent text," +
                        "AppReadStatus text," +
                        "Date text," +
                        "Time text," +
                        "HomeworkTitle text" +
                        ")"

        );


        sqLiteDatabase.execSQL(
                "create table " + IMAGES +
                        "("
                        + " ID text," +
                        "Subject text," +
                        "URL text," +
                        "AppReadStatus text," +
                        "Date text," +
                        "Time text," +
                        "Description text" +
                        ")"

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + COUNT_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + VOICE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TEXT_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + ATTEDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + HOLIDAYS);
        db.execSQL("DROP TABLE IF EXISTS " + VIDEOS);
        db.execSQL("DROP TABLE IF EXISTS " + ASSIGNMENT);
        db.execSQL("DROP TABLE IF EXISTS " + EXAM_TEST);
        db.execSQL("DROP TABLE IF EXISTS " + EMERGENCY_VOICE);
        db.execSQL("DROP TABLE IF EXISTS " + CHILD_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + NOTICE_BOARD);
        db.execSQL("DROP TABLE IF EXISTS " + DOCUMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + DAY_BY_DAY_VOICE_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + DAY_BY_DAY_TEXT_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + VOICE_HOME_WORK_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TEXT_HOME_WORK_LIST);
    }


    public void addCountDetails(String emgvoicecount, String voicemsgcount, String textmessagecount, String photoscount,
                                String documentcount, String noticeboard, String examtest, String schoolevent, String homework, String child_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(CHILD_ID, child_ID);
        initialValues.put(PDF_COUNT, documentcount);
        initialValues.put(IMAGE_COUNT, photoscount);
        initialValues.put(EMERGENCY_COUNT, emgvoicecount);
        initialValues.put(VOICE_COUNT, voicemsgcount);
        initialValues.put(SMS_COUNT, textmessagecount);
        initialValues.put(NOTICEBOARD_COUNT, noticeboard);
        initialValues.put(EVENTS_COUNT, schoolevent);
        initialValues.put(HOMEWORK_COUNT, homework);
        initialValues.put(EXAM_COUNT, examtest);
        db.insert(COUNT_DETAILS, null, initialValues);
        db.close();

    }

    public void updateCountDetails(String emgvoicecount, String voicemsgcount, String textmessagecount,
                                   String photoscount, String documentcount, String noticeboard, String examtest,
                                   String schoolevent, String homework, String child_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        //  initialValues.put(CHILD_ID, child_id);
        initialValues.put(PDF_COUNT, documentcount);
        initialValues.put(IMAGE_COUNT, photoscount);
        initialValues.put(EMERGENCY_COUNT, emgvoicecount);
        initialValues.put(VOICE_COUNT, voicemsgcount);
        initialValues.put(SMS_COUNT, textmessagecount);
        initialValues.put(NOTICEBOARD_COUNT, noticeboard);
        initialValues.put(EVENTS_COUNT, schoolevent);
        initialValues.put(HOMEWORK_COUNT, homework);
        initialValues.put(EXAM_COUNT, examtest);
        //   db.update(COUNT_DETAILS, initialValues, "_id=" + child_id, null);
        db.update(COUNT_DETAILS, initialValues, "CHILD_ID=" + child_id, null);
        db.close();

    }

    public boolean checkCountDetails(String childId) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select Child_ID from " + COUNT_DETAILS + " where Child_ID =?";
        Log.d("query", query);
        Cursor cursor = db.rawQuery(query, new String[]{childId});
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public Cursor getCountDetails(String childId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + COUNT_DETAILS + " where Child_ID =?";
        Log.d("query", query);
        Cursor cursor = db.rawQuery(query, new String[]{childId});
        Log.d("cursor", String.valueOf(cursor));
        return cursor;
    }





    public void addAssignment(ArrayList<AssignmentViewClass> dateslist, Context context) {
        this.Assignment_list = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            AssignmentViewClass business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();
            initialValues.put(A_ID, business.getAssignmentId());
            initialValues.put(A_TYPE, business.getType());
            initialValues.put(A_SUBJECT, business.getContent());
            initialValues.put(A_TITLE, business.getTitle());
            initialValues.put(A_DATE, business.getDate());
            initialValues.put(A_TIME, business.getTime());
            initialValues.put(A_SUBMITTED_COUNT, business.getSubmittedCount());
            initialValues.put(A_SENT_BY, business.getTotalcount());
            initialValues.put(A_ISAPPREAD, business.getIsAppread());
            initialValues.put(A_END_DATE, business.getEnddate());
            initialValues.put(A_DETAIL_ID, business.getDeTailId());

            db.insert(ASSIGNMENT, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }


    public ArrayList<AssignmentViewClass> getAssignment() {

        String selectQuery = "SELECT * FROM " + ASSIGNMENT;
        Log.d("selectQuery", selectQuery);

        ArrayList<AssignmentViewClass> business = new ArrayList<AssignmentViewClass>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                AssignmentViewClass business1 = new AssignmentViewClass();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setAssignmentId(cursor.getString(cursor.getColumnIndex("AssignmentId")));
                business1.setType(cursor.getString(cursor.getColumnIndex("Type")));
                business1.setContent(cursor.getString(cursor.getColumnIndex("Subject")));
                business1.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
                business1.setDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setTime(cursor.getString(cursor.getColumnIndex("Time")));
                business1.setSubmittedCount(cursor.getString(cursor.getColumnIndex("SubmittedCount")));
                business1.setTotalcount(cursor.getString(cursor.getColumnIndex("SentBy")));
                business1.setIsAppread(cursor.getString(cursor.getColumnIndex("isAppRead")));
                business1.setEnddate(cursor.getString(cursor.getColumnIndex("EndDate")));
                business1.setDeTailId(cursor.getString(cursor.getColumnIndex("DeTailId")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }


    public boolean checkAssignment() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + ASSIGNMENT;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public void deleteAssignment()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //   db.delete(VOICE_MESSAGES,null,null);
        db.execSQL("delete from "+ ASSIGNMENT);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }



    public void addVideos(ArrayList<VideoModelClass> dateslist, Context context) {
        this.Videos_list = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            VideoModelClass business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();
            initialValues.put(V_ID, business.getVideoId());
            initialValues.put(V_CREATED_BY, business.getCreatedBy());
            initialValues.put(V_CREATED_ON, business.getCreatedOn());
            initialValues.put(V_TITLE, business.getTitle());
            initialValues.put(V_DESCRIPTION, business.getDescription());
            initialValues.put(V_VIMEO_URL, business.getVimeoUrl());
            initialValues.put(V_VIMEO_ID, business.getVimeoId());
            initialValues.put(V_DETAIL_ID, business.getDetailID());
            initialValues.put(V_IS_APPROVED, business.getIsAppViewed());
            initialValues.put(V_IFRAME, business.getIframe());

            db.insert(VIDEOS, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }




    public ArrayList<VideoModelClass> getVideos() {

        String selectQuery = "SELECT * FROM " + VIDEOS;
        Log.d("selectQuery", selectQuery);

        ArrayList<VideoModelClass> business = new ArrayList<VideoModelClass>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                VideoModelClass business1 = new VideoModelClass();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setCreatedBy(cursor.getString(cursor.getColumnIndex("CreatedBy")));
                business1.setCreatedOn(cursor.getString(cursor.getColumnIndex("CreatedOn")));
                business1.setDescription(cursor.getString(cursor.getColumnIndex("Description")));
                business1.setDetailID(cursor.getString(cursor.getColumnIndex("DetailID")));
                business1.setIframe(cursor.getString(cursor.getColumnIndex("Iframe")));
                business1.setIsAppViewed(cursor.getString(cursor.getColumnIndex("IsAppViewed")));
                business1.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
                business1.setVideoId(cursor.getString(cursor.getColumnIndex("VideoId")));
                business1.setVimeoId(cursor.getString(cursor.getColumnIndex("VimeoId")));
                business1.setVimeoUrl(cursor.getString(cursor.getColumnIndex("VimeoUrl")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }


    public boolean checkVideos() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + VIDEOS;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public void deleteVideos()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //   db.delete(VOICE_MESSAGES,null,null);
        db.execSQL("delete from "+ VIDEOS);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }


    public void addVoiceMeassges(ArrayList<DatesModel> dateslist, Context context) {
        this.Dates = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            DatesModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();
            initialValues.put(DAY, business.getDay());
            initialValues.put(DATE, business.getDate());
            initialValues.put(UNREAD_COUNT, business.getCount());

            db.insert(VOICE_MESSAGES, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }


    public ArrayList<DatesModel> getVoiceMessages() {

        String selectQuery = "SELECT * FROM " + VOICE_MESSAGES;
        Log.d("selectQuery", selectQuery);

        ArrayList<DatesModel> business = new ArrayList<DatesModel>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DatesModel business1 = new DatesModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setCount(cursor.getString(cursor.getColumnIndex("UnreadCount")));
                business1.setDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setDay(cursor.getString(cursor.getColumnIndex("Day")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }


    public boolean checkVoiceMeassageCount() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + VOICE_MESSAGES;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public void deleteVoiceMessageRecords()
    {
        SQLiteDatabase db = this.getWritableDatabase();
      //   db.delete(VOICE_MESSAGES,null,null);
        db.execSQL("delete from "+ VOICE_MESSAGES);
       // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }


    public void addHolidays(ArrayList<HolidayModel> dateslist, Context context) {
        this.Holidays_List = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            HolidayModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();
            initialValues.put(HOLIDAY_DATE, business.getDate());
            initialValues.put(HOLIDAY_REASON, business.getReason());
            db.insert(HOLIDAYS, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<HolidayModel> getHolidays() {

        String selectQuery = "SELECT * FROM " + HOLIDAYS;
        Log.d("selectQuery", selectQuery);

        ArrayList<HolidayModel> business = new ArrayList<HolidayModel>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HolidayModel business1 = new HolidayModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setDate(cursor.getString(cursor.getColumnIndex("H_Date")));
                business1.setReason(cursor.getString(cursor.getColumnIndex("H_Reason")));
                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public boolean checkHolidays() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + HOLIDAYS;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public void deleteHolidays()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //   db.delete(VOICE_MESSAGES,null,null);
        db.execSQL("delete from "+ HOLIDAYS);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }



    public void addAttedanceMessages(ArrayList<DatesModel> dateslist, Context context) {
        this.Attedance_list = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            DatesModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(TEXT_DAY, business.getDay());
            initialValues.put(TEXT_DATE, business.getDate());
            initialValues.put(TEXT_UNREAD_COUNT, business.getCount());

            db.insert(ATTEDANCE, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<DatesModel> getAttedance_list() {

        String selectQuery = "SELECT * FROM " + ATTEDANCE;
        Log.d("selectQuery", selectQuery);

        ArrayList<DatesModel> business = new ArrayList<DatesModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DatesModel business1 = new DatesModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setCount(cursor.getString(cursor.getColumnIndex("UnreadCount")));
                business1.setDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setDay(cursor.getString(cursor.getColumnIndex("Day")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }

    public void deletAttedance()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TEXT_MESSAGES,null,null);
        db.execSQL("delete from "+ ATTEDANCE);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public boolean checkAttedance() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + ATTEDANCE;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }


    public void addTextMessages(ArrayList<DatesModel> dateslist, Context context) {
        this.text_Dates = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            DatesModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(TEXT_DAY, business.getDay());
            initialValues.put(TEXT_DATE, business.getDate());
            initialValues.put(TEXT_UNREAD_COUNT, business.getCount());

            db.insert(TEXT_MESSAGES, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<DatesModel> getTextMessages() {

        String selectQuery = "SELECT * FROM " + TEXT_MESSAGES;
        Log.d("selectQuery", selectQuery);

        ArrayList<DatesModel> business = new ArrayList<DatesModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DatesModel business1 = new DatesModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setCount(cursor.getString(cursor.getColumnIndex("UnreadCount")));
                business1.setDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setDay(cursor.getString(cursor.getColumnIndex("Day")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteTextMeassages()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TEXT_MESSAGES,null,null);
        db.execSQL("delete from "+ TEXT_MESSAGES);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public boolean checkTextMeassages() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TEXT_MESSAGES;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }


    public void addExamTest(ArrayList<MessageModel> dateslist, Context context) {
        this.Exam_Test = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);

                 SQLiteDatabase db = this.getWritableDatabase();
                ContentValues initialValues = new ContentValues();

                initialValues.put(EXAMINATION_NAME, business.getMsgTitle());
                initialValues.put(EXAMINATION_SYLLABUS, business.getMsgContent());
                initialValues.put(SUBJECTS_FOR_EXAM, business.getMsgdescription());
                initialValues.put(EXAM_READ_STATUS, business.getMsgReadStatus());
                initialValues.put(EXAM_MSG_DATE, business.getMsgDate());
                initialValues.put(EXAM_MSG_TIME, business.getMsgTime());
                initialValues.put(EXAM_ID, business.getMsgID());

                db.insert(EXAM_TEST, null, initialValues);
                db.close();

        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<MessageModel> getExamTest() {

        String selectQuery = "SELECT * FROM " + EXAM_TEST;
        Log.d("selectQuery", selectQuery);

        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("ExamId")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("ExaminationSyllabus")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("ExaminationName")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("SubjectsForExam")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("msgReadStatus")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("msgDate")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("msgTime")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }


    public boolean checkExamTest() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + EXAM_TEST;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public void deleteExamTest()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //   db.delete(VOICE_MESSAGES,null,null);
        db.execSQL("delete from "+ EXAM_TEST);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }





    public void addEmergencyVoice(ArrayList<MessageModel> dateslist, Context context) {
        this.emergency_voice = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(EMERGENCY_MESSAGE_ID, business.getMsgID());
            initialValues.put(EMERGENCY_SUBJECT, business.getMsgTitle());
            initialValues.put(EMERGENCY_URL, business.getMsgContent());
            initialValues.put(EMERGENCY_APPREADSTATUS, business.getMsgReadStatus());
            initialValues.put(EMERGENCY_DATE, business.getMsgDate());
            initialValues.put(EMERGENCY_TIME, business.getMsgTime());
            initialValues.put(EMERGENCY_DESCRIPTION, business.getMsgdescription());

            db.insert(EMERGENCY_VOICE, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }
//
//
    public ArrayList<MessageModel> getEmergency_voice() {

        String selectQuery = "SELECT * FROM " + EMERGENCY_VOICE;
        Log.d("selectQuery", selectQuery);

        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("MessageId")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("URL")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("Time")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("AppReadStatus")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("Description")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("Subject")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
//
//
    public boolean checkEmergencyvoice() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + EMERGENCY_VOICE;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public void deleteEmergencyVoice()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //   db.delete(VOICE_MESSAGES,null,null);
        db.execSQL("delete from "+ EMERGENCY_VOICE);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public void addChildDetails(ArrayList<Profiles> dateslist, Context context) {
        this.Child_details = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            Profiles business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(CHILD_NAME, business.getChildName());
            initialValues.put(CHILD_DetailS_ID, business.getChildID());
            initialValues.put(CHILD_ROLL_NUMBER, business.getRollNo());
            initialValues.put(CHILD_STANDARD_NAME, business.getStandard());
            initialValues.put(CHILD_SECTION_NAME, business.getSection());
            initialValues.put(CHILD_SCHOOL_ID, business.getSchoolID());
            initialValues.put(CHILD_SCHOOL_NAME, business.getSchoolName());
            initialValues.put(CHILD_SCHOOL_CITY, business.getSchoolAddress());
            initialValues.put(CHILD_SCHOOL_LOGO, business.getSchoolThumbnailImgUrl());

            initialValues.put(CHILD_BOOK_ENABLE, business.getBookEnable());
            initialValues.put(CHILD_BOOK_LINK, business.getBookLink());





            db.insert(CHILD_DETAILS, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<Profiles> getChildDetails() {

        String selectQuery = "SELECT * FROM " + CHILD_DETAILS;
        Log.d("selectQuery", selectQuery);

        ArrayList<Profiles> business = new ArrayList<Profiles>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Profiles business1 = new Profiles();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setChildID(cursor.getString(cursor.getColumnIndex("ChildID")));
                business1.setChildName(cursor.getString(cursor.getColumnIndex("ChildName")));
                business1.setRollNo(cursor.getString(cursor.getColumnIndex("RollNumber")));
                business1.setSchoolID(cursor.getString(cursor.getColumnIndex("SchoolID")));
                business1.setSchoolAddress(cursor.getString(cursor.getColumnIndex("SchoolCity")));
                business1.setSchoolName(cursor.getString(cursor.getColumnIndex("SchoolName")));
                business1.setSection(cursor.getString(cursor.getColumnIndex("SectionName")));
                business1.setStandard(cursor.getString(cursor.getColumnIndex("StandardName")));
                business1.setSchoolThumbnailImgUrl(cursor.getString(cursor.getColumnIndex("SchoolLogoUrl")));

                business1.setBookEnable(cursor.getString(cursor.getColumnIndex("BookEnable")));
                business1.setBookLink(cursor.getString(cursor.getColumnIndex("BookLink")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteChildDetails()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TEXT_MESSAGES,null,null);
        db.execSQL("delete from "+ CHILD_DETAILS);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public boolean checkChildDetails() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + CHILD_DETAILS;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }







    public void addEvents(ArrayList<MessageModel> dateslist, Context context) {
        this.events = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(EVENT_STATUS, business.getMsgID());
            initialValues.put(EVENT_TITLE, business.getMsgTitle());
            initialValues.put(EVENT_CONTENT, business.getMsgContent());
            initialValues.put(EVENT_DATE, business.getMsgDate());
            initialValues.put(EVENT_TIME, business.getMsgTime());
            initialValues.put(EVENT_APP_READ_STATUS, business.getMsgReadStatus());
            initialValues.put(EVENT_DESCRIPTION, business.getMsgdescription());

            db.insert(EVENTS, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<MessageModel> getEvents() {

        String selectQuery = "SELECT * FROM " + EVENTS;
        Log.d("selectQuery", selectQuery);

        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("Status")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("EventTitle")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("EventContent")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("EventDate")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("EventTime")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("AppReadStatus")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("Description")));
                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteEvents()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TEXT_MESSAGES,null,null);
        db.execSQL("delete from "+ EVENTS);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public boolean checkEvents() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + EVENTS;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }



    public void addNoticeBoard(ArrayList<MessageModel> dateslist, Context context) {
        this.Notice = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(NOTICE_STATUS, business.getMsgID());
            initialValues.put(NOTICE_TITLE, business.getMsgTitle());
            initialValues.put(NOTICE_Content, business.getMsgContent());
            initialValues.put(NOTICE_DATE, business.getMsgDate());
            initialValues.put(NOTICE_DAY, business.getMsgTime());
            initialValues.put(NOTICE_APP_READ_STATUS, business.getMsgReadStatus());
            initialValues.put(NOTICE_DESCRIPTION, business.getMsgdescription());




            db.insert(NOTICE_BOARD, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<MessageModel> getNoticeBoard() {

        String selectQuery = "SELECT * FROM " + NOTICE_BOARD;
        Log.d("selectQuery", selectQuery);

        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("Status")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("NoticeBoardTitle")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("NoticeBoardContent")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("Day")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("AppReadStatus")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("Description")));






                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteNoticeBoard()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TEXT_MESSAGES,null,null);
        db.execSQL("delete from "+ NOTICE_BOARD);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public boolean checkNoticeBoard() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + NOTICE_BOARD;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }




    public void addDocuments(ArrayList<MessageModel> dateslist, Context context) {
        this.documents = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(DOCUMENT_MSG_ID, business.getMsgID());
            initialValues.put(DOCUMENT_SUBJECT, business.getMsgTitle());
            initialValues.put(DOCUMENT_URL, business.getMsgContent());
            initialValues.put(DOCUMENT_APPREAD_STATUS, business.getMsgReadStatus());
            initialValues.put(DOCUMENT_DATE, business.getMsgDate());
            initialValues.put(DOCUMENT_TIME, business.getMsgTime());
            initialValues.put(DOCUMENT_DESC, business.getMsgdescription());


            db.insert(DOCUMENTS, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<MessageModel> getDocuments() {

        String selectQuery = "SELECT * FROM " + DOCUMENTS;
        Log.d("selectQuery", selectQuery);

        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("MessageID")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("Subject")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("URL")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("AppReadStatus")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("Time")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("Description")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteDocuments()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TEXT_MESSAGES,null,null);
        db.execSQL("delete from "+ DOCUMENTS);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public boolean checkDocuments() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + DOCUMENTS;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }




    public void addHomeWork(ArrayList<CircularDates> dateslist, Context context) {
        this.Homework = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            CircularDates business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(HOME_WORK_DATE, business.getCircularDate());
            initialValues.put(HOME_WORK_DAY, business.getCircularDay());
            initialValues.put(HOME_WORK_VOICE_COUNT, business.getVoiceTotCount());
            initialValues.put(HOMEWORK_VOICE_UNREAD, business.getVoiceUnreadCount());
            initialValues.put(HOME_WORK_TEXT_COUNT, business.getTextTotCount());
            initialValues.put(HOME_WORK_TEXT_UNREAD, business.getTextUnreadCount());




            db.insert(HOMEWORK, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<CircularDates> getHomeWork() {

        String selectQuery = "SELECT * FROM " + HOMEWORK;
        Log.d("selectQuery", selectQuery);

        ArrayList<CircularDates> business = new ArrayList<CircularDates>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CircularDates business1 = new CircularDates();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setCircularDate(cursor.getString(cursor.getColumnIndex("HomeworkDate")));
                business1.setCircularDay(cursor.getString(cursor.getColumnIndex("HomeworkDay")));
                business1.setVoiceTotCount(cursor.getString(cursor.getColumnIndex("VoiceCount")));
                business1.setVoiceUnreadCount(cursor.getString(cursor.getColumnIndex("voice_unread")));
                business1.setTextTotCount(cursor.getString(cursor.getColumnIndex("TextCount")));
                business1.setTextUnreadCount(cursor.getString(cursor.getColumnIndex("text_unread")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteHomeWork()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TEXT_MESSAGES,null,null);
        db.execSQL("delete from "+ HOMEWORK);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public boolean checkHomeWork() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + HOMEWORK;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }






    public void addDayByDayVoiceList(ArrayList<MessageModel> dateslist, Context context) {
        this.Daybyday_voice_list = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(VOICE_LIST_ID, business.getMsgID());
            initialValues.put(SUBJECT_LIST, business.getMsgTitle());
            initialValues.put(URL_LIST, business.getMsgContent());
            initialValues.put(APPREADSTATUS_LIST, business.getMsgReadStatus());
            initialValues.put(DATE_LIST, business.getMsgDate());
            initialValues.put(TIME_LIST, business.getMsgTime());
            initialValues.put(DESCRIPTION_LIST, business.getMsgdescription());
            db.insert(DAY_BY_DAY_VOICE_LIST, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<MessageModel> getDayByDayVoiceList(String selDate) {

        //String selectQuery = "SELECT * FROM " + DAY_BY_DAY_VOICE_LIST;

        String selectQuery = "select * from " + DAY_BY_DAY_VOICE_LIST + " where "+ "Date" + " = '" + selDate + "'";
        Log.d("selectQuery", selectQuery);

        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("ID")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("Subject")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("URL")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("Time")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("AppReadStatus")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("Description")));


                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteVoiceList(String selDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //db.execSQL("delete from "+ DAY_BY_DAY_VOICE_LIST);

        db.execSQL("delete from  "+DAY_BY_DAY_VOICE_LIST+ " where "+DATE_LIST + "= '" +selDate +"'");

        db.close();
    }



    public boolean checkVoiceList(String selDate) {

        SQLiteDatabase db = this.getWritableDatabase();
       // String selectQuery = "SELECT * FROM " + DAY_BY_DAY_VOICE_LIST;
        String selectQuery = "select * from " + DAY_BY_DAY_VOICE_LIST + " where "+ "Date" + " = '" + selDate + "'";
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }





    public void addDayByDayTextList(ArrayList<MessageModel> dateslist, Context context) {
        this.Daybyday_text_list = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(VOICE_TEXT_LIST_ID, business.getMsgID());
            initialValues.put(SUBJECT_TEXT_LIST, business.getMsgTitle());
            initialValues.put(URL_TEXT_LIST, business.getMsgContent());
            initialValues.put(APPREADSTATUS_TEXT_LIST, business.getMsgReadStatus());
            initialValues.put(DATE_TEXT_LIST, business.getMsgDate());
            initialValues.put(TIME_TEXT_LIST, business.getMsgTime());
            initialValues.put(DESCRIPTION_TEXT_LIST, business.getMsgdescription());

            db.insert(DAY_BY_DAY_TEXT_LIST, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<MessageModel> getDayByDayTextList(String selDate) {

    // String selectQuery = "SELECT * FROM " + DAY_BY_DAY_TEXT_LIST ;

       // String selectQuery = "SELECT * FROM Text_List WHERE Date=" + selDate;

        String selectQuery = "select * from " + DAY_BY_DAY_TEXT_LIST + " where "+ "Date" + " = '" + selDate + "'";

        Log.d("selectQuery", selectQuery);

        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("ID")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("Subject")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("URL")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("Time")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("AppReadStatus")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("Description")));


                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteTextList(String selDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TEXT_MESSAGES,null,null);

    //    db.execSQL("delete from "+ DAY_BY_DAY_TEXT_LIST);

     //   db.execSQL("delete from  "+DAY_BY_DAY_TEXT_LIST+ " where "+"Date" + "= '" +selDate +"'");

        db.execSQL("delete from  "+DAY_BY_DAY_TEXT_LIST+ " where "+DATE_TEXT_LIST + "= '" +selDate +"'");



        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public boolean checkTextList(String selDate) {

        SQLiteDatabase db = this.getWritableDatabase();
       // String selectQuery = "SELECT * FROM " + DAY_BY_DAY_TEXT_LIST;
      //  String selectQuery = "SELECT * FROM Text_List WHERE Date="+ selDate;

        String selectQuery = "select * from " + DAY_BY_DAY_TEXT_LIST + " where "+ "Date" + " = '" + selDate + "'";

        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }



    public void addHomeWorkVoiceList(ArrayList<MessageModel> dateslist, Context context) {
        this.Voice_Homework_list = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(HOME_WORK_VOICE_ID, business.getMsgID());
            initialValues.put(HOME_WORK_VOICE_SUBJECT, business.getMsgTitle());
            initialValues.put(HOMEWORK_VOICE_CONTENT, business.getMsgContent());
            initialValues.put(HOME_WORK_VOICE_APPREADSTATUS, business.getMsgReadStatus());
            initialValues.put(HOME_WORK_VOICE_DATE, business.getMsgDate());
            initialValues.put(HOMEWORK_VOICE_TIME, business.getMsgTime());
            initialValues.put(HOMEWORK_VOICE_TITLE, business.getMsgdescription());

            db.insert(VOICE_HOME_WORK_LIST, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<MessageModel> getHomeWorkVoiceList(String selDate) {


       // String selectQuery = "SELECT * FROM " + VOICE_HOME_WORK_LIST;

        String selectQuery = "select * from " + VOICE_HOME_WORK_LIST + " where "+ "Date" + " = '" + selDate + "'";

        Log.d("selectQuery", selectQuery);

        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("HomeworkID")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("HomeworkSubject")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("HomeworkContent")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("AppReadStatus")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("Time")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("HomeworkTitle")));

                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteHomeWorkVoiceList(String selDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();

      //  db.execSQL("delete from "+ VOICE_HOME_WORK_LIST);

        db.execSQL("delete from  "+VOICE_HOME_WORK_LIST+ " where "+HOME_WORK_VOICE_DATE + "= '" +selDate +"'");


        db.close();
    }

    public boolean checkHomeWorkVoiceList(String selDate) {

        SQLiteDatabase db = this.getWritableDatabase();

    //    String selectQuery = "SELECT * FROM " + VOICE_HOME_WORK_LIST;
        String selectQuery = "select * from " + VOICE_HOME_WORK_LIST + " where "+ "Date" + " = '" + selDate + "'";


        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }




    public void addHomeWorkTextList(ArrayList<MessageModel> dateslist, Context context) {
        this.Voice_Homework_list = dateslist;
        this.context = context;

        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            initialValues.put(HOME_WORK_TEXT_ID, business.getMsgID());
            initialValues.put(HOME_WORK_TEXT_SUBJECT, business.getMsgTitle());
            initialValues.put(HOMEWORK_TEXT_CONTENT, business.getMsgContent());
            initialValues.put(HOME_WORK_TEXT_APPREADSTATUS, business.getMsgReadStatus());
            initialValues.put(HOME_WORK_TEXT_DATE, business.getMsgDate());
            initialValues.put(HOMEWORK_TEXT_TIME, business.getMsgTime());
            initialValues.put(HOMEWORK_TEXT_TITLE, business.getMsgdescription());

            db.insert(TEXT_HOME_WORK_LIST, null, initialValues);
            db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }

    public ArrayList<MessageModel> getHomeWorkTextList(String selDate) {

       // String selectQuery = "SELECT * FROM " + TEXT_HOME_WORK_LIST;



        String selectQuery = "select * from " + TEXT_HOME_WORK_LIST + " where "+ "Date" + " = '" + selDate + "'";


        Log.d("selectQuery_text111", selectQuery);

        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.d("Count_list", String.valueOf(cursor.getCount()));

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();
                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("HomeworkID")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("HomeworkSubject")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("HomeworkContent")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("AppReadStatus")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("Time")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("HomeworkTitle")));


                business.add(business1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;
    }
    public void deleteHomeWorkTextList(String selDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TEXT_MESSAGES,null,null);
     //   db.execSQL("delete from "+ TEXT_HOME_WORK_LIST);

        //db.execSQL("delete from  "+TEXT_HOME_WORK_LIST+ " where "+HOME_WORK_TEXT_DATE + "= '"+selDate +"'");
        db.execSQL("delete from  "+TEXT_HOME_WORK_LIST+ " where "+HOME_WORK_TEXT_DATE + "= '" +selDate +"'");

        db.close();
    }

    public boolean checkHomeWorkTextList(String selDate) {

        SQLiteDatabase db = this.getWritableDatabase();
      //  String selectQuery = "SELECT * FROM " + TEXT_HOME_WORK_LIST;

       // String selectQuery = "select * from " + TEXT_HOME_WORK_LIST + " where "+ "Date" + " = '"+selDate + "'";

        String selectQuery = "select * from " + TEXT_HOME_WORK_LIST + " where "+ "Date" + " = '" + selDate + "'";


        Log.d("query_check", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.d("counting", String.valueOf(cursor.getCount()));

        if (cursor.getCount() > 0) {
            Log.d("count_1", selectQuery);
            return true;
        } else
            return false;
    }





    public void addImages(ArrayList<MessageModel> dateslist, Context context,String ChildID) {
        this.images = dateslist;
        this.context = context;



        Log.d("listsize", String.valueOf(dateslist.size()));
        for (int i = 0; i < dateslist.size(); i++) {
            MessageModel business = dateslist.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues initialValues = new ContentValues();

            Cursor c = db.rawQuery("select * from " + IMAGES+" where ID =?", new String[]{ChildID+business.getMsgID()});
             if(c.getCount()>0)
             {
                 Log.d("Exist", "Values Exist" + "Count= " + String.valueOf(dateslist.size()));

             }
             else {
                 initialValues.put(IMAGE_LIST_ID, ChildID+business.getMsgID());
                 initialValues.put(IMAGE_SUBJECT_LIST, business.getMsgTitle());
                 initialValues.put(IMAGE_URL_LIST, business.getMsgContent());
                 initialValues.put(IMAGE_APPREADSTATUS_LIST, business.getMsgReadStatus());
                 initialValues.put(IMAGE_DATE_LIST, business.getMsgDate());
                 initialValues.put(IMAGE_TIME_LIST, business.getMsgTime());
                 initialValues.put(IMAGE_DESCRIPTION_LIST, business.getMsgdescription());
                 db.insert(IMAGES, null, initialValues);
             }
             db.close();
        }
        Log.d("BUSINESSALL", "Values Inserted" + "Count= " + String.valueOf(dateslist.size()));
    }
    //
//
    public ArrayList<MessageModel> getImages(String childID) {



        String selectQuery = "SELECT * FROM " + IMAGES;
        Log.d("selectQuery", selectQuery);
        ArrayList<MessageModel> business = new ArrayList<MessageModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MessageModel business1 = new MessageModel();

                Log.d("Bussinessdetailscount", String.valueOf(business.size()));
                business1.setMsgID(cursor.getString(cursor.getColumnIndex("ID")));
                business1.setMsgContent(cursor.getString(cursor.getColumnIndex("URL")));
                business1.setMsgTime(cursor.getString(cursor.getColumnIndex("Time")));
                business1.setMsgDate(cursor.getString(cursor.getColumnIndex("Date")));
                business1.setMsgReadStatus(cursor.getString(cursor.getColumnIndex("AppReadStatus")));
                business1.setMsgdescription(cursor.getString(cursor.getColumnIndex("Description")));
                business1.setMsgTitle(cursor.getString(cursor.getColumnIndex("Subject")));
                business.add(business1);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return business;

    }
    //
//
    public boolean checkImages() {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + IMAGES;
        Log.d("query", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public void deleteImages()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //   db.delete(VOICE_MESSAGES,null,null);
        db.execSQL("delete from "+ IMAGES);
        // db.execSQL("TRUNCATE table" + VOICE_MESSAGES);
        db.close();
    }

    public void updateImages(String readStatus,String msgID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("AppReadStatus",readStatus);
       // db.update(IMAGES, initialValues, "_id = ?", new String[]{msgID});
        db.update(IMAGES, initialValues, "ID=" + msgID, null);
        db.close();
    }
}

