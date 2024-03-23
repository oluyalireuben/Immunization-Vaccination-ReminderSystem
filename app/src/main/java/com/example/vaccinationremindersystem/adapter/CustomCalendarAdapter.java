package com.example.vaccinationremindersystem.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vaccinationremindersystem.model.Date;

import java.util.ArrayList;
import java.util.List;

public class CustomCalendarAdapter extends ArrayAdapter<java.util.Date> {
    private final ArrayList<Date> markedDates;

    public CustomCalendarAdapter(@NonNull Context context, int resource, @NonNull ArrayList<java.util.Date> dates) {
        super(context, resource, dates);
        markedDates = new ArrayList<>();
    }


    private void setMarkedDates(ArrayList<Date> dates) {
        markedDates.clear();
        markedDates.addAll(dates);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        java.util.Date date = getItem(position);
        if (markedDates.contains(date)) {
            view.setBackgroundColor(Color.GREEN);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }
}
