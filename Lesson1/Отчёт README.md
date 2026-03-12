**LESSON 1**

Целью данной практической работы было ознакомление с базовыми функциями Android Studio, её настройка и выполнение заданий, описанных ниже.
В начале был настроен проект, в котором будет производиться работа. 
<img width="1170" height="646" alt="image" src="https://github.com/user-attachments/assets/9d87fe3c-2ea7-4f7d-9894-b3c1a7bf8939" />
Структура всего проекта с модулями
Согласно требованиям, проект расчитан на минимальное SDK 26 API, а также имеет следующую форму:

<img width="834" height="372" alt="image" src="https://github.com/user-attachments/assets/7aa42557-7a15-4a36-bf63-7307ba9e4db0" />

__1.Модуль LayoutType, Виды layout__
В этом модуле происходила практика применения разлинчых элементов и вариантов расположения на экране. В первую очередь для ознакомления на стандартном экране было предложено создать и поменять элемент текста:
<img width="1633" height="799" alt="image" src="https://github.com/user-attachments/assets/d57f0c0d-5e7d-4d42-8463-bb0adba2c2b5" />


В ходе первого задания был создан модуль layouttype, в котором были изучены следующие типы layout:
1. LinearLayout
<img width="1809" height="867" alt="image" src="https://github.com/user-attachments/assets/30b6e483-ab93-449e-9881-acfefa230665" />
Этот экран отличается тем, что все элементы располагаются на одной линии, вертикальной или горизонтальной.


2. TableLayout
<img width="1836" height="885" alt="image" src="https://github.com/user-attachments/assets/db868431-3d38-4d11-9c5e-05375cbb9937" />
Этот экран напоминает grid-разметку, поскольку все элементы располагаются в таблице со строками и столбцами.


3. ConstraintLayout
<img width="1855" height="882" alt="image" src="https://github.com/user-attachments/assets/8a2de4a4-9d03-41cf-8508-4ceeb1664cb7" />
Этот экран является стандартным, в нём все элементы располагаются с помощью привязок либо к другим элементам, либо к границам экрана, чтобы при масштабировании  расположение ээлементов относительно соседей или себя сильно не менялось.
--------------------------------------------------------------------------------------------------------------------------------------------------------

  
Также было выполнено задание: Создать новый модуль «control_lesson1». Требуется создать собственный экран с использованием изученных элементов. 
В свой экран я добавила картинку, кнопки, чекбокс, текст, редактируемый текст и кнопку-картинку. Всё было оформлено с помощью визуального редактора экрана. Помимо этого, экран в стандартном формате Constraint, где все элементы подвязаны к соседям на одной линии или к краям.
<img width="1789" height="890" alt="image" src="https://github.com/user-attachments/assets/04486473-804e-466e-9cf7-e51a64dab001" />

__2.Смена ориентации экрана__
В качестве следующего задания необходимо было создать экран с текстом и 6 кнопками, а позже добавить экран формата land к этому экрану. 
<img width="1643" height="847" alt="image" src="https://github.com/user-attachments/assets/d1e6e115-2e13-4998-bf18-2b82483ff8a1" />

<img width="1719" height="842" alt="image" src="https://github.com/user-attachments/assets/f1dfc2bd-f2a5-4464-a2c9-ad31815fd82d" />

Отработав варианты ориентации экрана, было необходимо добавить горизонтальный вариант приложения и для созданного раньше кастомного экрана. Я сделала разметку через TableView, поскольку она надёжнее для размещения объектов таким образом.

<img width="1237" height="821" alt="image" src="https://github.com/user-attachments/assets/cdefca34-ff86-40c4-8b7d-0c98bc2ab466" />
<img width="568" height="376" alt="image" src="https://github.com/user-attachments/assets/43fbfa2d-5cef-4eb2-b134-c89d88b3194c" />


__3.Обработчики событий__
В следующем модуле было необходимо изучить обработчик событий и реакцию на нажатие кнопок. Вот как выглядит сам экран в итоговом варианте:
<img width="1245" height="696" alt="image" src="https://github.com/user-attachments/assets/ddca6033-08dc-4c95-a378-62b9414e81be" />
Согласно коду ниже, кнопкам были даны собственные идентификаторы, которые пригодятся позже. Также для следующего задания нужно было создать чекбокс, обработка которого также встретится в скрипте обработки. 
Код разметки:
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ButtonClicker">

    <Button
        android:id="@+id/btnWhoAmI"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Who am I?"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.233"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.572" />

    <TextView
        android:id="@+id/textViewStudent"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.488"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.417" />

    <Button
        android:id="@+id/btnItIsNotMe"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="oclBtnItIsNotMe"
        android:text="@string/it_s_not_me"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.15"
        app:layout_constraintStart_toEndOf="@+id/btnWhoAmI"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.572" />

    <CheckBox
        android:id="@+id/checkBoxX"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:checked="false"
        android:text="Чекбокс"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.498"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textViewStudent"
        app:layout_constraintVertical_bias="0.0" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

Далее следует скрипт для обработки нажатий кнопок. Работает он следуюшим образом: на первой кнопке висит слушатель, внутренний класс, ожидающий момента, когда на кнопку нажмут. Если это происходит, то текст посередине меняется на "Мой номер по списку № Х", а флаг чекбокса меняется на false. На второй кнопке применялся обработчик, заданный атрибутом onClick, его оформление можно наблюдать выше в коде разметки. При нажатии на кнопку таким образом текст меняется на "Это был/а не я!", а чекбокс получает значение флажка true. 
```Java
package com.example.buttonclicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ButtonClicker extends AppCompatActivity {
    private TextView textViewStudent;
    private Button btnWhoAmI;
    private Button btnItIsNotMe;
    private CheckBox checkBoxX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_button_clicker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textViewStudent = findViewById(R.id.textViewStudent);
        btnWhoAmI = findViewById(R.id.btnWhoAmI);
        btnItIsNotMe = findViewById(R.id.btnItIsNotMe);
        checkBoxX = findViewById(R.id.checkBoxX);

        View.OnClickListener oclBtnWhoAmI = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewStudent.setText("Мой номер по списку № Х");
                checkBoxX.setChecked(false);
            }
        };
        btnWhoAmI.setOnClickListener(oclBtnWhoAmI);


    }

    public void oclBtnItIsNotMe(View view) {
        textViewStudent.setText("Это был/а не я!");
        checkBoxX.setChecked(true);
    }
}
```

__Вывод__
В ходе данной практической работы была изучена платформа Android Studio, в которой можно создавать мобильные приложения с различными настройками: от различных вариантов разметки элементов до смены ориентации экрана. Также произошло ознакомление с обработчиками событий при нажатии на кнопку, в двух различных формах: через внутренний listener и через событие с аттрибутом в разметке.
