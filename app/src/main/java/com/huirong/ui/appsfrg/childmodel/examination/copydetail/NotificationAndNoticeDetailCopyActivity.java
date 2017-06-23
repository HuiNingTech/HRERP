package com.huirong.ui.appsfrg.childmodel.examination.copydetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huirong.R;
import com.huirong.base.BaseActivity;
import com.huirong.common.MyException;
import com.huirong.dialog.Loading;
import com.huirong.helper.UserHelper;
import com.huirong.inject.ViewInject;
import com.huirong.model.MyCopyModel;
import com.huirong.model.copydetailmodel.NotificationAndNoticeCopyModel;
import com.huirong.utils.PageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请 通知公告详情
 * Created by sjy on 2017/1/16.
 */

public class NotificationAndNoticeDetailCopyActivity extends BaseActivity {
    //back
    @ViewInject(id = R.id.layout_back, click = "forBack")
    RelativeLayout layout_back;

    //
    @ViewInject(id = R.id.tv_title)
    TextView tv_title;

    //
    @ViewInject(id = R.id.tv_right)
    TextView tv_right;

    //公告类型
    @ViewInject(id = R.id.tv_notificaitonAndNotice_type)
    TextView tv_notificaitonAndNotice_type;

    //标题
    @ViewInject(id = R.id.tv_notificaitonAndNotice_title)
    TextView tv_notificaitonAndNotice_title;

    //接收部门
    @ViewInject(id = R.id.tv_notificaitonAndNotice_whom)
    TextView tv_notificaitonAndNotice_whom;

    //内容
    @ViewInject(id = R.id.layout_content, click = "forContent")
    LinearLayout layout_content;
    @ViewInject(id = R.id.tv_notificaitonAndNotice_content)
    TextView tv_notificaitonAndNotice_content;

    //备注
    @ViewInject(id = R.id.tv_remark, click = "RemarkExpended")
    TextView tv_remark;

    //审批人
    @ViewInject(id = R.id.tv_Requester)
    TextView tv_Requester;

    //审批状况
    @ViewInject(id = R.id.tv_state_result)
    TextView tv_state_result;
    @ViewInject(id = R.id.layout_state, click = "forState")
    LinearLayout layout_state;

    //获取子控件个数的父控件
    @ViewInject(id = R.id.layout_ll)
    LinearLayout layout_ll;

    //抄送人
    @ViewInject(id = R.id.tv_copyer)
    TextView tv_copyer;

    //抄送时间
    @ViewInject(id = R.id.tv_copyTime)
    TextView tv_copyTime;

    //变量
    private Intent intent = null;
    private NotificationAndNoticeCopyModel notificationAndNoticeModel;
    private MyCopyModel model;
    //动态添加view
    private List<View> ls_childView;//用于保存动态添加进来的View
    private List<ViewHolder> listViewHolder = new ArrayList<>();

