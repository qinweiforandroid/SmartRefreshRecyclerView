# PR3 设计思考：SmartListCompat 职责拆分

## 现象

- `SmartListCompat` 越来越像一个“大而全”的组件入口。
- 它既做模板，又做 footer，又做分页落地，还做宿主桥接。

## 初步观察

- 最重的两块职责不是模板本身，而是：
  - footer / retry / load more UI
  - 分页结果落地

- 如果继续把这些逻辑留在 `SmartListCompat` 内部，后续每加一个能力都会继续膨胀。

## 问题拆解

1. 是否应该直接移除 `SmartListCompat`？
2. 是否应该让业务方自己组合多个对象？
3. 新拆分对象该用什么命名？

## 当前判断

- 不应该移除 `SmartListCompat`
- 不应该让业务方承担新的拼装复杂度
- 应该保留 facade，只做内部职责拆分
- 新对象应该使用 `Delegate`，而不是 `UseCase`

## 决策

- `SmartListCompat` 保留 facade
- 先拆 `LoadMoreFooterDelegate`
- 再拆 `PagingDataDelegate`
- public API 命名收敛放到后续阶段做

## 取舍原因

- facade 还保留着较高易用性
- footer 逻辑是最重的 UI 耦合点，优先拆收益最大
- 分页结果落地逻辑是第二重的耦合点
- `UseCase` 会把 UI 内部职责误导成领域层对象

## 待确认

- `page` 是否在拆分时同步从 `lateinit` 改为更安全的形态
- `setUpPage` / `notifyDataChanged` / `notifyError` 是否在后续命名收敛中统一升级

## 下一步

- 已完成 `LoadMoreFooterDelegate`
- 已完成 `PagingDataDelegate`
- 已完成首轮 facade 命名收敛
- 下一步可以视需要继续做 sample 全量迁移或补 PR3 说明文档
