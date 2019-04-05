package com.kaity.dev.cuongproject.fragment;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.kaity.dev.cuongproject.GameScreenActivity;
import com.kaity.dev.cuongproject.R;
import com.kaity.dev.cuongproject.adapter.CommonRecyclerViewAdapter;
import com.kaity.dev.cuongproject.adapter.ViewHolder;
import com.kaity.dev.cuongproject.utils.DensityUtils;
import com.kaity.dev.cuongproject.view.SquareGridSpacingItemDecoration;

import java.util.Arrays;

import me.drakeet.mailotto.Mail;
import me.drakeet.mailotto.Mailbox;

public class GameScreenFragment extends Fragment implements View.OnTouchListener {

    public static final int DIRECTION_DOWN = 100;
    public static final int DIRECTION_UP = -100;
    public static final int DIRECTION_RIGHT = 101;
    public static final int DIRECTION_LEFT = -101;


    public static GameScreenFragment newInstance(Bitmap[] bitmapBricks, int[][] goalStatus) {
        Bundle args = new Bundle();
        args.putSerializable("bricks", bitmapBricks);
        args.putSerializable("goal", goalStatus);
        GameScreenFragment fragment = new GameScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public GameScreenFragment(){

    }
    private int mSpanCount;
    private Bitmap[] mBitmapBricks;
    private int[][] mGoalStatus;
    private int[][] mCurrStatus;
    private Point mCurrBlankPos;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        mGoalStatus = (int[][]) args.getSerializable("goal");
        mCurrStatus = copyStatus(mGoalStatus);
        mCurrBlankPos = findBlankBrick(mCurrStatus);
        mBitmapBricks = (Bitmap[]) args.getSerializable("bricks");
        mSpanCount = mGoalStatus.length;

        generateRandomStatus();
        Mailbox.getInstance().post(new Mail(GameScreenActivity.MAIL_GAME_STARTED, GameScreenActivity.class, GameScreenFragment.class));

        View view = inflater.inflate(R.layout.fragment_game, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_bricks);
        assert mRecyclerView != null;
        mRecyclerView.setAdapter(new CommonRecyclerViewAdapter<Bitmap>(getContext(), mBitmapBricks, R.layout.brick_item) {
            @Override
            public void onItemViewAppear(ViewHolder holder, Bitmap bitmap, int position) {
                int row = position / mSpanCount;
                int column = position % mSpanCount;
                holder.setViewImageBitmap(R.id.iv_brick, mBitmapBricks[mCurrStatus[row][column]]);
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), mSpanCount) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecyclerView.addItemDecoration(new SquareGridSpacingItemDecoration(getContext(), R.dimen.brick_divider_width, mGoalStatus.length));
        mRecyclerView.setOnTouchListener(this);
    }

    private float mLastX = 0;
    private float mLastY = 0;
    private int mLastDirection = 0;
    private boolean mMovedOnTouch = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = rawX;
                mLastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) (rawX - mLastX);
                int offsetY = (int) (rawY - mLastY);
                int slop = DensityUtils.dp2px(getContext(), 80);
                if (Math.abs(offsetX) > slop || Math.abs(offsetY) > slop
                        && !(Math.abs(offsetX) > slop && Math.abs(offsetY) > slop)) {
                    int direction = 0;
                    if (Math.abs(offsetX) > slop) {
                        direction = offsetX > 0 ? DIRECTION_LEFT : DIRECTION_RIGHT;
                    } else if (Math.abs(offsetY) > slop) {
                        direction = offsetY > 0 ? DIRECTION_UP : DIRECTION_DOWN;
                    }

                    if (!mMovedOnTouch || direction == -mLastDirection) {
                        if (moveBlankBrick(direction, true)) {
                            handleMoved();
                            mLastDirection = direction;
                            mMovedOnTouch = true;
                            mLastX = rawX;
                            mLastY = rawY;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mMovedOnTouch = false;
                break;
        }
        return false;
    }

    private void handleMoved() {
        Mailbox.getInstance().post(new Mail(GameScreenActivity.MAIL_STEP_MOVED, GameScreenActivity.class, GameScreenFragment.class));

        if (isWon()) {
            Mailbox.getInstance().post(new Mail(GameScreenActivity.MAIL_GAME_WON, GameScreenActivity.class, GameScreenFragment.class));
        }
    }

    private boolean isWon() {
        for (int i = 0; i < mSpanCount; i++) {
            if (!Arrays.equals(mCurrStatus[i], mGoalStatus[i])) {
                return false;
            }
        }
        return true;
    }

    private Point findBlankBrick(int[][] status) {
        for (int i = 0; i < status.length; i++) {
            for (int j = 0; j < status[i].length; j++) {
                if (status[i][j] == GameScreenActivity.BLANK_BRICK) {
                    return new Point(i, j);
                }
            }
        }
        return new Point(-1, -1);
    }

    private void generateRandomStatus() {
        int count = (int) (40 + Math.random() * 20);
        int lastD = -1;
        for (int i = 0; i < count; i++) {
            int d;
            do {
                d = (int) (Math.random() * 4);
            } while (lastD == d);
            lastD = d;

            int direction = 0;
            switch (d) {
                case 0:
                    direction = DIRECTION_UP;
                    break;
                case 1:
                    direction = DIRECTION_DOWN;
                    break;
                case 2:
                    direction = DIRECTION_LEFT;
                    break;
                case 3:
                    direction = DIRECTION_RIGHT;
                    break;
            }
            moveBlankBrick(direction, false);
        }
    }

    private boolean moveBlankBrick(int direction, boolean shouldNotifyView) {
        Point p = mCurrBlankPos;
        int newX = p.x;
        int newY = p.y;
        if (direction == DIRECTION_UP || direction == DIRECTION_DOWN) {
            newY += direction == DIRECTION_UP ? -1 : 1;
        } else if (direction == DIRECTION_LEFT || direction == DIRECTION_RIGHT) {
            newX += direction == DIRECTION_LEFT ? -1 : 1;
        } else {
            return false;
        }
        if (newX >= mSpanCount || newX < 0 || newY >= mSpanCount || newY < 0) {
            return false;
        }

        int tInt = mCurrStatus[p.y][p.x];
        mCurrStatus[p.y][p.x] = mCurrStatus[newY][newX];
        mCurrStatus[newY][newX] = tInt;

        if (shouldNotifyView) {
            int posA = p.y * mSpanCount + p.x;
            int posB = newY * mSpanCount + newX;
            if (posA > posB) {
                int t = posA;
                posA = posB;
                posB = t;
            }
            mRecyclerView.getAdapter().notifyItemMoved(posB, posA);
            mRecyclerView.getAdapter().notifyItemMoved(posA + 1, posB);
        }

        p.x = newX;
        p.y = newY;
        return true;
    }

    private int[][] copyStatus(int[][] status) {
        int[][] result = new int[status.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = status[i].clone();
        }
        return result;
    }
}
