package com.nm.themealdb;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<ArrayList<MealItems>>{

    private Button btnSearch;
    private ListView lvMeal;
    private EditText etKeyword;
    private ImageView imgMeal;
    private MealAdapter adapter;

    static final String EXTRAS_MEAL = "extra_meal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        adapter = new MealAdapter(this);
        adapter.notifyDataSetChanged();

        lvMeal = (ListView) findViewById(R.id.lv_meal);
        etKeyword = (EditText) findViewById(R.id.et_keyword);
        imgMeal = (ImageView) findViewById(R.id.image_meal);

        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(mealListener);

        String title = etKeyword.getText().toString();
        lvMeal.setAdapter(adapter);
        lvMeal.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                MealItems item = (MealItems) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, ActivityDetailMeal.class);

                intent.putExtra(ActivityDetailMeal.EXTRA_TITLE, item.getMealName());
                intent.putExtra(ActivityDetailMeal.EXTRA_OVERVIEW, item.getMealInstructions());
                intent.putExtra(ActivityDetailMeal.EXTRA_DATE_RELEASE, item.getMealArea());
                intent.putExtra(ActivityDetailMeal.EXTRA_IMAGE_MEAL, item.getMealImage());
                intent.putExtra(ActivityDetailMeal.EXTRA_RATING, item.getMealCategory());

                startActivity(intent);
            }
        });

        //bundle is used to get data when button is clicked
        Bundle bundle = new Bundle();
        bundle.putString(EXTRAS_MEAL, title);

        getSupportLoaderManager().initLoader(0, bundle, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_about) {
            Intent a = new Intent(MainActivity.this, About.class);
            startActivity(a);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<ArrayList<MealItems>> onCreateLoader(int i, Bundle bundle) {
        String title = "";
        if (bundle != null) {
            title = bundle.getString(EXTRAS_MEAL);
        }
        return new MealAsyncTaskLoader(this,title);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MealItems>> loader, ArrayList<MealItems> meals) {
        adapter.setData(meals);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MealItems>> loader) {
        adapter.setData(null);
    }

    //listener function if button is clicked, check value based on title
    View.OnClickListener mealListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String title = etKeyword.getText().toString();
            if (TextUtils.isEmpty(title)) {
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString(EXTRAS_MEAL, title);
            getSupportLoaderManager().restartLoader(0, bundle, MainActivity.this);
        }
    };
}