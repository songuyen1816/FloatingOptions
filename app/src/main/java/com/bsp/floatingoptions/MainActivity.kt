package com.bsp.floatingoptions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<com.songuyen1816.floatingoptions.FloatingOptions>(R.id.floatingOptions).setOptionSelectedListener(object :
            com.songuyen1816.floatingoptions.OptionSelectedListener {
            override fun onOptionItemSelected(id: Int) {
                when (id) {
                    R.id.menu_1 -> Toast.makeText(this@MainActivity, "Menu 1", Toast.LENGTH_SHORT)
                        .show()
                    R.id.menu_2 -> Toast.makeText(this@MainActivity, "Menu 2", Toast.LENGTH_SHORT)
                        .show()
                    R.id.menu_3 -> Toast.makeText(this@MainActivity, "Menu 3", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }
}