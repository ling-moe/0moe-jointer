# bean协同器
该项目是为了解决实际项目中存在单接口多实现时，各个实现中的同名方法需要互相覆盖，联动的问题。


## 问题
1. 解决单接口多实现bean实例化时注入的问题
2. bean调用同名方法的联动问题

## 应用场景
可能有三种应用场景。
1. 租户逻辑隔离
2. 多插件联动
3. 事件流组合