package cn.junhua.simpletitemdecoration

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * 条目适配器
 * Created by junhualin on 2019/11/11.
 */
class ItemAdapter() : RecyclerView.Adapter<ItemViewHolder>() {

    var dataList = mutableListOf<String>()
        set


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) =
        viewHolder.bindData(dataList[position], position)
}

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var tv_hint: TextView

    init {
        tv_hint = itemView.findViewById(R.id.tv_hint)
    }

    fun bindData(data: String, position: Int) {
        tv_hint.text = "$data"
    }

}