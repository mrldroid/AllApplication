package xxx.test.allapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import xxx.test.allapplication.R;
import xxx.test.allapplication.adapter.MyAdapter2;
import xxx.test.allapplication.model.User;

public class RecyclerViewActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<User> list = new ArrayList<>();
        for(int i = 0 ; i < 20; i++){
            User user = new User();
            user.name = "neo"+i;
            list.add(user);
        }

        MyAdapter2 myAdapter = new MyAdapter2(this,list);
        mRecyclerView.setAdapter(myAdapter);
    }
}
