package com.iedayan03.sportsupport.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iedayan03.sportsupport.Classes.Field;
import com.iedayan03.sportsupport.Classes.User;
import com.iedayan03.sportsupport.CustomAdapters.FieldAdapter;
import com.iedayan03.sportsupport.FieldActivity;
import com.iedayan03.sportsupport.R;
import com.iedayan03.sportsupport.SessionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple fragment that displays a list of soccer fields.
 */
public class HomeFragment extends Fragment implements LocationListener {

    private int GPS_PERMISSION_CODE = 1;
    private static final String CURR_FIELD = "CURR_FIELD";

    private ListView fieldListView; // We could maybe implement a RecyclerView. Should look into it if we have time.
    private ArrayList<Field> fieldArray;
    private RequestQueue mQueue;
    private String fetchFieldsUrl = "http://iedayan03.web.illinois.edu/fetch_fields.php";

    private SessionHandler session;
    private User user;

    // Since we are in a fragment, we need to get the context of the Activity that launched this fragment.
    private Context mContext;
    private LocationManager locationManager;

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context Context of the activity that launched this fragment.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        session = new SessionHandler(getContext());
        user = session.getUserDetails();

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        /*
         * First checks if the GPS permission is already granted: if it is then set location of the user;
         * if not then calls 'requestGPSPermission()'.
         */
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            if (locationManager != null) {
                Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) {
                    user.setLongitude(loc.getLongitude());
                    user.setLatitude(loc.getLatitude());
                }
            }
        } else {
            requestGPSPermission();
        }
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

        /*
         * A listener that will direct the user to the activity "FieldActivity" when a soccer field
         * is clicked. This is incomplete as of now.
         */
        fieldListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // change this so that the Field object is sent to the new Intent!
                Field fieldItem = (Field) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), FieldActivity.class);
                intent.putExtra(CURR_FIELD, fieldItem);
                mQueue.stop();
                startActivity(intent);
            }
        });
        return view;
    }

    /**
     * Displays a dialog box that asks the user to grant permission for GPS.
     */
    private void requestGPSPermission() {
        // stack overflow said to use requireActivity() instead of getActivity() as getActivity() might return null
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {

            // This Dialog explains to the user why this permission is needed
            new AlertDialog.Builder(mContext)
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed to find the nearest soccer fields")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            // Request permission if the user presses "Ok".
                            // Note this is the same as in the else statement down below.
                            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GPS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method makes a GET request to our database using the Volley library and stores the response into fieldArray.
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
     * This function set's the custom adapter 'FieldAdapter' to our field list view.
     */
    private void displayFields() {
        if (getActivity() != null) {
            FieldAdapter fieldAdapter = new FieldAdapter(getActivity(), R.layout.field_list, fieldArray);
            fieldListView.setAdapter(fieldAdapter);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        user.setLongitude(location.getLongitude());
        user.setLatitude(location.getLatitude());
    }

    /**
     * @param s
     * @param i
     * @param bundle
     * @deprecated
     */
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
