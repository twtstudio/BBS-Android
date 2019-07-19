package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.create_thread

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.jaeger.library.StatusBarUtil
import com.twtstudio.bbs.bdpqchen.bbs.R
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseActivity
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.*
import com.twtstudio.bbs.bdpqchen.bbs.forum.ForumModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.BoardsModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.picker.PickerDialog
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.UploadImageModel
import com.zhihu.matisse.Matisse
import kotterknife.bindView

class CreateThread2Activity : BaseActivity(), CreateThreadContract.View, DataContract {
    override fun onGetForumList(list: MutableList<ForumModel>) {
        list.apply {
            mForumModelList = this
            if (!specifyBoard) {
                mForumNames.clear()
                mapTo(mForumNames) { it.name }
                forEach { t -> presenter.getBoardList(t.id) }
            }
        }
    }

    override fun onUploaded(model: UploadImageModel) {
        model?.apply {
            contentInput.isFocusable = true
            TextUtil.addImg2Content(model.id, contentInput)
            SnackBarUtil.normal(this@CreateThread2Activity, "图片已添加")
        }
        hideProgress()
    }

    private val titleInput by bindView<TextInputEditText>(R.id.et_title)
    private val contentInput by bindView<TextInputEditText>(R.id.et_content)
    private val editor by bindView<TextView>(R.id.tv_open_editor)
    private val selectImage by bindView<TextView>(R.id.tv_select_image)
    private val switch by bindView<Switch>(R.id.anonymous_switch)
    private val itemSelect by bindView<ConstraintLayout>(R.id.item_select_board)
    private val toolbar by bindView<Toolbar>(R.id.toolbar)
    private val anonTv by bindView<TextView>(R.id.anonymous)
    private lateinit var presenter: CreateThreadContract.Presenter

    private val boardTv by bindView<TextView>(R.id.board_name)
    private var mAlertDialog: MaterialDialog? = null
    private var mProgressDialog: MaterialDialog? = null
    private var mBoardNames = mutableListOf<String>()
    private var mForumNames = mutableListOf<String>()
    private var forumPos = 0
    private var boardPos = 0
    private var title = ""
    private var content = ""
    private var selected = false
    private var specifyBoard = false
    private var isAnonymous = false
    private var isPublished = false
    private var canAnonymous = 0
    private var mForumId = 0
    private var mBoardsModel = BoardsModel()
    private var mForumModelList = mutableListOf<ForumModel>()
    private var mSelectedBoardId = 0
    private var mSelectedForumId = 0
    private var map: MutableMap<String, List<BoardsModel.BoardsBean>> = HashMap() //实例化


    override fun getLayoutResourceId() = R.layout.activity_create_thread2

    override fun getToolbarView(): Toolbar {
        toolbar.title = "发布帖子"
        return toolbar
    }

    override fun getPresenter(): BasePresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = CreateThreadPresenter(this)
        StatusBarUtil.setColor(this, Color.WHITE, 0)
        enableLightStatusBarMode(true)
        mContext = this
        intent?.apply {
            mForumId = getIntExtra(Constants.INTENT_FORUM_ID, 0)
            canAnonymous = getIntExtra(Constants.INTENT_BOARD_CAN_ANON, 0)
            if (getStringExtra(Constants.INTENT_BOARD_TITLE) != null) {
                title = getStringExtra(Constants.INTENT_BOARD_TITLE)
            }
            specifyBoard = getBooleanExtra(Constants.INTENT_IS_SPECIFY_BOARD, false)
        }
        if (specifyBoard) {
            itemSelect.visibility = View.GONE
        }

