# EfficientAdapter
一个可以提高开发效率的adapter

# 使用方法

## 第一步
将你列表中使用的实体类实现 MultiTypeEntity 接口：
```java
public class NumberInfo implements MultiTypeEntity<NumberInfo> {
    public int number;

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public boolean areItemsTheSame(NumberInfo newItem) {
        return number == newItem.number;
    }
}
```
因为只有一种类型，所以 getItemType 不用管，如果你希望使用 DiffUtil ，那么我们需要重写 areItemsTheSame 方法，该方法对应着
DiffUtil 的 areItemsTheSame 方法。

## 第二步
创建 EfficientAdapter 
```java
EfficientAdapter mAdapter = new EfficientAdapter(this);
//注册holder
mAdapter.register(new HolderInjector<NumberInfo>() {
    @Override
    public int getLayoutRes() {
        return R.layout.layout_item;
    }

    @Override
    public void onInject(Context context, NumberInfo data, int position, Object... objects) {
        setText(R.id.number, String.valueOf(data.number));
    }
});
//设置adapter
mRecyclerView.setAdapter(mAdapter);
```

如上面代码所示，创建 EfficientAdapter，并传入上下文，EfficientAdapter 默认使用 DiffUtil 工具，如果你不想使用，则在构造方法中
将第二个方法设为 false 即可。

调用 adapter 的 register 方法，传入 HolderInjector 实例，尖括号中填写对应的实体类，然后重写两个方法。

- getLayoutRes 方法传入该 holder 对应的布局
- onInject 方法中实现控件的赋值，HolderInjector 中提供了很多方便控件赋值的方法，比如 setText，setImageResource 等，如果这些方法
不适合使用在你的控件中，则可以使用 findViewById 方法查找你的控件。

最后把 RecyclerView 设置 adapter。

## 第三步
把数据付给 EfficientAdapter
```java
final List<NumberInfo> list = initData();
mAdapter.submitList(list);

//模拟网络数据
private List<NumberInfo> initData() {
    List<NumberInfo> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        NumberInfo info = new NumberInfo();
        info.number = i;
        list.add(info);
    }
    return list;
}
```

数据赋值使用 submitList 方法。

完成这三步，即可轻松实现列表功能。

## 其他说明
1. 除了 submitList 方法，EfficientAdapter 还提供了 insertedData ，removedData，updateData 方法，分别对应 添加数据，
移除数据，和更新数据功能。

2. onInject 方法中有几个参数，相信除了 Object 参数其他应该都知道是什么意思，那么 Object 参数的作用是：
如果 holder 中还需要用到 context，data，position 这三个参数外的其他参数时，那么可以通过 adapter.setObjects 方法，给
Object 参数赋值，这样你就能使用自定义参数了。

3. 调用 updateData 方法里面其实是调用了 notifyItemChanged 方法，这个方法会导致更新的 item 有闪烁的效果，
如果你不想要这个闪烁的效果，可以在调用 updateData 方法时传入第三个参数为 true，这时候会使用更高效率的刷新功能，而且界面不会闪烁，
注意如果你第三个参数为 true 时，HolderInjector 中回调的不是 onInject 方法，而是 onInjectUpdate 方法，你需要重写它。例子：
```java
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

//更新数据演示
update.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
    index = 0;
    mHandler.post(mRunnable);
}
});

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
        mAdapter.updateData(3, info2, true);//高效率刷新，会回调onInjectUpdate方法，界面不会闪烁
        mHandler.postDelayed(mRunnable, 1000); 
    }
};
```

4. 如果你的列表有多个 viewType，那么你的实体类中需要重写 getItemType 方法，里面返回对应的 viewType，然后调用 register 的时候，
第一个参数传入该 holder 对应的 viewType 类型，第二个参数才传入 HolderInjector 实例。

## 多 viewType 时的使用例子：
先看效果图
<a href="art/1.png"><img src="art/1.png" width="40%"/></a>

实现上图效果你只需要这简单的代码即可：
```java
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
```

全部代码都在项目中有具体例子，请自行下载体验。


感谢：  
[SlimAdapter](https://github.com/MEiDIK/SlimAdapter)  
[diffadapter](https://github.com/SilenceDut/diffadapter)  
[MultiTypeRecyclerViewAdapter](https://github.com/crazysunj/MultiTypeRecyclerViewAdapter)  



## License

```
MIT License

Copyright (c) [2018] [lizixian]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

