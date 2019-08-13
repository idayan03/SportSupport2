package com.iedayan03.sportsupport.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iedayan03.sportsupport.Classes.Field;
import com.iedayan03.sportsupport.CustomAdapters.FieldAdapter;
import com.iedayan03.sportsupport.FieldActivity;
import com.iedayan03.sportsupport.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple fragment that displays a list of soccer fields.
 */
public class HomeFragment extends Fragment {

    private static final String FIELD_NAME = "Field Name";
    public static final String FIELD_ADDRESS = "Field Address";
    private static final String FIELD_PLACE_ID = "Field PlaceId";

    ListView fieldListView; // We could maybe implement a RecyclerView. Should look into it if we have time.
    ArrayList<Field> fieldArray;
    private RequestQueue mQueue;
    private String fetchFieldsUrl = "http://iedayan03.web.illinois.edu/fetch_fields.php";

    private Double latitude; // could not finish location on time :/
    private Double longitude;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Soccer fields near you");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fieldListView = view.findViewById(R.id.fieldListView);
        fieldArray = new ArrayList<>();

        mQueue = Volley.newRequestQueue(getActivity());
        loadFields();
        /**
         * A listener that will direct the user to the activity "FieldActivity" when a soccer field
         * is clicked. This is incomplete as of now.
         */
        fieldListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Make sure to send more data/information about the soccer field. I have not yet retrieved
                // other information about the soccer field from the database.
                Field fieldItem = (Field) adapterView.getItemAtPosition(position);
                String itemName = fieldItem.getFieldName();
                String itemAddress = fieldItem.getAddress();
                String fieldPlaceId = fieldItem.getPlaceId();

                Intent intent = new Intent(getContext(), FieldActivity.class);
                intent.putExtra(FIELD_NAME, itemName);
                intent.putExtra(FIELD_ADDRESS, itemAddress);
                intent.putExtra(FIELD_PLACE_ID, fieldPlaceId);
                mQueue.stop();
                startActivity(intent);
            }
        });
        return view;
    }

    /**
     * This method makes a GET request to our database using the Volley library and stores the response into fieldArray
     */
    private void loadFields() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fetchFieldsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject result = jsonArray.getJSONObject(i);
                        String fieldName = result.getString("place_name").trim();
                        String fieldAddress = result.getString("address");
                        String fieldPlaceId = result.getString("place_id");
                        String latitude = result.getString("latitude");
                        String longitude = result.getString("longitude");
                        String str_rating = result.getString("rating");

                        Double rating;
                        if (!str_rating.equals("null")) rating = Double.valueOf(str_rating);
                        else rating = 0.0; // rating doesn't exist so I chose 0.0 as default

                        Field field = new Field(fieldPlaceId, fieldName, fieldAddress, longitude, latitude, rating);
                        fieldArray.add(field);
                    }
                    displayFields();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    /**
     *
     */
    private void displayFields() {
        if (getActivity() != null) {
            FieldAdapter fieldAdapter = new FieldAdapter(getActivity(), R.layout.field_list, fieldArray);
            fieldListView.setAdapter(fieldAdapter);
        }
    }
}
