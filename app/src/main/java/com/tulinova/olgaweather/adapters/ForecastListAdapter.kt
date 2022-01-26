package com.tulinova.olgaweather.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tulinova.olgaweather.data.ForecastEntry
import com.tulinova.olgaweather.utils.IconsUtil
import com.tulinova.olgaweather.R
import com.tulinova.olgaweather.data.ForecastResponse
import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

private const val ITEM_VIEW_TYPE_HEADER = 1
private const val ITEM_VIEW_TYPE_ITEM = 2
private val timeFormatter: DateFormat = SimpleDateFormat("HH:mm")

class ForecastListAdapter(sourceList: List<ForecastEntry>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var forecastList: List<ForecastListItem>
    init {
        var outputFormatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        val groupedList = sourceList.groupBy { outputFormatter.format(it.date) }
        var myList = ArrayList<ForecastListItem>()

        for (i in groupedList.keys) {
            myList.add(ForecastListItem.DayHeader(i))
            for (v in groupedList.getValue(i)) {
                myList.add(ForecastListItem.ForecastItem(v))
            }
        }

        // myList



        //withContext(Dispatchers.Main) {
        forecastList = myList
        //}

    }

    fun addHeaderAndSumbitList(list: List<ForecastEntry>) {
        // adapterScope.launch {






    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_forecast_section_header, parent, false)
                HeaderHolder(itemView)
            }
            ITEM_VIEW_TYPE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_forecast_list, parent, false)
                ForecastEntryHolder(itemView)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderHolder -> {
                val item = forecastList.get(position) as ForecastListItem.DayHeader
                holder.dayNameTextView.text = item.dayName
            }
            is ForecastEntryHolder -> {
                val item = forecastList.get(position) as ForecastListItem.ForecastItem
                holder.timeTextview.text = timeFormatter.format(item.forecastEntry.date)
                holder.descriptionTextView.text =
                    item.forecastEntry.weather.getOrNull(0)?.description
                holder.temperatureTextView.text = "${item.forecastEntry.main.temp.roundToInt()}℃"
                holder.weatherImageView.setImageResource(
                    IconsUtil.getIconResId(
                        item.forecastEntry.weather.getOrNull(
                            0
                        )?.icon
                    )
                )
            }
        }


    }

    override fun getItemViewType(position: Int): Int {

        return when (forecastList[position]) {
            is ForecastListItem.DayHeader -> ITEM_VIEW_TYPE_HEADER
            is ForecastListItem.ForecastItem -> ITEM_VIEW_TYPE_ITEM
        }
    }


    override fun getItemCount(): Int {
        return forecastList.size
    }

    sealed class ForecastListItem {
        data class ForecastItem(val forecastEntry: ForecastEntry) : ForecastListItem() {
            override val id = forecastEntry.dt
        }

        data class DayHeader(val dayName: String) : ForecastListItem() {
            override val id = dayName.hashCode().toLong()
        }

        abstract val id: Long
    }

    class ForecastEntryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weatherImageView: ImageView = itemView.findViewById(R.id.image_forecast_entry)
        val temperatureTextView: TextView = itemView.findViewById(R.id.text_temperature)
        val timeTextview: TextView = itemView.findViewById(R.id.text_forecast_date)
        val descriptionTextView: TextView =
            itemView.findViewById(R.id.text_forecast_description)
    }

    class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayNameTextView: TextView = itemView.findViewById(R.id.text_forecast_day)

    }

}