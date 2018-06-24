package tcd.android.com.codecombatmobile.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import tcd.android.com.codecombatmobile.R;
import tcd.android.com.codecombatmobile.data.game.Session;
import tcd.android.com.codecombatmobile.util.TimeUtil;

public class CalendarGraph extends View {

    private static final int DAY_TEXT_SIZE = 24;

    @Nullable
    private List<Session> mSessions;
    private int mCellWidth = 0, mCellHeight = 0;
    private int mCellTotal;
    private int mRowCellTotal;
    private int mColCellTotal = 7;

    private Paint mPaint;
    private Paint mTextPaint;
    private int[] mCellColors;

    @NonNull
    private Record[] mRecords = new Record[0];

    public CalendarGraph(Context context) {
        super(context);
        init(null, 0);
    }

    public CalendarGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CalendarGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint();

        mTextPaint = new Paint(TextPaint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(DAY_TEXT_SIZE);

        Context context = getContext();
        mCellColors = new int[]{
                ContextCompat.getColor(context, R.color.record_level_1),
                ContextCompat.getColor(context, R.color.record_level_2),
                ContextCompat.getColor(context, R.color.record_level_3),
                ContextCompat.getColor(context, R.color.record_level_4),
                ContextCompat.getColor(context, R.color.record_level_5)
        };
    }

    public void setLevelSessions(@NonNull List<Session> sessions) {
        mSessions = sessions;
        prepareCellData();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        prepareCellData();
    }

    private void prepareCellData() {
        if (mSessions == null || getWidth() == 0) {
            return;
        }

        // calculate number of cells
        mCellHeight = (getHeight() - DAY_TEXT_SIZE * 2) / mColCellTotal;
        mRowCellTotal = getWidth() / mCellHeight;
        mCellWidth = getWidth() / mRowCellTotal;

        generateCellData();
        invalidate();
    }

    private void generateCellData() {
        if (mSessions == null) {
            return;
        }

        long start = getStartDate();
        long end = getEndOfToday();

        // init cell records
        mCellTotal = TimeUtil.getDayOfYear(end) - TimeUtil.getDayOfYear(start);
        mRecords = new Record[mCellTotal];
        Calendar curCal = Calendar.getInstance();
        curCal.setTimeZone(TimeZone.getDefault());
        curCal.setTimeInMillis(start);
        for (int i = 0; i < mCellTotal; i++) {
            mRecords[i] = new Record(curCal.get(Calendar.DAY_OF_MONTH), curCal.get(Calendar.MONTH));
            curCal.add(Calendar.DAY_OF_YEAR, 1);
        }

        for (Session session : mSessions) {
            long timeChanged = session.getTimeChanged();
            if (timeChanged > start && timeChanged < end) {
                int diff = TimeUtil.getDayOfYear(timeChanged) - TimeUtil.getDayOfYear(start);
                mRecords[diff].levelCount++;
            }
        }
    }

    private long getStartDate() {
        Calendar start = Calendar.getInstance();
        start.setTimeZone(TimeZone.getDefault());
        start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
        start.add(Calendar.WEEK_OF_YEAR, -(mRowCellTotal - 1));
        start.clear(Calendar.HOUR);
        start.clear(Calendar.MINUTE);
        start.clear(Calendar.SECOND);
        return start.getTimeInMillis();
    }

    private long getEndOfToday() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTimeZone(TimeZone.getDefault());
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        return todayEnd.getTimeInMillis();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mCellTotal; i++) {
            Record record = mRecords[i];
            int colorIndex = record.levelCount < mCellColors.length - 1 ? record.levelCount : mCellColors.length - 1;
            mPaint.setColor(mCellColors[colorIndex]);

            // draw cell
            int left = i / mColCellTotal * mCellWidth;
            int top = i % mColCellTotal * mCellHeight + DAY_TEXT_SIZE * 2;
            int right = left + mCellWidth - 5;
            int bottom = top + mCellHeight - 5;
            canvas.drawRect(left, top, right, bottom, mPaint);

            // draw the day
            String day = String.valueOf(record.day);
            float textWidth = mTextPaint.measureText(day);
            canvas.drawText(day, (right + left - textWidth) / 2, (top + bottom + DAY_TEXT_SIZE) / 2, mTextPaint);
        }

        // month labels
        if (mCellTotal > 0) {
            drawMonth(canvas, 6);
            for (int i = 14 - 1; i < mCellTotal; i += 7) {
                if (mRecords[i].day >= 7 && mRecords[i].day < 14) {
                    drawMonth(canvas, i);
                }
            }
        }
    }

    private void drawMonth(Canvas canvas, int index) {
        int left = index / mColCellTotal * mCellWidth;
        int right = left + mCellWidth - 5;
        String month = TimeUtil.getMonthName(mRecords[index].month);
        float textWidth = mTextPaint.measureText(month);
        canvas.drawText(month, (left + right - textWidth) / 2, 15 + DAY_TEXT_SIZE / 2, mTextPaint);
    }

    private static class Record {
        private int levelCount;
        private int day;
        private int month;

        public Record(int day, int month) {
            this.levelCount = 0;
            this.day = day;
            this.month = month;
        }
    }
}
