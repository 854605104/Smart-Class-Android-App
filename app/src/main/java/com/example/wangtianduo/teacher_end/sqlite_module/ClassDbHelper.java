package com.example.wangtianduo.teacher_end.sqlite_module;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.wangtianduo.teacher_end.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;


public class ClassDbHelper extends SQLiteOpenHelper {


    private final Context context;
    private static String PACKAGE_NAME;
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDatabase readableDb;
    private SQLiteDatabase writeableDb;

    private static ClassDbHelper classDbHelper;
    private static String TAG = "ss";


    // Singleton Pattern
    private ClassDbHelper(Context context){
        super(context, ClassContract.ClassEntry.TABLE_NAME, null, DATABASE_VERSION );
        this.context = context;
    }

    public static ClassDbHelper createClassDbHelper(Context context) {
        // context object of a particular activity is passed to it
        // so we get the context object of the entire app below
        if (classDbHelper == null) {
            classDbHelper = new ClassDbHelper(context.getApplicationContext());
        }
        return classDbHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("ASSS", "Database OnCreate");
        sqLiteDatabase.execSQL(ClassContract.ClassSql.SQL_CREATE_TABLE);
        fillTable(sqLiteDatabase);
        Log.i(TAG, "onCreate is activated");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(ClassContract.ClassSql.SQL_DROP_TABLE);
        onCreate(sqLiteDatabase);
        Log.i(TAG, "onUgrade is activated");
    }


