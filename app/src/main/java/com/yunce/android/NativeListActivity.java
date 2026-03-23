package com.yunce.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NativeListActivity extends AppCompatActivity {

    private static final int INITIAL_NORMAL_ITEM_COUNT = 54;
    private static final int PAGE_NORMAL_ITEM_COUNT = 54;
    private static final int MAX_NORMAL_ITEM_COUNT = 200;

    private RecyclerView recyclerView;
    private NativeListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private int normalItemCount = INITIAL_NORMAL_ITEM_COUNT;
    private boolean isLoadingMore = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_list);
        initView();
        initListeners();
    }

    private void initView() {
        Button btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> finish());

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        recyclerView = findViewById(R.id.recycler_native_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Add item spacing to avoid "double border lines" between adjacent items.
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(dpToPx(6)));
        adapter = new NativeListAdapter(mainHandler);
        adapter.setNormalItemCount(normalItemCount);
        recyclerView.setAdapter(adapter);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private static class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacePx;

        VerticalSpaceItemDecoration(int spacePx) {
            this.spacePx = Math.max(0, spacePx);
        }

        @Override
        public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view,
                                     @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.set(0, spacePx, 0, spacePx);
        }
    }

    private void initListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> refreshList());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) {
                    return;
                }
                if (isLoadingMore) {
                    return;
                }
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) {
                    return;
                }
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                int totalCount = adapter.getItemCount();
                if (totalCount > 0 && lastVisible >= totalCount - 3) {
                    loadMore();
                }
            }
        });
    }

    private void refreshList() {
        isLoadingMore = false;
        normalItemCount = INITIAL_NORMAL_ITEM_COUNT;

        adapter.release();
        adapter.setNormalItemCount(normalItemCount);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);

        mainHandler.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 600);
    }

    private void loadMore() {
        if (normalItemCount >= MAX_NORMAL_ITEM_COUNT) {
            Toast.makeText(this, "No more items", Toast.LENGTH_SHORT).show();
            return;
        }

        isLoadingMore = true;

        int oldTotalItemCount = adapter.getItemCount();
        int newNormalCount = Math.min(MAX_NORMAL_ITEM_COUNT, normalItemCount + PAGE_NORMAL_ITEM_COUNT);
        if (newNormalCount == normalItemCount) {
            isLoadingMore = false;
            return;
        }

        adapter.setNormalItemCount(newNormalCount);
        int newTotalItemCount = adapter.getItemCount();

        if (newTotalItemCount > oldTotalItemCount) {
            adapter.notifyItemRangeInserted(oldTotalItemCount, newTotalItemCount - oldTotalItemCount);
        } else {
            adapter.notifyDataSetChanged();
        }
        normalItemCount = newNormalCount;
        isLoadingMore = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.release();
        }
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
        }
    }
}

