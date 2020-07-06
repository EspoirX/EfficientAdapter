# EfficientAdapter
一个可以提高开发效率的 adapter

# 使用方法

只有三个类，所以没打包，大家可以直接下载代码使用。




## 第一步
定义好你的实体类，比如 NumberInfo，Image，String... 之类的。

# Java 用法
```java
EfficientAdapter<NumberInfo> adapter = new EfficientAdapter<NumberInfo>()
       .register(new ViewHolderCreator<NumberInfo>() {
           @Override
           public boolean isForViewType(NumberInfo data, int position) {
               return data != null;
           }

           @Override
           public int getResourceId() {
               return R.layout.layout_item;
           }

           @Override
           public void onBindViewHolder(NumberInfo data, List<NumberInfo> items,
                   int position, @NotNull ViewHolderCreator<NumberInfo> holder,
                   @NotNull List<Object> payloads) {
               setText(this, R.id.number, String.valueOf(data.number));
           }
       }).attach(recyclerView);
adapter.submitList(data);
```

1. 首先我们 new 一个 EfficientAdapter 的实例，在泛型里面传入你的实体类类型。

2. 然后我们调用 register 方法，并传入一个 ViewHolderCreator 实例，register 方法的作用是注册一个 ViewHolder 到 Adapter 中，
而 ViewHolderCreator 封装了 ViewHolder 的创建。

3. 如果你是单类型列表，那么你只需要调用 register 一次就可以了，如果是多类型列表，那么有多少个类型，你就调用 register 多少次。
这跟你有多少个类型就创建多少个 ViewHolder 是一个道理。

4. isForViewType 方法的作用是区分 ViewType，注意它的返回类型是 boolean，在参数中，你可以拿到具体 item 的数据 data，以及所
在的 position。

5. 如果你是单类型的列表，你可以跟例子代码一样简单的返回 data != null 即可，那么在所有 item 里面这个条件都是成立的。
如果你是多类型的列表，那你可以根据参数去判断：

如果你的数据源由多个实体类组成，比如：
```java
private List<Object> data = new ArrayList<>();
data.add(new User("Marry", 17, R.drawable.icon2, "123456789XX"));
data.add(new SectionHeader("My Images"));
data.add(new Image(R.drawable.cover1));
```

那么在构建 EfficientAdapter 时，泛型传入的自然是 Object，然后在 isForViewType 方法中你可以这样区分类型：
```java
 // 代表这是 User 类型
 public boolean isForViewType(Object data, int position) {
   return data instanceof User;
}

 // 代表这是 SectionHeader 类型
 public boolean isForViewType(Object data, int position) {
   return data instanceof SectionHeader;
}

 // 代表这是 Image 类型
 public boolean isForViewType(Object data, int position) {
   return data instanceof Image;
}
```

如果你的数据源只有一个实体类，但是实体类里面有某个字段可以区分类型，你可以这样：
```java
 // 代表这是 User 类型
 public boolean isForViewType(ListInfo data, int position) {
   return data.type = ListInfo.USER
}

 // 代表这是 SectionHeader 类型
 public boolean isForViewType(ListInfo data, int position) {
   return data.type = ListInfo.HEADER
}

 // 代表这是 Image 类型
 public boolean isForViewType(ListInfo data, int position) {
   return data.type = ListInfo.IMAGE
}
```
更多类型的判断可根据自己项目的具体逻辑而定。

6. getResourceId 方法返回的是 ViewHolder 具体的布局

7. onBindViewHolder 是实现 item 具体逻辑的方法，在 ViewHolderCreator 里面，实现了很多类似 setText，setImageResource
等等之类的方法，目的是可以方便操作。也实现了 findViewById 方法，你可以通过它来找到你的控件，然后做具体逻辑。

8.创建完 adapter 后，你就可以把它配置给 RecycleView 了。当然，EfficientAdapter 提供了 attach 方法，直接传入 RecycleView，方便配置。

9. 调用 adapter 的 submitList 方法，就可以将数据源绑定了。除了 submitList ，还提供了 insertedData，removedData，updateData 方法，
其中 updateData 第三个参数如果为 true 的话，回调的是 Adapter 的

onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>)

如果为 false 的话，回调的是 Adapter 的

onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int)

默认为 true。


# Kotlin 用法
有了上面的解释，想必大家已经对 EfficientAdapter 的方法有一个大概的认识，那么就可以愉快的把 Java 变成 kotlin 了：
```kotlin
val adapter = EfficientAdapter<NumberInfo>()
        .register(object : ViewHolderCreator<NumberInfo>() {
            override fun isForViewType(data: NumberInfo?, position: Int) = data != null
            override fun getResourceId() = R.layout.layout_item
            override fun onBindViewHolder(
                    data: NumberInfo?, items: MutableList<NumberInfo>?, position: Int,
                    holder: ViewHolderCreator<NumberInfo>
            ) {
                setText(R.id.number, data?.number.toString())
            }
        }).attach(recycle_view)
adapter.submitList(data)
```

## 更优雅的方法（DSL）

```kotlin
val adapter = efficientAdapter<NumberInfo> {
    addItem(R.layout.layout_item) {
        isForViewType { it != null }
        bindViewHolder { data, position, holder ->
            setText(R.id.number, data?.number.toString())
        }
    }
}.attach(recycle_view)
adapter.submitList(data)
```
使用 DSL 可以很好的减少我们的代码量和增加阅读性，在 DSL 的使用中，直接使用 efficientAdapter 方法。
注意，efficientAdapter 是一个方法，不是 EfficientAdapter。

还是一样的传入泛型，这里把 register 替换成 addItem 了，在构造函数中传入 ViewHolder 的布局，同样地，需要实现
isForViewType 以及 bindViewHolder 方法。

## 再优雅一些

上面 DSL 中，虽然代码简单了很多，但是需要定义 adapter 变量才能使用 adapter 对应的增删查改方法，感觉不太好，那么
我们可以通过扩展函数的方式，把这些操作都写在 RecycleView 身上：

```kotlin
 recycle_view.setup<NumberInfo> {
    dataSource(initData())
    withLayoutManager { LinearLayoutManager(context) }
    adapter {
        addItem(R.layout.layout_item) {
            isForViewType { it != null }
            bindViewHolder { data, _, _ ->
                setText(R.id.number, data?.number.toString())
            }
        }
    }
}
```

通过 RecycleView 的 setup 方法，我们可以把列表的实现压缩成以上短短的几行代码。setup 方法内部提供了几个方法：

dataSource: 就是数据源的意思，如果你的数据源在创建 RecycleView 的时候已经确定，那么可以通过这个方法传进去。
这个方法不写的话，RecycleView 还提供了 submitList，insertedData，removedData，updateData 等扩展方法供使用。

withLayoutManager: 配置 RecycleView 的 LayoutManager 的地方，不写的话，默认是 LinearLayoutManager

adapter: 就是配置 adapter 的地方，用法跟上面 DSL 的用法一样。

全部代码都在项目中有具体例子，请自行下载体验。


下面是例子效果图：

<a href="art/1.png"><img src="art/1.png" /></a>

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

