package xxx.test.allapplication.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import xxx.test.allapplication.R;
import xxx.test.allapplication.model.User;

/**
 * Created by liujun on 17/9/26.
 */

public class MyAdapter2 extends BaseRecyclerAdapter<User,MyAdapter2.MyViewHolder2> implements ItemTouchMoveListener{
    StartDragListener listener;
    public MyAdapter2(Context context, List<User> mList,StartDragListener listener) {
        super(context, mList);
        this.listener = listener;
    }

    @Override
    public MyViewHolder2 createHolder(View view) {
        return new MyViewHolder2(view);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recycler;
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // 1.数据交换；2.刷新
        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public boolean onItemRemove(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        return true;
    }

    class MyViewHolder2 extends BaseRecyclerViewHolder{
        TextView textView;
        public MyViewHolder2(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void onBindViewHolder(int position) {
           textView.setText(mList.get(position).name);
        }
    }
}
