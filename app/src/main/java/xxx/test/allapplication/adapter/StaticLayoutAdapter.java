package xxx.test.allapplication.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xxx.test.allapplication.R;
import xxx.test.allapplication.base.BaseRecyclerAdapter;
import xxx.test.allapplication.base.BaseRecyclerViewHolder;
import xxx.test.allapplication.custom.StaticLayoutView;
import xxx.test.allapplication.model.StaticLayoutModel;

/**
 * Created by neo on 17/1/6.
 */

public class StaticLayoutAdapter extends BaseRecyclerAdapter<StaticLayoutModel> {

    public StaticLayoutAdapter(Activity mActivity, List<StaticLayoutModel> list) {
        super(mActivity, list);
    }

    @Override
    public BaseRecyclerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new StaticLayoutHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_static_layout,parent,false));
    }

    @Override
    protected void onBindItemView(BaseRecyclerViewHolder holder, int position) {
        holder.onBindViewHolder(position);
    }

    private class StaticLayoutHolder extends BaseRecyclerViewHolder{
        StaticLayoutView staticLayoutView;
        StaticLayoutHolder(View itemView) {
            super(itemView);
            staticLayoutView = (StaticLayoutView) itemView.findViewById(R.id.static_layout_view);
        }

        @Override
        public void onBindViewHolder(int position) {
            staticLayoutView.setLayout(list.get(position).staticLayout);
        }
    }
}
