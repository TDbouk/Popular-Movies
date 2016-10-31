package tdbouk.udacity.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PopularMoviesSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static PopularMoviesSyncAdapter sPopularMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sPopularMovieSyncAdapter == null) {
                sPopularMovieSyncAdapter = new PopularMoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopularMovieSyncAdapter.getSyncAdapterBinder();
    }
}