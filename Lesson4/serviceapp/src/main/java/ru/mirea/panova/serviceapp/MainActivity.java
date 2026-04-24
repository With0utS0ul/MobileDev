package ru.mirea.panova.serviceapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import ru.mirea.panova.serviceapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());   // ← важно!

        binding.btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerService.class);
            ContextCompat.startForegroundService(this, intent);
        });

        binding.btnStop.setOnClickListener(v -> {
            stopService(new Intent(this, PlayerService.class));
        });
    }
}