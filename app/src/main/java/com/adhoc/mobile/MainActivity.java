package com.adhoc.mobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adhoc.mobile.core.application.AdhocManager;
import com.adhoc.mobile.core.application.AdhocManagerCallbacks;
import com.adhoc.mobile.core.application.Endpoint;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ContactsAdapter.RecyclerViewClickListener {

    private static final String[] REQUIRED_PERMISSIONS;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            REQUIRED_PERMISSIONS =
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            REQUIRED_PERMISSIONS =
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    };
        } else {
            REQUIRED_PERMISSIONS =
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    };
        }
    }
    private String name;
    private final String TAG = this.getClass().getName();
    private final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private AdhocManager adhocManager;
    private List<Contact> contactList;
    private RecyclerView contactsRecyclerView;
    private ContactsAdapter contactAdapter;
    AdhocManagerCallbacks callbacks = new AdhocManagerCallbacks() {
        @Override
        public void onConnectionSucceed(Endpoint endpoint) {
            contactList.add(new Contact(endpoint.getId(), endpoint.getName()));
            contactAdapter.notifyDataSetChanged();
            contactsRecyclerView.setAdapter(contactAdapter);
            Toast.makeText(MainActivity.this, "added to recycler view", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected(String endpointId) {

        }

        @Override
        public void onPayloadReceived(String endpointId, String message) {
            System.out.println("Main "+"Received : " +  endpointId + message);
        }
    };

    /**
     * Returns true if the app was granted all the permissions. Otherwise, returns false.
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = getIntent().getStringExtra("EXTRA_NAME");

        contactList = new ArrayList<>();

        adhocManager = new AdhocManager(this, name, callbacks);

        contactsRecyclerView = (RecyclerView) findViewById(R.id.rvContacts);
        contactAdapter = new ContactsAdapter(contactList, this);
        contactsRecyclerView.setAdapter(contactAdapter);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactList.add(new  Contact("0","seif"));
        contactList.add(new  Contact("1","ahmed"));

        // TODO
        adhocManager.joinNetwork();
//        adhocManager.leaveNetwork();
//        adhocManager.sendMessage("message", "id");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return;
        }

        int i = 0;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Log.i(TAG, "Failed to request the permission " + permissions[i]);
                Toast.makeText(this, "Error requesting permission", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            i++;
        }
        recreate();
    }

    @Override
    public void onClick(int position) {
        Contact contact = contactList.get(position);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("EXTRA_ID", contact.getId());
        intent.putExtra("EXTRA_NAME", contact.getName());
        intent.putExtra("EXTRA_USER_NAME", name);
        startActivity(intent);
    }
}