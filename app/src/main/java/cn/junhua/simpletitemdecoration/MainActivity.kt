package cn.junhua.simpletitemdecoration

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import cn.junhua.android.decoration.SimpleItemDecoration

class MainActivity : AppCompatActivity() {

    private lateinit var rv_content: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val orientation = LinearLayoutManager.VERTICAL

        rv_content = findViewById(R.id.rv_content)
        rv_content.layoutManager = LinearLayoutManager(this, orientation, false)

        val adapter = ItemAdapter()
        adapter.dataList = genDataList()

        rv_content.adapter = adapter

        val simpleItemDecoration = SimpleItemDecoration
            .create(this, orientation)
            .setSkipLast()

        rv_content.addItemDecoration(simpleItemDecoration)
    }

    fun genDataList(): MutableList<String> {
        val dataList = mutableListOf<String>()
        for (i in 0 until 50) {
            dataList.add("item i=$i")
        }
        return dataList
    }

}
