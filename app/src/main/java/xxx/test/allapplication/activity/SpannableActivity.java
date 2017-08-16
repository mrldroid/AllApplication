package xxx.test.allapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.yyydjk.library.DropDownMenu;
import java.util.ArrayList;
import java.util.List;
import xxx.test.allapplication.R;

public class SpannableActivity extends AppCompatActivity {

    @BindView(R.id.edt_text)
    EditText edt_text;
    @BindView(R.id.dropdown_menu)
    DropDownMenu dropDownMenu;
    @BindString(R.string.app_name)String app_name;
    @BindView(R.id.text)TextView text;

    private List<View> popupViews = new ArrayList<>();
    private String spans[] = {"ForegroundColorSpan","BackgroundColorSpan"};
    private String flags[] = {"INCLUSIVE_INCLUSIVE","INCLUSIVE_EXCLUSIVE","EXCLUSIVE_INCLUSIVE","EXCLUSIVE_EXCLUSIVE"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spannable);
        ButterKnife.bind(this);
        int len = app_name.length();
        String str = "14.80万";
        int  index = str.indexOf("子");
        Log.i("neo","index = "+index);
        SpannableString ss = new SpannableString("14.80万");
        ss.setSpan(new AbsoluteSizeSpan(13,true), index, str.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        text.setText(ss);
        //ss.setSpan(new ForegroundColorSpan(Color.BLUE),1,2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //ss.setSpan(new BackgroundColorSpan(ContextCompat.getColor(this,R.color.colorGold)),0,3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //ss.setSpan(new StyleSpan(BOLD),0,2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //ss.setSpan(new AbsoluteSizeSpan(30),0,2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        Log.i("neo", edt_text.getText().toString());
    }

    public void finish(View view){
        setResult(RESULT_OK);
        finish();
    }


}
