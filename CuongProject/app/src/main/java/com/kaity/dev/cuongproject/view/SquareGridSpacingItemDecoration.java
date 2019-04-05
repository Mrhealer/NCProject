package com.kaity.dev.cuongproject.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SquareGridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpacing;
    private int mSpanCount;

    public SquareGridSpacingItemDecoration(int spacing, int spanCount) {
        mSpacing = spacing;
        mSpanCount = spanCount;
    }

    public SquareGridSpacingItemDecoration(@NonNull Context context, @DimenRes int spacingId, int spanCount) {
        this(context.getResources().getDimensionPixelSize(spacingId), spanCount);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int row = position / mSpanCount;
        int column = position % mSpanCount;

        outRect.set(mSpacing, mSpacing / 2, mSpacing, mSpacing / 2);
    }
}
