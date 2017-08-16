package xxx.test.allapplication.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xxx.test.allapplication.R;
import xxx.test.allapplication.adapter.StaticLayoutAdapter;
import xxx.test.allapplication.custom.LinkSpan;
import xxx.test.allapplication.model.StaticLayoutModel;
import xxx.test.allapplication.utils.DensityUtil;

public class StaticLayoutActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<StaticLayoutModel> mStaticLayoutModels = new ArrayList<>();
    private StaticLayoutAdapter mStaticLayoutAdapter;
    private int flags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

    private static final Layout.Alignment DefaultAlignment = Layout.Alignment.ALIGN_NORMAL;
    private static final float DefaultSpacingmult = 1.0f;//行间倍数,默认是1
    private static final float DefaultSpacingadd = DensityUtil.dip2px(2f);//行距增加值，默认是0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_layout);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(60);
        for(int i = 10000; i < 10015; i++) {
            StaticLayout layout = null;
            final String s = i+""+"";
            SpannableString ss = new SpannableString(s);
            int end = s.length();
            ss.setSpan(new LinkSpan(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(StaticLayoutActivity.this,"i = "+s,Toast.LENGTH_SHORT).show();
                }
            }), 0, end, flags);//设置点击事件
            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, end, flags);//设置字体样式
            layout = new StaticLayout(ss,0,end,textPaint, DensityUtil.getMetricsWidth(this)-DensityUtil.dip2px(45),DefaultAlignment,DefaultSpacingmult,DefaultSpacingadd,true);
            mStaticLayoutModels.add(new StaticLayoutModel(layout));
        }

        mStaticLayoutAdapter = new StaticLayoutAdapter(this,mStaticLayoutModels);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mStaticLayoutAdapter);
    }
}
