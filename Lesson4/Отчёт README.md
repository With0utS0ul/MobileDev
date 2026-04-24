**LESSON 4** 

В ходе выполнения серии практических заданий были изучены подходы к привязке графических компонентов с помощью ViewBinding, принципы асинхронной работы в Android: создание и управление потоками (Thread), передача данных между потоками через Looper/Handler, использование Loader для асинхронной загрузки данных (на примере шифрования AES), создание и запуск фоновых сервисов (Service, foreground service) для воспроизведения мультимедиа, а также работа с WorkManager – современным API для отложенных и фоновых задач. Каждое задание реализовано в виде отдельного модуля проекта.

__1. ViewBinding и экран плеера__

Требовалось настроить ViewBinding в проекте, создать экран «музыкального плеера» с адаптацией под горизонтальную и вертикальную ориентацию.

В файл build.gradle (Module :app) добавлена настройка viewBinding true. В разметке activity_main.xml размещены элементы управления плеером (кнопки Play, Pause, Stop, TextView, SeekBar). Для горизонтальной ориентации создана альтернативная разметка в папке res/layout-land с изменённым расположением элементов. В MainActivity вместо setContentView(R.layout...) используется binding = ActivityMainBinding.inflate(getLayoutInflater()) и setContentView(binding.getRoot()), доступ к View осуществляется через поля binding.

Main Activity
```java
package ru.mirea.panova.lesson4;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.panova.lesson4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.playBtn.setText("Играть");
        binding.pauseBtn.setText("Пауза");
        binding.status.setText("Играем музыку");
    }
}
```

Приложение корректно отображается в обеих ориентациях, все View доступны через binding без риска NullPointerException.
<img width="417" height="816" alt="image" src="https://github.com/user-attachments/assets/cbce98f9-80de-44b5-93b1-682b2ef2e64b" />
<img width="1173" height="624" alt="image" src="https://github.com/user-attachments/assets/73319e5e-6ded-4684-940c-46116bbf684c" />


__2. Модуль thread: фоновый расчёт и работа с главным потоком__

Создан модуль thread. На главном экране расположены два поля ввода (общее количество пар, количество учебных дней), кнопка «Рассчитать» и TextView для результата. При нажатии кнопки в фоновом потоке вычисляется среднее количество пар в день, после чего результат отображается в UI через runOnUiThread. Дополнительно продемонстрирована блокировка главного потока (ANR) с помощью зацикленного обработчика на кнопке.
MainActivity.java
```java
package ru.mirea.panova.thread;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.panova.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        binding.calcBtn.setOnClickListener(v -> {
            String totalStr = binding.editTextTotal.getText().toString();
            String daysStr = binding.editTextDays.getText().toString();

            if (totalStr.isEmpty() || daysStr.isEmpty()) {
                binding.textViewResult.setText("Заполните все поля");
                return;
            }

            int totalPairs = Integer.parseInt(totalStr);
            int days = Integer.parseInt(daysStr);

            new Thread(() -> {
                double avg = (double) totalPairs / days;
                // Обновляем UI в главном потоке
                runOnUiThread(() -> binding.textViewResult.setText("Среднее пар в день: " + avg));
            }).start();
        });
        
        Thread mainThread = Thread.currentThread();
        String oldName = mainThread.getName();
        mainThread.setName("MyMainThread");
        Log.d("Main", "Имя до: " + oldName);
        Log.d("Main", "Имя после: " + mainThread.getName());
        Log.d("Main", "Приоритет: " + mainThread.getPriority());
        Log.d("Main", "Группа: " + mainThread.getThreadGroup());
        
        binding.btnMirea.setOnClickListener(v -> {
            long endTime = System.currentTimeMillis() + 20 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
```

<img width="372" height="722" alt="image" src="https://github.com/user-attachments/assets/c3bcc50f-dc9c-42da-9b99-a1327233f63e" />
После нажатия второй кнопки, происходит следующее:
<img width="1354" height="352" alt="image" src="https://github.com/user-attachments/assets/3eee2232-44fc-4cfe-aeb5-44470d414fad" />

