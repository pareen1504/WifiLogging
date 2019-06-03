package com.pd.wifilogging.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.pd.wifilogging.R
import com.pd.wifilogging.model.database.ListData

@BindingAdapter("setWifiName")
fun TextView.setWifiname(item: ListData?) {
    item?.let {
        text = item.wifiname
    }
}

@BindingAdapter("setStrengthIndicator")
fun ImageView.setStrengthIndicator(item: ListData?) {
    item?.let {
        setImageResource(
            when (item.wifiStrength) {
                in 0 downTo -55 -> R.drawable.ic_signal_wifi_4_bar_black_24dp
                in -55 downTo -75 -> R.drawable.ic_signal_wifi_3_bar_black_24dp
                in -75 downTo -85 -> R.drawable.ic_signal_wifi_2_bar_black_24dp
                in -85 downTo -100 -> R.drawable.ic_signal_wifi_1_bar_black_24dp
                else -> R.drawable.ic_signal_wifi_0_bar_black_24dp
            }
        )
    }
}

@BindingAdapter("setStrengthValue")
fun TextView.setStrengthValue(item: ListData?){
    item?.let {
        text = item.wifiStrength.toString()
    }
}

@BindingAdapter("setStrengthStatus")
fun TextView.setStrengthStatus(item: ListData?) {
    item?.let {
        when (item.wifiStrength) {
            in 0 downTo -55 -> text = context.getString(R.string.excellent)
            in -55 downTo -75 -> text = context.getString(R.string.fair)
            in -75 downTo -85 -> text = context.getString(R.string.weak)
            in -85 downTo -100 -> text = context.getString(R.string.poor)
            else -> text = context.getString(R.string.no_signal)
        }
    }
}