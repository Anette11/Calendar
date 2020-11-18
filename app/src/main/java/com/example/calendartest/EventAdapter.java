package com.example.calendartest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterHolder> {
    private List<Event> eventList;
    private LayoutInflater layoutInflater;
    private MyOnItemClickListenerInterface myOnItemClickListenerInterface;

    public interface MyOnItemClickListenerInterface {
        void myOnItemClickListenerInterfaceMethod(int position);
    }

    public void setMyOnItemClickListenerInterface(MyOnItemClickListenerInterface myOnItemClickListenerInterface) {
        this.myOnItemClickListenerInterface = myOnItemClickListenerInterface;
    }

    public Event getItemAt(int position) {
        return eventList.get(position);
    }

    public EventAdapter(Context context, List<Event> eventList) {
        this.eventList = eventList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EventAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.linear_layout_one_event, parent, false);
        return new EventAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapterHolder holder, int position) {
        final Event event = eventList.get(position);
        holder.textViewEventDate.setText(event.getEventDate());
        holder.textViewEventTitle.setText(event.getEventTitle());
        holder.textViewEventDescription.setText(event.getEventDescription());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventAdapterHolder extends RecyclerView.ViewHolder {
        private final TextView textViewEventDate;
        private final TextView textViewEventTitle;
        private final TextView textViewEventDescription;
        private final LinearLayout linearLayoutOneEvent;

        public EventAdapterHolder(@NonNull final View itemView) {
            super(itemView);
            textViewEventDate = itemView.findViewById(R.id.text_view_event_date);
            textViewEventTitle = itemView.findViewById(R.id.text_view_event_title);
            textViewEventDescription = itemView.findViewById(R.id.text_view_event_description);
            linearLayoutOneEvent = itemView.findViewById(R.id.linear_layout_one_event);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (myOnItemClickListenerInterface != null && position != RecyclerView.NO_POSITION) {
                        myOnItemClickListenerInterface.myOnItemClickListenerInterfaceMethod(position);
                    }
                }
            });
        }
    }
}
