package xxx.test.allapplication.adapter;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import android.view.ViewGroup;

import xxx.test.allapplication.R;

public class MyItemTouchHelperCallback extends Callback {
    private ItemTouchMoveListener moveListener;

    public MyItemTouchHelperCallback(ItemTouchMoveListener moveListener) {
        this.moveListener = moveListener;
    }

    //Callback回调监听时先调用的，用来判断当前是什么动作，比如判断方向（意思就是我要监听哪个方向的拖动）
    @Override
    public int getMovementFlags(RecyclerView recyclerView, ViewHolder holder) {
        //方向：up,down,left,right
        //常量：
        int up = ItemTouchHelper.UP;//1  0x0001
        int down = ItemTouchHelper.DOWN;//2 0x0010
//		ItemTouchHelper.LEFT
//		ItemTouchHelper.RIGHT
        //我要监听的拖拽方向是哪两个方向。
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //我要监听的swipe侧滑方向是哪个方向
//		int swipeFlags = 0;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;


        int flags = makeMovementFlags(dragFlags, swipeFlags);
        return flags;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        // 是否允许长按拖拽效果
        return true;
    }

    //当移动的时候回调的方法--拖拽
    @Override
    public boolean onMove(RecyclerView recyclerView, ViewHolder srcHolder, ViewHolder targetHolder) {
        if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) {
            return false;
        }
        // 在拖拽的过程当中不断地调用adapter.notifyItemMoved(from,to);
        boolean result = moveListener.onItemMove(srcHolder.getAdapterPosition(), targetHolder.getAdapterPosition());
        return result;
    }

    //侧滑的时候回调的
    @Override
    public void onSwiped(ViewHolder holder, int arg1) {
        // 监听侧滑，1.删除数据；2.调用adapter.notifyItemRemove(position)
       // moveListener.onItemRemove(holder.getAdapterPosition());
    }


    @Override
    public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
        //判断选中状态
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.color_f85d00));
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
        // 恢复
        viewHolder.itemView.setBackgroundColor(recyclerView.getContext().getResources().getColor(R.color.colorGold));
        viewHolder.itemView.setAlpha(1);//1~0
        viewHolder.itemView.setScaleX(1);//1~0
        viewHolder.itemView.setScaleY(1);//1~0
        viewHolder.itemView.setTranslationX(0);//1~0
        super.clearView(recyclerView, viewHolder);
    }
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;//是否允许侧滑
    }
    public int getSlideLimitation(RecyclerView.ViewHolder viewHolder){
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        return viewGroup.getChildAt(1).getLayoutParams().width;
    }
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            ViewHolder viewHolder, float dX, float dY, int actionState,
                            boolean isCurrentlyActive) {
        //dX:水平方向移动的增量（负：往左；正：往右）范围：0~View.getWidth  0~1











        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //透明度动画
//            float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
//            viewHolder.itemView.setAlpha(alpha);//1~0
//            viewHolder.itemView.setScaleX(alpha);//1~0
//            viewHolder.itemView.setScaleY(alpha);//1~0
            if (Math.abs(dX) > viewHolder.itemView.getWidth() / 2) {
                viewHolder.itemView.setTranslationX(-0.5f * viewHolder.itemView.getWidth());
            } else {
                viewHolder.itemView.setTranslationX(dX);
            }
            Log.i("neo", "x = " + dX + " state = " + actionState + " w = " + viewHolder.itemView.getWidth() / 2);
        }else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                    isCurrentlyActive);

        }
    }


}