# SmartRefreshRecyclerView

一个面向 Android 列表场景的刷新与加载更多能力库，提供：

- 下拉刷新
- 上拉加载更多
- 线性列表 / 网格 / 瀑布流支持
- 可定制的 load more UI
- `SmartListCompat` 模板能力
- drag / swipe 示例

## 模块说明

当前项目主要包含以下模块：

- `recyclerview-core`
  - 核心抽象、模板能力、load more 状态与公共逻辑

- `recyclerview-swiperefresh`
  - 基于 `SwipeRefreshLayout` 的实现
  - 适合大多数普通列表刷新场景

- `recyclerview-smartrefreshlayout`
  - 基于 `SmartRefreshLayout` 的实现
  - 适合项目已经使用该刷新体系的场景

- `app`
  - sample 工程
  - 用于演示不同接入方式与交互场景

## 当前仓库基线

当前仓库源码的构建基线为：

- `minSdk = 23`
- `compileSdk = 36`
- `targetSdk = 36`
- `Java = 17`
- `Kotlin = 2.2.x`

## 推荐接入路线

如果你是第一次接这个库，建议按下面顺序选：

1. 普通列表刷新 + 加载更多
   先看 `SwipeRecyclerView`

2. 想减少样板代码、快速搭列表
   先看 `SmartListCompat + SwipeRecyclerView`

3. 项目已经在使用 `SmartRefreshLayout`
   再看 `SmartRecyclerView` 或 `SmartV2RecyclerView`

## 快速开始

## 发布到 JitPack

仓库内置了一键发布脚本：

```bash
./scripts/release-jitpack.sh 4.2.0701
```

这个脚本会按顺序完成：

- 校验当前 Git 工作区是否干净
- 校验目标 tag 是否已存在
- 执行 3 个 library module 的 `assembleRelease`
- 创建并推送 Git tag 到 `origin`
- 输出 JitPack 构建页和最终依赖坐标

如果你只想跳过本地构建检查：

```bash
./scripts/release-jitpack.sh 4.2.0701 --skip-checks
```

### 1. 添加仓库

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. 添加依赖

注意：

- `recyclerview-core` 需要显式依赖
- `recyclerview-swiperefresh` / `recyclerview-smartrefreshlayout` 不应视为可单独替代 core
- 当前仓库中的本地发布版本名与对外 JitPack tag 不是同一套语义
- README 以下示例按 JitPack 接入方式说明

以当前仓库中使用的 release tag 风格为例：

```groovy
def smartRvVersion = "4.1.0324" // 请替换为你要接入的 release tag

dependencies {
    implementation "com.github.qinweiforandroid.SmartRefreshRecyclerView:recyclerview-core:$smartRvVersion"
    implementation "com.github.qinweiforandroid.SmartRefreshRecyclerView:recyclerview-swiperefresh:$smartRvVersion"
}
```

如果你使用 `SmartRefreshLayout` 体系：

```groovy
def smartRvVersion = "4.1.0324" // 请替换为你要接入的 release tag

dependencies {
    implementation "com.github.qinweiforandroid.SmartRefreshRecyclerView:recyclerview-core:$smartRvVersion"
    implementation "com.github.qinweiforandroid.SmartRefreshRecyclerView:recyclerview-smartrefreshlayout:$smartRvVersion"
}
```

## 基础用法：SwipeRecyclerView

### 布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/mSwipeRefreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/mRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
```

### 代码

```kotlin
private lateinit var smart: ISmartRecyclerView
private val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("create your item view holder")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit

    override fun getItemCount(): Int = 0
}

