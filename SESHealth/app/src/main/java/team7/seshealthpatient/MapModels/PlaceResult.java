package team7.seshealthpatient.MapModels;

import android.util.Log;

import com.google.android.gms.location.places.Place;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceResult {
    private String _id;
    private String _placeId;
    private String _name;
    private String _address;
    private double _lat;
    private double _lng;

    public PlaceResult(JSONObject jsonRes) {
        try {
            _id = jsonRes.getString("id");
            _placeId = jsonRes.getString("place_id");
            _name = jsonRes.getString("name");
            _address = jsonRes.getString("vicinity");
            _lat = jsonRes.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            _lng = jsonRes.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        } catch (JSONException e) {
            Log.d("PlaceResult", e.toString());
        }
    }

    public String getId() { return _id; }
    public String getPlaceId() { return _placeId; }
    public String getName() { return _name; }
    public String get_address() { return _address; }
    public Double getLat() { return _lat; }
    public Double getLng() { return _lng; }
    @Override
    public String toString() {
        return "(" + _id + ": " + _name + ") " + _lat + ", " + _lng;
    }
}