__3. Модуль data_thread: последовательность выполнения Runnable__

В модуле data_thread исследовались методы отправки задач в UI-поток: runOnUiThread, View.post, View.postDelayed. На экране расположен TextView с атрибутами maxLines="10" и lines="10" для многострочного вывода. При нажатии кнопки запускаются три действия: два немедленных и одно с задержкой 2 секунды. В результате в TextView сначала появляются первые два сообщения, а через 2 секунды – третье, что демонстрирует поведение различных способов.

MainActivity.java
```java
package ru.mirea.panova.data_thread;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.panova.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "DataThread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStart.setOnClickListener(v -> {
            Log.d(TAG, "Кнопка нажата");
            binding.tVResult.setText("");
            Log.d(TAG, "TextView очищен");

            // 1. runOnUiThread
            runOnUiThread(() -> {
                Log.d(TAG, "runOnUiThread выполняется");
                appendText("1. runOnUiThread (немедленно)");
            });

            // 2. post
            binding.tVResult.post(() -> {
                Log.d(TAG, "post выполняется");
                appendText("2. post (тоже немедленно в очередь UI)");
            });

            // 3. postDelayed
            binding.tVResult.postDelayed(() -> {
                Log.d(TAG, "postDelayed выполняется");
                appendText("3. postDelayed (выполнен через 2 секунды)");
            }, 2000);

            appendText("--- Запуск теста ---");
        });
    }

    private void appendText(String text) {
        Log.d(TAG, "appendText: " + text);
        String current = binding.tVResult.getText().toString();
        String newText;
        if (current.isEmpty()) {
            newText = text;
        } else {
            newText = current + "\n" + text;
        }
        binding.tVResult.setText(newText);
        Log.d(TAG, "TextView теперь содержит: " + newText);
    }
}
```
<img width="323" height="637" alt="image" src="https://github.com/user-attachments/assets/1cb8e385-12dd-4a57-9856-2be4d421f8b0" />



__4. Модуль looper: передача сообщений между потоками__

В задании реализована очередь сообщений с помощью Handler и Looper. Создан класс MyLooper, наследующий Thread, в методе run которого подготовлен Looper, создан Handler, обрабатывающий входящие сообщения. В MainActivity запускается MyLooper, создаётся Handler для приёма результата в главном потоке. По нажатию кнопки формируется сообщение с данными, отправляется в фоновый поток, который «засыпает» на количество секунд, равное возрасту, а затем возвращает результат. В UI отображается Toast и текст в TextView.

MyLooper.java
```java
package ru.mirea.panova.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MyLooper extends Thread {
    public Handler mHandler;
    private Handler mainHandler;

    public MyLooper(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                String text = data.getString("KEY");
                // Имитация задержки (возраст секунд)
                try {
                    int age = Integer.parseInt(data.getString("AGE"));
                    Thread.sleep(age * 1000L);
                } catch (Exception ignored) {}
                // Отправка результата обратно в главный поток
                Message reply = Message.obtain();
                Bundle replyData = new Bundle();
                replyData.putString("result", "Обработано: " + text);
                reply.setData(replyData);
                mainHandler.sendMessage(reply);
            }
        };
        Looper.loop();
    }
}
```

MainActivity.java
```java
package ru.mirea.panova.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.panova.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализируем ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Обработчик для приёма результатов из фонового потока
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("result");
                Log.d("MainActivity", "Получен результат: " + result);
                binding.tvResult.setText(result);
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        };

        // Запускаем фоновый поток с Looper
        myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        // Обработчик нажатия кнопки
        binding.btnSend.setOnClickListener(v -> {
            String ageStr = binding.editTextAge.getText().toString().trim();
            String job = binding.etJob.getText().toString().trim();

            if (ageStr.isEmpty() || job.isEmpty()) {
                Toast.makeText(MainActivity.this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
                return;
            }

            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("KEY", job);
            bundle.putString("AGE", ageStr);
            msg.setData(bundle);
            myLooper.mHandler.sendMessage(msg);

            Toast.makeText(MainActivity.this, "Сообщение отправлено в фоновый поток", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Останавливаем Looper (для выхода из потока)
        if (myLooper != null && myLooper.mHandler != null) {
            myLooper.mHandler.getLooper().quit();
        }
    }
}
```
<img width="322" height="684" alt="image" src="https://github.com/user-attachments/assets/276eaf62-3a94-415a-b8bf-6ad011a3e9f2" />

