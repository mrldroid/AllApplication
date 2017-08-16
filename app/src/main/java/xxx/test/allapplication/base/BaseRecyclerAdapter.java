package xxx.test.allapplication.base;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    public interface Item {
        int HEADER_TYPE = 202;
        int LINE_TYPE = 206;
        int LOGIN_BUTTON_TYPE = 236;
        int BANNER_TYPE = 240;
    }

    private Activity mActivity;
    protected List<T> list = null;

    protected OnItemClickListener onItemClickListener;

    public BaseRecyclerAdapter(Activity mActivity, List<T> list) {
        this.mActivity = mActivity;
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        onBindItemView(holder, position);
    }

    public abstract BaseRecyclerViewHolder onCreateHolder(ViewGroup parent, int viewType);

    protected abstract void onBindItemView(BaseRecyclerViewHolder holder, int position);


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeAll() {
        if (getList() != null) {
            getList().clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(List<T> list) {
        if (getList() != null) {
            getList().addAll(list);
            notifyDataSetChanged();
        }
    }

    public void removeItem(T item) {
        if (getList() != null) {
            getList().remove(item);
            notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        if (getList() != null) {
            if (position < getItemCount()) {
                getList().remove(position);
                notifyItemChanged(position);
            }
        }
    }

    public Activity getActivity() {
        return mActivity;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

}