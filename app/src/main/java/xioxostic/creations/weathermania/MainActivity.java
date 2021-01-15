package xioxostic.creations.weathermania;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    UTIL util;

    TextView mWeatherDetails, mQuoteTExt,mHumidity,mTempText,mFeelsLike,mWindSpeed,mSunRise,mSunSet,mPressure,mCloud,mLocation,mLastUpdatedTime;
    EditText mInputText;
    ImageView mCloudsImage,mMainWeatherIcon;
    Button mSearchBtn,mBackButton;
    LinearLayout mSearchBar;
    RelativeLayout mMainCurrentWeatherDataLayout,rootView;
    LottieAnimationView mWeatherLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialization();
    }

    private void initialization(){

        rootView=findViewById(R.id.rootView);

        mWeatherLoading=findViewById(R.id.lodaindAnimation);
        mQuoteTExt=findViewById(R.id.quotesBar);
        mMainCurrentWeatherDataLayout=findViewById(R.id.allCurrentWeatherDataLayout);
        mSearchBar=findViewById(R.id.searchBar);
        mTempText =findViewById(R.id.tempText);
        mWeatherDetails = findViewById(R.id.weatherDes);
        mHumidity= findViewById(R.id.humidityText);
        mFeelsLike= findViewById(R.id.feelsLikeText);
        mWindSpeed= findViewById(R.id.windSpeedText);
        mInputText = findViewById(R.id.input_text);
        mSearchBtn = findViewById(R.id.action_btn);
        mBackButton= findViewById(R.id.backButton);
        mSunRise= findViewById(R.id.sunriseText);
        mSunSet=findViewById(R.id.sunsetText);
        mPressure=findViewById(R.id.pressureText);
        mCloud=findViewById(R.id.cloudText);
        mLocation=findViewById(R.id.locationText);
        mLastUpdatedTime=findViewById(R.id.lastUpdatedText);
        mCloudsImage=findViewById(R.id.yellowCloud);
        mMainWeatherIcon=findViewById(R.id.mainWeatherIcon);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                String input = mInputText.getText().toString();
                mWeatherLoading.setVisibility(View.VISIBLE);
                mSearchBar.setVisibility((View.GONE));
                util.printLogError(input);
                makeJSONRequest(input);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setBackgroundColor(Color.parseColor("#134655"));
                mMainCurrentWeatherDataLayout.setVisibility(View.GONE);
                mSearchBar.setVisibility(View.VISIBLE);
                mCloudsImage.setVisibility(View.VISIBLE);
                mQuoteTExt.setVisibility(View.VISIBLE);
            }
        });

        util=new UTIL();
    }

    private void makeJSONRequest(String cityName) {
        String tag_json_obj = "json_obj_req";

        String url = API.OPENWEATHER_CITY_URL + cityName +API.OPENWEATHER_API_ID;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setText(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        return Double.valueOf(twoDForm.format(d));
    }

    private void setText (JSONObject response){

        try {
            JSONObject main = response.getJSONObject("main");
            Double temp = main.getDouble("temp");
            mTempText.setText(roundTwoDecimals(temp-273)+" C");
            Double feels_like = main.getDouble("feels_like");
            mFeelsLike.setText("Feels like : "+roundTwoDecimals(feels_like-273)+" C");
            Double humidity = main.getDouble("humidity");
            mHumidity.setText(""+humidity+"%");
            Integer pressure = main.getInt("pressure");
            mPressure.setText(""+pressure);


            JSONArray weatherArray = response.getJSONArray("weather");
            JSONObject item = weatherArray.getJSONObject(0);
            String weatherdes = item.getString("main");
            mWeatherDetails.setText(weatherdes);

            String icon=item.getString("icon");
            Picasso.get().load(API.OPENWEATHER_IMAGEURL+icon+"@2x.png").into(mMainWeatherIcon);

            JSONObject wind =response.getJSONObject("wind");
            Double speed = wind.getDouble("speed");
            mWindSpeed.setText(roundTwoDecimals(speed)+" m/s");

            JSONObject sys =response.getJSONObject("sys");
            long sunrise = sys.getLong("sunrise");
            long sunset = sys.getLong("sunset");
            mSunRise.setText(""+getDate(sunrise*(long)1000,"h:mm aaa"));
            mSunSet.setText(""+getDate(sunset*(long)1000,"h:mm aaa"));



            String country = sys.getString("country");

            String city = response.getString("name");

            mLocation.setText(""+city+", "+country);

            long dt =response.getLong("dt");
            String datetime=getDate(dt*(long)1000,"h:mm aaa - MMM d, yyyy");
            mLastUpdatedTime.setText("Updated: "+datetime);

            JSONObject cloud =response.getJSONObject("clouds");
            int cloud_data = cloud.getInt("all");
            mCloud.setText(""+cloud_data+" %");


            mSearchBar.setVisibility(View.GONE);
            mQuoteTExt.setVisibility(View.GONE);
            mWeatherLoading.setVisibility(View.GONE);
            mMainCurrentWeatherDataLayout.setVisibility(View.VISIBLE);


             if (cloud_data<40){
                mCloudsImage.setVisibility(View.GONE);
            }
            if ( (dt>sunrise)&&(dt<sunset) ){
                rootView.setBackgroundColor(Color.parseColor("#07A5AA"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}