        if (mForumId == 0 && !specifyBoard) {//从主页跳
            presenter.getForumList()//加载全部
            setAnonymous()
        }
        if (specifyBoard) { //从BA跳
            canAnonymous = intent.getIntExtra(Constants.INTENT_BOARD_CAN_ANON, 0)
            setAnonymous()
            mSelectedForumId = intent.getIntExtra(Constants.INTENT_BOARD_ID, 0)
            toolbar.title = "发布帖子 | $title"
            presenter.getBoardList(mSelectedForumId)//加载特定bid的,mSelectedForumId是大id，小id有选择器返回
        }
        switch.setOnCheckedChangeListener { _, b -> isAnonymous = b }
        selectImage.setOnClickListener { ImagePickUtil.commonPickImage(this) }
        editor.setOnClickListener { startActivityForResult(IntentUtil.toEditor(mContext, titleInput.text.toString(), contentInput.text.toString(), 0), Constants.REQUEST_CODE_EDITOR) }
        itemSelect.setOnClickListener { }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_thread, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create_thread -> checkInput()
            android.R.id.home -> isDangerExit()
        }
        return false
    }

    private fun setUpData() {
        title = titleInput.text.toString()
        content = contentInput.text.toString()
    }

    private fun checkInput() {
        val err = "不能为空啊大哥"
        setUpData()
        if (title.isEmpty()) {
            titleInput.error = err
            return
        } else if (content.isEmpty()) {
            contentInput.error = err
            return
        }
        if (mSelectedBoardId == 0) {
            SnackBarUtil.error(this, "你还没有选择板块")
            return
        }
        if (!isPublished) {
            LogUtil.dd("send content", content)
            publish()
        } else {
            mAlertDialog = DialogUtil.alertDialog(this, "你已经成功发布过帖子，还要再发一次吗？", "再次发布", "不要", { _, _ -> publish() }, null)
        }
    }

    private fun publish() {
        val bundle = Bundle()
        bundle.putString(Constants.TITLE, title)
        bundle.putString(Constants.CONTENT, content)
        bundle.putInt(Constants.BID, mSelectedBoardId)
        bundle.putBoolean(Constants.IS_ANONYMOUS, isAnonymous)
        presenter.doPublishThread(bundle)
        showProgress("正在发布，请稍候..")
    }

    private fun isDangerExit() {
        LogUtil.dd("isDangerExit()")
        setUpData()
        if (isPublished) {
            finishMe()
            return
        }
        if (title.isNotEmpty() || content.isNotEmpty()) {
            mAlertDialog = DialogUtil.alertDialog(this, "确定放弃所写的内容吗？？", "放弃", "就不放弃",
                    { _, _ -> finishMe() }, null)
        } else {
            finishMe()
        }
    }

    override fun onBackPressedSupport() {
        isDangerExit()
    }

    /**
     * Dispatch incoming result to the correct fragment.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.apply {
                when (requestCode) {
                    Constants.REQUEST_CODE_IMAGE_SELECTED -> {
                        val mSelected = Matisse.obtainResult(data)
                        presenter.uploadImages(PathUtil.getRealPathFromURI(mContext, mSelected[0]))
                        showProgress("正在添加图片，请稍后")
                    }
                    Constants.REQUEST_CODE_EDITOR -> {
                        val contentResult = data.getStringExtra(Constants.INTENT_EDITOR_CONTENT)
                        contentInput.setText(contentResult)
                        contentInput.setSelection(contentResult.length)
                    }
                }
            }
        }
    }

    private fun showProgress(msg: String) {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.showProgressDialog(this, msg)
        } else {
            mProgressDialog!!.show()
        }
    }

    override fun setUpPicker() {
        itemSelect.setOnClickListener {
            val frag = PickerDialog()
            frag.show(supportFragmentManager, "PickerDialog")
        }
    }

    private fun hideProgress() {
        mProgressDialog?.dismiss()
    }

    private fun setAnonymous() {
        anonTv.visibility = if (canAnonymous == 1) View.VISIBLE else View.GONE
        switch.visibility = if (canAnonymous == 1) View.VISIBLE else View.GONE
        if (canAnonymous == 1 && PrefUtil.getIsFirstCreateAnonymous()) {

        }
    }

    override fun onPublished() {
        SnackBarUtil.normal(this, "发布成功")
        mProgressDialog?.dismiss()
        isPublished = true
        this.finish()
    }

    override fun onPublishFailed(msg: String?) {
        SnackBarUtil.error(this, msg, true)
        mProgressDialog?.dismiss()
    }

    override fun onUploadFailed(m: String?) {
        SnackBarUtil.error(this, "图片添加失败\n$m")
        hideProgress()
    }



    override fun onGetBoardList(model: BoardsModel) {
        model?.apply {
            if (specifyBoard) {
                mBoardsModel = this // 加载特定板块时是固定的，加载全部板块时不能用这个model
                mBoardNames.clear() // 同上
                boards.mapTo(mBoardNames) { m -> m.name }
            } else {
                map[forum.name] = boards
            }
        }
    }

    override fun onGetBoardListFailed(m: String) {
        presenter.getBoardList(mForumId)
        SnackBarUtil.normal(this, m)
    }


    override fun onGetForumFailed(m: String) {
        SnackBarUtil.normal(this, m)
    }

    override fun getData(): Map<String, List<BoardsModel.BoardsBean>> {
        return if (specifyBoard) {
            val res = HashMap<String, List<BoardsModel.BoardsBean>>()
            res[title] = mBoardsModel.boards
            res
        } else {
            map
        }
    }

    override fun setSelected(forumId: Int, boardId: Int, boardName: String, canAnon: Int) {
        canAnonymous = canAnon
        setAnonymous()
        mSelectedBoardId = boardId
        boardTv.text = boardName
    }


    override fun getBoardPos() = boardPos

    override fun getForumPos() = forumPos

    override fun isSpecified() = specifyBoard

}
