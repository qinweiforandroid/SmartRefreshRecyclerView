# SmartRefreshRecyclerView
是一个列表显示工具框架

* 支持下拉刷新
* 支持加载更多
* 支持线性列表
* 支持网格列表
* 支持瀑布流列表
* 可定制加载更多的样式

## 1、How To Use

**Step 1.** Add the JitPack repository to your build file

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2.** Add the dependency [![](https://jitpack.io/v/qinweiforandroid/SmartRefreshRecyclerView.svg)](https://jitpack.io/#qinweiforandroid/SmartRefreshRecyclerView)

```groovy
//核心组件（必须）
api 'com.github.qinweiforandroid.SmartRefreshRecyclerView:recyclerview-core:3.0.0608'
//使用smartrefreshlayout库实现的下拉刷新
api 'com.github.qinweiforandroid.SmartRefreshRecyclerView:recyclerview-smartrefreshlayout:3.0.0608'
//使用swiperefreshlayout库实现的下拉刷新（推荐）
api 'com.github.qinweiforandroid.SmartRefreshRecyclerView:recyclerview-swiperefresh:3.0.0608'
//用到加载更多需要引入
api 'com.github.qinweiforandroid.SmartRefreshRecyclerView:recyclerview-footer:3.0.0608'
```

以recyclerview-swiperefresh为例

### 1.1、xml引入布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.swiperefreshlayout.widget.SwipeRefreshLayout 	xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/mSwipeRefreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/mRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
```

### 1.2、代码实现

```kotlin
fun setUpView(){
  //1.配置RecyclerView
  val mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
  mRecyclerView.layoutManager = LinearLayoutManager(this)
  adapter = ListAdapter()
  mRecyclerView.adapter = adapter

  //2.配置SwipeRefreshLayout
  val mSwipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.mSwipeRefreshLayout)
  //3.配置SwipeRecyclerView
  val smart = SwipeRecyclerView(mRecyclerView, mSwipeRefreshLayout)
  .setRefreshEnable(true)//启用下拉刷新
  .setLoadMoreEnable(true)//启用加载更多
  .setOnRefreshListener(object : OnRefreshListener {
    override fun onRefresh() {
      //下拉刷新回调
    }
  })
  .setOnLoadMoreListener(object : OnLoadMoreListener {
    override fun onLoadMore() {
      //滑动到列表底部触发
    }

    override fun onStateChanged(state: State) {
     //处理加载更多UI显示
    }
  })
  //触发下拉刷新
  smart.autoRefresh()
}
```

ListAdapter

```kotlin
internal inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return modules.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : BaseViewHolder(LayoutInflater.from(this@SwipeRefreshLayout1Activity).inflate(android.R.layout.simple_list_item_1, parent, false)) {
            private val label: TextView = itemView as TextView
            override fun initData(position: Int) {
                val text = modules[position]
                label.text = text
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        (holder as BaseViewHolder).initData(position, payloads)
    }
}

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun initData(position: Int)
    open fun initData(position: Int, payloads: List<Any>) {
        initData(position)
    }
}
```



## 2、扩展的一些工具

### 2.1、`BaseListComponent<T>`

* 不用自己定义数据源
* 不用自己写adapter

```kotlin
val modules = ArrayList<T>()
val adapter = object : BaseListAdapter(){}
```

**引入布局**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/mRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**代码实现**

```kotlin
class Recycler2Activity : AppCompatActivity() {
    private lateinit var bind: RecyclerviewLayoutActivityBinding
    private lateinit var listComponent: BaseListComponent<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = RecyclerviewLayoutActivityBinding.inflate(layoutInflater)
        setContentView(bind.root)
        listComponent = object : BaseListComponent<String>(bind.mRecyclerView) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return object : BaseViewHolder(
                    LayoutInflater.from(this@Recycler2Activity)
                        .inflate(android.R.layout.simple_list_item_1, parent, false)
                ) {
                    override fun initData(position: Int) {
                        val label: TextView = itemView as TextView
                        val text = modules[position]
                        label.text = text
                    }
                }
            }
        }
        listComponent.setLayoutManager(LinearLayoutManager(this))
        for (i in 0..19) {
          listComponent.modules.add("" + i)
        }
        listComponent.adapter.notifyDataSetChanged()
    }
}
```

### 2.2、`SwipeListComponent<T>`

极力推荐使用此组建来实现列表的需求

* 对SwipeRecyclerView二次封装
* 支持动态配置加载更多状态
* 下拉刷新，上拉加载更多

**引入布局**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.swiperefreshlayout.widget.SwipeRefreshLayout 	xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/mSwipeRefreshLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/mRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
```

**代码实现**

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    bind = SwipeRefreshLayoutActivityBinding.inflate(layoutInflater)
    setContentView(bind.root)
    val mList =
        object : SwipeListComponent<String>(bind.mRecyclerView, bind.mSwipeRefreshLayout) {
            override fun onCreateBaseViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): BaseViewHolder {
                return Holder(
                    LayoutInflater.from(this@SwipeComponentActivity)
                        .inflate(android.R.layout.simple_list_item_1, parent, false)
                )
            }

            inner class Holder(itemView: View) : BaseViewHolder(itemView) {
                override fun initData(position: Int) {
                    val label: TextView = itemView as TextView
                    val text = mList.modules[position]
                    label.text = text
                }
            }
        }
  //构建加载更多UI组件（这里可自行扩展样式）
    val loadMore = DefaultLoadMore()
        .setEmptyHint("我是有底线的……")
        .setFailHint("哎呦，加载失败了")
        .setLoadingHint("努力加载中")
  //SwipeListComponent 配置 loadmore
    mList.supportLoadMore(loadMore, object : OnLoadMoreListener {
        override fun onLoadMore() {

        }
    })
    mList.setLayoutManager(MyLinearLayoutManager(this))
        .setRefreshEnable(true)
        .setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {

            }
        }).autoRefresh()
}
```

## 3、联系我

有什么问题可以加QQ（备注来源）或发邮件沟通

* QQ：435231045
* 邮箱：qinwei_it@163.com