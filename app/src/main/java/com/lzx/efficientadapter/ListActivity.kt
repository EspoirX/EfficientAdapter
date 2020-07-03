package com.lzx.efficientadapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.lzx.efficientadapter.bean.Image
import com.lzx.efficientadapter.bean.Music
import com.lzx.efficientadapter.bean.SectionHeader
import com.lzx.efficientadapter.bean.User
import com.lzx.library.EfficientAdapter
import com.lzx.library.ViewHolderCreator
import com.lzx.library.addItem
import com.lzx.library.efficientAdapter
import com.lzx.library.setImageResource
import com.lzx.library.setText
import com.lzx.library.setup
import com.lzx.library.submitList
import kotlinx.android.synthetic.main.activity_list.*
import java.util.ArrayList

class ListActivity : AppCompatActivity() {
    private val data: MutableList<Any> = ArrayList()
    var adapter: EfficientAdapter<Any>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        initData()

//        impl1()
//        impl2()
        impl3()
    }

    private fun impl1() {
        val gridLayoutManager = GridLayoutManager(this@ListActivity, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter?.getItem(position) is Image) 1 else 3
            }
        }
        recycle_view.layoutManager = gridLayoutManager
        adapter = EfficientAdapter<Any>()
                .register(object : ViewHolderCreator<Any>() {
                    override fun isForViewType(data: Any?, position: Int) = data is SectionHeader
                    override fun getResourceId() = R.layout.item_setion_header

                    override fun onBindViewHolder(
                            data: Any?,
                            items: MutableList<Any>?, position: Int,
                            holder: ViewHolderCreator<Any>
                    ) {
                        val header = data as SectionHeader
                        setText(R.id.section_title, header.title)
                    }
                }).register(object : ViewHolderCreator<Any>() {
                    override fun isForViewType(data: Any?, position: Int) = data is User
                    override fun getResourceId() = R.layout.item_user

                    override fun onBindViewHolder(
                            data: Any?,
                            items: MutableList<Any>?, position: Int,
                            holder: ViewHolderCreator<Any>
                    ) {
                        val user = data as User
                        setText(R.id.name, user.name)
                        setImageResource(R.id.avatar, user.avatarRes)

                        //如果你的控件找不到方便赋值的方法，可以通过 findViewById 去查找
                        val phone = findViewById<TextView>(R.id.phone)
                        phone.text = user.phone
                    }
                }).register(object : ViewHolderCreator<Any>() {
                    override fun isForViewType(data: Any?, position: Int) = data is Image

                    override fun getResourceId() = R.layout.item_image

                    override fun onBindViewHolder(
                            data: Any?,
                            items: MutableList<Any>?, position: Int,
                            holder: ViewHolderCreator<Any>
                    ) {
                        val image = data as Image
                        setImageResource(R.id.imageView, image.res)
                    }
                }).register(object : ViewHolderCreator<Any>() {
                    override fun isForViewType(data: Any?, position: Int) = data is Music
                    override fun getResourceId() = R.layout.item_music

                    override fun onBindViewHolder(
                            data: Any?,
                            items: MutableList<Any>?, position: Int,
                            holder: ViewHolderCreator<Any>
                    ) {
                        val music = data as Music?
                        setText(R.id.name, music!!.name)
                        setImageResource(R.id.cover, music.coverRes)
                    }
                }).attach(recycle_view)
        adapter?.submitList(data)
    }

    private fun impl2() {
        val gridLayoutManager = GridLayoutManager(this@ListActivity, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter?.getItem(position) is Image) 1 else 3
            }
        }
        recycle_view.layoutManager = gridLayoutManager
        adapter = efficientAdapter<Any> {
            addItem(R.layout.item_setion_header) {
                isForViewType { it is SectionHeader }
                bindViewHolder { data, _, _ ->
                    val header = data as SectionHeader
                    setText(R.id.section_title, header.title)
                }
            }
            addItem(R.layout.item_user) {
                isForViewType { it is User }
                bindViewHolder { data, _, _ ->
                    val user = data as User
                    setText(R.id.name, user.name)
                    setImageResource(R.id.avatar, user.avatarRes)
                    //如果你的控件找不到方便赋值的方法，可以通过 findViewById 去查找
                    val phone = findViewById<TextView>(R.id.phone)
                    phone.text = user.phone
                }
            }
            addItem(R.layout.item_image) {
                isForViewType { it is Image }
                bindViewHolder { data, _, _ ->
                    val image = data as Image
                    setImageResource(R.id.imageView, image.res)
                }
            }
            addItem(R.layout.item_music) {
                isForViewType { it is Music }
                bindViewHolder { data, _, _ ->
                    val music = data as Music
                    setText(R.id.name, music.name)
                    setImageResource(R.id.cover, music.coverRes)
                }
            }
        }.attach(recycle_view)
        adapter?.submitList(data)
    }

    private fun impl3() {
        recycle_view.setup<Any> {
            withLayoutManager {
                val gridLayoutManager = GridLayoutManager(this@ListActivity, 3)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (adapter?.getItem(position) is Image) 1 else 3
                    }
                }
                return@withLayoutManager gridLayoutManager
            }
            withLayoutManager { LinearLayoutManager(context) }
            adapter {
                addItem(R.layout.item_setion_header) {
                    isForViewType { it is SectionHeader }
                    bindViewHolder { data, _, _ ->
                        val header = data as SectionHeader
                        setText(R.id.section_title, header.title)
                    }
                }
                addItem(R.layout.item_user) {
                    isForViewType { it is User }
                    bindViewHolder { data, _, _ ->
                        val user = data as User
                        setText(R.id.name, user.name)
                        setImageResource(R.id.avatar, user.avatarRes)
                        //如果你的控件找不到方便赋值的方法，可以通过 findViewById 去查找
                        val phone = findViewById<TextView>(R.id.phone)
                        phone.text = user.phone
                    }
                }
                addItem(R.layout.item_image) {
                    isForViewType { it is Image }
                    bindViewHolder { data, _, _ ->
                        val image = data as Image
                        setImageResource(R.id.imageView, image.res)
                    }
                }
                addItem(R.layout.item_music) {
                    isForViewType { it is Music }
                    bindViewHolder { data, _, _ ->
                        val music = data as Music?
                        setText(R.id.name, music!!.name)
                        setImageResource(R.id.cover, music.coverRes)
                    }
                }
            }
        }
        recycle_view.submitList(data)
    }

    private fun initData() {
        data.add(SectionHeader("My Friends"))
        data.add(User("Jack", 21, R.drawable.icon1, "123456789XX"))
        data.add(User("Marry", 17, R.drawable.icon2, "123456789XX"))
        data.add(SectionHeader("My Images"))
        data.add(Image(R.drawable.cover1))
        data.add(Image(R.drawable.cover2))
        data.add(Image(R.drawable.cover3))
        data.add(Image(R.drawable.cover4))
        data.add(Image(R.drawable.cover5))
        data.add(Image(R.drawable.cover6))
        data.add(Image(R.drawable.cover7))
        data.add(Image(R.drawable.cover8))
        data.add(Image(R.drawable.cover9))
        data.add(Image(R.drawable.cover10))
        data.add(Image(R.drawable.cover11))
        data.add(SectionHeader("My Musics"))
        data.add(Music("Love story", R.drawable.icon3))
        data.add(Music("Nothing's gonna change my love for u", R.drawable.icon4))
        data.add(Music("Just one last dance", R.drawable.icon5))
    }
}