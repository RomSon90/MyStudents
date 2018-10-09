package com.englishalternative.mystudents;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.englishalternative.mystudents.fragments.AboutFragment;
import com.englishalternative.mystudents.fragments.ClassesFragment;
import com.englishalternative.mystudents.fragments.PlacesFragment;
import com.englishalternative.mystudents.fragments.StatisticsFragment;
import com.englishalternative.mystudents.fragments.StudentsFragment;


public class MainActivity extends Activity {

    public static final String VERSION = "1.0";

    DrawerLayout drawerLayout;
    ListView drawerList;

    String[] menuTitles;
    int[] menuIcons;
    DrawerItem[] drawerItems;

    Fragment fragment;

    ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;
    StudentsFragment savedStudentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create drawer resources
        menuTitles = getResources().getStringArray(R.array.drawer_list);
        menuIcons = new int[] {
                R.drawable.ic_students_white,
                R.drawable.ic_class_white,
                R.drawable.ic_places_white,
                R.drawable.ic_chart_white,
                R.drawable.ic_about_white
        };

        drawerItems = new DrawerItem[menuTitles.length];
        for (int i = 0; i < drawerItems.length; i ++) {
            drawerItems[i] = new DrawerItem(menuIcons[i], menuTitles[i]);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Populate drawer with DrawerAdapter
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new DrawerAdapter(this, R.layout.drawer_item, drawerItems));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
        } else {
            selectItem(0);
            drawerList.setItemChecked(0, true);
        }

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                syncState();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        /* Back button now closes the app

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fm = getFragmentManager();
                Fragment fragment = fm.findFragmentByTag("visible_fragment");
                if (fragment instanceof StudentsFragment)
                    currentPosition = 0;
                if (fragment instanceof ClassesFragment )
                    currentPosition = 1;
                if (fragment instanceof PlacesFragment )
                    currentPosition = 2;
                if (fragment instanceof StatisticsFragment )
                    currentPosition = 3;
                if (fragment instanceof AboutFragment )
                    currentPosition = 4;
                setActionBarTitle(currentPosition);
                drawerList.setItemChecked(currentPosition, true);

            }
        });
        */
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        currentPosition = position;

        // Navigation drawer fragment selector
        switch (position) {
            default:
            case 0:
                if (savedStudentsFragment == null) {
                    savedStudentsFragment = new StudentsFragment();
                }
                fragment = savedStudentsFragment;
                break;
            case 1:
                fragment = new ClassesFragment();
                break;

            case 2:
                fragment = new PlacesFragment();
                break;

            case 3:
                fragment = new StatisticsFragment();
                break;
            case 4:
                fragment = new AboutFragment();
                break;
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, "visible_fragment")
                //.addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        setActionBarTitle(position);
        drawerLayout.closeDrawer(drawerList);
        invalidateOptionsMenu();
    }

    private void setActionBarTitle(int position) {
        // Set title of actionBar corresponding to selected fragment, modify items
        String title = menuTitles[position];
        getActionBar().setTitle(title);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Returns true if indicator was clicked
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass new configuration to drawerToggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }
}
