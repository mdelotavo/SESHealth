package team7.seshealthpatient.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;

import team7.seshealthpatient.Fragments.ChatFragment;
import team7.seshealthpatient.Fragments.ConnectFragment;
import team7.seshealthpatient.Fragments.DiagnoseFragment;
import team7.seshealthpatient.Fragments.MapFragment;
import team7.seshealthpatient.Fragments.PatientInformationFragment;
import team7.seshealthpatient.Fragments.PatientListFragment;
import team7.seshealthpatient.Fragments.SettingsFragment;
import team7.seshealthpatient.R;


/**
 * Class: MainActivity
 * Extends:  {@link AppCompatActivity}
 * Author:  Carlos Tirado < Carlos.TiradoCorts@uts.edu.au>, and YOU!
 * Description:
 * <p>
 * For this project I encourage you to use Fragments. It is up to you to build up the app as
 * you want, but it will be a good practice to learn on how to use Fragments. A very good tutorial
 * on how to use fragments can be found on this site:
 * http://www.vogella.com/tutorials/AndroidFragments/article.html
 * <p>
 * I basically chose to use fragments because of the design of the app, again, you can choose to change
 * completely the design of the app, but for this design specifically I will use Fragments.
 * <p>
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser fireBaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Location userLocation;
    private Fragment fragment;
    private NavigationView navigationView;

    /**
     * A basic Drawer layout that helps you build the side menu. I followed the steps on how to
     * build a menu from this site:
     * https://developer.android.com/training/implementing-navigation/nav-drawer
     * I recommend you to have a read of it if you need to do any changes to the code.
     */
    private DrawerLayout mDrawerLayout;

    private Toolbar toolbar;

    /**
     * Helps to manage the fragment that is being used in the main view.
     */
    private FragmentManager fragmentManager;

    private static String TAG = "MainActivity";

    /**
     * I am using this enum to know which is the current fragment being displayed, you will see
     * what I mean with this later in this code.
     */
    private enum MenuStates {
        PATIENT_INFO, NAVIGATION_MAP, CHAT, CONNECT, PATIENT_LIST, DIAGNOSE, PROFILE, SETTINGS, LOGOUT
    }

    /**
     * The current fragment being displayed.
     */
    private MenuStates currentState;

    @BindView(R.id.nav_patient_info)
    MenuItem patientInfoMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fireBaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(fireBaseUser.getUid());

        // the default fragment on display is the patient information
        currentState = MenuStates.PATIENT_INFO;
        fragment = new PatientInformationFragment();

        // go look for the main drawer layout
        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        };

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Set up the menu button
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // Setup the navigation drawer, most of this code was taken from:
        // https://developer.android.com/training/implementing-navigation/nav-drawer
        navigationView = findViewById(R.id.nav_view);

        reference.child("accountType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(MainActivity.this, "null account type", Toast.LENGTH_SHORT).show();
                } else {
                    navigationView.getMenu().clear();
                    if (dataSnapshot.getValue().toString().equals("patient")) {
                        navigationView.inflateMenu(R.menu.drawer_view_patient);
                    } else if (dataSnapshot.getValue().toString().equals("doctor")) {
                        navigationView.inflateMenu(R.menu.drawer_view_doctor);
                        fragment = new PatientListFragment();
                        currentState = MenuStates.PATIENT_LIST;
                        ChangeFragment(fragment);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Using a switch to see which item on the menu was clicked
                        switch (menuItem.getItemId()) {
                            // You can find these id's at: res -> menu -> drawer_view_patient.xml
                            case R.id.nav_patient_info:
                                // If the user clicked on a different item than the current item
                                if (currentState != MenuStates.PATIENT_INFO) {
                                    // change the fragment to the new fragment
                                    fragment = new PatientInformationFragment();
                                    currentState = MenuStates.PATIENT_INFO;
                                }
                                break;
                            case R.id.nav_map:
                                if (currentState != MenuStates.NAVIGATION_MAP) {
                                    fragment = new MapFragment();
                                    currentState = MenuStates.NAVIGATION_MAP;
                                }
                                break;
                            case R.id.nav_chat:
                                if (currentState != MenuStates.CHAT) {
                                    fragment = new ChatFragment();
                                    currentState = MenuStates.CHAT;
                                }
                                break;
                            case R.id.nav_connect:
                                if (currentState != MenuStates.CONNECT) {
                                    fragment = new ConnectFragment();
                                    currentState = MenuStates.CONNECT;
                                }
                                break;
                            case R.id.nav_patient_list:
                                if (currentState != MenuStates.PATIENT_LIST) {
                                    fragment = new PatientListFragment();
                                    currentState = MenuStates.PATIENT_LIST;
                                }
                                break;
                            case R.id.diagnose:
                                if (currentState != MenuStates.DIAGNOSE) {
                                    fragment = new DiagnoseFragment();
                                    currentState = MenuStates.DIAGNOSE;
                                }
                                break;
                            case R.id.nav_settings:
                                if (currentState != MenuStates.SETTINGS) {
                                    fragment = new SettingsFragment();
                                    currentState = MenuStates.SETTINGS;
                                }
                                break;
                        }

                        return true;
                    }
                });

        // If you need to listen to specific events from the drawer layout.
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                        ChangeFragment(fragment);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        // More on this code, check the tutorial at http://www.vogella.com/tutorials/AndroidFragments/article.html
        fragmentManager = getFragmentManager();

        // Add the default Fragment once the user logged in
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fragment_container, new PatientInformationFragment());
        ft.commit();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Using this at the moment to stop the activity being recreated on orientation change
     * This is needed as otherwise it will overlay any fragment with the patient info fragment
     **/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "Providers: " + FirebaseAuth.getInstance().getCurrentUser().getProviders().toString());
    }


    /**
     * Called when one of the items in the toolbar was clicked, in this case, the menu button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function changes the title of the fragment.
     *
     * @param newTitle The new title to write in the
     */
    public void ChangeTitle(String newTitle) {
        toolbar.setTitle(newTitle);
    }

    /**
     * This function allows to change the content of the Fragment holder
     *
     * @param selectedFragment The fragment to be displayed
     */
    private void ChangeFragment(Fragment selectedFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, selectedFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else {
            this.finishAffinity();
        }
    }

    public void setTVValuesProfile(final TextView textView, String child) {
        reference.child("Profile").child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    textView.setText("null");
                } else {
                    // Added (+ """) to make our Long values Strings so that we could set appropriate text values
                    textView.setText((dataSnapshot.getValue() + "").toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setTVValues(final TextView textView, String child) {
        reference.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    textView.setText("null");
                else {
                    textView.setText("");
                    for (String value : dataSnapshot.getValue().toString().split(","))
                        textView.append("- " + value.trim() + "\n");
                    textView.setText(textView.getText().toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setUserLocation(Location location) {
        this.userLocation = location;
    }

    public Location getUserLocation() {
        return this.userLocation;
    }

    public FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }
}
