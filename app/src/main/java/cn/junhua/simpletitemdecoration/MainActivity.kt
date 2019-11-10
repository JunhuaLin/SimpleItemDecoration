package cn.junhua.simpletitemdecoration

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.junhua.android.decoration.SimpleItemDecoration

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val simpleItemDecoration = SimpleItemDecoration
            .create(this, SimpleItemDecoration.VERTICAL)
            .setSkipLast()
    }
}
