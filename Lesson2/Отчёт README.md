**LESSON 2**
В ходе выполнения серии практических заданий были изучены основные механизмы работы приложений на платформе Android: жизненный цикл Activity, передача данных между экранами с помощью Intent, а также способы взаимодействия с пользователем через уведомления и диалоговые окна. Каждое задание реализовано в виде отдельного модуля проекта

__1. Жизненный цикл Activity__
Требовалось создать приложение, в котором на главном экране располагается поле ввода EditText. Необходимо переопределить все методы жизненного цикла MainActivity и выводить в лог сообщения с именем каждого вызванного метода. Дополнительно следовало проанализировать сохранение состояния поля ввода при сворачивании приложения (нажатие Home) и при полном закрытии (нажатие Back).
В разметке activity_main.xml размещён EditText без дополнительных элементов управления. В классе MainActivity переопределены методы onCreate(), onStart(), onResume(), onPause(), onStop(), onDestroy(), onRestart(), а также onSaveInstanceState() и onRestoreInstanceState() для отслеживания сохранения состояния. Каждый метод содержит вызов Log.i() с именем метода.
```Java
package ru.mirea.panova.activitylifecycle;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LifecycleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.i(TAG, "onCreate()");

        if (savedInstanceState != null) {
            Log.i(TAG, "onCreate() - восстановление сохраненного состояния");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState()");
    }
}

<img width="1271" height="827" alt="image" src="https://github.com/user-attachments/assets/62f5ca84-2272-444e-a869-892f97c1b79d" />

```

При первом запуске в Logcat появилась последовательность: onCreate, onStart, onResume. После нажатия кнопки Home были зафиксированы вызовы onPause, onStop, onSaveInstanceState. При возврате в приложение – onRestart, onStart, onResume. При нажатии Back и повторном запуске – полный цикл от onCreate до onDestroy. Поле ввода после сворачивания сохраняло введённый текст, а после закрытия через Back – нет, что соответствует поведению, описанному в теории.
<img width="1381" height="200" alt="image" src="https://github.com/user-attachments/assets/d3087d5d-99ce-40b8-b16a-68b62b4a9989" />
<img width="1682" height="178" alt="image" src="https://github.com/user-attachments/assets/2e6850aa-755a-45c7-a34c-4e31014d6a27" />

__2.Создание и вызов Activity__
Требовалось создать два Activity. В первом разместить поле ввода и кнопку «Отправить». При нажатии кнопки текст из поля должен передаваться во второе Activity и отображаться в TextView. 
В модуле MultyActivity созданы MainActivity и SecondActivity. В MainActivity по нажатию кнопки формируется явный Intent с указанием класса SecondActivity и добавлением текста через putExtra(). В SecondActivity полученный текст извлекается и выводится в TextView. Для возврата используется вызов finish().
Ниже можно наблюдать листинг MainActivity.java
```Java
package ru.mirea.panova.multyactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText editTextInput;
    private Button buttonSend;

    public static final String EXTRA_MESSAGE = "com.example.myapplication.MESSAGE";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editTextInput = findViewById(R.id.editText);
        buttonSend = findViewById(R.id.buttonSend);
        
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    private void sendMessage() {
        String message = editTextInput.getText().toString();
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState()");
    }
}
```

А также можно рассмотреть SecondActivity.java
```Java
package ru.mirea.panova.multyactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setContentView(R.layout.activity_second);

        textViewResult = findViewById(R.id.textViewResult);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        if (message != null && !message.isEmpty()) {
            textViewResult.setText("Получено сообщение:\n" + message);
        } else {
            textViewResult.setText("Сообщение не было передано или пустое");
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }
}
```
При вводе текста и нажатии кнопки открывается второе окно с переданной строкой
<img width="314" height="668" alt="image" src="https://github.com/user-attachments/assets/6830047f-53ff-4575-91a8-61e85304ea7e" />
<img width="327" height="541" alt="image" src="https://github.com/user-attachments/assets/3d91b037-4d5d-413b-bc5c-81d045388a71" />


