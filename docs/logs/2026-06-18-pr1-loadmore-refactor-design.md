# PR1 设计思考：公共 Load More 逻辑抽取

## 现象

- `SmartRecyclerView` 和 `SwipeRecyclerView` 都在各自类中维护了完整的滚动触发加载更多逻辑。
- 这部分逻辑高度重复，但刷新容器层面的实现又并不完全相同。

## 初步观察

- 真正重复的是“滚动 load more 状态机”，不是“刷新控件接入行为”。
- `SmartV2RecyclerView` 采用的是另一套 load more 触发路径，不适合一起纳入第一步重构。

## 问题拆解

1. 公共逻辑应抽成工具类、委托类，还是组合 coordinator？
2. `SmartV2RecyclerView` 是否应该一起处理？
3. 本次是否应该顺手统一更多状态逻辑？

## 当前判断

- 组合 coordinator 更适合 PR1。
- `SmartV2RecyclerView` 暂不纳入，避免扩大变更面。
- 这次只收敛重复，不额外扩需求。

## 决策

- 在 `recyclerview_core` 中新增 `ScrollLoadMoreCoordinator`
- 把滚动监听、load more 判定、相关状态收尾收进去
- 让 `SmartRecyclerView` 和 `SwipeRecyclerView` 作为宿主类进行组合接入
- 将 `finishLoadMore(success, noMoreData)` 收敛为 `finishLoadMore(result: LoadMoreResult)` 作为推荐接口

## 取舍原因

- 共享的是行为片段，不是稳定的继承层级。
- 组合比继承更利于后续扩展和单测。
- 先不碰 `ISmartRecyclerView`，可以把风险控制在最小范围。
- `SmartV2RecyclerView` 后续若要部分复用，也更容易接入组合组件。
- coordinator 不应该理解 refresh 语义，`finishRefresh(success, footerState)` 需要由宿主拆解后再同步 load more 状态。
- refresh 不拆出去的核心原因是：主流宿主 UI 容器通常已经具备自己的 refresh 能力，真正缺少且需要横向补齐的是 load more 能力。
- 因此 PR1 的抽象目标不是统一 refresh，而是为不同宿主补一个稳定的 load more 协调层。
- `finishLoadMore(success, noMoreData)` 的布尔表达允许脏组合，语义不够强，适合作为兼容接口保留，但不适合继续作为主接口扩散。

## 待确认

- `StaggeredGridLayoutManager` 的最后可见项判断是否需要严格保持旧行为
- 后续是否把 `SmartV2RecyclerView` 作为推荐实现

## 下一步

- 进入代码实现
- 先完成 coordinator 抽取
- 再做最小化编译验证
