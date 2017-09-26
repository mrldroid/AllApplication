package xxx.test.allapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by liujun on 17/9/26.
 */

public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder{
    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void onBindViewHolder(int position);
}
