package com.jessica.gardenwateringschedulesystem.fragment.jadwal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jessica.gardenwateringschedulesystem.R

class JadwalFragment : Fragment() {

    private lateinit var jadwalViewModel: JadwalViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        jadwalViewModel =
            ViewModelProvider(this).get(JadwalViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_jadwal, container, false)
        return root
    }
}