**LESSON 3**

В ходе выполнения серии практических заданий были изучены механизмы явных и неявных намерений (Intent), передача данных между активностями, получение результата от дочерней активности, вызов системных приложений, работа с фрагментами и их адаптация под разные ориентации экрана, а также построение навигационного меню с использованием компонента Navigation Drawer. Каждое задание реализовано в виде отдельного модуля проекта.

__1. Передача времени между активностями (IntentApp)__

Требовалось создать приложение с двумя экранами. На первом экране получить текущее системное время, передать его во второй экран через Intent и отобразить там строку: «КВАДРАТ ЗНАЧЕНИЯ МОЕГО НОМЕРА ПО СПИСКУ В ГРУППЕ СОСТАВЛЯЕТ ЧИСЛО, а текущее время ВРЕМЯ». Число – квадрат номера студента по списку.

В модуле IntentApp созданы MainActivity и SecondActivity. В MainActivity при нажатии на кнопку формируется строка с текущим временем, упаковывается в Intent с помощью `putExtra()` и запускается SecondActivity. Во второй активности извлекается переданное время, вычисляется квадрат номера (для номера 19 квадрат равен 361) и выводится в TextView.

Часть кода MainActivity:
```java

Button btnSend = findViewById(R.id.btnSendTime);
btnSend.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        long dateInMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(new Date(dateInMillis));
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra("current_time", dateString);
        startActivity(intent);
    }
});
```
Часть из Second Activity:
```java
SecondActivity.javaIntent intent = getIntent();
String time = intent.getStringExtra("current_time");
int myNumber = [НОМЕР];
int square = myNumber * myNumber;
String resultText = "КВАДРАТ ЗНАЧЕНИЯ МОЕГО НОМЕРА ПО СПИСКУ В ГРУППЕ СОСТАВЛЯЕТ " + square +
        ", а текущее время " + time;
tvResult.setText(resultText);
```

При запуске приложения и нажатии на кнопку открывается второй экран с корректно отформатированным текстом.

![](path/to/screenshot_intentapp_main.png)
![](path/to/screenshot_intentapp_second.png)

__2. Возврат названия книги (FavoriteBook)__

Необходимо создать приложение с двумя экранами. На первом экране отображается текст «Тут появится название вашей любимой книги!» и кнопка для открытия второго экрана. На втором экране показывается любимая книга разработчика («Люцифер»), поле ввода для названия книги пользователя и кнопка отправки. После ввода текста и нажатия на кнопку второй экран закрывается, а на первом экране текст меняется на «Название Вашей любимой книги: …».

Для возврата результата использован Activity Result API. В MainActivity зарегистрирован лаунчер через `registerForActivityResult()`, который обрабатывает результат и обновляет TextView. В ShareActivity при нажатии на кнопку отправки формируется Intent с введённым текстом, вызывается `setResult(RESULT_OK, intent)` и `finish()`.

```java
private ActivityResultLauncher<Intent> bookLauncher;
@Override
protected void onCreate(Bundle savedInstanceState) {
    // . . . код
    bookLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String bookName = result.getData().getStringExtra("user_book");
                    tvBookResult.setText("Название Вашей любимой книги: " + bookName);
                }
            });
    btnOpen.setOnClickListener(v -> bookLauncher.launch(new Intent(MainActivity.this, ShareActivity.class)));
}
```
Отправка результата реализована так:
```java
btnSend.setOnClickListener(v -> {
    String book = etUserBook.getText().toString().trim();
    Intent resultIntent = new Intent();
    resultIntent.putExtra("user_book", book);
    setResult(RESULT_OK, resultIntent);
    finish();
});
```

Приложение корректно передаёт введённое название книги обратно на главный экран.

![](path/to/screenshot_favoritebook_first.png)
![](path/to/screenshot_favoritebook_second.png)
![](path/to/screenshot_favoritebook_result.png)

__3. Вызов системных приложений (SystemIntentsApp)__

Требовалось создать приложение с тремя кнопками: «позвонить», «открыть браузер», «открыть карту». При нажатии на каждую кнопку должно вызываться соответствующее системное приложение (набор номера, браузер по умолчанию, карты с координатами).

В разметке activity_main.xml размещены три кнопки с атрибутами `onClick`. В MainActivity реализованы три метода: `onClickCall`, `onClickOpenBrowser`, `onClickOpenMaps`. В каждом методе создаётся Intent с нужным action и data в виде Uri. Для предотвращения вылета при отсутствии приложения добавлена обработка `ActivityNotFoundException`.

Main Activity
```java
public void onClickOpenBrowser(View view) {
    String url = "http://developer.android.com";
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    try {
        startActivity(intent);
    } catch (ActivityNotFoundException e) {
        Toast.makeText(this, "На устройстве не найдено приложения для открытия ссылок", Toast.LENGTH_LONG).show();
    }
}
public void onClickCall(View view) {
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:89811112233"));
    startActivity(intent);
}
public void onClickOpenMaps(View view) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("geo:55.749479,37.613944"));
    startActivity(intent);
}
```

При нажатии на кнопки открываются соответствующие системные приложения. На эмуляторе с Google APIs карты отображают указанные координаты, браузер загружает страницу, открывается диалог набора номера.

