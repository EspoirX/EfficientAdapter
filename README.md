# EfficientAdapter
一个可以提高开发效率的adapter

# 使用方法

## 对于只有一种 item 类型的列表

### 第一步
将你列表中使用的实体类实现 MultiTypeEntity 接口：
```
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
}```
