package com.approquo.rxandroiddemo.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.approquo.rxandroiddemo.model.Data;
import com.approquo.rxandroiddemo.R;
import com.approquo.rxandroiddemo.adapter.CardAdapter;
import com.approquo.rxandroiddemo.databinding.ActivityMainBinding;
import com.approquo.rxandroiddemo.model.Github;
import com.approquo.rxandroiddemo.service.GithubService;
import com.approquo.rxandroiddemo.service.ServiceFactory;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends Activity {

    CardAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        OnClickHandlers onClickHandlers = new OnClickHandlers();
        binding.setHandlers(onClickHandlers);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCardAdapter = new CardAdapter();
        mRecyclerView.setAdapter(mCardAdapter);
    }


    public class OnClickHandlers
    {
        public void onClickClear(View view) {
            mCardAdapter.clear();
        }

        public void onClickFetch(View view)
        {
            GithubService service = ServiceFactory.createRetrofitService(GithubService.class, GithubService.SERVICE_ENDPOINT);
            for(String login : Data.githubList)
            {
                service.getUser(login)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Github>() {
                            @Override
                            public final void onCompleted() {
                                // do nothing
                            }

                            @Override
                            public final void onError(Throwable e) {
                                Log.e("GithubDemo", e.getMessage());
                            }

                            @Override
                            public final void onNext(Github response) {
                                mCardAdapter.addData(response);
                            }
                        });
            }
        }
    }
}
