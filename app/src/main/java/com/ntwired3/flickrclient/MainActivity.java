package com.ntwired3.flickrclient;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.koushikdutta.async.future.FutureCallback;
import com.ntwired3.flickrclient.adapters.ImageAdapter;
import com.ntwired3.flickrclient.models.FlickrImage;
import com.ntwired3.flickrclient.utils.DatabaseHelper;
import com.ntwired3.flickrclient.utils.FlickrHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements FutureCallback<JsonObject>, MultiplePermissionsListener {

    //Used to search for items in json response
    private static final String FLICKR_ROOT = "items";

    private ImageAdapter mAdapter;

    @BindView(R.id.search_text)
    EditText searchText;

    @OnClick(R.id.search)
    public void onClick(View view) {
        mAdapter.clear();
        refreshData();
    }

    @BindView(R.id.results)
    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupGridView();
    }

    private void setupGridView() {
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        gridView.setNumColumns(cols);
        mAdapter = new ImageAdapter(this, new ArrayList<FlickrImage>());
        gridView.setAdapter(mAdapter);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * The method to start a new api call
     */
    public void refreshData() {
        hideKeyboard(searchText);
        FlickrHelper manager = FlickrHelper.getInstance();
        String url = manager.getApiUrl(searchText.getText().toString());
        manager.loadData(url, this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestPermissions();

        if (mAdapter != null && mAdapter.getCount() <= 0) {
            //Try to load from database
            List cacheItems = DatabaseHelper.getInstance().getCache(FlickrImage.class);
            if (cacheItems != null && cacheItems.size() > 0) {
                mAdapter.loadData(cacheItems);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper.getInstance().close();
    }

    /**
     * This is called recursively as long as the user doesn't deny / approve permissions
     * In a real-app scenario this is not a good practice and the user should receive a window with explanation and the app not try to run
     */
    private void requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(this).check();
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {
        if (!report.areAllPermissionsGranted())
            requestPermissions();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

    }

    /**
     * This is our callback for the api calls
     * @param e - incase there was an error
     * @param result - the results from the api request
     */
    @Override
    public void onCompleted(Exception e, JsonObject result) {
        try {
            if (e != null)
                throw e;

            JsonArray results = result.getAsJsonArray(FLICKR_ROOT);
            if (results.size() <= 0) {
                throw new Exception(getString(R.string.noResults));
            }
            List images = DatabaseHelper.getInstance().processApiResult(results, FlickrImage[].class);
            Collections.sort(images, FlickrImage.COMPARE_BY_DATE_TAKEN);
            mAdapter.loadData(images);
        } catch (Exception ex) {
            // toast any error we encounter (most image search APIs have a throttling limit that sometimes gets hit)
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
