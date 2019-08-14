package com.iedayan03.sportsupport.CustomAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iedayan03.sportsupport.Classes.User;
import com.iedayan03.sportsupport.R;

import java.util.ArrayList;

public class TeamAdapter extends ArrayAdapter<User> {

    private ArrayList<User> players;
    private Context mContext;
    private int mResource;

    public TeamAdapter(Context context, int resource, ArrayList<User> users) {
        super(context, resource, users);
        this.mContext = context;
        this.players = users;
        this.mResource = resource;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View userItem = convertView;
        if (userItem == null) {
            userItem = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        User selectedUser = players.get(position);

        TextView userNameView = userItem.findViewById(R.id.userNameId);
        userNameView.setText(selectedUser.getUsername());

        TextView userPosition = userItem.findViewById(R.id.userPosId);
        if (selectedUser.getPassword() != null) {
            userPosition.setText(selectedUser.getPosition());
        } else {
            userPosition.setText("Unavailable");
        }

        TextView userRatingView = userItem.findViewById(R.id.userRatingId);
        userRatingView.setText(selectedUser.getRating().toString());

        return userItem;
    }
}
