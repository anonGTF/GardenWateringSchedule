package com.jessica.gardenwateringschedulesystem.ui.fragment.home

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.here.android.mpa.common.*
import com.here.android.mpa.mapping.Map
import com.jessica.gardenwateringschedulesystem.R
import com.jessica.gardenwateringschedulesystem.databinding.FragmentHomeBinding
import com.jessica.gardenwateringschedulesystem.utils.getCurrentDateTime
import com.jessica.gardenwateringschedulesystem.utils.toString
import java.io.File
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        MapEngine.getInstance().init(ApplicationContext(requireContext()), engineInitHandler)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timeExtra = arguments?.getString("time")
        if (timeExtra != null) {
            showNotif(timeExtra)
        }
        showNotif("08:00")
        initialize()
    }

    private fun initialize() {

    }

    private val engineInitHandler =
        OnEngineInitListener { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                val map = Map()
                binding.extMapview.map = map
                // more map initial settings
                map.setCenter(
                    GeoCoordinate(-7.2783266,112.7604853, 0.0),
                    Map.Animation.BOW
                )
                // Set the zoom level to the average between min and max
                map.zoomLevel = (map.maxZoomLevel + map.minZoomLevel) / 2
                val cachePath = StringBuilder()
                    .append(activity?.applicationContext?.getExternalFilesDir(null))
                    .append(File.separator)
                    .append(".here-maps")
                    .toString()
                MapSettings.setDiskCacheRootPath(cachePath)
            } else {
                Log.e("coba", "ERROR: Cannot initialize MapEngine $error")
            }
        }

    override fun onResume() {
        super.onResume()
        MapEngine.getInstance().onResume()
        binding.extMapview.onResume()
    }

    override fun onPause() {
        MapEngine.getInstance().onPause()
        super.onPause()
    }

    private fun showNotif(time: String) {
        val date = getCurrentDateTime().toString("dd/MM/yyyy")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, time.split(":")[0].toInt())
        calendar.set(Calendar.MINUTE, time.split(":")[1].toInt())
        calendar.add(Calendar.HOUR_OF_DAY, 7)
        val endTime = calendar.time.toString("HH:mm")
        val endText = SpannableStringBuilder("Perkiraan selesai pukul $endTime WIB")
        endText.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorGreen)),
            10,
            17,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        val suggestion = SpannableStringBuilder("Klik mulai untuk memulai perjalanan Anda")
        suggestion.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorGreen)),
            5,
            10,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        binding.tvTanggalReminder.text = date
        binding.tvPerkiraanSelesai.text = endText
        binding.tvSuggestion.text = suggestion
        binding.cvMulaiRute.visibility = View.VISIBLE
        binding.cvReminder.visibility = View.VISIBLE
    }
}