package ru.mirea.panova.toastapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editTextInput = findViewById(R.id.editTextInput);
        Button buttonCount = findViewById(R.id.buttonCount);

        buttonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editTextInput.getText().toString();
                int length = text.length();

                String message = "СТУДЕНТ № 19 ГРУППА БСБО-08-23 Количество символов - " + length;
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}