package com.dou361.jjdxm_recyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.dou361.baseutils.utils.DateUtils;
import com.dou361.baseutils.utils.LogUtils;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.jjdxm_recyclerview.adapter.MenuBeanAdapter;
import com.dou361.jjdxm_recyclerview.bean.TimeBean;
import com.dou361.recyclerview.listener.Closeable;
import com.dou361.recyclerview.listener.OnSwipeMenuItemClickListener;
import com.dou361.recyclerview.swipe.SwipeMenu;
import com.dou361.recyclerview.swipe.SwipeMenuCreator;
import com.dou361.recyclerview.swipe.SwipeMenuItem;
import com.dou361.recyclerview.swipe.SwipeMenuRecyclerView;
import com.dou361.recyclerview.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    SwipeMenuRecyclerView recyclerView;
    @Bind(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    private MenuBeanAdapter adapter;
    private int currentPosition;
    private String TAG = this.getClass().getSimpleName();

    private List<TimeBean> list = new ArrayList<TimeBean>();
    private String[] arrs = new String[]{"http://tupian.enterdesk.com/2014/mxy/01/10/03/11.jpg"
            , "http://image.tianjimedia.com/uploadImages/2013/324/E85BW32E3U69_1000x500.jpg"
            , "http://pic.yesky.com/uploadImages/2014/315/00/J3U360Y9NYJ2.jpg"
            , "http://www.redvi.com/uploadfile/hy/6c/kt/edo42ay1aaj.jpg"
            , "http://c11.eoemarket.com/app0/210/210768/icon/437326.png"
            , "http://tupian.enterdesk.com/2015/xll/02/26/2/rili2.jpg"
            , "http://img5.duitang.com/uploads/item/201504/16/20150416H0755_LfSyA.jpeg"
            , "http://image.tianjimedia.com/uploadImages/2013/150/VD58N0X2J2Q8.jpg"
            , "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1210/16/c2/14454649_1350371218906.jpg"
            , "http://image.tianjimedia.com/uploadImages/2015/083/30/VVJ04M7P71W2.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);

        adapter = new MenuBeanAdapter(this, list);
        swipeLayout.setOnRefreshListener(mOnRefreshListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        recyclerView.addItemDecoration(new DividerItemDecoration(this, UIUtils.dip2px(8), true, getResources().getColor(R.color.color_bg_01), false, true, false));
        // 添加滚动监听。
        recyclerView.addOnScrollListener(mOnScrollListener);
        // 为SwipeRecyclerView的Item创建菜单就两句话，不错就是这么简单：
        // 设置菜单创建器。
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        recyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
        recyclerView.setAdapter(adapter);
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        });
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * 刷新监听。
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {

            if (swipeLayout != null) {
                swipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeLayout != null) {
                            list.clear();
                            list.addAll(loadData());
                            adapter.notifyDataSetChanged();
                            swipeLayout.setRefreshing(false);

                        }
                    }
                }, 1500);
            }
        }
    };

    /**
     * 加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)) {// 手指不能向上滑动了
                // TODO 这里有个注意的地方，如果你刚进来时没有数据，但是设置了适配器，这个时候就会触发加载更多，需要开发者判断下是否有数据，如果有数据才去加载更多。
                if (list.size() <= 0) {
                    return;
                }
                if (swipeLayout != null) {
                    swipeLayout.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            list.addAll(loadData());
                            adapter.notifyDataSetChanged();
                        }
                    }, 1500);
                }
            }
        }
    };

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_height);
            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(MenuActivity.this)
                        .setBackgroundDrawable(R.drawable.red_shape_select)
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

            }
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                LogUtils.logTagName(TAG).log("list第" + adapterPosition + "右侧菜单第" + menuPosition);
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                LogUtils.logTagName(TAG).log("list第" + adapterPosition + "左侧菜单第" + menuPosition);
            }
            // TODO 推荐调用Adapter.notifyItemRemoved(position)，也可以Adapter.notifyDataSetChanged();
            if (menuPosition == 0) {
                // 删除按钮被点击。
                currentPosition = adapterPosition;
                UIUtils.showToastLong("点击了" + currentPosition);
            }
        }
    };

    /**
     * 模拟加载数据
     */
    private List<TimeBean> loadData() {
        List<TimeBean> temp = new ArrayList<>();
        Random rd = new Random();
        for (int i = 0; i < 5; i++) {
            TimeBean liveBean = new TimeBean();
            liveBean.setTitle("标题" + i);
            liveBean.setDescription("简介" + i);
            liveBean.setStartTime(DateUtils.getCurrentTimeMillis() + rd.nextInt(1000 * 60 * 60 * 24 * 2));
            liveBean.setThumb(arrs[rd.nextInt(10)]);
            temp.add(liveBean);
        }
        return temp;
    }
}
