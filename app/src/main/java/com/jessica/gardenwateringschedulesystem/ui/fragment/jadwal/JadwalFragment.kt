package com.jessica.gardenwateringschedulesystem.ui.fragment.jadwal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jessica.gardenwateringschedulesystem.databinding.FragmentJadwalBinding
import com.jessica.gardenwateringschedulesystem.model.Schedule
import com.jessica.gardenwateringschedulesystem.utils.SCHEDULES
import com.jessica.gardenwateringschedulesystem.utils.monthNumberToString
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class JadwalFragment : Fragment() {

    private lateinit var binding: FragmentJadwalBinding
    private lateinit var jadwalAdapter: JadwalAdapter
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJadwalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        val userId = auth.currentUser?.uid
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val ref = StringBuilder()
            .append(monthNumberToString(month))
            .append(" ")
            .append(year)
            .toString()

        if (userId != null) {
            db.collection(SCHEDULES).document(userId).collection(ref).get()
                .addOnSuccessListener { collection ->
                    val schedules = ArrayList<Schedule>()
                    collection.documents.forEach {
                        val tanggal = it.data?.get("tanggal").toString()
                        val hari = it.data?.get("hari").toString()
                        val jam = it.data?.get("jam").toString()
                        schedules.add(
                            Schedule(
                                hari,
                                jam,
                                tanggal
                        ))
                    }
                    jadwalAdapter.differ.submitList(schedules.sortedBy { it.tanggal })
            } .addOnFailureListener {
                    Log.d("coba", "onViewCreated: ${it.localizedMessage}")
            }
        }
        val bulanText = StringBuilder()
            .append("Bulan : ")
            .append(ref)
            .toString()
        binding.tvBulan.text = bulanText
    }

    private fun setupRecyclerView() {
        jadwalAdapter = JadwalAdapter()
        with(binding.rvJadwal) {
            adapter = jadwalAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }
}