fun setUpView() {
    val recyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = adapter

    val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.mSwipeRefreshLayout)

    smart = SwipeRecyclerView(recyclerView, swipeRefreshLayout)
        .setRefreshEnable(true)
        .setLoadMoreEnable(true)
        .setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                refresh()
            }
        })
        .setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                loadMore()
            }

            override fun onStateChanged(state: State) {
                // 更新 load more UI
            }
        })

    smart.setRefreshing(true)
}
```

## 推荐的 load more 完成方式

当前推荐使用 load more 状态接口：

```kotlin
smart.setLoadMoreState(LoadMoreState.SUCCESS)
smart.setLoadMoreState(LoadMoreState.HIDDEN)
smart.setLoadMoreState(LoadMoreState.NO_MORE)
smart.setLoadMoreState(LoadMoreState.ERROR)
```

含义如下：

- `SUCCESS`
  - 加载成功，后续还有更多数据

- `NO_MORE`
  - 加载成功，但没有更多数据

- `ERROR`
  - 加载失败

- `HIDDEN`
  - 隐藏 load more 视图

`LoadMoreState` 当前直接表达 load more UI 状态：

- `SUCCESS`
- `HIDDEN`
- `NO_MORE`
- `ERROR`

像 `SmartRefreshLayout` 这类第三方控件需要的布尔参数映射，交由具体实现类内部处理。

## SmartListCompat

如果你不想反复写 adapter、modules 和 load more footer 逻辑，可以直接用 `SmartListCompat`。

更接近实际项目的推荐写法是：

```kotlin
val smart = SwipeRecyclerView(bind.mRecyclerView, bind.mSwipeRefreshLayout)
val loadMore = DefaultLoadMore()

val list = object : SmartListCompat<String>(smart) {
    override fun onCreateBaseViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return object : BaseViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
        ) {
            override fun initData(position: Int) {
                val label = itemView as TextView
                label.text = modules[position]
            }
        }
    }
}

list.setLoadMoreView(loadMore)
    .setRefreshEnable(true)
    .setLoadMoreEnable(true)
    .setOnRefreshListener(object : OnRefreshListener {
        override fun onRefresh() {
            refresh()
        }
    })
    .setOnLoadMoreListener(object : OnLoadMoreListener {
        override fun onLoadMore() {
            loadMore()
        }
    })
```

如果你已经有分页对象或 ViewModel，可以继续结合：

```kotlin
viewModel.result.observe(this, list::submitPageData)
```

错误场景可以直接使用：

```kotlin
list.submitPageError()
```

## SmartRefreshLayout 路线

如果你的项目已经使用 `SmartRefreshLayout`，可以选择：

- `SmartRecyclerView`
  - 宿主保留自己的 refresh 能力
  - 内部复用公共的 `ScrollLoadMoreCoordinator`

- `SmartV2RecyclerView`
  - 使用 `SmartRefreshLayout` 自带的 load more 机制

建议：

- 如果你希望和 `SwipeRecyclerView` 保持一致的项目级 load more 语义，优先看 `SmartRecyclerView`
- 如果你已经依赖并习惯 `SmartRefreshLayout` 原生 load more 生命周期，再考虑 `SmartV2RecyclerView`

## Sample 对照表

当前 sample 页面与能力对应关系如下：

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
  - 分页真实示例

- `DragSwipeListActivity`
  - 拖拽 / 侧滑交互示例

- `ChatActivity`
  - 多类型列表示例

- `StickHeaderListActivity`
  - 吸顶效果示例

- `ViewPagerActivity`
  - ViewPager 风格滑动示例

## 迁移说明

当前统一使用：

```kotlin
setLoadMoreState(LoadMoreState.SUCCESS)
setLoadMoreState(LoadMoreState.HIDDEN)
setLoadMoreState(LoadMoreState.NO_MORE)
setLoadMoreState(LoadMoreState.ERROR)
```

## 分页状态

`IPage` 当前会输出分页状态以驱动 UI：

- `PageAction`
  - `REFRESH`
  - `LOAD_MORE`

- `PagePhase`
  - `IDLE`
  - `LOADING`
  - `ERROR`

- `PageState`
  - 当前行为
  - 当前阶段
  - 是否还有下一页
  - 当前是否为第一页请求

推荐分页事件入口：

```kotlin
page.onLoadFirstPage()
page.onLoadNextPage()
page.onLoadSuccess(hasNextPage = true)
page.onLoadFailure()
```

## 文档

项目内部设计记录见：

- `docs/architecture/`
- `docs/logs/`