<img width="326" height="702" alt="image" src="https://github.com/user-attachments/assets/3307295a-2ca8-44f1-b5d9-56ddb89a8c7c" />


__5. Модуль CryptoLoader: шифрование AES с Loader__

В модуле реализован асинхронный загрузчик MyLoader, который в фоновом потоке дешифрует текст, зашифрованный алгоритмом AES. Пользователь вводит фразу в EditText, при нажатии кнопки генерируется ключ, фраза шифруется, и зашифрованные данные вместе с ключом передаются в Loader через Bundle. Loader выполняет дешифрование в методе loadInBackground и возвращает расшифрованную строку, которая отображается через Toast. Использован LoaderManager для управления жизненным циклом загрузчика.

MyLoader.java
```java
package ru.mirea.panova.cryptoloader;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {
    public static final String ARG_WORD = "crypt";
    private byte[] cryptText;
    private byte[] keyBytes;

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        if (args != null) {
            cryptText = args.getByteArray(ARG_WORD);
            keyBytes = args.getByteArray("key");
        }
    }

    @Override
    public String loadInBackground() {
        try {
            SecretKey originalKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] decrypted = cipher.doFinal(cryptText);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка дешифрования";
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad(); // запустить loadInBackground
    }
}
```
<img width="327" height="706" alt="image" src="https://github.com/user-attachments/assets/2d23482b-d321-4b81-8bbd-2de568f1fbc1" />


__6. Модуль ServiceApp: музыкальный сервис__

Создан сервис PlayerService, воспроизводящий аудиофайл. Сервис запускается как foreground с уведомлением. В MainActivity размещены кнопки «Start» и «Stop», которые, соответственно, запускают и останавливают сервис. Для Android 13+ запрошено разрешение POST_NOTIFICATIONS. В манифест добавлены разрешения FOREGROUND_SERVICE и POST_NOTIFICATIONS, а также объявлен сервис.

PlayerService.java

```java
package ru.mirea.panova.serviceapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PlayerService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.truemusic);
        mediaPlayer.setLooping(false);
        // Создание уведомления для foreground
        NotificationChannel channel = new NotificationChannel("channel_id", "Music", NotificationManager.IMPORTANCE_LOW);
        NotificationManagerCompat.from(this).createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Играет Салют Вера Валерий Мармеладзе")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
```
<img width="422" height="716" alt="image" src="https://github.com/user-attachments/assets/06845bbc-ca2c-4a0b-82dc-9914de6c71a8" />
<img width="288" height="111" alt="image" src="https://github.com/user-attachments/assets/ac932594-08e1-437d-bb75-e0555d78c097" />


__7. Модуль WorkManager: фоновая задача с ограничениями__

Библиотека WorkManager использована для выполнения отложенной задачи с условиями (наличие интернета без тарификации, зарядка). Создан класс UploadWorker, в методе doWork задача «спит» 10 секунд и возвращает Result.success(). В MainActivity построен OneTimeWorkRequest с Constraints, задача поставлена в очередь. Статус выполнения отслеживается через LiveData<WorkInfo>.

UploadWorker.java
```java
package ru.mirea.panova.workmanager;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;

public class UploadWorker extends Worker {
    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("UploadWorker", "Работа начата");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("UploadWorker", "Работа закончена");
        return Result.success();
    }
}
```

MainActivity.java
```java
package ru.mirea.panova.workmanager;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Constraints;        // ← правильный импорт
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

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

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(UploadWorker.class)
                //.setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);
    }
}
```
<img width="849" height="169" alt="image" src="https://github.com/user-attachments/assets/efd4c589-963e-4783-a21f-a0f54cbddb77" />


