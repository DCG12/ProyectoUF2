package com.example.a46406163y.proyectouf2;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.events.MapEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.Marker;
import java.util.ArrayList;

import android.app.Application;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.a46406163y.proyectouf2.R.drawable.ghost;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapaFragment extends Fragment {

    private View view;
    private MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private CompassOverlay mCompassOverlay;
    private IMapController mapController;
    private RadiusMarkerClusterer paranormalMarkers;

    private Firebase ref;

    public MapaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mapa, container, false);

        map = (MapView) view.findViewById(R.id.map);



        initializeMap();
        setZoom();
        setOverlays();

        map.invalidate();

        return view;

    }

    private void putMarkers() {
        setupMarkerOverlay();


        ref = new Firebase("https://proyectouf2-7f166.firebaseio.com");

        final Firebase paranormal = ref.child("lugares");

                paranormal.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            POJO pojo = Snapshot.getValue(POJO.class);
                            System.out.println(pojo.toString());
                            Marker marker = new Marker(map);

                            GeoPoint point = new GeoPoint(

                                    Double.parseDouble(String.valueOf(pojo.getLat())),
                                    Double.parseDouble(String.valueOf(pojo.getLon())));

                            marker.setPosition(point);

                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                            marker.setTitle(pojo.getArchivo());
                            if(isAdded()){
                            marker.setIcon(getResources().getDrawable(R.drawable.ghost));}
                            marker.setSnippet(String.valueOf("Aqui ha ocurrido un hecho paranormal"));
                            marker.setImage(Drawable.createFromPath(pojo.getArchivo()));

                            marker.setAlpha(0.6f);
                            paranormalMarkers.add(marker);
                        }
                        paranormalMarkers.invalidate();
                        map.invalidate();

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
    }


    private void setupMarkerOverlay() {
        paranormalMarkers = new RadiusMarkerClusterer(getContext());
        map.getOverlays().add(paranormalMarkers);

        Drawable clusterIconD = getResources().getDrawable(R.drawable.devil);
        Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

        paranormalMarkers.setIcon(clusterIcon);
        paranormalMarkers.setRadius(100);
    }

    private void initializeMap() {
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setTilesScaledToDpi(true);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
    }

    private void setZoom() {

        mapController = map.getController();
        mapController.setZoom(14);

        GeoPoint startPoint = new GeoPoint(41.378889, 2.14);
        mapController.setCenter(startPoint);
    }

    private void setOverlays() {
        final DisplayMetrics dm = getResources().getDisplayMetrics();

        myLocationOverlay = new MyLocationNewOverlay(
                getContext(),
                new GpsMyLocationProvider(getContext()),
                map
        );
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.animateTo( myLocationOverlay
                        .getMyLocation());
            }
        });

        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        mCompassOverlay = new CompassOverlay(
                getContext(),
                new InternalCompassOrientationProvider(getContext()),
                map
        );

        mCompassOverlay.enableCompass();

        map.getOverlays().add(myLocationOverlay);
        map.getOverlays().add(this.mScaleBarOverlay);
        map.getOverlays().add(this.mCompassOverlay);
    }

    @Override
    public void onStart(){
        super.onStart();
        RefreshDataTask task = new RefreshDataTask();
        task.execute();

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            RefreshDataTask task = new RefreshDataTask();
            task.execute();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class RefreshDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(Firebase.getDefaultConfig().isPersistenceEnabled()==false){
            Firebase.setAndroidContext(getContext());
            Firebase.getDefaultConfig().setPersistenceEnabled(true);}
            putMarkers();

        }


    }
}
