package com.tulinova.olgaweather.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tulinova.olgaweather.ForecastFragment
import com.tulinova.olgaweather.TodayWeatherFragment


const val TODAY_TAB_INDEX = 0
const val FORECAST_TAB_INDEX = 1
const val TAB_COUNT = 2


class ViewPagerAdapter(activity : FragmentActivity) :
    FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        return when(position){
            TODAY_TAB_INDEX -> TodayWeatherFragment()
            FORECAST_TAB_INDEX -> ForecastFragment()
            else -> throw IndexOutOfBoundsException()
        }

    }

//    override fun getPageTitle(position: Int): CharSequence? {
//        return when(position){
//            TODAY_TAB_INDEX -> activity.getString(R.string.tab_today_title)
//            FORECAST_TAB_INDEX -> context.getString(R.string.tab_forecast_title)
//            else -> throw IndexOutOfBoundsException()
//        }
//    }

    override fun getItemCount(): Int {
        return TAB_COUNT
    }
}

