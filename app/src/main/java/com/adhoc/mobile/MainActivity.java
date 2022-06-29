package com.adhoc.mobile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adhoc.mobile.core.application.AdhocManager;
import com.adhoc.mobile.core.application.AdhocManagerCallbacks;
import com.adhoc.mobile.core.application.MessageServer;
import com.adhoc.mobile.core.datalink.AdhocDevice;

import java.util.ArrayList;
import java.util.HashSet;
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
                            Manifest.permission.READ_CONTACTS
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
                            Manifest.permission.READ_CONTACTS
                    };
        } else {
            REQUIRED_PERMISSIONS =
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_CONTACTS
                    };
        }
    }

    private final String TAG = this.getClass().getName();
    private final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private String name;
    private String phoneNumber;
    private Button joinButton;
    //    private TextView profileName;
    private boolean joined = false;
    private AdhocManager adhocManager;
    private List<Contact> contactList;
    private RecyclerView contactsRecyclerView;
    private ContactsAdapter contactAdapter;
    private ContactsAdapter commonContactAdapter;
    private ArrayList<Contact> localContacts;
    private ArrayList<Contact> commonContactsOnly;
    private boolean isContactsOnly;
    private ProgressBar progressBar;
    private TextView joinTextView;

    AdhocManagerCallbacks callbacks = new AdhocManagerCallbacks() {
        @Override
        public void onConnectionSucceed(AdhocDevice device) {
            Contact newContact = new Contact(device.getId(), device.getName(), device.getPhoneNumber());
            contactList.add(newContact);
            addToCommonContactIfExist(newContact);

            showRecyclerView();
            updateContactsListAdapter();
        }

        @Override
        public void onDisconnected(String id) {
            // TODO(Do something when a device disconnect)
            for (Contact c : contactList) {
                if (c.getId().equals(id)) {
                    contactList.remove(c);
                    break;
                }
            }

            for (Contact c : commonContactsOnly) {
                if (c.getId().equals(id)) {
                    commonContactsOnly.remove(c);
                    break;
                }
            }

            updateContactsListAdapter();

            if (contactList.isEmpty()) {
                showProgressBar();
            }
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

    private void showRecyclerView() {
        contactsRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        joinTextView.setVisibility(View.GONE);
    }

    private void updateContactsListAdapter() {
        if (isContactsOnly) {
            commonContactAdapter.notifyDataSetChanged();
            contactsRecyclerView.setAdapter(commonContactAdapter);
        } else {
            contactAdapter.notifyDataSetChanged();
            contactsRecyclerView.setAdapter(contactAdapter);
        }
    }

    private void addToCommonContactIfExist(Contact newContact) {
        for (Contact contact : localContacts) {
            if (contact.getPhoneNumber().contains(newContact.getPhoneNumber()) ||
                    newContact.getPhoneNumber().contains(contact.getPhoneNumber())) {
                commonContactsOnly.add(newContact);
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Chatto");

        Log.i(TAG, "Create new instance of MainActivity");

        contactList = new ArrayList<>();
        localContacts = new ArrayList<>();
        commonContactsOnly = new ArrayList<>();

        name = getIntent().getStringExtra("EXTRA_NAME");
        phoneNumber = getIntent().getStringExtra("EXTRA_PHONE_NUMBER");
        MessageServer.adhocManager = AdhocManager.getInstance(this, name, phoneNumber, callbacks);
        adhocManager = MessageServer.adhocManager;

        joinButton = findViewById(R.id.joinButton);
        progressBar = findViewById(R.id.progress_bar);
        joinTextView = findViewById(R.id.join_text);
//        profileName = findViewById(R.id.profileName);
//        profileName.setText(name);

        contactsRecyclerView = findViewById(R.id.rvContacts);
        contactAdapter = new ContactsAdapter(contactList, this);
        commonContactAdapter = new ContactsAdapter(commonContactsOnly, this);
        contactsRecyclerView.setAdapter(contactAdapter);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        joinButton.setOnClickListener(view -> {
            joined = !joined;
            if (joined) {
                if (MessageServer.adhocManager == null) {
                    MessageServer.adhocManager = AdhocManager.getInstance(this, name, phoneNumber, callbacks);
                    adhocManager = MessageServer.adhocManager;
                }
                adhocManager = MessageServer.adhocManager;
                adhocManager.joinNetwork();
                showProgressBar();
//                contactsRecyclerView.setVisibility(View.VISIBLE);
                joinButton.setText("Leave Network");
            } else {
                adhocManager.leaveNetwork();
                contactList.clear();
                commonContactsOnly.clear();
                updateContactsListAdapter();
                MessageServer.adhocManager = null;
                showJoinText();
//                contactsRecyclerView.setVisibility(View.GONE);
                joinButton.setText("Join Network");
            }
        });

    }

    private void showJoinText() {
        joinTextView.setVisibility(View.VISIBLE);
        contactsRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        contactsRecyclerView.setVisibility(View.GONE);
        joinTextView.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }

        if (localContacts.size() == 0) {
            localContacts = getContactList();
        }
    }

    @Override
    public void onClick(int position) {
        Contact contact = contactList.get(position);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("EXTRA_ID", contact.getId());
        intent.putExtra("EXTRA_NAME", contact.getName());
        intent.putExtra("EXTRA_USER_NAME", name);
        intent.putExtra("EXTRA_PHONE_NUMBER", contact.getPhoneNumber());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contacts_only:
                if (item.isChecked()) {
                    item.setChecked(false);
                    isContactsOnly = false;
                    updateContactsListAdapter();
                } else {
                    item.setChecked(true);
                    isContactsOnly = true;
                    updateContactsListAdapter();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private ArrayList<Contact> getContactList() {
        final String[] PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        ArrayList<Contact> res = new ArrayList<>();
        ContentResolver cr = getContentResolver();

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    if (!mobileNoSet.contains(number)) {
                        res.add(new Contact(name, number));
                        mobileNoSet.add(number);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return res;
    }
}