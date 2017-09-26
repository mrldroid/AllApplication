package xxx.test.allapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by liujun on 17/9/26.
 */

public abstract class BaseRecyclerAdapter<Model,Holder extends BaseRecyclerViewHolder> extends RecyclerView.Adapter<Holder>{
    protected List<Model> mList;
    protected Context mContext;

    public BaseRecyclerAdapter(Context context,List<Model> mList) {
        this.mList = mList;
        this.mContext = context;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(getLayoutId(),parent,false);
        return createHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.onBindViewHolder(position);
    }

    @Override
    public int getItemCount() {
        return mList == null?0:mList.size();
    }

    public abstract Holder createHolder(View view);
    public abstract int getLayoutId();
}
