# 求实BBS-Android

## 应用介绍
  天津大学求实BBS社区客户端



## 项目结构

#### 关于模块
> 采用类模块化-功能分包结构，所有功能模块统一依赖于commons，包括网络层，基础UI层，本地数据库，统一使用commons所提供的实现。添加模块时，可直接创建模块文件夹，继承/实现commons提供的基类/接口



#### 关于bugly

> 项目使用了bugly的bug上报模块



#### 快速添加模块


> 添加任一功能模块时都需要在commons同级目录下新建对应package，任意两个package（功能模块）不应该相互依赖，
> 功能模块下仍可以创建所需的子package

##### 新建

- Activity : 提供对应的Contract接口；Presenter类应继承RxPresenter并实现Contract接口；可见的Activity应列入对应的manifests，继承BaseActivity，实现Contract.View接口 例如：
```kotlin
PersonContract.kt
interface PersonContract {

    interface Presenter : BasePresenter{
        fun getPersonInfo(uid:Int)
    }

    interface View : BaseView {
        fun onPersonInfoSuccess(person : PeopleModel)
        fun onLoadFailed(info : String)
        fun onThreadInfoSuccess(thread: List<ThreadModel.ThreadBean>)
    }
}

PersonPresenter.kt
class PersonPresenter(view: PersonContract.View?) : RxPresenter(), PersonContract.Presenter {

    private var mView : PersonContract.View? = view

    override fun getPersonInfo(uid: Int) {
        val connectObservable = sHttpClient.getUserInfo(uid)
                .map(ResponseTransformer())
                .publish()

        val peopleObserver: SimpleObserver<PeopleModel> = object : SimpleObserver<PeopleModel>() {
            override fun _onError(msg: String) {
                mView?.onLoadFailed(msg)
            }

            override fun _onNext(t: PeopleModel) {
                mView?.onPersonInfoSuccess(t)
            }

        }

        addSubscribe(connectObservable.refCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(peopleObserver))

        val threadObserver : SimpleObserver<ThreadModel.ThreadBean> = object : SimpleObserver<ThreadModel.ThreadBean>() {
            val list = mutableListOf<ThreadModel.ThreadBean>()
            override fun _onError(msg: String) {
                mView?.onLoadFailed(msg)
            }

            override fun _onNext(t: ThreadModel.ThreadBean) {
                list.add(t)
            }

            override fun onComplete() {
                mView?.onThreadInfoSuccess(list)
            }
        }

        //用于RxJava的生命周期管理
        addSubscribe(connectObservable.refCount()
                .map { it.recent }
                .flatMap { Observable.fromIterable(it) }
                .map { it.id }
                .concatMap { sHttpClient.getThread(it,0) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map (ResponseTransformer())
                .map { it.thread }
                .subscribeWith(threadObserver))

    }

}

PersonActivity.kt
class PersonActivity : BaseActivity() , PersonContract.View {

    //在BaseActivity的onCreate()方法中会执行setContentView(getLayoutResourceId());
    override fun getLayoutResourceId() = R.layout.activity_person

    //若Activity页面没有Toolbar，返回null
    override fun getToolbarView(): Toolbar? = null

    //用于RxJava生命周期的管理
    override fun getPresenter(): BasePresenter = mPresenter

    private val mPresenter = PersonPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
       //...
    }

    override fun onPersonInfoSuccess(person: PeopleModel) {
        //...
    }

    override fun onLoadFailed(info: String) {
        //...
    }

    override fun onThreadInfoSuccess(thread: List<ThreadModel.ThreadBean>) {
        //...
    }
}
```

- Fragment（作为容器）: 容器Fragment需要继承SimpleFragment重写对应函数
```java
public class MainFragment extends SimpleFragment {
    //...
}
```

- Fragment（作为内容）: 作为内容提供者，需要继承BaseFragment, 并重写相应函数，类似与Activity需要提供Presenter，Contract

- Network ：之前提到了所有的Presenter类都继承了RxPresenter类，而RxPresenter持有了一个sHttpClient的静态实例，调用某一现有的网络接口时，可直接在Presenter类中使用```sHttpClient.doLogin()```；若要增加新网络请求方法，先在```commons.rx```包下BaseApi类中添加Retrofit规范的方法，如：
```java
    @GET("index/event")
    Observable<EventBean> getEvent();
```
之后在同包下的RxDoHttpClient类下增加对应供外界调用的的静态方法
```java
    public Observable<EventBean> getEvent() {
        return mApi.getEvent();
    }
```



##### RecyclerView DSL

>  本项目部分RecyclerView采用了RecyclerViewDSL，参考资料请点击[链接](https://github.com/life2015/RecyclerViewDSL)
