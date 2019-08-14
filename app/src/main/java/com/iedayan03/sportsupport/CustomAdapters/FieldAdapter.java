package com.iedayan03.sportsupport.CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iedayan03.sportsupport.Classes.Field;
import com.iedayan03.sportsupport.R;

import java.util.ArrayList;

public class FieldAdapter extends ArrayAdapter<Field> {

    // The activity where the adapter was created
    private Context context;

    // The ArrayList that will contain the fields
    private ArrayList<Field> fields;

    // This is the ID of the layout resource that getView() would inflate to create the view
    private int mResource;

    /**
     * Simple FieldAdapter Constructor
     *
     * @param context The activity where the adapter was created
     * @param fields The ArrayList that will contain the fields
     */
    public FieldAdapter(@NonNull Context context, int resource, ArrayList<Field> fields) {
        super(context, resource, fields);
        this.context = context;
        this.fields = fields;
        this.mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View fieldItem = convertView;
        if (fieldItem == null) {
            fieldItem = LayoutInflater.from(context).inflate(mResource, parent, false);
        }

        Field currField = fields.get(position);

        TextView fieldName = fieldItem.findViewById(R.id.fieldNameId);
        fieldName.setText(currField.getFieldName());

        TextView fieldRating = fieldItem.findViewById(R.id.fieldRatingId);
        fieldRating.setText(String.format("Rating: %.2f", currField.getRating()));

        return fieldItem;
    }
}
