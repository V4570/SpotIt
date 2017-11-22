package uom.android.dev;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView) findViewById(R.id.resultsListView);

        final String apikey = "d22eee316a280d357babf1f7b1e56205";

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl httpUrl = original.url();

                        HttpUrl newhttpUrl = httpUrl.newBuilder()
                                .addQueryParameter("api_key", apikey).build();

                        Request.Builder requestBuilder = original.newBuilder().url(newhttpUrl);

                        Request request = requestBuilder.build();

                        return chain.proceed(request);
                    }
                })
                .build();


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://ws.audioscrobbler.com/2.0/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LastFmClient client = retrofit.create(LastFmClient.class);
        Call<List<MatchSongSearch>> call = client.findSong("believe");

        call.enqueue(new Callback<List<MatchSongSearch>>() {
            @Override
            public void onResponse(Call<List<MatchSongSearch>> call, Response<List<MatchSongSearch>> response) {
                List<MatchSongSearch> songs = response.body();
                SearchResultsAdapter searchAdapter = new SearchResultsAdapter(SearchActivity.this,  songs);
                listView.setAdapter(searchAdapter);
            }

            @Override
            public void onFailure(Call<List<MatchSongSearch>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
