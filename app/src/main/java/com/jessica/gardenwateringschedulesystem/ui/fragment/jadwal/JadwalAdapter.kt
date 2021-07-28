package com.jessica.gardenwateringschedulesystem.ui.fragment.jadwal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jessica.gardenwateringschedulesystem.databinding.ItemJadwalBinding
import com.jessica.gardenwateringschedulesystem.model.Schedule

class JadwalAdapter: RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder>() {

    inner class JadwalViewHolder(val binding: ItemJadwalBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule) =
            oldItem.tanggal == newItem.tanggal

        override fun areContentsTheSame(oldSchedule: Schedule, newSchedule: Schedule) =
            oldSchedule == newSchedule
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalViewHolder {
        val itemBinding = ItemJadwalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JadwalViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: JadwalViewHolder, position: Int) {
        val schedule = differ.currentList[position]
        holder.binding.tvItemTanggal.text = schedule.tanggal
        holder.binding.tvItemHari.text = schedule.hari
        holder.binding.tvItemJam.text = schedule.jam
    }

    override fun getItemCount() = differ.currentList.size
}