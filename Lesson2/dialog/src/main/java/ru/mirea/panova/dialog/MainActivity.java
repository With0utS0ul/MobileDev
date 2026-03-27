package ru.mirea.panova.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Простой AlertDialog
    public void onClickShowDialog(View view) {
        DialogFragment dialogFragment = new MyDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "mirea");
    }

    // TimePickerDialog
    public void onClickShowTimeDialog(View view) {
        DialogFragment timeDialog = new MyTimeDialogFragment();
        timeDialog.show(getSupportFragmentManager(), "timePicker");
    }

    // DatePickerDialog
    public void onClickShowDateDialog(View view) {
        DialogFragment dateDialog = new MyDateDialogFragment();
        dateDialog.show(getSupportFragmentManager(), "datePicker");
    }

    // ProgressDialog
    public void onClickShowProgressDialog(View view) {
        DialogFragment progressDialog = new MyProgressDialogFragment();
        progressDialog.show(getSupportFragmentManager(), "progress");
    }

    // Обработчики нажатий кнопок AlertDialog
    public void onOkClicked() {
        Toast.makeText(this, "Вы выбрали кнопку \"Иду дальше\"!", Toast.LENGTH_LONG).show();
    }

    public void onCancelClicked() {
        Toast.makeText(this, "Вы выбрали кнопку \"Нет\"!", Toast.LENGTH_LONG).show();
    }

    public void onNeutralClicked() {
        Toast.makeText(this, "Вы выбрали кнопку \"На паузе\"!", Toast.LENGTH_LONG).show();
    }

    // Обработчик выбора времени
    public void onTimeSet(int hourOfDay, int minute) {
        String time = String.format("%02d:%02d", hourOfDay, minute);
        Toast.makeText(this, "Выбрано время: " + time, Toast.LENGTH_LONG).show();
    }

    // Обработчик выбора даты
    public void onDateSet(int year, int month, int dayOfMonth) {
        String date = String.format("%02d.%02d.%d", dayOfMonth, month + 1, year);
        Toast.makeText(this, "Выбрана дата: " + date, Toast.LENGTH_LONG).show();
    }
}