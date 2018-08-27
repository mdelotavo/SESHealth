package team7.seshealthpatient.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import team7.seshealthpatient.Fragments.DataPacketFragment;
import team7.seshealthpatient.Fragments.HeartRateFragment;
import team7.seshealthpatient.Fragments.MapFragment;
import team7.seshealthpatient.Fragments.PatientInformationFragment;
import team7.seshealthpatient.Fragments.RecordVideoFragment;
import team7.seshealthpatient.Fragments.SendFileFragment;
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

    /**
     * TAG to use
     */
    private static String TAG = "MainActivity";

    /**
     * I am using this enum to know which is the current fragment being displayed, you will see
     * what I mean with this later in this code.
     */
    private enum MenuStates {
        PATIENT_INFO, DATA_PACKET, HEARTRATE, RECORD_VIDEO, SEND_FILE, NAVIGATION_MAP, LOGOUT
    }

    /**
     * The current fragment being displayed.
     */
    private MenuStates currentState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fireBaseUser = mAuth.getCurrentUser();

        // the default fragment on display is the patient information
        currentState = MenuStates.PATIENT_INFO;

        // go look for the main drawer layout
        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
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
        NavigationView navigationView = findViewById(R.id.nav_view);
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
                            // You can find these id's at: res -> menu -> drawer_view.xml
                            case R.id.nav_patient_info:
                                // If the user clicked on a different item than the current item
                                if (currentState != MenuStates.PATIENT_INFO) {
                                    // change the fragment to the new fragment
                                    ChangeFragment(new PatientInformationFragment());
                                    currentState = MenuStates.PATIENT_INFO;
                                }
                                break;
                            case R.id.nav_data_packet:
                                if (currentState != MenuStates.DATA_PACKET) {
                                    ChangeFragment(new DataPacketFragment());
                                    currentState = MenuStates.DATA_PACKET;
                                }
                                break;
                            case R.id.nav_heartrate:
                                if (currentState != MenuStates.HEARTRATE) {
                                    ChangeFragment(new HeartRateFragment());
                                    currentState = MenuStates.HEARTRATE;
                                }
                                break;
                            case R.id.nav_recordvideo:
                                if (currentState != MenuStates.RECORD_VIDEO) {
                                    ChangeFragment(new RecordVideoFragment(getApplicationContext()));
                                    currentState = MenuStates.RECORD_VIDEO;
                                }
                                break;
                            case R.id.nav_sendfile:
                                if (currentState != MenuStates.SEND_FILE) {
                                    ChangeFragment(new SendFileFragment());
                                    currentState = MenuStates.SEND_FILE;
                                }
                                break;
                            case R.id.nav_map:
                                if (currentState != MenuStates.NAVIGATION_MAP) {
                                    ChangeFragment(new MapFragment());
                                    currentState = MenuStates.NAVIGATION_MAP;
                                }
                                break;
                            case R.id.logout:
                                if (currentState != MenuStates.LOGOUT) {
                                    mAuth.signOut();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
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
     * @param fragment The fragment to be displayed
     */
    private void ChangeFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