    private void fillTable(SQLiteDatabase sqLiteDatabase){

        ArrayList<ClassData> arrayList = new ArrayList<>();
        PACKAGE_NAME = context.getPackageName();

        InputStream inputStream = context.getResources().openRawResource(R.raw.courses);
        String string = Utils.convertStreamToString(inputStream);

        Log.i("ASSS", string);

        //parse the Json file and store data in the ArrayList using the ClassData class
        Log.i("ASDF", "ssssdddddd");
        try{
            JSONArray jsonArray = new JSONArray(string);
            for(int i = 0; i <= jsonArray.length(); i++){
                String name = jsonArray.getJSONObject(i).getString("name");
                String session = jsonArray.getJSONObject(i).getString("session");
                String date = jsonArray.getJSONObject(i).getString("date");
                String timing = jsonArray.getJSONObject(i).getString("timing");
                String venue = jsonArray.getJSONObject(i).getString("venue");
                String studentNumber = jsonArray.getJSONObject(i).getString("studentNumber");
                String studentStatus = jsonArray.getJSONObject(i).getString("studentStatus");

                arrayList.add(new ClassDbHelper.ClassData(name, session, date, timing, venue,studentNumber,studentStatus));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        //Each entry in the arrayList is stored as a ContentValues object
        //Then this ContentValues object is inserted to the sqLiteDatabase to create a new row
        for(int i = 0; i< arrayList.size(); i++){
            Log.i("Norman","" + arrayList.get(i).getSession());
            ContentValues cv = new ContentValues();

            cv.put(ClassContract.ClassEntry.COL_NAME, arrayList.get(i).getName());
            cv.put(ClassContract.ClassEntry.COL_SESSION, arrayList.get(i).getSession());
            cv.put(ClassContract.ClassEntry.COL_DATE, arrayList.get(i).getDate());
            cv.put(ClassContract.ClassEntry.COL_TIMING, arrayList.get(i).getTiming());
            cv.put(ClassContract.ClassEntry.COL_VENUE, arrayList.get(i).getVenue());
            cv.put(ClassContract.ClassEntry.COL_NUMBER, arrayList.get(i).getNumber());
            cv.put(ClassContract.ClassEntry.COL_STATUS, arrayList.get(i).getStatus());

            sqLiteDatabase.insert(ClassContract.ClassEntry.TABLE_NAME,null,cv);
        }

        Cursor cursor = sqLiteDatabase.rawQuery(ClassContract.ClassSql.SQL_QUERY_ALL_ROWS, null);
        Log.i("Norman","Table Filled. Rows = " + cursor.getCount());


    }


    public ClassData queryOneRowRandom(){

        if (readableDb == null) {
            readableDb = getReadableDatabase();
        }

        Log.i("ASDF", "RANDOM");
        Cursor cursor = readableDb.rawQuery(
                ClassContract.ClassSql.SQL_QUERY_ONE_RANDOM_ROW, null);
        return getDataFromCursor(0, cursor);

    }


    public ClassData queryOneRow(int position){

        if (readableDb == null) {
            readableDb = getReadableDatabase();
        }

        Cursor cursor = readableDb.rawQuery(ClassContract.ClassSql.SQL_QUERY_ALL_ROWS, null);

        return getDataFromCursor(position, cursor);

    }


    private ClassData getDataFromCursor(int position, Cursor cursor){

        String name = null;
        String session = null;
        String date = null;
        String timing = null;
        String venue = null;
        String number = null;
        String status = null;

        cursor.moveToPosition(position);
        // extract the name column
        int nameIndex = cursor.getColumnIndex(ClassContract.ClassEntry.COL_NAME);
        name = cursor.getString(nameIndex);

        int sessionIndex = cursor.getColumnIndex(ClassContract.ClassEntry.COL_SESSION);
        session = cursor.getString(sessionIndex);

        int dateIndex = cursor.getColumnIndex(ClassContract.ClassEntry.COL_DATE);
        date = cursor.getString(dateIndex);

        int timingIndex = cursor.getColumnIndex(ClassContract.ClassEntry.COL_TIMING);
        timing = cursor.getString(timingIndex);

        int venueIndex = cursor.getColumnIndex(ClassContract.ClassEntry.COL_VENUE);
        venue = cursor.getString(venueIndex);

        int numberIndex = cursor.getColumnIndex(ClassContract.ClassEntry.COL_NUMBER);
        number = cursor.getString(numberIndex);

        int statusIndex = cursor.getColumnIndex(ClassContract.ClassEntry.COL_STATUS);
        status = cursor.getString(statusIndex);


        return new ClassData(name, session, date, timing, venue, number, status);
    }


    public void insertOneRow(ClassData ClassData){
        if (writeableDb == null) {
            writeableDb = getWritableDatabase();
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(ClassContract.ClassEntry.COL_NAME,
                ClassData.getName());

        contentValues.put(ClassContract.ClassEntry.COL_SESSION,
                ClassData.getSession());

        contentValues.put(ClassContract.ClassEntry.COL_DATE,
                ClassData.getDate());

        contentValues.put(ClassContract.ClassEntry.COL_TIMING,
                ClassData.getTiming());

        contentValues.put(ClassContract.ClassEntry.COL_VENUE,
                ClassData.getVenue());


        long row = writeableDb.insert(ClassContract.ClassEntry.TABLE_NAME, null, contentValues);
        Log.i("Logcat", "insertOneRow: row = " + row);
    }



    public int deleteOneRow(String name){
        if (writeableDb == null) {
            writeableDb = getWritableDatabase();
        }

        String WHERE_CLAUSE = ClassContract.ClassEntry.COL_NAME + " = ?";
        String[] WHERE_ARGS = {name};
        int rowsDeleted = writeableDb.delete(
                ClassContract.ClassEntry.TABLE_NAME,
                WHERE_CLAUSE,
                WHERE_ARGS
        );

        return rowsDeleted;
    }


    public long queryNumRows(){

        if (readableDb == null) {
            readableDb = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(readableDb, ClassContract.ClassEntry.TABLE_NAME);
    }

    public Context getContext(){
        return context;
    }



    public static class ClassData{

        private String name;
        private String session;
        private String date;
        private String timing;
        private String venue;
        private String number;
        private String status;

        public ClassData(String name, String session, String date,
                         String timing, String venue,String number, String status) {
            this.name = name;
            this.session = session;
            this.date = date;
            this.timing = timing;
            this.venue = venue;
            this.number = number;
            this.status = status;
        }

        public String getSession() { return session; }

        public String getName() {
            return name;
        }

        public String getTiming() { return timing; }

        public String getDate() { return date; }

        public String getVenue() { return venue; }

        public String getNumber() { return number;}

        public String getStatus() {return status;}
    }

}
