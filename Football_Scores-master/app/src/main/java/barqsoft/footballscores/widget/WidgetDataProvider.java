package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;
import barqsoft.footballscores.data.DatabaseContract;
/**
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = "WidgetDataProvider";

    private static final String[] MATCH_COLUMNS = {
                DatabaseContract.scores_table.MATCH_ID,
                DatabaseContract.scores_table.HOME_COL,
                DatabaseContract.scores_table.AWAY_COL,
                DatabaseContract.scores_table.HOME_GOALS_COL,
                DatabaseContract.scores_table.AWAY_GOALS_COL,
                DatabaseContract.scores_table.TIME_COL
        };
        // these indices must match the projection
        private static final int INDEX_MATCH_ID = 0;
        private static final int INDEX_HOME_COL = 1;
        private static final int INDEX_AWAY_COL = 2;
        private static final int INDEX_HOME_GOALS_COL = 3;
        private static final int INDEX_AWAY_GOALS_COL = 4;
        private static final int INDEX_TIME_COL = 5;

    List<String> match_ids = new ArrayList<>();
    List<String> homeTeams = new ArrayList<>();
    List<String> awayTeams = new ArrayList<>();
    List<String> scores = new ArrayList<>();
    List<String> times = new ArrayList<>();

    private Context mContext = null;
    private Intent mIntent = null;
    private Cursor c;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return times.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view;
        if(mContext.getString(R.string.no_scores_text).equals(times.get(position))){
            view = new RemoteViews(mContext.getPackageName(),
                    R.layout.no_scores_list_item);
            view.setTextViewText(R.id.no_data_textview, times.get(position));
            view.setTextColor(R.id.no_data_textview, Color.BLACK);
            Log.d(LOG_TAG, "times.size() = " + times.size());
        }
        else{
            view = new RemoteViews(mContext.getPackageName(),
                    R.layout.scores_list_item);
            view.setTextViewText(R.id.home_name, homeTeams.get(position));
            view.setTextColor(R.id.home_name, Color.BLACK);

            view.setTextViewText(R.id.away_name, awayTeams.get(position));
            view.setTextColor(R.id.away_name, Color.BLACK);

            view.setTextViewText(R.id.score_textview, scores.get(position));
            view.setTextColor(R.id.score_textview, Color.RED);

            view.setTextViewText(R.id.data_textview, times.get(position));
            view.setTextColor(R.id.data_textview, Color.BLACK);


        }
        return view;



//        return view;
}

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        Date todayDate = new Date(System.currentTimeMillis());//-86400000);
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = mformat.format(todayDate);
        c = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(), MATCH_COLUMNS, null,
                new String[]{dateString}, DatabaseContract.scores_table.DATE_COL + " ASC");
        if (c.getCount() > 0) {
            c.moveToFirst();
            homeTeams.clear();
            awayTeams.clear();
            scores.clear();
            times.clear();
            match_ids.clear();
            String homeTeam, awayTeam, score, time, matchId;
            do {
                homeTeam = c.getString(INDEX_HOME_COL);
                awayTeam = c.getString(INDEX_AWAY_COL);
                score = Utilities.getScores(c.getInt(INDEX_HOME_GOALS_COL), c.getInt(INDEX_AWAY_GOALS_COL));
                time = c.getString(INDEX_TIME_COL);
                matchId = c.getString(INDEX_MATCH_ID);
                homeTeams.add(homeTeam);
                awayTeams.add(awayTeam);
                scores.add(score);
                times.add(time);
                match_ids.add(matchId);
            }while (c.moveToNext());
        }else{
            times.clear();
            times.add(mContext.getString(R.string.no_scores_text));
        }
    }

}