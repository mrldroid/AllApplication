package xxx.test.allapplication.adapter;

import android.support.v7.widget.RecyclerView;

public interface StartDragListener {
	/**
	 * 该接口用于需要主动回调拖拽效果的
	 * @param viewHolder
	 */
	public void onStartDrag(RecyclerView.ViewHolder viewHolder);

}
