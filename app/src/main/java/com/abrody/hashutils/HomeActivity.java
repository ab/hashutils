package com.abrody.hashutils;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static final String TAG = "HomeActivity";
    public static final String PREFS_NAME = "HashUtilsPrefs";

    private Set<String> hashAlgorithms;

    private static final LayoutParams matchWidth_wrapHeight = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private static final LayoutParams wrapWidth_matchHeight = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

    public List<String> getAllHashAlgoriths() {
        List<String> list = new ArrayList<>(HashLib.supportedAlgorithms);
        Collections.sort(list);
        return list;
    }

    private List<String> getHashAlgorithmsSorted() {
        ArrayList<String> list = new ArrayList<>(hashAlgorithms);
        Collections.sort(list);
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        loadHashAlgoPreferences();
    }

    @Override
    protected void onStop() {
        saveHashAlgoPreferences();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings: {
                Log.d(TAG, "action_settings clicked");
                return true;
            }
            case R.id.action_appsettings: {
                Log.d(TAG, "trying to inflate menu_hashes");
                PopupMenu popup = new PopupMenu(this, findViewById(id));
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d(TAG, "onMenuItemClick!");
                        onHashesMenuClick(item);
                        return true;
                    }
                });

                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_hashes, popup.getMenu());


                populateHashesMenu(popup);

                popup.show();
                return true;
            }
        }

        Log.d(TAG, "unexpected onOptionsItemSelected: " + id);

        return super.onOptionsItemSelected(item);
    }

    private void populateHashesMenu(PopupMenu popup) {
        Menu menu = popup.getMenu();
        for (String algo : getAllHashAlgoriths()) {
            MenuItem item = menu.add(0, Menu.FIRST, 0, algo);
            item.setCheckable(true);
            item.setChecked(hashAlgorithms.contains(algo));
        }
    }

    private void onHashesMenuClick(MenuItem item) {
        String algo = item.getTitle().toString();
        if (item.isChecked()) {
            item.setChecked(false);
            hashAlgorithms.remove(algo);
        } else {
            item.setChecked(true);
            hashAlgorithms.add(algo);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return new HomeTextFragment();
                case 1:
                    return new HomeFileFragment();
                case 2:
                    return new HomeMultiFileFragment();
            }

            throw new RuntimeException("Unexpectedly many tabs?");
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TEXT";
                case 1:
                    return "FILE";
                case 2:
                    return "MULTI FILE";
            }
            return null;
        }
    }

    /**
     * This fragment contains the text checksum view.
     */
    public static class HomeTextFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_home_text, container, false);
        }
    }

    /**
     * This fragment contains the file checksum view.
     */
    public static class HomeFileFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_home_file, container, false);
        }
    }


    /**
     * This fragment contains the multi-file checksum view.
     */
    public static class HomeMultiFileFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_home_multifile, container, false);
            ((HomeActivity) v.getContext()).textInitializeResultsLayout();
            return v;
        }
    }

    public void fileGenerateHash(View view) {
        Log.d(TAG, "fileGenerateHash()");

        EditText et_filepath = (EditText) findViewById(R.id.home_file_filepath);
        String filepath = et_filepath.getText().toString();


        if (!Misc.isExternalStorageReadable()) {
            throw new RuntimeException("External storage not readable");
        }

        if (filepath.isEmpty()) {
            // List files in external root directory
            String extRootPath = Environment.getExternalStorageDirectory().toString();
            Log.d(TAG, "sdcard root: " + extRootPath);
            File extRoot = new File(extRootPath);

            for (File f : extRoot.listFiles()) {
                Log.d(TAG, "> " + f.getPath());
            }

            Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filepath);
        if (file.isDirectory()) {
            Toast.makeText(this, "Path is a directory", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Path is a directory: " + filepath);
            for (File f : file.listFiles()) {
                Log.d(TAG, "> " + f.getPath());
            }
            return;
        }

        String digest;

        try {
            digest = HashLib.hexDigest("SHA1", file);
        }
        catch (IOException e) {
            Toast.makeText(this, "IOException: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "SHA1: " + digest, Toast.LENGTH_LONG).show();
    }

    public void textGenerateHash(View view) {
        Log.d(TAG, "textGenerateHash()");

        EditText et_text = (EditText) findViewById(R.id.home_edit_text);
        String text = et_text.getText().toString();

        /*
        EditText et_digest = (EditText) findViewById(R.id.home_text_hexdigest);
        String digest = HashLib.sha1sum(text);
        et_digest.setText(digest);
        */


        LinearLayout container = (LinearLayout) findViewById(R.id.home_text_results);
        container.removeAllViews();

        for (String algo : getHashAlgorithmsSorted()) {
            addHashResultView(container, algo, text);
        }
    }

    public void textInitializeResultsLayout() {
        LinearLayout container = (LinearLayout) findViewById(R.id.home_text_results);

        if (container.getChildAt(0).getId() != R.id.home_text_results_placeholder) {
            Log.d(TAG, "textInitializeResultsLayout(): init already performed");
            return;
        }

        Log.d(TAG, "textInitializeResultsLayout()");

        container.removeAllViews();

        for (String algo : getHashAlgorithmsSorted()) {
            addHashResultView(container, algo, null);
        }
    }

    private int dpAsPixels(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void addHashResultView(LinearLayout container, String algorithm, String text) {
        Log.d(TAG, String.format("addHashResultView(%s)", algorithm));

        // create LinearLayout to contain label and digest
        LinearLayout resultLayout = new LinearLayout(this);
        LinearLayout.LayoutParams resultLayoutParams = new LinearLayout.LayoutParams(matchWidth_wrapHeight);
        resultLayoutParams.topMargin = dpAsPixels(6);
        resultLayoutParams.bottomMargin = dpAsPixels(6);
        resultLayout.setLayoutParams(resultLayoutParams);
        resultLayout.setOrientation(LinearLayout.HORIZONTAL);
        resultLayout.setBackgroundResource(R.drawable.myrect);
//        View.setElevation(resultLayout, dpAsPixels(2));
        ViewCompat.setElevation(resultLayout, dpAsPixels(2));

        container.addView(resultLayout);

        // create algorithm label
        TextView label = new TextView(this);
        label.setLayoutParams(wrapWidth_matchHeight);
        label.setText(algorithm);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        label.setGravity(Gravity.CENTER);
        label.setPadding(dpAsPixels(4), 0, dpAsPixels(4), 0);
        resultLayout.addView(label);

        // create edittext for digest result
        EditText digest = new EditText(this);
        digest.setLayoutParams(matchWidth_wrapHeight);
        // this didn't work: digest.setInputType(InputType.TYPE_NULL);
        digest.setKeyListener(null);
        digest.setTextIsSelectable(true);
        resultLayout.addView(digest);

        if (text != null) {
            digest.setText(HashLib.hexDigest(algorithm, text));
        }

        /*
   <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:elevation="2dp"
            android:background="@drawable/myrect"
            android:layout_marginTop="16dp"
            >
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:text="SHA1"
                android:textSize="20sp"
                android:gravity="center"
                android:paddingLeft="4sp"
                android:paddingRight="4sp"
                tools:ignore="HardcodedText" />

            <EditText android:id="@+id/home_text_hexdigest"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"

                android:inputType="none"
                android:textIsSelectable="true"
                />
         */


    }

    public static final String ALGO_PREFS = "EnabledAlgorithms";

    private void loadHashAlgoPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Set<String> saved = settings.getStringSet(ALGO_PREFS, null);
        if (saved == null) {
            hashAlgorithms = defaultHashAlgoPreferences();
            Log.d(TAG, "Using default algo preferences: " + TextUtils.join(",", hashAlgorithms));

        } else {
            // copy saved object to avoid mutating it
            hashAlgorithms = new HashSet<String>(saved);
            Log.d(TAG, "Loaded algo preferences: " + TextUtils.join(",", hashAlgorithms));
        }
    }

    private void saveHashAlgoPreferences() {
        Log.d(TAG, "Saving hash algo preferences: " + TextUtils.join(",", hashAlgorithms));
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(ALGO_PREFS, hashAlgorithms).apply();
    }

    private Set<String> defaultHashAlgoPreferences() {
        return new HashSet<String>(Arrays.asList("SHA1", "SHA256"));
    }

    private void DEBUG_PREFLIST(String ... prefs) { // TODO XXX

            // Define default return values. These should not display, but are needed
            final String STRING_ERROR = "error!";
            final Integer INT_ERROR = -1;
            // ...
            final Set<String> SET_ERROR = new HashSet<>(1);

            // Add an item to the set
            SET_ERROR.add("Set Error!");

            // Loop through the Shared Prefs
            Log.i("Loading Shared Prefs", "-----------------------------------");
            Log.i("------------------", "-------------------------------------");

            for (String pref_name: prefs) {

                SharedPreferences preference = getSharedPreferences(pref_name, MODE_PRIVATE);
                Map<String, ?> prefMap = preference.getAll();

                Object prefObj;
                Object prefValue = null;

                for (String key : prefMap.keySet()) {

                    prefObj = prefMap.get(key);

                    if (prefObj instanceof String) prefValue = preference.getString(key, STRING_ERROR);
                    if (prefObj instanceof Integer) prefValue = preference.getInt(key, INT_ERROR);
                    // ...
                    if (prefObj instanceof Set) prefValue = preference.getStringSet(key, SET_ERROR);

                    Log.i(String.format("Shared Preference : %s - %s", pref_name, key),
                            String.valueOf(prefValue));

                }

                Log.i("------------------", "-------------------------------------");

            }

            Log.i("Loaded Shared Prefs", "------------------------------------");
    }
}
