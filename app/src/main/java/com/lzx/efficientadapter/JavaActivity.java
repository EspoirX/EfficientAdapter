package com.lzx.efficientadapter;

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
import com.lzx.library.ViewHolderCreator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.lzx.library.ViewHolderCreatorKt.setImageResource;
import static com.lzx.library.ViewHolderCreatorKt.setText;

public class JavaActivity extends AppCompatActivity {
    private EfficientAdapter<Object> adapter;
    private List<Object> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initData();

        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItem(position) instanceof Image ? 1 : 3;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new EfficientAdapter<>()
                .register(new ViewHolderCreator<Object>() {
                    @Override
                    public boolean isForViewType(@org.jetbrains.annotations.Nullable Object data,
                                                 int position) {
                        return data instanceof SectionHeader;
                    }

                    @Override
                    public int getResourceId() {
                        return R.layout.item_setion_header;
                    }

                    @Override
                    public void onBindViewHolder(@org.jetbrains.annotations.Nullable Object data,
                                                 @org.jetbrains.annotations.Nullable
                                                         List<Object> items, int position,
                                                 @NotNull ViewHolderCreator<Object> holder) {
                        SectionHeader header = (SectionHeader) data;
                        setText(this, R.id.section_title, header.getTitle());
                    }
                }).register(new ViewHolderCreator<Object>() {
                    @Override
                    public boolean isForViewType(@org.jetbrains.annotations.Nullable Object data,
                                                 int position) {
                        return data instanceof User;
                    }

                    @Override
                    public int getResourceId() {
                        return R.layout.item_user;
                    }

                    @Override
                    public void onBindViewHolder(@org.jetbrains.annotations.Nullable Object data,
                                                 @org.jetbrains.annotations.Nullable
                                                         List<Object> items, int position,
                                                 @NotNull ViewHolderCreator<Object> holder) {
                        User user = (User) data;
                        setText(this, R.id.name, user.getName());
                        setImageResource(this, R.id.avatar, user.getAvatarRes());
                        //如果你的控件找不到方便赋值的方法，可以通过 findViewById 去查找
                        TextView phone = findViewById(R.id.phone);
                        phone.setText(user.getPhone());
                    }
                }).register(new ViewHolderCreator<Object>() {
                    @Override
                    public boolean isForViewType(@org.jetbrains.annotations.Nullable Object data,
                                                 int position) {
                        return data instanceof Image;
                    }

                    @Override
                    public int getResourceId() {
                        return R.layout.item_image;
                    }

                    @Override
                    public void onBindViewHolder(@org.jetbrains.annotations.Nullable Object data,
                                                 @org.jetbrains.annotations.Nullable
                                                         List<Object> items, int position,
                                                 @NotNull ViewHolderCreator<Object> holder) {
                        Image image = (Image) data;
                        setImageResource(this, R.id.imageView, image.getRes());
                    }
                }).register(new ViewHolderCreator<Object>() {
                    @Override
                    public boolean isForViewType(@org.jetbrains.annotations.Nullable Object data,
                                                 int position) {
                        return data instanceof Music;
                    }

                    @Override
                    public int getResourceId() {
                        return R.layout.item_music;
                    }

                    @Override
                    public void onBindViewHolder(@org.jetbrains.annotations.Nullable Object data,
                                                 @org.jetbrains.annotations.Nullable
                                                         List<Object> items, int position,
                                                 @NotNull ViewHolderCreator<Object> holder) {
                        Music music = (Music) data;
                        setText(this, R.id.name, music.getName());
                        setImageResource(this, R.id.cover, music.getCoverRes());
                    }
                }).attach(recyclerView);
        adapter.submitList(data);
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
