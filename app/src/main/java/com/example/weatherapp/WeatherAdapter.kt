package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class WeatherAdapter(context : Context, private var weatherList : List<WeatherModel>) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_weather_details,parent,false)
        return ViewHolder(view)
    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.textViewWeatherTime.text = weatherList[position].time
        holder.textViewWeatherTemperature.text = weatherList[position].temperature + "°C"
        holder.textViewWindSpeed.text = weatherList[position].windSpeed + "Km/h"

        val imageUrl = weatherList[position].icon
        Picasso.get()
            .load("http://$imageUrl")
            .fit()
            .centerCrop()
            .into(holder.imageViewWeatherCondition)

        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val outputDateFormat = SimpleDateFormat("hh:mm aa")

        try {
            var date = Date()
            date = inputDateFormat.parse(weatherList[position].time) as Date
            holder.textViewWeatherTime.text = outputDateFormat.format(date)
        }catch (e:ParseException){
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return weatherList.size
    }





    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {


            var textViewWeatherTime : TextView = itemView.findViewById(R.id.tv_weather_time)
            var textViewWeatherTemperature : TextView = itemView.findViewById(R.id.cv_tv_weather_temperature)
            var textViewWindSpeed : TextView = itemView.findViewById(R.id.tv_windSpeed)
            var imageViewWeatherCondition : ImageView = itemView.findViewById(R.id.cv_iv_weather_condition)


    }



}