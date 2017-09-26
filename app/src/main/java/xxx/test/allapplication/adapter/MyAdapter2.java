package xxx.test.allapplication.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import xxx.test.allapplication.R;
import xxx.test.allapplication.model.User;

/**
 * Created by liujun on 17/9/26.
 */

public class MyAdapter2 extends BaseRecyclerAdapter<User,MyAdapter2.MyViewHolder2> {
    public MyAdapter2(Context context, List<User> mList) {
        super(context, mList);
    }

    @Override
    public MyViewHolder2 createHolder(View view) {
        return new MyViewHolder2(view);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recycler;
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
