package com.xcyoung.recyclebauble.footer;

public interface BaseLoadFooter {
    int STATE_LOADING = 0;
    int STATE_COMPLETE = 1;
    int STATE_NOMORE = 2;

    void loading();

    void loadCompelete();

    void noMore();
}
