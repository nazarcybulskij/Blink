/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nazar.cybulskij.blinkr;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

import nazar.cybulskij.blinkr.fragment.ContentFragment;
import nazar.cybulskij.blinkr.fragment.MessagesListFragment;
import nazar.cybulskij.blinkr.fragment.NavigationDrawerSettingsFragment;


/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class MainActivity extends FragmentActivity implements   NavigationDrawerSettingsFragment.NavigationDrawerCallbacks,
                                                        com.google.android.gms.location.LocationListener,
                                                        GoogleApiClient.ConnectionCallbacks,
                                                        GoogleApiClient.OnConnectionFailedListener{
    private NavigationDrawerSettingsFragment mNavigationDrawerSettingsFragment;

    ///parse

    private static final String TAG = MainActivity.class.getName();
    // Maximum results returned from a Parse query
    public static final int MAX_POST_SEARCH_RESULTS = 20;
    // Maximum post search radius for map in kilometers
    private static final int MAX_POST_SEARCH_DISTANCE = 100;


    /*
 * Define a request code to send to Google Play services This code is returned in
 * Activity.onActivityResult
 */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    // A request to connect to Location Services
    private LocationRequest locationRequest;

    // Stores the current instantiation of the location client in this object
    private GoogleApiClient locationClient;


    /*
 * Constants for location update parameters
 */
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    public Location getLastLocation() {
        return lastLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    private Location lastLocation;
    private Location currentLocation;

    private boolean hasSetUpInitialLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mNavigationDrawerSettingsFragment = (NavigationDrawerSettingsFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer_left);



        // Set up the drawer.
        mNavigationDrawerSettingsFragment.setUp(
                R.id.navigation_drawer_left,
                (DrawerLayout) findViewById(R.id.drawer_layout));



        DrawerLayout drawer = ((DrawerLayout) findViewById(R.id.drawer_layout));

        drawer.closeDrawer(Gravity.LEFT);

        // Create a new global location parameters object
        locationRequest = LocationRequest.create();

        // Set the update interval
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Create a new location client, using the enclosing class to handle callbacks.
        locationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();





    }

    @Override
    public void onNavigationDrawerItemSelected(int group, int childr) {



        Fragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

    }





    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls

    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }


    /*
* Called when the Activity is no longer visible at all. Stop updates and disconnect.
*/
    @Override
    public void onStop() {
        // If the client is connected
        if (locationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        locationClient.disconnect();

        super.onStop();
    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Connect to the location services client
        locationClient.connect();
    }


    /*
  * Handle results returned to this Activity by other Activities started with
  * startActivityForResult(). In particular, the method onConnectionFailed() in
  * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to start
  * an Activity that handles Google Play services problems. The result of this call returns here,
  * to onActivityResult.
  */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        Log.i(TAG, "Connected to Google Play services");


                        break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.i(TAG, "Could not connect to Google Play services");

                        break;
                }

                // If any other request code was received
            default:
                // Report that this Activity received an unknown requestCode
                Log.i(TAG, "Unknown request code received for the activity");

                break;
        }
    }


    /*
         * In response to a request to start updates, send a request to Location Services
         */
    private void startPeriodicUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, this);
    }

    /*
     * In response to a request to stop updates, send a request to Location Services
     */
    private void stopPeriodicUpdates() {
        locationClient.disconnect();
    }


    /*
* Get the current location
*/
    private Location getLocation() {
        // If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
            return LocationServices.FusedLocationApi.getLastLocation(locationClient);
        } else {
            return null;
        }
    }


    /*
 * Verify that Google Play services is available before making a request.
 *
 * @return true if Google Play services is available, otherwise false
 */
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.i(TAG, "Google play services available");

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), TAG);
            }
            return false;
        }
    }

    /*
  * Show a dialog returned by Google Play services for the connection error code
  */
    private void showErrorDialog(int errorCode) {
        // Get the error dialog from Google Play services
        Dialog errorDialog =
                GooglePlayServicesUtil.getErrorDialog(errorCode, this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), TAG);
        }
    }




    /*
       * Called by Location Services if the connection to the location client drops because of an error.
      */
    public void onDisconnected() {
        Log.i(TAG, "Disconnected from location services");

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to location services");
        currentLocation = getLocation();
        startPeriodicUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (lastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.01) {
            // If the location hasn't changed by more than 10 meters, ignore it.
            return;
        }
        lastLocation = location;
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (!hasSetUpInitialLocation) {
            // Zoom to the current location.
            hasSetUpInitialLocation = true;
        }

        MessagesListFragment fragment = (MessagesListFragment) getFragmentManager().findFragmentByTag("messages");
        if (fragment!=null){
            fragment.doListQuery();
        }


    }



    /*
   * Set up a query to update the list view
   */
    private void doListQuery() {
        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        // If location info is available, load the data
        if (myLoc != null) {
            // Refreshes the list view with new data based
            // usually on updated location data.
            // postsQueryAdapter.loadObjects();
        }
    }



    /*
   * Helper method to get the Parse GEO point representation of a location
   */
    public  static ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Play services can resolve some errors it detects. If the error has a resolution, try
        // sending an Intent to start a Google Play services activity that can resolve error.
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {

                // Thrown if Google Play services canceled the original PendingIntent
                Log.i(TAG, "An error occurred when connecting to location services.", e);

            }
        } else {
            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }



    /*
  * Define a DialogFragment to display the error dialog generated in showErrorDialog.
  */
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /*
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

}