package net.kwmt27.codesearch.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakewharton.rxbinding.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;

import net.kwmt27.codesearch.R;
import net.kwmt27.codesearch.entity.GithubRepoEntity;
import net.kwmt27.codesearch.presenter.repolist.IRepositoryListPresenter;
import net.kwmt27.codesearch.presenter.repolist.RepositoryListPresenter;
import net.kwmt27.codesearch.util.Logger;
import net.kwmt27.codesearch.util.ToastUtil;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;


/**
 * レポジトリ一覧
 */
public class RepositoryListFragment extends Fragment implements RepositoryListPresenter.IRepositoryListView {

    public static final String TAG = RepositoryListFragment.class.getSimpleName();
    private IRepositoryListPresenter mPresenter;
    private GitHubRepoListAdapter mGitHubRepoListAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private Subscription mSubscription;
    private boolean mIsCalled = false;
    private boolean mAddedAd = false;

    private View mErrorLayout;
    private View mProgressLayout;

    public static RepositoryListFragment newInstance() {
        return new RepositoryListFragment();
    }

    public RepositoryListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new RepositoryListPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repository_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        mSubscription.unsubscribe();
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    public void setupComponents(View view, Bundle savedInstanceState) {
        //getActivity().setTitle("レポジトリ一覧");

        mProgressLayout = view.findViewById(R.id.progress_layout);
        mErrorLayout = view.findViewById(R.id.error_layout);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.github_repo_list);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), R.drawable.divider));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mGitHubRepoListAdapter = new GitHubRepoListAdapter(getActivity().getApplicationContext(), new OnItemClickListener<GitHubRepoListAdapter, GithubRepoEntity>() {
            @Override
            public void onItemClick(GitHubRepoListAdapter adapter, int position, GithubRepoEntity repo) {
                DetailActivity.startActivity(getActivity(), repo.getName(), repo.getHtmlUrl(), repo);
            }
        });
        mRecyclerView.setAdapter(mGitHubRepoListAdapter);

        rxRecyclerViewScrollSubscribe();
    }



    private void rxRecyclerViewScrollSubscribe() {
        mSubscription = RxRecyclerView.scrollEvents(mRecyclerView).subscribe(
                new Action1<RecyclerViewScrollEvent>() {
                    @Override
                    public void call(RecyclerViewScrollEvent event) {
                        int totalItemCount = mLayoutManager.getItemCount();
                        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                        if (totalItemCount - 1 <= lastVisibleItemPosition) {
                            if(mIsCalled){
                                return;
                            }
                            mIsCalled = true;
                            Logger.d("onLastVisible");
                            mSubscription.unsubscribe();

                            mPresenter.onScrollToBottom();
                        }
                    }
                }
        );
    }


    @Override
    public void updateGitHubRepoListView(List<GithubRepoEntity> githubRepoEntities) {
        mIsCalled = false;
        rxRecyclerViewScrollSubscribe();
        mGitHubRepoListAdapter.setGithubRepoEntityList(githubRepoEntities);
        mGitHubRepoListAdapter.notifyDataSetChanged();

        if(!mAddedAd) {
            mGitHubRepoListAdapter.addAdItemTypeThenNotify();
            mAddedAd = true;
        }

    }

    @Override
    public void showProgress() {
        mProgressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressLayout.setVisibility(View.GONE);
    }

    @Override
    public void showError() {
        mErrorLayout.setVisibility(View.VISIBLE);

        Button button = (Button) mErrorLayout.findViewById(R.id.reload_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mErrorLayout.setVisibility(View.GONE);
                mPresenter.onClickReloadButton();
            }
        });

    }
    @Override
    public void showProgressOnScroll() {
        mGitHubRepoListAdapter.addProgressItemTypeThenNotify();
    }

    @Override
    public void hideProgressOnScroll() {
        mGitHubRepoListAdapter.removeProgressItemTypeThenNotify();
    }

    @Override
    public void showErrorOnScroll() {
        ToastUtil.show(getActivity().getApplicationContext(), "データ取得に失敗しました。");
    }

}