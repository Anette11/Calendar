package com.example.calendartest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarCustomView extends LinearLayout {
    private ImageView imageViewBack;
    private ImageView imageViewForward;
    private TextView textViewCurrentDate;
    private GridView gridView;
    private SimpleDateFormat simpleDateFormat;
    private Calendar calendar;
    private CalendarAdapter calendarAdapter;
    private LinearLayout linearLayout;
    private static final int DAYS_COUNT = 42;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private EditText editTextTitleEvent;
    private EditText editTextDescriptionEvent;
    private TextView textViewTitleAlertDialog;
    private List<Date> listOfDatesInMonth;
    private List<Event> listOfEvents;
    private String textTitleItIsEventDate;
    private MyDataBaseOpenHelper myDataBaseOpenHelper;
    private String eventDate;
    private int eventDateInt;
    private Context context;
    private final String blockChar = "'";
    private InputFilter filter;

    public CalendarCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
        setCalendarAdapter();
        imageViewBackSetOnClickListener();
        imageViewForwardSetOnClickListener();
        gridViewSetOnItemClickListener();
    }

    private void setFilter() {
        filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && blockChar.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };
    }

    private void initialize() {
        context = getContext();
        LayoutInflater layoutInflater
                = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        layoutInflater.inflate(R.layout.calendar_layout, this);
        myFindViewById();
        calendar = Calendar.getInstance(Locale.ENGLISH);
        simpleDateFormat
                = new SimpleDateFormat(getContext().getString(R.string.mmmm_yyyy), Locale.ENGLISH);
        myDataBaseOpenHelper = new MyDataBaseOpenHelper(getContext());
        setFilter();
    }

    private void myFindViewById() {
        linearLayout = findViewById(R.id.linear_layout_calendar_custom_activity);
        imageViewBack = findViewById(R.id.image_view_back);
        imageViewForward = findViewById(R.id.image_view_forward);
        textViewCurrentDate = findViewById(R.id.text_view_current_date);
        gridView = findViewById(R.id.grid_view);
    }

    private void imageViewBackSetOnClickListener() {
        imageViewBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                setCalendarAdapter();
            }
        });
    }

    private void imageViewForwardSetOnClickListener() {
        imageViewForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                setCalendarAdapter();
            }
        });
    }

    private void gridViewSetOnItemClickListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setAlertDialog(position);
            }
        });
    }

    private void setAlertDialog(int position) {
        alertDialogBuilderFindViewByIdAndCreate();
        getCalendarDate(position);
        textViewTitleAlertDialog.setText(textTitleItIsEventDate);
        alertDialogSetOnShowListener();
        alertDialog.show();
    }

    private void getCalendarDate(int position) {
        Calendar calendar = Calendar.getInstance();
        Date date = listOfDatesInMonth.get(position);
        assert date != null;
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        String addEvent = getContext().getString(R.string.add_event_on);
        eventDate = String.format(Locale.ENGLISH, "%02d/%02d/", day, month) + year;
        eventDateInt = Integer.parseInt(year + String.format(Locale.ENGLISH, "%02d%02d", month, day));
        textTitleItIsEventDate = addEvent + eventDate;
    }

    private void alertDialogBuilderFindViewByIdAndCreate() {
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        Context context = getContext();
        LayoutInflater inflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view1 = inflater.inflate(R.layout.alert_dialog_layout_add_event, null);
        editTextTitleEvent = view1.findViewById(R.id.editTextTitleEvent);
        editTextTitleEvent.setFilters(new InputFilter[]{filter});
        editTextDescriptionEvent = view1.findViewById(R.id.editTextDescription);
        editTextDescriptionEvent.setFilters(new InputFilter[]{filter});
        textViewTitleAlertDialog = view1.findViewById(R.id.textViewTitleAlertDialog);
        setPositiveButtonAlertDialog();
        setNegativeButtonAlertDialog();
        alertDialogBuilder.setView(view1);
        alertDialog = alertDialogBuilder.create();
    }

    private void alertDialogSetOnShowListener() {
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(18);
                alertDialogButtonPositiveSetOnClickListener();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(18);
                alertDialogButtonNegativeSetOnClickListener();
            }
        });
    }

    private void alertDialogButtonNegativeSetOnClickListener() {
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void alertDialogButtonPositiveSetOnClickListener() {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextTitleEvent.getText().toString().trim().isEmpty()
                        || !editTextDescriptionEvent.getText().toString().trim().isEmpty()) {
                    addEventToDataBase();
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    toastShow(getContext().getString(R.string.saved));
                    alertDialog.dismiss();
                } else {
                    toastShow(getContext().getString(R.string.fill_title_or_description));
                }
            }
        });
    }

    private void addEventToDataBase() {
        Event event = new Event();
        event.setEventDateInt(eventDateInt);
        event.setEventDate(eventDate);
        event.setEventTitle(editTextTitleEvent.getText().toString());
        event.setEventDescription(editTextDescriptionEvent.getText().toString());
        addEvent(event);
    }

    private void setPositiveButtonAlertDialog() {
        alertDialogBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    private void setNegativeButtonAlertDialog() {
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    private void setCalendarAdapter() {
        listOfDatesInMonth = new ArrayList<>();
        listOfEvents = new ArrayList<>();

        Calendar calendar = (Calendar) this.calendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = calendar.get(Calendar.DAY_OF_WEEK) + 5;
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);

        while (listOfDatesInMonth.size() < DAYS_COUNT) {
            listOfDatesInMonth.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        String sDate = simpleDateFormat.format(this.calendar.getTime());
        textViewCurrentDate.setText(sDate);
        calendarAdapter = new CalendarAdapter(getContext(), listOfDatesInMonth, listOfEvents);
        gridView.setAdapter(calendarAdapter);
    }

    private void addEvent(Event event) {
        new AddEventAsyncTask(myDataBaseOpenHelper).execute(event);
    }

    private static class AddEventAsyncTask extends AsyncTask<Event, Void, Void> {
        private final MyDataBaseOpenHelper myDataBaseOpenHelper;

        public AddEventAsyncTask(MyDataBaseOpenHelper myDataBaseOpenHelper) {
            this.myDataBaseOpenHelper = myDataBaseOpenHelper;
        }

        @Override
        protected Void doInBackground(Event... events) {
            myDataBaseOpenHelper.addEvent(events[0]);
            return null;
        }
    }

    private void toastShow(String toastMessage) {
        LayoutInflater layoutInflater
                = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View layout = layoutInflater
                .inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_linear_layout));
        TextView text = layout.findViewById(R.id.toast_message);
        text.setText(toastMessage);
        Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
