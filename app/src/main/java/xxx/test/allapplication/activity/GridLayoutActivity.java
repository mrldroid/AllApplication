package xxx.test.allapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

import xxx.test.allapplication.R;
import xxx.test.allapplication.utils.DensityUtil;

public class GridLayoutActivity extends AppCompatActivity {

    List<Integer>list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);
        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid);

        int size = 1;
        int row = 0;
        int mod = size %3;
        if(mod == 0){
            row = size/3;
        }else {
            row = size/3+1;
        }

        gridLayout.setColumnCount(3);

        for (int i = 0; i < row; i++) {
            int column = 3;
            if(i == row -1){
                if(mod != 0){
                    column = mod;
                }
            }
            for (int j = 0; j < column; j++) {
               View view  = LayoutInflater.from(this).inflate(R.layout.item,null);
                //设置它的行 和 权重 有了权重才能水平均匀分布
                //由于方法重载，注意这个地方的1.0f 必须是float，
                GridLayout.Spec rowSpec = GridLayout.spec(i, 1.0f);
                GridLayout.Spec columnSpec = GridLayout.spec(j, 1.0f);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                //左边的靠左，右边的靠右，中间的居中，默认居中
                switch (j) {
                    case 0:
                        params.setGravity(Gravity.LEFT);
                        break;
                    case 1:
                        params.setGravity(Gravity.CENTER);
                        break;
                    case 2:
                        params.setGravity(Gravity.RIGHT);
                        break;
                    default:
                        params.setGravity(Gravity.CENTER);
                        break;
                }
                params.bottomMargin = DensityUtil.dip2px(this,10);
                gridLayout.addView(view, params);
            }
        }
    }


    }


