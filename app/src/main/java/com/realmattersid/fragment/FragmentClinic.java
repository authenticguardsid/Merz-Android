package com.realmattersid.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.realmattersid.LocationProvider;
import com.realmattersid.R;
import com.realmattersid.utils.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentClinic extends Fragment implements LocationProvider.LocationCallback {
    private static final int LOCATION_REQUEST_CODE = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public String CLINIC_NAME = "";
    public String ADDRESS_PLACE = "";
    public String WEBSITE_ADDRESS = "";
    public String NUMBER_PHONE = "";
    public static final String TAG = "FragmentClinic";
    MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationManager mLocationManager;
    private LocationProvider mLocationProvider;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private SlidingUpPanelLayout mLayout;
    private static int TIME_OUT = 2000;
    public View rootView, progressOverlay;
    private static TextView fclistClinicname, fclistClinicaddress, fclistClinicwebsite;
    private static Button bCallClinic, bDirectionsClinic, bFindOtherClinic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_clinic, container, false);
        mLocationProvider = new LocationProvider(getActivity(), this);
        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        final Utils utils = new Utils(getActivity());
        progressOverlay = rootView.findViewById(R.id.progress_overlay);

        final Button bFindOtherClinic = (Button) rootView.findViewById(R.id.bFindOtherClinic);

        final Button bDirectionsClinic = (Button) rootView.findViewById(R.id.bDirectionsClinic);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    return;
                } else {
                    list_clinic();
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(final Marker marker) {
                            utils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                            final String CliniNameStreet = marker.getTitle();
                            final double vlat = marker.getPosition().latitude;
                            final double vlong = marker.getPosition().latitude;
                            get_detail_clinic(marker.getSnippet());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                                    utils.animateView(progressOverlay, View.GONE, 0, 200);
                                }
                            }, TIME_OUT);
                            bDirectionsClinic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri gmmIntentUri = Uri.parse("geo:"+vlat+","+vlong+"?q="+CliniNameStreet);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);
                                }
                            });
                            bFindOtherClinic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                }
                            });

                            return true;
                        }
                    });
                }
                googleMap.setMyLocationEnabled(true);
            }
        });
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onResume();
        mLocationProvider.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mLocationProvider.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mLocationProvider.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    public void list_clinic(){
        JsonArrayRequest arrayRequest = new JsonArrayRequest("http://control.zodapos.com/core/api/clinic.class.php?action=newview", new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject data = response.getJSONObject(i);
                            String keypartner = data.getString("key");
                            String clinicname = data.getString("title");
                            double latitudeC = data.getDouble("latlnga");
                            double longtitudeC = data.getDouble("lotlnga");
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitudeC, longtitudeC))
                                    .title(clinicname)
                                    .snippet(keypartner)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_marker)));
                            } catch (JSONException e) {

                            }
                    }
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(getActivity()).add(arrayRequest);
    }

    public void get_detail_clinic(String key){
        final TextView fclistClinicname = (TextView) rootView.findViewById(R.id.fclistClinicname);
        final TextView fclistClinicaddress = (TextView) rootView.findViewById(R.id.fclistClinicaddress);
        final TextView fclistClinicwebsite = (TextView) rootView.findViewById(R.id.fclistClinicwebsite);
        final Button bCallClinic = (Button) rootView.findViewById(R.id.bCallClinic);
        JsonArrayRequest arrayRequest = new JsonArrayRequest("http://control.zodapos.com/core/api/clinic.class.php?action=detail&id="+key, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject data = response.getJSONObject(i);
                            CLINIC_NAME = data.getString("title");
                            ADDRESS_PLACE = data.getString("address");
                            WEBSITE_ADDRESS = data.getString("website");
                            NUMBER_PHONE = data.getString("phone");
                        } catch (JSONException e) {

                        }
                    }
                    fclistClinicname.setText(CLINIC_NAME);
                    fclistClinicaddress.setText(ADDRESS_PLACE);
                    fclistClinicwebsite.setText(WEBSITE_ADDRESS);
                    bCallClinic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", NUMBER_PHONE, null)));
                        }
                    });

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(getActivity()).add(arrayRequest);
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are Here");
        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to location user
                .zoom(10)                   // Sets the zoom
                .bearing(location.getBearing())                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}