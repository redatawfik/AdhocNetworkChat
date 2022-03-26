package com.adhoc.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {
    EditText nameEditText;
    EditText phoneNumberEditText;
    MaterialButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEditText = findViewById(R.id.name);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);

        registerButton = (MaterialButton) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                openHomePage(name);
            }
        });
    }

    private void openHomePage(String name) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("EXTRA_NAME", name);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