__8. Контрольное задание: добавление фоновой задачи в проект MireaProject__

В ранее разработанный проект с навигационным меню (MireaProject) добавлен новый фрагмент BackgroundTaskFragment. Во фрагмент помещена кнопка запуска задачи и TextView для отображения статуса. При нажатии кнопки формируется OneTimeWorkRequest без ограничений (ограничения по интернету закомментированы), в Worker выполняется задача (задержка 10 секунд). С помощью WorkManager.getInstance(...).getWorkInfoByIdLiveData отслеживается состояние задачи и обновляется интерфейс. Пункт меню добавлен в activity_main_drawer.xml, навигационный граф пополнен соответствующим фрагментом, а AppBarConfiguration в MainActivity дополнен новым идентификатором.

BackgroundTaskFragment.java
```java
package ru.mirea.panova.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class BackgroundTaskFragment extends Fragment {

    private TextView tvStatus;
    private Button btnStart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_background_task, container, false);
        tvStatus = view.findViewById(R.id.tvTaskStatus);
        btnStart = view.findViewById(R.id.btnStartTask);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStart.setOnClickListener(v -> {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                   // .setConstraints(constraints)
                    .build();

            WorkManager.getInstance(requireContext()).enqueue(workRequest);
            tvStatus.setText("Задача запущена, ожидание...");

            WorkManager.getInstance(requireContext())
                    .getWorkInfoByIdLiveData(workRequest.getId())
                    .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null) {
                                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                    tvStatus.setText("Задача выполнена успешно!");
                                    Toast.makeText(getContext(), "Фоновая задача завершена", Toast.LENGTH_SHORT).show();
                                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                                    tvStatus.setText("Задача завершилась ошибкой");
                                    Toast.makeText(getContext(), "Ошибка выполнения задачи", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        });
    }
}
```

MyWorker.java
```java
package ru.mirea.panova.mireaproject;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;

public class MyWorker extends Worker {
    static final String TAG = "MyWorker";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: начата фоновая задача");

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }

        Log.d(TAG, "doWork: задача завершена успешно");
        return Result.success();
    }
}
```
<img width="350" height="448" alt="image" src="https://github.com/user-attachments/assets/f5138f89-195f-4753-b5b8-18ecab8b9add" />
<img width="328" height="530" alt="image" src="https://github.com/user-attachments/assets/9b3dc35e-a566-4416-9767-df64b93d2fa6" />
<img width="347" height="486" alt="image" src="https://github.com/user-attachments/assets/35e79463-ee7c-4091-8b95-f0e10a6468a4" />
<img width="550" height="686" alt="image" src="https://github.com/user-attachments/assets/02de628c-87da-4db3-9c30-874cc924e31c" />
<img width="875" height="130" alt="image" src="https://github.com/user-attachments/assets/1fc36ec4-6c6b-479d-a7e9-2f88bac75ac0" />

__Вывод__

В ходе практической работы №4 были успешно освоены следующие технологии и подходы разработки под Android:

- ViewBinding – привязка элементов разметки, устраняющая ошибки времени выполнения.

- Управление потоками: создание фоновых потоков, блокировка UI-потока (ANR), методы передачи задач в UI-поток.

- Механизм Handler / Looper для организации очередей сообщений между потоками.

- Компонент AsyncTaskLoader для асинхронной загрузки данных на примере AES-шифрования.

- Создание служб (Service) и запуск их как foreground с уведомлением, управление воспроизведением мультимедиа.

- Библиотека WorkManager – современное решение для выполнения отложенных и периодических фоновых задач с учётом условий (зарядка, сеть).

- Интеграция фоновой задачи в существующее приложение с навигацией.

Все задания выполнены в полном объёме, приложения протестированы на эмуляторе и реальных устройствах. Полученные навыки позволяют создавать отзывчивые приложения, не блокирующие пользовательский интерфейс, и эффективно управлять фоновыми операциями.
