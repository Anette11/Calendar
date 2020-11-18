package com.example.calendartest;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends ArrayAdapter<Date> {
    private final LayoutInflater layoutInflater;
    private final List<Date> listOfDatesInMonth;
    private final List<Event> listOfEvents;

    public CalendarAdapter(Context context, List<Date> listOfDatesInMonth, List<Event> listOfEvents) {
        super(context, R.layout.single_cell_layout);
        this.listOfDatesInMonth = listOfDatesInMonth;
        this.listOfEvents = listOfEvents;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.single_cell_layout, parent, false);
        }

        TextView textViewDaySingleCell = view.findViewById(R.id.calendar_date_id);
        LinearLayout linearLayout = view.findViewById(R.id.event_color);

        Calendar calendar = Calendar.getInstance();
        Date date = getItem(position);
        assert date != null;
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        Calendar calendarToday = Calendar.getInstance();
        Date dateToday = new Date();
        calendarToday.setTime(dateToday);
        int dayToday = calendarToday.get(Calendar.DAY_OF_MONTH);
        int monthToday = calendarToday.get(Calendar.MONTH) + 1;
        int yearToday = calendarToday.get(Calendar.YEAR);

        MyDataBaseOpenHelper myDataBaseOpenHelper = new MyDataBaseOpenHelper(getContext());
        Cursor cursor = myDataBaseOpenHelper.getAllEvents();

        while (cursor.moveToNext()) {
            String eventDate = cursor.getString(3);
            String dayCheck = String.format(Locale.ENGLISH, "%02d", day);
            String monthCheck = String.format(Locale.ENGLISH, "%02d", month);

            if (eventDate.equals(dayCheck + "/" + monthCheck + "/" + year)) {
                linearLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.linear_layout_background_3));
            }
        }

        textViewDaySingleCell.setTextColor(getContext().getResources().getColor(R.color.color2));

        if (month != monthToday || year != yearToday) {
            textViewDaySingleCell.setTextColor(getContext().getResources().getColor(R.color.color3));
        }

        if (day == dayToday && month == monthToday && year == yearToday) {
            textViewDaySingleCell.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round_background));
        }

        textViewDaySingleCell.setText(String.valueOf(day));
        return view;
    }

    @Override
    public int getCount() {
        return listOfDatesInMonth.size();
    }

    @Nullable
    @Override
    public Date getItem(int position) {
        return listOfDatesInMonth.get(position);
    }

    @Override
    public int getPosition(Date item) {
        return listOfDatesInMonth.indexOf(item);
    }
}