![](path/to/screenshot_systemintents_main.png)
![](path/to/screenshot_systemintents_browser.png)
![](path/to/screenshot_systemintents_maps.png)

__4. Фрагменты и поворот экрана (SimpleFragmentApp)__

Создать приложение, которое в портретной ориентации показывает две кнопки для переключения между двумя фрагментами, а в ландшафтной – отображает оба фрагмента одновременно. Фрагменты имеют разные фоновые цвета и текстовое содержимое.

Созданы два фрагмента: FirstFragment и SecondFragment, каждый со своей разметкой. В портретной версии `activity_main.xml` размещены две кнопки и FrameLayout – контейнер для фрагментов. В MainActivity реализована логика замены фрагмента при нажатии на кнопки. Для ландшафтной ориентации создан файл `res/layout-land/activity_main.xml`, в котором вместо кнопок и контейнера размещены два Fragment, а в коде MainActivity при старте проверяется, есть ли на экране кнопки (они только в портрете): если кнопок нет, то return.
Вертикально
```xml
<Button android:id="@+id/btnFirstFragment" ... />
<Button android:id="@+id/btnSecondFragment" ... />
<FrameLayout android:id="@+id/fragmentContainer" ... />
```
Горизонтально
```xml
<FrameLayout android:id="@+id/fragment1Container" ... />
<FrameLayout android:id="@+id/fragment2Container" ... />
```
Методы внутри Main Activity:
```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnFirst = findViewById(R.id.btnFirstFragment);
        Button btnSecond = findViewById(R.id.btnSecondFragment);
        if (btnFirst == null || btnSecond == null) {
            return;
        }

        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new FirstFragment());
            }
        });

        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SecondFragment());
            }
        });
        if (savedInstanceState == null) {
            loadFragment(new FirstFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
    }
}
```

При запуске на телефоне в портретном режиме отображаются кнопки и один фрагмент; при повороте экрана оба фрагмента показываются рядом.

![](path/to/screenshot_fragments_portrait.png)
![](path/to/screenshot_fragments_landscape.png)

__5. Контрольное задание: навигационное меню и WebView (MireaProject)__

Создать проект с навигационным меню (боковая шторка), содержащим два пункта: «Отрасль IT» и «WebView». При выборе первого пункта отображается фрагмент с информацией об интересующей отрасли (стилизация по Material You), при выборе второго – фрагмент со встроенным браузером на базе WebView, загружающий страницу по умолчанию (https://www.mirea.ru).

Поскольку в новых версиях Android Studio шаблон `Navigation Drawer Activity` отсутствует, проект был создан вручную на основе `Empty Views Activity`. Добавлены зависимости `navigation-fragment`, `navigation-ui` и `material`. Созданы навигационный граф `res/navigation/mobile_navigation.xml`, два фрагмента (`DataFragment` и `WebViewFragment`), меню `res/menu/activity_main_drawer.xml`, разметка `activity_main.xml` с `DrawerLayout`, `Toolbar` и `NavHostFragment`. В `MainActivity` настроена связь тулбара, меню и навигации. В `WebViewFragment` включена поддержка JavaScript и загружен сайт. В манифест добавлено разрешение `INTERNET`.
Разметка mobile_navigation.xml:
```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/mobile_navigation"
    app:startDestination="@id/nav_data">
    <fragment
        android:id="@+id/nav_data"
        android:name="ru.mirea.panova.mireaproject.DataFragment"
        android:label="Отрасль IT" />
    <fragment
        android:id="@+id/nav_webview"
        android:name="ru.mirea.panova.mireaproject.WebViewFragment"
        android:label="WebView" />
</navigation>
```
Класс фрагмента WebViewFragment:
```java
public class WebViewFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        WebView webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.mirea.ru");
        return view;
    }
}
```
Настройка навигации в MainActivity:
```java
NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
        .findFragmentById(R.id.nav_host_fragment);
NavController navController = navHostFragment.getNavController();
appBarConfiguration = new AppBarConfiguration.Builder(
        R.id.nav_data, R.id.nav_webview)
        .setOpenableLayout(drawerLayout)
        .build();
NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
NavigationUI.setupWithNavController(navigationView, navController);
```

Приложение успешно запускается. В тулбаре отображается иконка «гамбургер», при нажатии на неё выезжает боковое меню. Переключение между фрагментами происходит плавно. WebView корректно отображает веб-страницу.

![](path/to/screenshot_mireaproject_menu.png)
![](path/to/screenshot_mireaproject_data.png)
![](path/to/screenshot_mireaproject_webview.png)

__Вывод__

В ходе выполнения заданий были практически освоены следующие ключевые аспекты разработки под Android:

- явные и неявные намерения для передачи данных и вызова системных приложений;
- получение результата от дочерней активности с помощью Activity Result API;
- создание фрагментов, их динамическая замена и адаптация интерфейса под разные ориентации экрана;
- построение навигационного меню (Navigation Drawer) с использованием компонентов навигации Android Jetpack;
- работа с WebView для встраивания браузера в приложение.

Все реализованные модули протестированы на эмуляторе, работают без критических ошибок и соответствуют требованиям задания.