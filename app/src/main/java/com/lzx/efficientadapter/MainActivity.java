package com.lzx.efficientadapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.lzx.efficientadapter.bean.NumberInfo;
import com.lzx.library.EfficientAdapter;
import com.lzx.library.HolderInjector;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EfficientAdapter mAdapter;
    private Button mAdd, remove, update, stop, reset, viewtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycle_view);
        mAdd = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        update = findViewById(R.id.update);
        stop = findViewById(R.id.stop);
        reset = findViewById(R.id.reset);
        viewtype = findViewById(R.id.viewtype);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //创建adapter
        mAdapter = new EfficientAdapter(this, true);
        //注册holder
        mAdapter.register(new HolderInjector<NumberInfo>() {
            @Override
            public int getLayoutRes() {
                return R.layout.layout_item;
            }

            /**
             * 控件赋值
             */
            @Override
            public void onInject(Context context, NumberInfo data, int position, Object... objects) {
                setText(R.id.number, String.valueOf(data.number));
            }

            /**
             * 高效率刷新，界面不会闪烁
             */
            @Override
            public void onInjectUpdate(Context context, NumberInfo data, int position, Object... objects) {
                super.onInjectUpdate(context, data, position, objects);
                setText(R.id.number, String.valueOf(data.number));
            }
        });
        //设置adapter
        mRecyclerView.setAdapter(mAdapter);
        //adapter赋值
        final List<NumberInfo> list = initData();
        mAdapter.submitList(list);

        //添加数据演示
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberInfo info = new NumberInfo();
                info.number = 100;
                mAdapter.insertedData(3, info);
            }
        });
        //删除数据演示
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.removedData(3);
            }
        });
        //更新数据演示
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                mHandler.post(mRunnable);
            }
        });
        //停止更新数据
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacksAndMessages(null);
            }
        });
        //重新刷新数据演示
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.submitList(list);
            }
        });
        //界面转跳
        viewtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });
    }

    Handler mHandler = new Handler();
    int index = 0;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            index++;

            NumberInfo info = new NumberInfo();
            info.number = index;

            NumberInfo info2 = new NumberInfo();
            info2.number = index * 2;

            mAdapter.updateData(1, info);  //普通更新，会回调onInject方法，界面会闪烁
            mAdapter.updateData(3, info2, true);
            mHandler.postDelayed(mRunnable, 1000); //高效率刷新，会回调onInjectUpdate方法，界面不会闪烁
        }
    };

    //初始化数据
    private List<NumberInfo> initData() {
        List<NumberInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            NumberInfo info = new NumberInfo();
            info.number = i;
            list.add(info);
        }
        return list;
    }
}
