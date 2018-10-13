package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import team7.seshealthpatient.Fragments.ChatMessage;
import team7.seshealthpatient.MapModels.PlaceResult;
import team7.seshealthpatient.PacketInfo;
import team7.seshealthpatient.R;
import team7.seshealthpatient.Services.Shigleton;

public class PatientLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int DEFAULT_ZOOM = 15;
    Button sendLocationBtn;
    Location facilityLocation;
    Marker mMarker;

    Double Latitude, Longitude, dLatitude, dLongitude;
    String uid, name, dLocation;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sendLocationBtn = findViewById(R.id.send_location_btn);

        Intent intent = getIntent();
        String sessionLat = intent.getStringExtra("Latitude");
        Latitude = Double.parseDouble(sessionLat);
        String sessionLong = getIntent().getStringExtra("Longitude");
        Longitude = Double.parseDouble(sessionLong);
        uid = intent.getStringExtra("uid");


        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Profile").child("name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = (String) dataSnapshot.getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Latitude == 0 || Longitude == 0 ){
            Toast.makeText(PatientLocationActivity.this, "No Patient Location Found", Toast.LENGTH_SHORT).show();
        }
        else
            loadPatientLocation();

    }

    public void loadPatientLocation(){
        LatLng sydney = new LatLng(Latitude, Longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Patient Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));
        getMedicalPlaces(Latitude, Longitude);
        sendDoctorPreference();
    }

    private void getMedicalPlaces(double lat, double lng) {
        String url = getUrl(lat, lng, "hospital");
        Log.d("getUrl", url);

        Shigleton.getInstance(PatientLocationActivity.this).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("googlePlacesResponse", response.toString());
                JSONArray places;
                try {
                    places = response.getJSONArray("results");
                    for (int i = 0; i < places.length(); i++) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        PlaceResult place = new PlaceResult(places.getJSONObject(i));
                        Log.d("placeResult", place.toString());
                        markerOptions.position(new LatLng(place.getLat(), place.getLng()));
                        markerOptions.title(place.getName());
                        mMap.addMarker(markerOptions);
                    }

                } catch (JSONException e) {
                    Log.d("GooglePlaceResults", e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    private String getUrl(double lat, double lng, String placeType) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location=" + String.valueOf(lat) + "," + String.valueOf(lng));
        url.append("&radius=" + String.valueOf(5000));
        url.append("&type=" + placeType);
        url.append("&key=" + "AIzaSyCGgXz5rhoUx-TgIzy1vcPHvYsCiHieSH4");
        //url.append("&key=" + getResources().getString(R.id.api_key));
        return url.toString();
    }

    public void sendDoctorPreference(){

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
            if(marker.isVisible()){
                sendLocationBtn.setVisibility(View.VISIBLE);
                sendLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    dLocation = marker.getPosition().toString();
                    sendLocationBtn.setVisibility(View.GONE);

                    // Adds the location into the packet info to be sent when the doctor clicks reply
                    Intent intent = new Intent(PatientLocationActivity.this, PacketInfoActivity.class);
                    intent.putExtra("location", dLocation);
                    setResult(RESULT_OK, intent);
                    finish();
                    }
                });
            }
            return false;
            }
        });
    }

}
