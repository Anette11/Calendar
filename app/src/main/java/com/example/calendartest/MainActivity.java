package com.example.calendartest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private CalendarCustomView calendarCustomView;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private MyDataBaseOpenHelper myDataBaseOpenHelper;
    private List<Event> eventList;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog.Builder alertDialogBuilder1;
    private AlertDialog alertDialog;
    private AlertDialog alertDialog1;
    private TextView textViewTitleAlertDialogEditOrDelete;
    private EditText editTextTitleEventEditOrDelete;
    private EditText editTextDescriptionEventEditOrDelete;
    private ImageView imageViewDeleteAllEvents;
    private String eventDate;
    private int eventDateInt;
    private String oldTitle;
    private String newTitle;
    private String oldDescription;
    private String newDescription;
    private Context context;
    private int eventId;
    private final String blockChar = "'";
    private InputFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_calendar);
        initialize();
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
        context = MainActivity.this;
        calendarCustomView = findViewById(R.id.calendar_custom_view);
        eventList = new ArrayList<>();
        addEventsInRecyclerViewFromDataBase();
        initRecyclerView();
        imageViewDeleteAllEvents = findViewById(R.id.image_view_delete_all_events);
        imageViewDeleteAllEventsSetOnClickListener();
        setFilter();
    }

    private void imageViewDeleteAllEventsSetOnClickListener() {
        imageViewDeleteAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBuilder1 = new AlertDialog.Builder(context);
                LayoutInflater inflater
                        = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                View view1 = inflater.inflate(R.layout.alert_dialog_layout_delete_all_events, null);
                alertDialogBuilder1SetPositiveButton();
                alertDialogBuilder1SetNegativeButton();
                alertDialogBuilder1.setView(view1);
                alertDialog1 = alertDialogBuilder1.create();
                alertDialog1SetOnShowListener();
                alertDialog1.show();
                alertDialog1ButtonPositiveSetOnClickListener();
                alertDialog1ButtonNegativeSetOnClickListener();
            }
        });
    }

    private void alertDialogBuilder1SetPositiveButton() {
        alertDialogBuilder1.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    private void alertDialogBuilder1SetNegativeButton() {
        alertDialogBuilder1.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    private void alertDialog1SetOnShowListener() {
        alertDialog1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog1.getButton(DialogInterface.BUTTON_POSITIVE).setTextSize(18);
                alertDialog1.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(18);
            }
        });
    }

    private void alertDialog1ButtonPositiveSetOnClickListener() {
        alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDataBaseOpenHelper.deleteAddEvents();
                if (myDataBaseOpenHelper.isExists()) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    toastShow(getString(R.string.all_events_deleted));
                } else {
                    toastShow(getString(R.string.list_of_events_is_empty));
                }
                alertDialog1.dismiss();
            }
        });
    }

    private void alertDialog1ButtonNegativeSetOnClickListener() {
        alertDialog1.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
    }

    private void addEventsInRecyclerViewFromDataBase() {
        myDataBaseOpenHelper = new MyDataBaseOpenHelper(this);
        Cursor cursor = myDataBaseOpenHelper.getAllEvents();
        while (cursor.moveToNext()) {
            eventList.add(new Event(cursor.getString(3),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(4)));
        }
        cursor.close();
    }

    private void initRecyclerView() {
        eventAdapter = new EventAdapter(context, eventList);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        eventAdapterSetMyOnItemClickListenerInterface();
        recyclerView.setAdapter(eventAdapter);
        Objects.requireNonNull(recyclerView.getLayoutManager())
                .smoothScrollToPosition(recyclerView, new RecyclerView.State(),
                        Objects.requireNonNull(recyclerView.getAdapter()).getItemCount());
    }

    private void eventAdapterSetMyOnItemClickListenerInterface() {
        eventAdapter.setMyOnItemClickListenerInterface(new EventAdapter.MyOnItemClickListenerInterface() {
            @Override
            public void myOnItemClickListenerInterfaceMethod(int position) {
                alertDialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater
                        = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                View view1 = inflater.inflate(R.layout.alert_dialog_layout_edit_or_delete_event, null);
                textViewTitleAlertDialogEditOrDelete = view1.findViewById(R.id.textViewTitleAlertDialogEditOrDelete);
                editTextTitleEventEditOrDelete = view1.findViewById(R.id.editTextTitleEventEditOrDelete);
                editTextTitleEventEditOrDelete.setFilters(new InputFilter[]{filter});
                editTextDescriptionEventEditOrDelete = view1.findViewById(R.id.editTextDescriptionEditOrDelete);
                editTextDescriptionEventEditOrDelete.setFilters(new InputFilter[]{filter});
                setPositiveButtonAlertDialog();
                setNegativeButtonAlertDialog();
                setNeutralButtonAlertDialog();
                eventDate = eventAdapter.getItemAt(position).getEventDate();
                textViewTitleAlertDialogEditOrDelete.setText((getString(R.string.edit_event_on)).concat(eventDate));
                eventDateInt = eventAdapter.getItemAt(position).getEventDateInt();
                oldTitle = eventAdapter.getItemAt(position).getEventTitle();
                editTextTitleEventEditOrDelete.setText(oldTitle);
                oldDescription = eventAdapter.getItemAt(position).getEventDescription();
                eventId = myDataBaseOpenHelper.getEventId(oldTitle, oldDescription, eventDate, eventDateInt);
                editTextDescriptionEventEditOrDelete.setText(oldDescription);
                alertDialogBuilder.setView(view1);
                alertDialog = alertDialogBuilder.create();
                alertDialogSetOnShowListener();
                alertDialog.show();
            }
        });
    }

    private void alertDialogSetOnShowListener() {
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(18);
                alertDialogButtonPositiveSetOnClickListener();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(18);
                alertDialogButtonNegativeSetOnClickListener();
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(18);
                alertDialogButtonNeutralSetOnClickListener();
            }
        });
    }

    private void alertDialogButtonNeutralSetOnClickListener() {
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDataBaseOpenHelper.deleteEvent(oldTitle, eventId);
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                toastShow(getString(R.string.deleted));
                alertDialog.dismiss();
            }
        });
    }

    private void alertDialogButtonNegativeSetOnClickListener() {
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void alertDialogButtonPositiveSetOnClickListener() {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextTitleEventEditOrDelete.getText().toString().trim().isEmpty()
                        || !editTextDescriptionEventEditOrDelete.getText().toString().trim().isEmpty()) {
                    newTitle = editTextTitleEventEditOrDelete.getText().toString();
                    newDescription = editTextDescriptionEventEditOrDelete.getText().toString();
                    myDataBaseOpenHelper.updateEvent
                            (eventDate, newTitle, newDescription, eventId, eventDateInt);
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    toastShow(getString(R.string.updated));
                    alertDialog.dismiss();
                } else {
                    toastShow(getString(R.string.fill_title_or_description));
                }
            }
        });
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

    private void setNeutralButtonAlertDialog() {
        alertDialogBuilder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    private void toastShow(String toastMessage) {
        LayoutInflater layoutInflater
                = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View layout = layoutInflater
                .inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_linear_layout));
        TextView text = layout.findViewById(R.id.toast_message);
        text.setText(toastMessage);
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}