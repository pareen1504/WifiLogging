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
                in 0 downTo -50 -> R.drawable.ic_signal_wifi_4_bar_black_24dp
                in -50 downTo -70 -> R.drawable.ic_signal_wifi_3_bar_black_24dp
                in -70 downTo -80 -> R.drawable.ic_signal_wifi_2_bar_black_24dp
                in -80 downTo -100 -> R.drawable.ic_signal_wifi_1_bar_black_24dp
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