package xxx.test.allapplication.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yyydjk.library.DropDownMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
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
//        int len = app_name.length();
//        String str = "14.80万";
//        int  index = str.indexOf("子");
//        Log.i("neo","index = "+index);
//        SpannableString ss = new SpannableString("14.80万");
//        ss.setSpan(new AbsoluteSizeSpan(13,true), index, str.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        text.setText(ss);
        //ss.setSpan(new ForegroundColorSpan(Color.BLUE),1,2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //ss.setSpan(new BackgroundColorSpan(ContextCompat.getColor(this,R.color.colorGold)),0,3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //ss.setSpan(new StyleSpan(BOLD),0,2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        //ss.setSpan(new AbsoluteSizeSpan(30),0,2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);


        String comment = "xxx这是一辆2010年10月上牌行驶了14.74万公里的好车。奔驰的外观真的是没什么可说的，尤其10款的，完美。奔驰的外观真的是没什么可说的，尤其10款的，完美xxx";
        SpannableString ss = new SpannableString(comment);
        Drawable quotesLeft = ContextCompat.getDrawable(this,R.drawable.double_quotes_left);
        Drawable quotesRight = ContextCompat.getDrawable(this,R.drawable.double_quotes_right);
        quotesLeft.setBounds(0, 0, quotesLeft.getIntrinsicWidth(), quotesLeft.getIntrinsicHeight());
        quotesRight.setBounds(0, 0, quotesRight.getIntrinsicWidth(), quotesRight.getIntrinsicHeight());
        ss.setSpan(new ImageSpan(quotesLeft), 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new ImageSpan(quotesRight), comment.length()-3, comment.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        String str = "这是一辆2010年10月上牌行驶了14.74万公里的好车。奔驰的外观真的是没什么可说的，尤其10款的，完美。奔驰的外观真的是没什么可说的，尤其10款的，完美";
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(str);
        ImageSpan imageSpan = new ImageSpan(this, R.drawable.double_quotes_left, DynamicDrawableSpan.ALIGN_BOTTOM);
        ImageSpan imageSpan2 = new ImageSpan(this, R.drawable.double_quotes_right,DynamicDrawableSpan.ALIGN_BASELINE);
        spannableString.setSpan(imageSpan, 0, 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(imageSpan2, 10, 11, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        text.setText(spannableString);
    }

    public void finish(View view){
        setResult(RESULT_OK);
        finish();
    }


}
