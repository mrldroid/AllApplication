package xxx.test.allapplication.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xxx.test.allapplication.R;
import xxx.test.allapplication.adapter.MyAdapter2;
import xxx.test.allapplication.adapter.MyItemTouchHelperCallback;
import xxx.test.allapplication.adapter.StartDragListener;
import xxx.test.allapplication.model.User;

import static xxx.test.allapplication.R.id.recyclerView;

public class RecyclerViewActivity extends AppCompatActivity implements StartDragListener {

    RecyclerView mRecyclerView;
    ItemTouchHelper itemTouchHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = (RecyclerView) findViewById(recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<User> list = new ArrayList<>();
        for(int i = 0 ; i < 20; i++){
            User user = new User();
            user.name = "neo"+i;
            list.add(user);
        }

        MyAdapter2 myAdapter = new MyAdapter2(this,list,this);
        mRecyclerView.addItemDecoration(new LineItemDecoration(this));
        mRecyclerView.setAdapter(myAdapter);
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(myAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    public static class LineItemDecoration extends RecyclerView.ItemDecoration{
        private Drawable mDivider;
        public LineItemDecoration(Context context) {
//            int[] attrs = new int[]{android.R.attr.listDivider};
//            TypedArray a = context.obtainStyledAttributes(attrs);
//            mDivider = a.getDrawable(0);
//            a.recycle();
            mDivider = ContextCompat.getDrawable(context,R.drawable.correct);
        }

        @Override//绘制
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int left = child.getLeft() - params.leftMargin;
                int top = child.getBottom() + params.bottomMargin;
                int right = child.getRight() + params.rightMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override//设置条目周边的偏移量
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        }
    }
}
