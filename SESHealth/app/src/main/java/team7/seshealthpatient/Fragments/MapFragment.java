package team7.seshealthpatient.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import team7.seshealthpatient.Activities.MainActivity;
//import team7.seshealthpatient.Manifest;
import team7.seshealthpatient.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

//    private static final String TAG = "MapFragment";
//    private static final int ERROR_DIALOG_REQUEST = 9001;

//    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
//    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
//
//
//    private Boolean mLocationPermissionGranted = false;

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;


    public MapFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize((getContext()));

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



        googleMap.addMarker(new MarkerOptions().position(new LatLng(-33.856159, 151.215256)).title("Statue of Liberty").snippet("please work"));
        CameraPosition Sydney = CameraPosition.builder().target(new LatLng(-33.856159, 151.215256)).zoom(12).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Sydney));

    }

}
