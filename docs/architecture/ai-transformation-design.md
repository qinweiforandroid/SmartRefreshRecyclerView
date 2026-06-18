# SmartRefreshRecyclerView AI 改造设计方案

## 1. 背景

当前项目是一个多模块 Android RecyclerView 能力库，主要包含以下模块：

- `recyclerview_core`
- `recyclerview_smartrefreshlayout`
- `recyclerview_swiperefresh`
- `app`

项目已经具备可用能力，但随着功能增长，出现了重复逻辑、职责耦合、文档与现状偏移等问题。为了提升维护效率、降低改动风险，并让后续能够持续借助 AI 协助开发，需要先建立一套清晰的改造方案。

## 2. 改造目标

- 保持现有功能可用，优先做低风险演进。
- 收敛重复实现，降低维护成本。
- 补齐文档与思路沉淀，方便后续持续迭代。
- 为后续测试完善、API 收敛、现代化升级预留空间。

## 3. 当前问题

### 3.1 重复逻辑

- `SmartRecyclerView` 和 `SwipeRecyclerView` 中存在高度相似的滚动触发加载更多逻辑。
- 状态判断、末尾检测、错误态与无更多数据拦截逻辑重复。

### 3.2 职责耦合

- `SmartListCompat` 同时承担模板、分页、footer 状态控制等职责。
- 不同层次的关注点混杂在一起，后续扩展和测试都不够顺手。

### 3.3 文档滞后

- `README.md` 中的接入说明与当前工程结构、推荐使用方式存在一定偏移。
- 新接手的人难以快速理解推荐入口与模块关系。

### 3.4 演进路线不清晰

- `SmartRecyclerView`、`SmartV2RecyclerView`、`SwipeRecyclerView` 的定位还不够清楚。
- 兼容实现和推荐实现缺少明确边界。

## 4. 改造原则

- 小步迭代，避免一次性大改。
- 优先内部抽象，尽量不破坏现有公开 API。
- 先收敛重复，再补文档，再补测试，最后做更深层结构演进。
- 方案发生变化时，先更新设计文档，再执行代码或后续任务。
- 所有重要决策都沉淀为文档，方便 AI 和人工协作。

## 5. 目标结构

### 5.1 模块职责

- `recyclerview_core`
  - 通用状态机
  - 分页模型
  - load more 通用触发策略
  - 模板能力与基础抽象

- `recyclerview_swiperefresh`
  - `SwipeRefreshLayout` 接入层
  - 与具体刷新控件相关的 UI 行为

- `recyclerview_smartrefreshlayout`
  - `SmartRefreshLayout` 接入层
  - 与具体刷新控件相关的 UI 行为

- `app`
  - 示例工程
  - 用于展示典型接入姿势与交互场景

## 6. 分阶段方案

### 阶段一：内部去重

- 以组合方式抽取公共滚动加载更多逻辑到 `recyclerview_core`
- 保持宿主 UI 容器各自的 refresh 能力，不做 refresh 大一统抽象
- 新增 `ScrollLoadMoreCoordinator`，统一 `scroll + load more` 行为
- 将 `finishLoadMore(success, noMoreData)` 收敛为结果型接口 `LoadMoreResult`
- 保持现有外部调用方式基本兼容

### 阶段二：文档和示例整理

- 重写 README 的模块说明和接入说明
- 明确推荐使用入口
- 用 sample 展示空态、错误、重试、无更多数据等典型场景

### 阶段三：API 收敛

- 明确 `SmartRecyclerView`、`SmartV2RecyclerView`、`SwipeRecyclerView` 的定位
- 评估是否需要兼容层和推荐层并存
- 将弱语义的布尔参数接口逐步收敛为明确结果型接口
- 为未来迁移方案做准备

### 阶段四：测试补齐

- 优先补核心状态流转测试
- 补分页边界行为测试
- 提高后续重构的安全性

## 7. 第一批建议任务

### PR1

- 抽取公共 load more 逻辑
- 引入 `ScrollLoadMoreCoordinator`
- 引入 `LoadMoreResult` 并保留旧接口兼容

### PR2

- 对外接入体验整理
- 重写 README，统一推荐接入路径
- 补充 sample 映射和迁移说明

### PR3

- 拆分 `SmartListCompat` 的职责

### PR4

- 补核心逻辑测试

## 8. 风险与关注点

- 触发加载更多的边界条件是否与现有行为完全一致
- 刷新和加载更多状态切换是否会出现回归
- sample 中的演示行为是否依赖当前实现细节
- 兼容旧 API 时是否需要补更多迁移说明

## 9. 当前已落地的接口收敛方向

### 9.1 Load More 结果表达

加载更多完成接口不再推荐继续使用：

- `finishLoadMore(success: Boolean, noMoreData: Boolean)`

当前已新增更明确的结果型表达：

- `LoadMoreResult.SUCCESS`
- `LoadMoreResult.NO_MORE`
- `LoadMoreResult.ERROR`

推荐调用方式：

```kotlin
smart.finishLoadMore(LoadMoreResult.SUCCESS)
smart.finishLoadMore(LoadMoreResult.NO_MORE)
smart.finishLoadMore(LoadMoreResult.ERROR)
```

### 9.2 兼容策略

- 保留旧布尔接口，避免一次性破坏历史调用
- 旧接口内部统一转发到 `LoadMoreResult`
- 新代码与 sample 优先使用结果型接口
- 后续 README 与迁移文档统一以结果型接口为主

## 10. 当前已落地的阶段一结论

### 10.1 去重方式

阶段一并没有采用继承基类的方式去重，而是采用组合方案：

- 宿主类：`SmartRecyclerView`、`SwipeRecyclerView`
- 公共组件：`ScrollLoadMoreCoordinator`

### 10.2 职责边界

- 宿主管理 refresh
- coordinator 管理 scroll + load more
- `finishRefresh(success, footerState)` 继续由宿主解释
- load more 完成结果优先使用 `LoadMoreResult`

### 10.3 设计原因

- 主流 refresh 容器本身已经有 refresh 能力
- 当前项目更缺少的是横向可复用的 load more 能力
- 组合比继承更适合后续扩展、定位问题和补测试

## 11. 后续记录方式

后续每次设计变化、问题分析、方案取舍，都优先记录到：

- `docs/architecture/`
- `docs/logs/`

以便形成持续可追踪的项目知识库。
