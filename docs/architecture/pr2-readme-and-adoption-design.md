# PR2 详细设计：README 与对外接入体验整理

## 1. 背景

在 PR1 完成后，项目内部的 load more 抽象和接口语义已经完成第一轮收敛：

- 使用组合方式引入 `ScrollLoadMoreCoordinator`
- 明确宿主 refresh / coordinator load more 的职责边界
- 引入 `LoadMoreResult`

但当前对外文档仍存在明显滞后，主要体现在 README 与 sample 的表达未能准确反映当前工程状态和推荐接入方式。

因此 PR2 的重点不再是内部结构调整，而是对外接入体验整理。

## 2. PR2 目标

- 重写 README，使其与当前模块结构和推荐用法一致
- 明确不同接入路径的适用场景
- 补充 sample 对照关系，降低使用者理解成本
- 将 `LoadMoreResult` 作为推荐接口正式写入文档

## 3. 非目标

本次 PR 不处理以下内容：

- 不继续做内部架构重构
- 不修改 `MainActivity` 的 sample 展示结构
- 不增加新的功能模块
- 不处理测试补齐
- 不调整发布脚本

## 4. 当前问题

### 4.1 README 与工程现状不一致

当前 README 仍保留较老的依赖示例和接入写法：

- 版本号与当前工程不一致
- 模块说明不够准确
- 示例代码没有体现 `LoadMoreResult`
- 对推荐路径和兼容路径没有明确说明

### 4.2 接入路径过多但缺少引导

当前项目主要有以下路径：

- `SwipeRecyclerView`
- `SmartRecyclerView`
- `SmartV2RecyclerView`
- `SmartListCompat`

这些能力本身没有问题，但如果 README 不给出“推荐怎么选”，使用者会难以判断。

### 4.3 sample 存在但缺少映射说明

sample 覆盖面其实已经不错，但 README 没有把页面和能力对应起来，导致使用者需要自己翻源码才能理解。

## 5. PR2 核心原则

- README 优先服务“第一次接入的人”
- 推荐路径要明确，不写模糊建议
- 文档描述以当前工程真实状态为准
- 兼容接口可以提到，但不作为主路径展示
- 先讲怎么选，再讲怎么用

## 6. 推荐对外表达

### 6.1 模块说明

README 应明确说明：

- `recyclerview-core`
  - 核心抽象与模板能力

- `recyclerview-swiperefresh`
  - 基于 `SwipeRefreshLayout` 的实现
  - 适合大多数普通列表刷新场景

- `recyclerview-smartrefreshlayout`
  - 基于 `SmartRefreshLayout` 的实现
  - 适合项目已使用该刷新体系的场景

### 6.2 推荐接入路径

建议 README 明确推荐顺序：

1. 普通列表场景优先看 `SwipeRecyclerView`
2. 需要更高层模板能力时优先看 `SmartListCompat`
3. 项目已经使用 `SmartRefreshLayout` 时，再看 `SmartRecyclerView` / `SmartV2RecyclerView`

### 6.3 Load More 接口说明

README 应明确区分：

- 推荐接口
  - `setLoadMoreState(state: LoadMoreState)`

并明确说明 `LoadMoreState` 直接表达 footer UI 状态，不承载第三方控件的布尔参数。

## 7. README 建议结构

建议 README 重构为以下结构：

1. 项目简介
2. 能力概览
3. 模块说明
4. 推荐接入路线
5. 快速开始
6. `LoadMoreResult` 说明
7. `SmartListCompat` 说明
8. sample 对照表
9. 迁移说明

## 8. sample 对照表建议

建议 README 中增加如下映射：

- `SwipeRecyclerViewActivity`
  - 基础 `SwipeRecyclerView`

- `SwipeCompatActivity`
  - `SmartListCompat + SwipeRecyclerView`

- `SmartCompatActivity`
  - `SmartListCompat + SmartRecyclerView`

- `SmartV2RecyclerViewActivity`
  - 基础 `SmartV2RecyclerView`

- `SmartV2CompatActivity`
  - `SmartListCompat + SmartV2RecyclerView`

- `ArticleListActivity`
  - 分页数据示例

- `DragSwipeListActivity`
  - 拖拽 / 侧滑交互示例

## 9. 迁移说明建议

README 中建议补充：

- 推荐统一使用 `setLoadMoreState(LoadMoreState.*)`
- 如果只需要基础列表刷新与加载更多，优先采用 `SwipeRecyclerView`

## 10. 预计改动文件

### 修改

- `README.md`

### 新增

- `docs/architecture/pr2-readme-and-adoption-design.md`
- `docs/logs/2026-06-18-pr2-readme-and-adoption.md`

## 11. 完成定义

满足以下条件即可视为 PR2 完成：

- README 与当前工程结构一致
- 推荐接入路径明确
- `LoadMoreResult` 已正式写入文档
- sample 页面与能力映射可直接查阅
