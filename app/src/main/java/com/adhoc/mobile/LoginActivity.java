package com.adhoc.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText nameEditText;
    EditText phoneNumberEditText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEditText = findViewById(R.id.name);
        phoneNumberEditText = findViewById(R.id.phoneNumber);

        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(view -> {
            String name = nameEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            openHomePage(name, phoneNumber);
        });
    }

    private void openHomePage(String name, String phoneNumber) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("EXTRA_NAME", name);
        intent.putExtra("EXTRA_PHONE_NUMBER", phoneNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
