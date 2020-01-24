package com.miltolstoy.roundcalendar;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.miltolstoy.roundcalendar.MainActivity.TAG;

class CalendarAdapter {

    private Activity context;

    CalendarAdapter(Activity context) {
        this.context = context;
    }

    void requestCalendarPermissionsIfNeeded() {
        Log.d(TAG, "Checking READ_CALENDAR permission");
        int permission_status = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR);
        if (permission_status == PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "READ_CALENDAR permission granted");
            return;
        }

        Log.d(TAG, "Requesting READ_CALENDAR permission");
        int request_status = 0;
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_CALENDAR},
                request_status);
        Log.d(TAG, "Permission request status: " + request_status);
    }

    List<Event> getTodayEvents() {
        Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
        long dayStart = getCurrentDayStart();
        ContentUris.appendId(builder, dayStart);
        ContentUris.appendId(builder, dayStart + DateUtils.DAY_IN_MILLIS);

        Cursor cursor = context.getContentResolver().query(
                builder.build(),
                new String[] { "title", "dtstart", "dtend"},
                null,null, null);

        List<Event> events = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            Log.w(TAG, "No events for today");
            return events;
        }

        Log.d(TAG, "Today events:");
        do {
            Event event = new Event(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            Log.d(TAG, String.format("%s: %s %s-%s", event.getTitle(), event.getStartDate(), event.getStartTime(), event.getFinishTime()));
            events.add(event);
        } while (cursor.moveToNext());
        Log.d(TAG, "Total: " + events.size());

        cursor.close();
        return events;
    }

    private long getCurrentDayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);

        return calendar.getTime().getTime();
    }

}