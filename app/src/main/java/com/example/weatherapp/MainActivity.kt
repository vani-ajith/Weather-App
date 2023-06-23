package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var relativeLayoutHome: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var imageViewSearch: ImageView
    private lateinit var imageViewIcon: ImageView
    private lateinit var imageViewBackground: ImageView
    private lateinit var textViewCityName: TextView
    private lateinit var textViewWeatherTemperature: TextView
    private lateinit var textViewWeatherCondition: TextView
    private lateinit var textInputEditTextCityName: TextInputEditText
    private lateinit var recyclerViewTodayWeatherList: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter
    lateinit var weatherList : ArrayList<WeatherModel>
    private lateinit var locationManager : LocationManager
    private var PERMISSION_CODE = 1
    private var cityName = " "

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContentView(R.layout.activity_main)


        relativeLayoutHome = findViewById(R.id.relative_layout_home)
        progressBar = findViewById(R.id.progress_bar)
        textViewCityName = findViewById(R.id.tv_cityName)
        imageViewSearch = findViewById(R.id.iv_search)
        imageViewIcon = findViewById(R.id.iv_cloud)
        imageViewBackground = findViewById(R.id.iv_background)
        textViewWeatherTemperature = findViewById(R.id.tv_weather_temperature)
        textViewWeatherCondition = findViewById(R.id.tv_weather_condition)
        textInputEditTextCityName = findViewById(R.id.editText_city_name)
        recyclerViewTodayWeatherList = findViewById(R.id.rv_weather_forecast)

        weatherList = ArrayList()
        weatherAdapter = WeatherAdapter(this,weatherList)
        recyclerViewTodayWeatherList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        recyclerViewTodayWeatherList.adapter = weatherAdapter

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_CODE)
        }

        val location : Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            cityName = getCityName(location.latitude,location.longitude)
            getWeatherInfo(cityName)
        }else{
            cityName = "Bengaluru"
            getWeatherInfo(cityName)
        }


        imageViewSearch.setOnClickListener{

            val city = textInputEditTextCityName.text.toString()
            if(city.isEmpty()){
                Toast.makeText(this,"Please enter the city name",Toast.LENGTH_LONG).show()
            }else{
                textViewCityName.text = cityName
                getWeatherInfo(city)

            }
        }



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_CODE){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission granted...",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"Please provide the permissions",Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }


    private fun getCityName(lat : Double, lon : Double) : String{

        var cityName = "Not Found"
        val geocoder = Geocoder(baseContext, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 10) as List<Address>
            for (adrs in addresses) {
                if (adrs.locality != null && adrs.locality.isNotEmpty()) {
                    cityName = adrs.locality
                    break
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cityName

//        var cityName = "Not Found"
//        val geocoder = Geocoder(baseContext, Locale.getDefault())
//        try {
//            val addresses : List<Address> = geocoder.getFromLocation(lat,lon,10) as List<Address>
//            for (adrs in addresses) {
//               if(adrs != null){
//                   val city = adrs.locality
//                   if(city != null && !city.equals("")){
//                       cityName = city
//                   }
//               }else{
//                   Log.d(TAG, "getCityName: CITY NOT FOUND")
//                   Toast.makeText(this,"User city Not Found",Toast.LENGTH_LONG).show()
//               }
//            }
//        }catch (e : IOException){
//            e.printStackTrace()
//        }
//        return cityName
    }



    private fun getWeatherInfo(cityName : String){
        val url = "http://api.weatherapi.com/v1/forecast.json?key=a764b76046ca44f7923105155232206&q=$cityName&days=1&aqi=yes&alerts=yes\n"
        textInputEditTextCityName.setText(cityName)

        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
                progressBar.visibility = View.GONE
                relativeLayoutHome.visibility = View.VISIBLE
                weatherList.clear()

            val temperature = response.getJSONObject("current").getString("temp_c")
            textViewWeatherTemperature.text = temperature +"°C"
            val is_Day = response.getJSONObject("current").getInt("is_day")
            val condition = response.getJSONObject("current").getJSONObject("condition").getString("text")
            val conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon")
            Picasso.get().load("http://$conditionIcon").into(imageViewIcon)
            textViewWeatherCondition.text = condition
//            if(is_Day == 1){
//                Picasso.get().load("").into(imageViewBackground)
//            }else{
//                Picasso.get().load("").into(imageViewBackground)
//            }

            val forecastObj = response.getJSONObject("forecast")
            val forecast = forecastObj.getJSONArray("forecastday").getJSONObject(0)
            val hourArray : JSONArray = forecast.getJSONArray("hour")

            for( i in 0 until hourArray.length()){
                val hourObj : JSONObject = hourArray.getJSONObject(i)
                val time = hourObj.getString("time")
                val temperature = hourObj.getString("temp_c")
                val img = hourObj.getJSONObject("condition").getString("icon")
                val wind = hourObj.getString("wind_kph")
                weatherList.add(WeatherModel(time,temperature,img,wind))
             }
            weatherAdapter.notifyDataSetChanged()
                                                                                 },
            {
                Toast.makeText(this,"Please enter valid city name",Toast.LENGTH_LONG).show()
        })

        requestQueue.add(jsonObjectRequest)
    }
}