Следующее задание требует создать приложение с двумя кнопками: первая открывает веб-страницу МИРЭА через неявный Intent (ACTION_VIEW), вторая позволяет поделиться текстом с ФИО студента и названием университета через ACTION_SEND.
В модуле IntentFilter разметка содержит две кнопки. Для первой кнопки создаётся Intent с действием ACTION_VIEW и URI https://www.mirea.ru/. Для второй – Intent с действием ACTION_SEND, дополнительными полями EXTRA_SUBJECT и EXTRA_TEXT. Для выбора приложения используется Intent.createChooser().
<img width="1271" height="750" alt="image" src="https://github.com/user-attachments/assets/2319f986-9865-48df-b7e6-c65ce768af6e" />
```Java
package ru.mirea.panova.intentfilter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonBrowser = findViewById(R.id.buttonBrowser);
        Button buttonShare = findViewById(R.id.buttonShare);
        buttonBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri address = Uri.parse("https://www.mirea.ru/");
                Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);
                startActivity(openLinkIntent);
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MIREA");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Иванов Иван Иванович, Университет МИРЭА");
                startActivity(Intent.createChooser(shareIntent, "МОИ ФИО"));
            }
        });

    }
}
```

При нажатии на первую кнопку открывается системный браузер с сайтом МИРЭА, нажатие Back возвращает в приложение. Вторая кнопка вызывает диалог выбора приложения (сообщения, заметки и т.п.) для отправки текста.
Результат нажатия первой кнопки:
<img width="368" height="667" alt="image" src="https://github.com/user-attachments/assets/6226978a-3a1c-4ce5-aee5-ca8d0a097902" />
Результат нажатия второй:
<img width="316" height="626" alt="image" src="https://github.com/user-attachments/assets/d3e9aacd-6929-4d8d-a174-7891e2dff43d" />

__3.Диалоговые окна__
В модуле ToastApp реализовано приложение с полем ввода и кнопкой. При нажатии подсчитывается количество символов и выводится сообщение через Toast с указанием номера студента, группы и количества символов.
<img width="291" height="656" alt="image" src="https://github.com/user-attachments/assets/dbe4f6be-e8c3-4c97-81b8-60a520cc5617" />
<img width="289" height="590" alt="image" src="https://github.com/user-attachments/assets/69ae8d09-f1b8-4bbd-8f16-27f437fdf5ef" />
```Java
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
```
Далее в новом модуле NotificationApp создано приложение с одной кнопкой, при нажатии которой отображается уведомление в статус-баре. Реализована проверка разрешения POST_NOTIFICATIONS для Android 13+, создание канала уведомлений и формирование уведомления через NotificationCompat.Builder. В манифест добавлено соответствующее разрешение.
``` Java
package ru.mirea.panova.notificationapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NotificationApp";
    private static final String CHANNEL_ID = "com.mirea.asd.notification.ANDROID";
    private static final int PERMISSION_CODE = 200;
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешение на уведомления уже есть");
            } else {
                Log.d(TAG, "Нет разрешения на уведомления, запрашиваем...");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_CODE);
            }
        } else {
            Log.d(TAG, "Версия Android ниже 13, разрешение не требуется");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешение на уведомления получено");
            } else {
                Log.d(TAG, "Разрешение на уведомления не получено");
            }
        }
    }

    public void onClickNewMessageNotification(View view) {
        // Проверяем разрешение (для Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Нет разрешения, уведомление не будет показано");
            return;
        }

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("МИРЭА")
                .setContentText("Поздравление!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Студент №19 группы БСБО-08-23, вы успешно создали уведомление!"))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d(TAG, "Уведомление отправлено с ID " + NOTIFICATION_ID);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Студент ФИО Уведомления",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Канал для уведомлений приложения");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Канал уведомлений создан");
        }
    }
}
```
<img width="288" height="619" alt="image" src="https://github.com/user-attachments/assets/daeda86c-b910-4995-8db5-a5da41370df5" />



В модуле Dialog создан класс MyDialogFragment, наследующий DialogFragment. В методе onCreateDialog() построено диалоговое окно с тремя кнопками: «Иду дальше», «На паузе», «Нет». При нажатии каждой кнопки вызывается соответствующий метод в MainActivity, который отображает Toast с подтверждением выбора. Для связи используется приведение getActivity() к MainActivity. 
```Java
package ru.mirea.panova.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Здравствуй МИРЭА!")
                .setMessage("Успех близок?")
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton("Иду дальше", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity) getActivity()).onOkClicked();
                        dialog.cancel();
                    }
                })
                .setNeutralButton("На паузе", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity) getActivity()).onNeutralClicked();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((MainActivity) getActivity()).onCancelClicked();
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
```
<img width="294" height="283" alt="image" src="https://github.com/user-attachments/assets/7b6ab2ab-0eb1-4d37-ad01-0b5fba150d22" />
<img width="222" height="89" alt="image" src="https://github.com/user-attachments/assets/4fabb148-e656-44ca-921e-b871bee9b381" />

