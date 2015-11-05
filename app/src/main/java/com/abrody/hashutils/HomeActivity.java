package com.abrody.hashutils;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
        super.onStop();
        saveHashAlgoPreferences();
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
            return inflater.inflate(R.layout.fragment_home_multifile, container, false);
        }
    }

    public void textGenerateHash(View view) {
        Log.d(TAG, "textGenerateHash()");
        EditText et_digest = (EditText) findViewById(R.id.home_text_hexdigest);
        EditText et_text = (EditText) findViewById(R.id.home_edit_text);
        String digest = HashLib.sha1sum(et_text.getText().toString());
        et_digest.setText(digest);
    }

    public static final String ALGO_PREFS = "EnabledAlgorithms";

    private void loadHashAlgoPreferences() {
        ArrayList<String> list = new ArrayList<String>();


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        hashAlgorithms = settings.getStringSet(ALGO_PREFS, defaultHashAlgoPreferences());
        Log.d(TAG, "Loaded algo preferences: " + TextUtils.join(",", hashAlgorithms));
    }

    private void saveHashAlgoPreferences() {
        Log.d(TAG, "Saving hash algo preferences: " + TextUtils.join(",", hashAlgorithms));
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(ALGO_PREFS, hashAlgorithms);
        editor.apply();
    }

    private Set<String> defaultHashAlgoPreferences() {
        return new HashSet<String>(Arrays.asList("SHA1", "SHA256"));
    }
}
