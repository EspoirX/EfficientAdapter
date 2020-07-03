package com.lzx.efficientadapter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.lzx.efficientadapter.bean.NumberInfo
import com.lzx.library.EfficientAdapter
import com.lzx.library.addItem
import com.lzx.library.setup
import com.lzx.library.submitList
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mAdapter: EfficientAdapter<NumberInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        recycle_view.layoutManager = LinearLayoutManager(this)

//        mAdapter = efficientAdapter<NumberInfo> {
//            addItem(R.layout.layout_item) {
//                isForViewType { it != null }
//                bindViewHolder { data, _, _ ->
//                    setText(R.id.number, data?.number.toString())
//                }
//            }
//        }.attach(recycle_view)
//        mAdapter?.submitList(initData())

        recycle_view.setup<NumberInfo> {
            adapter {
                addItem(R.layout.layout_item) {
                    isForViewType { it != null }
                    bindViewHolder { data, _, _ ->
                        setText(R.id.number, data?.number.toString())
                    }
                }
            }
        }

        recycle_view.submitList(initData())

        //添加数据演示
        add.setOnClickListener {
            val info = NumberInfo()
            info.number = 100
            mAdapter?.insertedData(3, info)
        }
        //删除数据演示
        remove.setOnClickListener { mAdapter?.removedData(3) }
        //更新数据演示
        update.setOnClickListener {
            index = 0
            mHandler.post(mRunnable)
        }
        //停止更新数据
        stop.setOnClickListener { mHandler.removeCallbacksAndMessages(null) }
        //重新刷新数据演示
        reset.setOnClickListener { mAdapter?.submitList(initData()) }
        //界面转跳
        viewtype.setOnClickListener {
            startActivity(Intent(this@MainActivity, ListActivity::class.java))
        }
    }

    var mHandler = Handler()
    var index = 0
    private var mRunnable: Runnable = object : Runnable {
        override fun run() {
            index++
            val info = NumberInfo()
            info.number = index
            val info2 = NumberInfo()
            info2.number = index * 2
            mAdapter?.updateData(1, info, false) //普通更新，界面会闪烁
            mAdapter?.updateData(3, info2)                //高效率刷新,界面不会闪烁
            mHandler.postDelayed(this, 1000)
        }
    }

    //初始化数据
    private fun initData(): MutableList<NumberInfo> {
        val list: MutableList<NumberInfo> = mutableListOf()
        for (i in 0..9) {
            val info = NumberInfo()
            info.number = i
            list.add(info)
        }
        return list
    }
}