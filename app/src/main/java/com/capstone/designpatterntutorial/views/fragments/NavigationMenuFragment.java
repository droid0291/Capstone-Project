package com.capstone.designpatterntutorial.views.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.capstone.designpatterntutorial.R;
import com.capstone.designpatterntutorial.di.MyApplication;
import com.capstone.designpatterntutorial.presenters.HomePresenter;
import com.capstone.designpatterntutorial.views.activities.HomeActivity;
import com.capstone.designpatterntutorial.views.adapters.NavigationMenuAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by gubbave on 5/19/2017.
 */

public class NavigationMenuFragment extends BaseFragment implements NavigationMenuAdapter.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String SELECTED_INDEX = "selectedIndex";
    private static String TAG = NavigationMenuFragment.class.getSimpleName();

    private static final int DEFAULT_SELECTED_INDEX = 0;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;

    RecyclerView mRecyclerView;

    TextView mCurrentLocation;

    TextView title;

    @Inject
    HomePresenter homePresenter;

    @Inject
    FirebaseAnalytics mFirebaseAnalytics;


    private NavigationMenuAdapter mAdapter;
    private String[] menuList;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private int mSelectedIndex = DEFAULT_SELECTED_INDEX;


    public static NavigationMenuFragment newInstance() {
        Bundle args = new Bundle();

        NavigationMenuFragment fragment = new NavigationMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void injectFragment() {
        MyApplication.getAppComponent(MyApplication.getAppContext()).inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.navigation_menu_fragment;
    }

    @Override
    protected void initFragmentCreation(Bundle savedInstanceState) {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (savedInstanceState != null) {
            mSelectedIndex = savedInstanceState.getInt(SELECTED_INDEX);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_INDEX, mSelectedIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initFragment(View rootView) {
        super.initFragment(rootView);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        menuList = getResources().getStringArray(R.array.navigation_menu_list);

        mAdapter = new NavigationMenuAdapter(menuList);
        mAdapter.setSelectedItem(mSelectedIndex);
        mAdapter.setOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {

        mSelectedIndex = position;
        int prevSelectedIndex = mAdapter.getSelectedItem();

        if (prevSelectedIndex != position) {
            mAdapter.setSelectedItem(position);
            mAdapter.notifyItemChanged(prevSelectedIndex);
            mAdapter.notifyItemChanged(position);
        }

        ((HomeActivity) getActivity()).closeDrawerLayout();

        if (getResources().getString(R.string.design_pattern).equalsIgnoreCase(menuList[position])) {
            homePresenter.getPattern(getActivity().getLoaderManager());
        } else if (getResources().getString(R.string.favorites).equalsIgnoreCase(menuList[position])) {
            ((HomeActivity) getActivity()).launchFavoriteFragment();
        } else if (getResources().getString(R.string.recents).equalsIgnoreCase(menuList[position])) {
            //Todo: Implement Recent visited pattern screen.
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG," onConnected ");
        //Check about the Last Known location
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Permission Not Granted");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            if (addresses.size() > 0) {
                Address addr = addresses.get(0);
                Log.d(TAG, "Locality :: " + addr.getLocality() + " Country Name :: " + addr.getCountryName());
                String location = addr.getLocality() + ", " + addr.getCountryName();
                mCurrentLocation.setText(location);

                //log Firebase Analytic for tracking Favorite Screen Launch
                Bundle firebaseBundle = new Bundle();
                firebaseBundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NavigationMenu");
                firebaseBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Fragment");
                firebaseBundle.putString(FirebaseAnalytics.Param.LOCATION, location);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                Log.d(TAG,"Permission granted");
                mGoogleApiClient.reconnect();
                break;
            default:
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG," onConnectionSuspended ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG," onConnectionFailed ");
    }
}