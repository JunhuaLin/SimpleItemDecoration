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


        val simpleItemDecoration = SimpleItemDecoration
            .create(this, orientation)
            .setHeight(20f)
            .enableItemOffsets(true)
            .setStartPadding(48)
            .setEndPadding(48)
            .setDrawable(R.drawable.ic_launcher_background)
//            .setSkipFirstLast()
//            .setSkipLast()


        val simpleItemDecoration1 = SimpleItemDecoration
            .create(this, orientation)
            .setHeight(4f)
            .enableItemOffsets(true)
            .setStartPadding(48)
//            .setEndPadding(48)
            .setDrawable(R.drawable.red_item_decotation_bg_shape)
//            .setSkipList(1, 3, 4)
//            .setSkipFirstLast()
//            .setSkipLast()

        rv_content.addItemDecoration(simpleItemDecoration)
        rv_content.addItemDecoration(simpleItemDecoration1)

        val adapter = ItemAdapter()
        adapter.dataList = genDataList()

        rv_content.adapter = adapter

    }

    fun genDataList(): MutableList<String> {
        val dataList = mutableListOf<String>()
        for (i in 0 until 50) {
            dataList.add("item i=$i")
        }
        return dataList
    }

}