    //常量
    public static final int POST_SUCCESS = 11;
    public static final int POST_FAILED = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_apps_examination_notificationandnotice_d3);
        initMyView();
        getDetailModel(model);

    }

    private void initMyView() {
        tv_title.setText(getResources().getString(R.string.notificaitonAndNotice));
        tv_right.setText("");

        Bundle bundle = this.getIntent().getExtras();
        model = (MyCopyModel) bundle.getSerializable("MyCopyModel");
    }

    private void setShow(NotificationAndNoticeCopyModel model) {
        tv_copyer.setText(model.getEmployeeName());
        tv_copyTime.setText(model.getApplicationCreateTime());
        //
        tv_notificaitonAndNotice_type.setText(model.getPublishType());
        tv_notificaitonAndNotice_type.setText(model.getPublishType());
        tv_notificaitonAndNotice_whom.setText(model.getAbstract());
        tv_notificaitonAndNotice_title.setText(model.getApplicationTitle());
        tv_remark.setText(model.getRemark());

        // 审批人
        List<NotificationAndNoticeCopyModel.ApprovalInfoLists> modelList = model.getApprovalInfoLists();
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < modelList.size(); i++) {
            nameBuilder.append(modelList.get(i).getApprovalEmployeeName() + " ");
        }
        tv_Requester.setText(nameBuilder);

        //审批状态
        if (notificationAndNoticeModel.getApprovalStatus().contains("0")) {
            tv_state_result.setText("未审批");
            tv_state_result.setTextColor(getResources().getColor(R.color.red));
        } else if (notificationAndNoticeModel.getApprovalStatus().contains("1")) {
            tv_state_result.setText("已审批");
            tv_state_result.setTextColor(getResources().getColor(R.color.green));
        } else if (notificationAndNoticeModel.getApprovalStatus().contains("2")) {
            tv_state_result.setText("审批中...");
            tv_state_result.setTextColor(getResources().getColor(R.color.black));
        } else {
            tv_state_result.setText("你猜猜！");
        }

        if (notificationAndNoticeModel.getApprovalStatus().contains("1") || notificationAndNoticeModel.getApprovalStatus().contains("2")) {
            //插入意见
            for (int i = 0, mark = layout_ll.getChildCount(); i < modelList.size(); i++, mark++) {//mark是布局插入位置，放在mark位置的后边（从1开始计数）
                ViewHolder vh = AddView(this, mark);//添加布局
                vh.tv_name.setText(modelList.get(i).getApprovalEmployeeName());
                vh.tv_time.setText(modelList.get(i).getApprovalDate());
                vh.tv_contains.setText(modelList.get(i).getComment());
                if (modelList.get(i).getYesOrNo().contains("0")) {
                    vh.tv_yesOrNo.setText("不同意");
                    vh.tv_yesOrNo.setTextColor(getResources().getColor(R.color.red));
                } else if (TextUtils.isEmpty(modelList.get(i).getYesOrNo())) {
                    vh.tv_yesOrNo.setText("未审批");
                    vh.tv_yesOrNo.setTextColor(getResources().getColor(R.color.red));
                } else if ((modelList.get(i).getYesOrNo().contains("1"))) {
                    vh.tv_yesOrNo.setText("同意");
                    vh.tv_yesOrNo.setTextColor(getResources().getColor(R.color.green));
                } else {
                    vh.tv_yesOrNo.setText("yesOrNo为null");
                }
            }
        }
    }

    /**
     * 获取详情数据
     */
    public void getDetailModel(final MyCopyModel model) {

        Loading.run(this, new Runnable() {
            @Override
            public void run() {
                //泛型
                try {
                    NotificationAndNoticeCopyModel model1 = new UserHelper<>(NotificationAndNoticeCopyModel.class)
                            .copyDetailPost(NotificationAndNoticeDetailCopyActivity.this,
                                    model.getApplicationID(),
                                    model.getApplicationType());
                    sendMessage(POST_SUCCESS, model1);
                } catch (MyException e) {
                    e.printStackTrace();
                    sendMessage(POST_FAILED, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case POST_SUCCESS: // 1001
                notificationAndNoticeModel = (NotificationAndNoticeCopyModel) msg.obj;
                setShow(notificationAndNoticeModel);
                break;
            case POST_FAILED: // 1001
                PageUtil.DisplayToast((String) msg.obj);
                break;
            default:
                break;
        }
    }

    /**
     * 动态插入view
     */
    public class ViewHolder {
        private int id = -1;
        private TextView tv_name;
        private TextView tv_yesOrNo;
        private TextView tv_time;
        private TextView tv_contains;
    }

    //初始化参数
    private ViewHolder AddView(Context context, int marks) {
        ls_childView = new ArrayList<View>();
        LayoutInflater inflater = LayoutInflater.from(context);
        View childView = inflater.inflate(R.layout.item_examination_status, new LinearLayout(this), false);
        childView.setId(marks);
        layout_ll.addView(childView, marks);
        return getViewInstance(childView);

    }

    private ViewHolder getViewInstance(View childView) {
        ViewHolder vh = new ViewHolder();
        vh.id = childView.getId();
        vh.tv_name = (TextView) childView.findViewById(R.id.tv_name);
        vh.tv_yesOrNo = (TextView) childView.findViewById(R.id.tv_yesOrNo);
        vh.tv_time = (TextView) childView.findViewById(R.id.tv_time);
        vh.tv_contains = (TextView) childView.findViewById(R.id.tv_contains);
        listViewHolder.add(vh);
        ls_childView.add(childView);
        return vh;
    }

    /**
     * back
     *
     * @param view
     */
    public void forBack(View view) {
        this.finish();
    }

    private boolean isRemarkExpend = false;

    public void RemarkExpended(View view) {
        if (!isRemarkExpend) {
            tv_remark.setMinLines(0);
            tv_remark.setMaxLines(Integer.MAX_VALUE);
            isRemarkExpend = true;
        } else {
            tv_remark.setLines(3);
            isRemarkExpend = false;
        }

    }
}
