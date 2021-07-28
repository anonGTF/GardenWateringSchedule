package com.jessica.gardenwateringschedulesystem.ui.fragment.home

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jessica.gardenwateringschedulesystem.R
import com.jessica.gardenwateringschedulesystem.databinding.FragmentHomeBinding
import com.jessica.gardenwateringschedulesystem.utils.getCurrentDateTime
import com.jessica.gardenwateringschedulesystem.utils.toString
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timeExtra = arguments?.getString("time")
        if (timeExtra != null) {
            showNotif(timeExtra)
        }
        showNotif("08:00")
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