**Полный MainActivity с обработчиками будет отмечен в конце этого раздела, т.к. он содержит связи со всеми выше и нижеперечисленными классами**

Дополнительно созданы классы MyTimeDialogFragment и MyDateDialogFragment, аналогично наследующие DialogFragment. В них возвращаются системные диалоги выбора времени и даты. После выбора значения передаются в MainActivity через методы onTimeSet() и onDateSet(), где отображаются в Toast.
```Java
package ru.mirea.panova.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class MyDateDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ((MainActivity) getActivity()).onDateSet(year, month, dayOfMonth);
                    }
                }, year, month, day);
    }
}
```

```Java
package ru.mirea.panova.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class MyDateDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ((MainActivity) getActivity()).onDateSet(year, month, dayOfMonth);
                    }
                }, year, month, day);
    }
}
```
<img width="283" height="463" alt="image" src="https://github.com/user-attachments/assets/ec89948b-0931-467e-bf78-102ed67f1fa0" />
<img width="223" height="94" alt="image" src="https://github.com/user-attachments/assets/1835d2e3-a2c0-4f3c-9d2b-b9024cef5dc9" />
<img width="271" height="488" alt="image" src="https://github.com/user-attachments/assets/15c51dd5-224d-459e-a7e0-7bd0168502f9" />
<img width="207" height="84" alt="image" src="https://github.com/user-attachments/assets/dbed07da-6484-403a-8eba-f45d64bda914" />

Класс MyProgressDialogFragment демонстрирует использование ProgressDialog. Диалог с индикатором загрузки автоматически закрывается через 2 секунды с помощью Handler.
```Java
package ru.mirea.panova.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MyProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Загрузка");
        progressDialog.setMessage("Подождите, идёт обработка...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }, 2000);

        return progressDialog;
    }
}
```
<img width="300" height="511" alt="image" src="https://github.com/user-attachments/assets/4503185a-ea9b-4d5d-b7ee-e6fea2cea6db" />


**ОБЩИЙ MAINACTIVITY**
```Java
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
    public void onClickShowDialog(View view) {
        DialogFragment dialogFragment = new MyDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "mirea");
    }

    public void onClickShowTimeDialog(View view) {
        DialogFragment timeDialog = new MyTimeDialogFragment();
        timeDialog.show(getSupportFragmentManager(), "timePicker");
    }

    public void onClickShowDateDialog(View view) {
        DialogFragment dateDialog = new MyDateDialogFragment();
        dateDialog.show(getSupportFragmentManager(), "datePicker");
    }
    public void onClickShowProgressDialog(View view) {
        DialogFragment progressDialog = new MyProgressDialogFragment();
        progressDialog.show(getSupportFragmentManager(), "progress");
    }

    public void onOkClicked() {
        Toast.makeText(this, "Вы выбрали кнопку \"Иду дальше\"!", Toast.LENGTH_LONG).show();
    }

    public void onCancelClicked() {
        Toast.makeText(this, "Вы выбрали кнопку \"Нет\"!", Toast.LENGTH_LONG).show();
    }

    public void onNeutralClicked() {
        Toast.makeText(this, "Вы выбрали кнопку \"На паузе\"!", Toast.LENGTH_LONG).show();
    }
    public void onTimeSet(int hourOfDay, int minute) {
        String time = String.format("%02d:%02d", hourOfDay, minute);
        Toast.makeText(this, "Выбрано время: " + time, Toast.LENGTH_LONG).show();
    }
    public void onDateSet(int year, int month, int dayOfMonth) {
        String date = String.format("%02d.%02d.%d", dayOfMonth, month + 1, year);
        Toast.makeText(this, "Выбрана дата: " + date, Toast.LENGTH_LONG).show();
    }
}
```

__Вывод__
В ходе выполнения заданий были практически освоены ключевые аспекты разработки под Android:

- управление жизненным циклом Activity и сохранение состояния;

- явные и неявные намерения для взаимодействия между компонентами и сторонними приложениями;

- различные способы вывода уведомлений и диалоговых окон.
