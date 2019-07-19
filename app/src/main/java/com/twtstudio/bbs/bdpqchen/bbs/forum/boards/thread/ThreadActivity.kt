package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.twtstudio.bbs.bdpqchen.bbs.R
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseActivity
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.helper.RecyclerViewItemDecoration
import com.twtstudio.bbs.bdpqchen.bbs.commons.model.BaseModel
import com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.*
import com.twtstudio.bbs.bdpqchen.bbs.commons.tools.MatcherTool
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.*
import com.twtstudio.bbs.bdpqchen.bbs.commons.view.BottomToolsView
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.*
import com.twtstudio.bbs.bdpqchen.bbs.individual.release.EndlessRecyclerOnScrollListener
import com.zhihu.matisse.Matisse
import kotterknife.bindView

class ThreadActivity: BaseActivity(),ThreadContract.View,PostAdapter.OnPostClickListener, View.OnClickListener{
    //status bar 颜色
    val mToolbar:Toolbar by bindView(R.id.toolbar_thread)
    val mAppbar:AppBarLayout by bindView(R.id.app_bar)
    private val mRvThreadPost: RecyclerView by bindView(R.id.rv_thread_post)
    private val mPbThreadLoading:ProgressBar by bindView(R.id.pb_thread_loading)
    private val mSrlThreadList:SwipeRefreshLayout by bindView(R.id.srl_thread_list)
    val mEtComment:EditText by bindView(R.id.et_comment)
    private val mLlComment:LinearLayout by bindView(R.id.ll_comment)
    val mIvCommentSend:ImageView by bindView(R.id.iv_comment_send)
    val mIvCommentOut:ImageView by bindView(R.id.iv_comment_out)
    private val mTvDynamicHint:TextView by bindView(R.id.tv_dynamic_hint)
    private val mCbAnonymousComment:AppCompatCheckBox by bindView(R.id.cb_anonymous_comment)
    val mToolbarTitleThread:TextView by bindView(R.id.toolbar_title_thread)
    val mToolbarTitleBoard:TextView by bindView(R.id.toolbar_title_board)
    private val mIvSelectImage:ImageView by bindView(R.id.iv_select_image)
    private val mIvOpenEditor:ImageView by bindView(R.id.iv_open_editor)
    private val mTvAtUser:TextView by bindView(R.id.tv_at_user)
    private val mBottomTools by bindView<BottomToolsView>(R.id.bottom_tools)
    private var mThreadTitle = ""
    private var mThreadId = 0
    private var mAdapter: PostAdapter? = null
    private var mComment = ""
    private var mProgress: MaterialDialog? = null
    private var mRefreshing = false
    private var postPosition = 0
    private var mReplyId = 0
    private var mIsAnonymous = false
    private var mCanAnonymous = false
    private var mBoardId = 0
    private var mBoardName = ""
    private lateinit var mLayoutManager: LinearLayoutManager
    private var lastVisibleItemPosition = 0
    private var mPage = 0
    private var mIsLoadingMore: Boolean = false
    private var mIsCommentAfter = false
    private var mPostCount = 0
    private var showingThreadTitle = false
    private var mIsShowingCommentDialog = false
    private var showingBoardName = true
    private var mIsFindingFloor = false
    private var mFindingPage = 0
    private var mFindingFloor = 0
    private var mInputFloor = 0
    private var mPossibleIndex = 0
    private var mIsFindEnd = false
    private var mFabShowing = true
    private var isLastPage = false
    private var mIsStared = false
    private var mIsLiked = false
    private lateinit var mPresenter: ThreadPresenter
    private val STARTHREAD:Int = 0
    val SHARETEXT:Int = 1
    val TOTOP:Int = 2
    val FINISHIME:Int = 3
    val JUMPFLOOR:Int = 4
    val LIKE:Int = 5
    private val SHOWCOMMENTINPUT:Int = 6

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_thread
    }

    override fun getToolbarView(): Toolbar {
        return mToolbar
    }

    override fun getPresenter(): BasePresenter {
        return mPresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = Color.rgb(255,255,255)
        mPresenter = ThreadPresenter(this)
        mThreadId = intent.getIntExtra(INTENT_THREAD_ID, 0)
        mFindingFloor =intent.getIntExtra(INTENT_THREAD_FLOOR, 0)
        mThreadTitle =intent.getStringExtra(INTENT_THREAD_TITLE)
        mBoardName = intent.getStringExtra(INTENT_BOARD_TITLE)
        mBoardId = intent.getIntExtra(INTENT_BOARD_ID, 0)
        mContext = this //BaseActivity里的mcontext
        super.onCreate(savedInstanceState)
        mAdapter = PostAdapter(mContext,this)
        mLayoutManager =LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        mRvThreadPost.animation = null
        mRvThreadPost.addItemDecoration(RecyclerViewItemDecoration(5))
        mRvThreadPost.layoutManager = mLayoutManager
        mRvThreadPost.adapter = mAdapter
        mRvThreadPost.addOnScrollListener(object : EndlessRecyclerOnScrollListener(mLayoutManager) {
            override fun onLoadMore() {
                mPage++
                mIsLoadingMore = true
                mPresenter.getThread(mThreadId, mPage)
            }
        })
        mRvThreadPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition()
                if (!mIsShowingCommentDialog) {
                    if (dy > 0 && mFabShowing) {
                        mFabShowing = false
                        hideFab()
                    } else if (dy < 0 && !mFabShowing) {
                        mFabShowing = true
                        showFab()
                    }
                }
                val d = 300
                if (lastVisibleItemPosition != 0 && mLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                    if (!showingThreadTitle) {
                        //出现标题
                        YoYo.with(Techniques.SlideOutUp).duration(d.toLong()).playOn(mToolbarTitleBoard)
                        YoYo.with(Techniques.SlideInUp).duration(d.toLong()).playOn(mToolbarTitleThread)
                        mToolbarTitleThread.text = mThreadTitle
                        showingThreadTitle = true
                        showingBoardName = false
                    }
                } else {
                    //出现板块名称
                    if (!showingBoardName) {
                        YoYo.with(Techniques.SlideOutUp).duration(d.toLong()).playOn(mToolbarTitleThread)
                        mToolbarTitleBoard.text = TextUtil.getLinkHtml(mBoardName)
                        YoYo.with(Techniques.SlideInUp).duration(d.toLong()).playOn(mToolbarTitleBoard)
                        showingBoardName = true
                        showingThreadTitle = false
                    }
                }
            }
        })

        mToolbarTitleThread.setOnClickListener { toTop() }
        mEtComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (mEtComment.text.toString().isNotEmpty()) {
                    mIvCommentOut.visibility = View.GONE
                    mIvCommentSend.visibility = View.VISIBLE
                } else {
                    mIvCommentOut.visibility = View.VISIBLE
                    mIvCommentSend.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
        mIvCommentOut.setOnClickListener { hideCommentInput() }
        mIvCommentSend.setOnClickListener{ ImagePickUtil.commonPickImage(this) }
        mSrlThreadList.setColorSchemeColors(*resources.getIntArray(R.array.swipeRefreshColors))
        mSrlThreadList.isRefreshing = true
        mSrlThreadList.setOnRefreshListener {
            mRefreshing = true
            mPage = 0
            mPresenter.getThread(mThreadId, mPage)
            //为啥这里要注释掉
            //mSrlThreadList.isRefreshing = false;
        }
        mCbAnonymousComment.setOnCheckedChangeListener { buttonView, isChecked -> mIsAnonymous = isChecked }
        mIvSelectImage.setOnClickListener { ImagePickUtil.commonPickImage(this) }
        mIvOpenEditor.setOnClickListener {startActivityForResult(IntentUtil.toEditor(mContext, mTvDynamicHint.getText().toString(), mEtComment.text.toString(), 1), REQUEST_CODE_EDITOR) }
        mTvAtUser.setOnClickListener { startActivityForResult(IntentUtil.toSearch(mContext, MODE_SEARCH_USER), REQUEST_CODE_AT_USER)  }
        mPresenter.getThread(mThreadId,0)
        initBottomTools()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && data!=null){
            if (requestCode == REQUEST_CODE_IMAGE_SELECTED){
                val mSelected:List<Uri> = Matisse.obtainResult(data)
                mPresenter.uploadImages(PathUtil.getRealPathFromURI(mContext,mSelected[0]))
                startProgress("正在添加图片，请稍后..")
            }
            if (requestCode == REQUEST_CODE_EDITOR){
                val resultContent:String = data.getStringExtra(INTENT_EDITOR_CONTENT)
                mEtComment.setText(resultContent)
                mEtComment.setSelection(resultContent.length)
            }
            if (requestCode == REQUEST_CODE_AT_USER) {
                val name = data.getStringExtra(INTENT_RESULT_AT_USER_NAME)
                val uid = data.getIntExtra(INTENT_RESULT_AT_USER_UID, 0)
                TextUtil.addAt2Content(name, mEtComment)
                MatcherTool.addAtName(name, uid)
            }
        }
    }

    override fun onGotThread(model: ThreadModel) {
        var canAnonymous = if (mCanAnonymous) 1 else 0
        val entity:ThreadBean = model.thread
        mPostCount = entity.c_post
        if (mPage == 0){
            canAnonymous = model.board!!.anonymous
        }
        pkTracker(entity.title,mPage+1)
        val postList:ArrayList<PostBean> = ArrayList()
        if (mPage==0){
            val thread:ThreadBean = model.thread
            mIsStared = IsUtil.isStarred(thread.in_collection)
            mIsLiked = IsUtil.isLiked(thread.liked)
            showStarOrNot()
            if (IsUtil.isAnon(thread.anonymous)){
                thread.author_name = ANONYMOUS_NAME
            }
            val post:PostBean = PostBean(thread.anonymous,thread.author_id,thread.author_name,thread.author_nickname,thread.content,thread.content_converted,thread.anonymous,1,thread.t_create,thread.t_modify,thread.title,thread.like,thread.liked,thread.friend)
            postList.add(post)
        }
        if (model.post == null|| model.post!!.isEmpty()){
            LogUtil.dd("no more post")
            isLastPage = true
            pageSS()
        }
        //回复后刷新当前页面
        if (mIsCommentAfter) {
            mIsCommentAfter = false
            if (model.post != null) {
                postList.addAll(model.post!!)
                mAdapter!!.refreshThisPage(postList, mPage)//!!
            }
        } else {
            if (mRefreshing) {
                mRefreshing = false
                postList.addAll(model.post!!)
                mAdapter!!.refreshList(postList)
            } else {
                if (!isLastPage) {
                    postList.addAll(model.post!!)
                }
                mAdapter!!.updateThreadPost(postList, mPage)
            }
        }
        //跳楼
        if (mIsFindingFloor) {
            if (model.post == null || model.post!!.isEmpty()) {
                //到了帖子的尽头
                cannotFindIt()
            } else {
                if (isInside()) {
                    findIt()
                } else {
                    mPresenter.getThread(mThreadId, ++mFindingPage)
                }
            }
            mPage = mFindingPage
        }
        if (mPage == 0 && model.board != null) {
            mBoardId = model.board!!.id
            mBoardName = model.board!!.name
            mToolbarTitleBoard.text = TextUtil.getLinkHtml(mBoardName)
            val finalCanAnonymous = canAnonymous
            mToolbarTitleBoard.setOnClickListener { v -> startActivity(IntentUtil.toThreadList(mContext, mBoardId, mBoardName, finalCanAnonymous)) }
        }
        setRefreshing(false)
        showAnonymousOrNot(canAnonymous)
        hideProgressBar()
        setCheckBox(false)
    }

    private fun setCheckBox(b: Boolean) {
        mCbAnonymousComment.isChecked = b
    }

    private fun showAnonymousOrNot(canAnonymous: Int) {
        if (canAnonymous == 1) {
            mCanAnonymous = true
            if (PrefUtil.isAlwaysAnonymous()) {
                HandlerUtil.postDelay({
                    mCbAnonymousComment.isChecked = true
                }, 600)
            }
            mCbAnonymousComment.visibility = View.VISIBLE
        } else {
            mCbAnonymousComment.visibility = View.GONE
        }
    }

    private fun hideProgressBar() {
        mPbThreadLoading.visibility = View.GONE
    }
    private fun findIt() {
        stopFinding()
    }

    private fun isInside(): Boolean {
        mPossibleIndex = 0
        var isFindIt = false
        if (mAdapter?.getPostList() == null) {
            return false
        }
        val posts = mAdapter!!.getPostList()
        val size = posts.size
        var start = 0
        if (mIsFindingFloor) {
            start = mFindingPage * MAX_LENGTH_POST
        }
        if (size > 0) {
            for (i in start until size) {
                val floor = posts[i].floor
                if (floor == mFindingFloor) {
                    isFindIt = true
                    LogUtil.dd("find it", i.toString())
                    mPossibleIndex = i
                    break
                }
                if (floor > mFindingFloor) {
                    mPossibleIndex = i
                    break
                }
                mPossibleIndex = i
            }
        }
        return isFindIt
    }
    private fun pageSS() {
        if (mPage != 0) {
            mPage--
        }
    }

    private fun cannotFindIt() {
        if (!mIsFindEnd) {
            SnackBarUtil.notice(this, "该楼层不存在,可能已经被删除\n已经帮你跳转到附近楼层", true)
        } else {
            mIsFindEnd = false
        }
        stopFinding()
    }

    private fun stopFinding() {
        mFindingFloor = 0
        mIsFindingFloor = false
        mRvThreadPost.scrollToPosition(mPossibleIndex)
        setRefreshing(false)
        hideProgress()
    }

    private fun hideProgress() {
        //        LogUtil.dd("hideProgress");
        if (mProgress != null) {
            mProgress!!.dismiss()//可能会变化
        }
    }

    private fun setRefreshing(refreshing: Boolean) {
        mRefreshing = refreshing
        mSrlThreadList.isRefreshing = refreshing
    }

    override fun onGetThreadFailed(m: String) {
        SnackBarUtil.error(this, m)
        hideProgressBar()
        mRefreshing = false
        if (mIsLoadingMore) {
            mIsLoadingMore = false
            mPage--
        }
        setRefreshing(false)
        pageSS()
    }

    override fun onCommentFailed(m: String?) {
        hideProgress()
        SnackBarUtil.error(this, m, true)
    }

    override fun onCommented(model: PostModel) {
        hideProgress()
        hideCommentInput()
        clearCommentData()
        SnackBarUtil.normal(this, "评论成功")
        mIsCommentAfter = true
        mPresenter.getThread(mThreadId, mPage)
    }

    private fun clearCommentData() {
        mComment = ""
        mEtComment.setText("")
        resetReply()
    }
    override fun onStarFailed(m: String) {
        SnackBarUtil.error(this, m)
    }

    override fun onUnStarFailed(m: String) {
        SnackBarUtil.error(this, m)
    }

    override fun onStarred() {
        mIsStared = true
        SnackBarUtil.normal(this, "收藏成功")
        showStarOrNot()
    }

    override fun onUnStarred() {
        mIsStared = false
        SnackBarUtil.normal(this, "已取消收藏")
        showStarOrNot()
    }

    override fun onUploadFailed(m: String) {
        SnackBarUtil.error(this, "图片添加失败\n$m")
        hideProgress()
    }

    override fun onUploaded(model: UploadImageModel) {
        TextUtil.addImg2Content(model.id, mEtComment)
        SnackBarUtil.normal(this, "图片已添加")
        hideProgress()
    }

    override fun onLike(model: BaseModel) {
    //？？？
    }

    override fun onLikeFailed(m: String, position: Int, isLike: Boolean) {
        SnackBarUtil.notice(this, m)
        if (position > 0) {
            mAdapter!!.likeItem(position, !isLike)
        } else {
            mIsLiked = false
            showLikedOrNot()
        }
    }

    private fun showLikedOrNot() {
        val iconIndex = 5
        if (mIsLiked) {
            mBottomTools.changeIconTint(iconIndex, ResourceUtil.getColor(mContext, R.color.colorPrimaryCopy))
        } else {
            mBottomTools.resetIconTint(iconIndex)
        }
    }

    override fun onUnlike(entity: BaseModel) {

    }

    override fun onUnlikeFailed(m: String, position: Int, isLike: Boolean) {
        SnackBarUtil.notice(this, m)
        if (position > 0) {
            mAdapter?.likeItem(position, !isLike)
        } else {
            mIsLiked = false
            showLikedOrNot()
        }}

    override fun onLikeClick(position: Int, isLike: Boolean, isPost: Boolean) {
        mPresenter.like(mAdapter!!.getPostId(position), position, isLike, isPost)
        mAdapter!!.likeItem(position, isLike)
    }

    override fun onReplyClick(position: Int) {
        postPosition = position
        mReplyId = mAdapter!!.getPostId(position)
        showCommentInput()
    }

    private fun showCommentInput() {
        mIsShowingCommentDialog = true
        mLlComment.visibility = View.VISIBLE
        mTvDynamicHint.text = mAdapter!!.getDynamicHint(postPosition)
        mEtComment.isFocusable = true
        mEtComment.isFocusableInTouchMode = true
        mEtComment.requestFocus()
        val imm = mLlComment
                .context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        HandlerUtil.postDelay({ imm.showSoftInput(mEtComment, 0) }, 100)

    }

    override fun onClick(v: View?) {
        val position = v?.tag as Int
        LogUtil.dd("bootom tools position clicked", position.toString())
        when (position) {
            //magic number
            STARTHREAD -> starThread()
            SHARETEXT -> shareText()
            TOTOP -> toTop()
            FINISHIME -> finishMe()
            JUMPFLOOR -> jumpFloor()
            LIKE -> like()
            SHOWCOMMENTINPUT -> showCommentInput()
        } }

    private fun starThread() {
        if (mIsStared) {
            mPresenter.unStarThread(mThreadId)
        } else {
            mPresenter.starThread(mThreadId)
        }
    }

    private fun like() {
        mIsLiked = !mIsLiked
        showLikedOrNot()
        mPresenter.like(mThreadId, 0, mIsLiked, true)
    }

    private fun shareText() {
        val url = "$BASE/forum/thread/$mThreadId"
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        //        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "这个是content");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BBS分享的标题")
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)
        //        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.type = "text/plain"
        //        shareIntent.setType()
        //        shareIntent.setType("image/*");
        //设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "分享到"))
    }

    private fun jumpFloor() {
        if (!mRefreshing) {
            DialogUtil.inputDialog(mContext,
                    "输入楼层,最大可能是" + mPostCount + "左右"
            ) { dialog, input ->
                mInputFloor = CastUtil.parse2intWithMax(input.toString())
                if (mInputFloor != 0) {
                    mFindingPage = mPage
                    startProgress("正在前往..")
                    findFloor(mInputFloor)
                }
            }
        }
    }

    private fun showFab() {
        bottomToolsBehavior(Techniques.SlideInDown)
    }

    private fun hideFab() {
        bottomToolsBehavior(Techniques.SlideOutUp)
    }

    private fun bottomToolsBehavior(technique: Techniques) {
        //        YoYo.with(technique).duration(500).playOn(m);
    }

    private fun toTop() {
        if (mPage == 0) {
            mRvThreadPost.smoothScrollToPosition(0)
        } else {
            mRvThreadPost.scrollToPosition(0)
        }
    }

    private fun resetReply(){
        postPosition =0
        mReplyId = 0
    }

    private fun hideCommentInput(){
        resetReply()
        mBottomTools.visibility = View.VISIBLE
        mLlComment.visibility = View.GONE
        val view:View? = window.peekDecorView()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        mIsShowingCommentDialog = false
    }

    private fun sendComment(replyId: Int) {
        mComment = MatcherTool.getAtContent(mEtComment.text.toString())
        if (mComment.isNotEmpty()) {
            if (replyId != 0) {
                mComment = mAdapter!!.comment2reply(postPosition, mComment)
            }
            LogUtil.dd("final comment content", mComment)
            mPresenter.doComment(mThreadId, mComment, replyId, mIsAnonymous)
            startProgress("正在发送，稍后..")
        }
    }

    private fun startProgress(msg: String) {
        mProgress = DialogUtil.showProgressDialog(this, msg)
    }

    private fun initBottomTools() {
        val reses = intArrayOf(R.drawable.ic_star_white_24dp, R.drawable.ic_share_white_24dp, R.drawable.ic_vertical_align_top_white_24dp, R.drawable.ic_clear_grey_24dp, R.drawable.ic_import_export_black_24dp, R.drawable.ic_thumb_up_white_24dp, R.drawable.ic_sms_black_24dp)
        mBottomTools.addTabs(reses, this)
        mBottomTools.changeIconPadding(1, 2)
        mBottomTools.changeIconPadding(5, 4)
        mBottomTools.changeIconPadding(6, 4)
        mBottomTools.changeIconPadding(4, 2)
    }

    private fun pkTracker(title: String, page: Int) {
        trackerHelper
                .screen("$PK_THREAD$mThreadId/page/$page/")
                .title(title).with(tracker)
    }

    private fun showStarOrNot() {
        val iconIndex = 0
        if (mIsStared) {
            mBottomTools.changeIconTint(iconIndex, ResourceUtil.getColor(mContext, R.color.material_yellow_600))
        } else {
            mBottomTools.resetIconTint(iconIndex)
        }
    }

    private fun findFloor(floor: Int) {
        mFindingFloor = floor
        if (isInside()) {
            mRvThreadPost.scrollToPosition(mPossibleIndex)
            mPage = mFindingPage
            hideProgress()
        } else {
            mIsFindingFloor = true
            mFindingPage = ++mPage
            mPresenter.getThread(mThreadId, mFindingPage)
        }
    }
}