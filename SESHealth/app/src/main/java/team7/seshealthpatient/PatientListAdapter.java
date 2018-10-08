package team7.seshealthpatient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PatientListAdapter extends ArrayAdapter<Patient> {
    private static final String TAG = "PatientListAdapter";
    private Context mContext;
    int mResource;

    public PatientListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Patient> objects) {
        super(context, resource, objects);
        this.mContext = mContext;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        String id = getItem(position).getId();

        Patient patient = new Patient(name, id);


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tv1 = (TextView) convertView.findViewById(R.id.adapterViewTV1);
        TextView tv2 = (TextView) convertView.findViewById(R.id.adapterViewTV2);
        TextView tv3 = (TextView) convertView.findViewById(R.id.adapterViewTV3);

        tv1.setText(name);
        tv2.setText(id);
        tv3.setText("TEMPORARY");

        return convertView;
    }
}
