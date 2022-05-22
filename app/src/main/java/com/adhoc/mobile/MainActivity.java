package com.adhoc.mobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adhoc.mobile.core.application.AdhocManager;
import com.adhoc.mobile.core.application.AdhocManagerCallbacks;
import com.adhoc.mobile.core.application.MessageServer;
import com.adhoc.mobile.core.datalink.AdhocDevice;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ContactsAdapter.RecyclerViewClickListener {

    private static final String[] REQUIRED_PERMISSIONS;
    public static String tempId =
            "9336dddb-ab37-4357-a034-18fc6f1c22bf";

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

    private final String TAG = this.getClass().getName();
    private final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private String name;
    private Button joinButton;
    private TextView profileName;
    private boolean joined = false;
    private AdhocManager adhocManager;
    private List<Contact> contactList;
    private RecyclerView contactsRecyclerView;
    private ContactsAdapter contactAdapter;

    AdhocManagerCallbacks callbacks = new AdhocManagerCallbacks() {
        @Override
        public void onConnectionSucceed(AdhocDevice device) {
            contactList.add(new Contact(device.getId(), device.getName()));
            contactAdapter.notifyDataSetChanged();
            contactsRecyclerView.setAdapter(contactAdapter);

            Toast.makeText(MainActivity.this, "added to recycler view", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected(String endpointId) {
            // TODO(Do something when a device disconnect)
        }

        @Override
        public void notifyMessageReceivedFrom(String sourceId) {

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
        Log.i(TAG, "Create new instance of MainActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();

        name = getIntent().getStringExtra("EXTRA_NAME");
//        adhocManager = AdhocManager.getInstance(this, name, callbacks);
        MessageServer.adhocManager = AdhocManager.getInstance(this, name, callbacks);
        adhocManager = MessageServer.adhocManager;

        joinButton = findViewById(R.id.joinButton);
        profileName = findViewById(R.id.profileName);
        profileName.setText(name);

        contactsRecyclerView = findViewById(R.id.rvContacts);
        contactAdapter = new ContactsAdapter(contactList, this);
        contactsRecyclerView.setAdapter(contactAdapter);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactList.add(new Contact(tempId, "Reda"));

        joinButton.setOnClickListener(view -> {
            joined = !joined;
            if (joined) {
                adhocManager.joinNetwork();
                contactsRecyclerView.setVisibility(View.VISIBLE);
                joinButton.setText("Leave Network");
            } else {
                adhocManager.leaveNetwork();
                contactsRecyclerView.setVisibility(View.GONE);
                joinButton.setText("Join Network");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
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
}