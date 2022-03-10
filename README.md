# Floating options [![](https://jitpack.io/v/songuyen1816/FloatingOptions.svg)](https://jitpack.io/#songuyen1816/FloatingOptions)

Just a small library help you implement floating menu, give a star if you find it useful

<img src="https://i.postimg.cc/sxy2MzDR/floating-options.gif" width="378" height="800"/>

## Usage

- Create menu file (test_menu.xml)

```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/menu_1"
        android:icon="@drawable/ic_fab_chat_white"
        android:title=""/>
    <item
        android:id="@+id/menu_2"
        android:icon="@drawable/ic_fab_feedback_white"
        android:title=""/>
    <item
        android:id="@+id/menu_3"
        android:icon="@drawable/ic_fab_hotline_white"
        android:title=""/>
    <item
        android:id="@+id/menu_4"
        android:icon="@drawable/ic_fab_survey_white"
        android:title=""/>
</menu>
```

- Add FloatingOptions to your layout

```
<com.songuyen1816.floatingoptions.FloatingOptions
       android:id="@+id/floatingOptions"
       android:layout_width="80dp"
       android:layout_height="wrap_content"
       android:layout_margin="5dp"
       app:buttonDrawable="@drawable/ic_fab_action"
       app:menu="@menu/test_menu"
       app:optionsSize="45dp"
       app:alphaWhenIdle="0.4"
       app:optionsMargin="10dp"
       app:layout_constraintTop_toTopOf="parent"
       app:layout_constraintStart_toStartOf="parent"
       />
```

## Get it
```
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```
```
dependencies {
        implementation 'com.github.songuyen1816:FloatingOptions:latest-version'
}
```

## Thank you
