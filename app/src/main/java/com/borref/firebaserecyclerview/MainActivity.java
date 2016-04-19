package com.borref.firebaserecyclerview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static String BASE_URL = "https://dazzling-inferno-7735.firebaseio.com/";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Item> currentItems = new ArrayList<>();
    Firebase myFirebaseRef;
    private TextView currentDBUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        this.mLayoutManager = new LinearLayoutManager(this);
        this.mRecyclerView.setLayoutManager(mLayoutManager);

        Firebase.setAndroidContext(this);
        currentDBUrl = (TextView) findViewById(R.id.currentDB);

        setFirebaseValueListener();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushValue();
                Snackbar.make(view, "New item added to Firebase", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, 22);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 22) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String urlDB = sharedPreferences.getString("urlDB", null);
            BASE_URL = urlDB;
            setFirebaseValueListener();
            Snackbar.make(this.getCurrentFocus(), "New connection to: " + BASE_URL, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void setFirebaseValueListener() {
        currentDBUrl.setText("Connecting to: " + BASE_URL);
        myFirebaseRef = new Firebase(BASE_URL);

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentItems.clear();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                    Item item = new Item(itemSnapshot.getKey(), itemSnapshot.child("name").getValue(String.class));
                    currentItems.add(item);
                }

                mAdapter = new RecyclerViewAdapter(currentItems);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void pushValue() {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("name", "item "+ (currentItems.size() + 1));
        myFirebaseRef.push().setValue(item);
    }

    public class Item {
        private String id;
        private String name;

        public Item (String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
