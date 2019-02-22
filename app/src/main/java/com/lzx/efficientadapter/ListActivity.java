package com.lzx.efficientadapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.lzx.efficientadapter.bean.Image;
import com.lzx.efficientadapter.bean.Music;
import com.lzx.efficientadapter.bean.SectionHeader;
import com.lzx.efficientadapter.bean.User;
import com.lzx.library.EfficientAdapter;
import com.lzx.library.HolderInjector;


import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EfficientAdapter mAdapter;

    private List<Object> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = findViewById(R.id.recycle_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getItem(position) instanceof Image ? 1 : 3;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new EfficientAdapter(this, true);
        mRecyclerView.setAdapter(mAdapter);

        initData();

        mAdapter.register(Image.TYPE_IMAGE, new HolderInjector<Image>() {
            @Override
            public int getLayoutRes() {
                return R.layout.item_image;
            }

            @Override
            public void onInject(Context context, Image data, int position, Object... objects) {
                setImageResource(R.id.imageView, data.getRes());
            }
        }).register(Music.TYPE_MUSIC, new HolderInjector<Music>() {
            @Override
            public int getLayoutRes() {
                return R.layout.item_music;
            }

            @Override
            public void onInject(Context context, Music data, int position, Object... objects) {
                setText(R.id.name, data.getName());
                setImageResource(R.id.cover, data.getCoverRes());
            }
        }).register(SectionHeader.TYPE_HEADER, new HolderInjector<SectionHeader>() {
            @Override
            public int getLayoutRes() {
                return R.layout.item_setion_header;
            }

            @Override
            public void onInject(Context context, SectionHeader data, int position, Object... objects) {
                setText(R.id.section_title, data.getTitle());
            }
        }).register(User.TYPE_USER, new UserHolder());

        //赋值
        mAdapter.submitList(data);
    }

    /**
     * 如果holder代码太多，可以通过继承 HolderInjector 去写
     */
    private static class UserHolder extends HolderInjector<User> {

        @Override
        public int getLayoutRes() {
            return R.layout.item_user;
        }

        @Override
        public void onInject(Context context, User data, int position, Object... objects) {
            setText(R.id.name, data.getName());
            setImageResource(R.id.avatar, data.getAvatarRes());

            //如果你的控件找不到方便赋值的方法，可以通过 findViewById 去查找
            TextView phone = findViewById(R.id.phone);
            phone.setText(data.getPhone());
        }
    }

    private void initData() {
        data.add(new SectionHeader("My Friends"));
        data.add(new User("Jack", 21, R.drawable.icon1, "123456789XX"));
        data.add(new User("Marry", 17, R.drawable.icon2, "123456789XX"));
        data.add(new SectionHeader("My Images"));
        data.add(new Image(R.drawable.cover1));
        data.add(new Image(R.drawable.cover2));
        data.add(new Image(R.drawable.cover3));
        data.add(new Image(R.drawable.cover4));
        data.add(new Image(R.drawable.cover5));
        data.add(new Image(R.drawable.cover6));
        data.add(new Image(R.drawable.cover7));
        data.add(new Image(R.drawable.cover8));
        data.add(new Image(R.drawable.cover9));
        data.add(new Image(R.drawable.cover10));
        data.add(new Image(R.drawable.cover11));
        data.add(new SectionHeader("My Musics"));
        data.add(new Music("Love story", R.drawable.icon3));
        data.add(new Music("Nothing's gonna change my love for u", R.drawable.icon4));
        data.add(new Music("Just one last dance", R.drawable.icon5));
    }

}
