package com.demozov.currencyconverter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demozov.currencyconverter.databinding.ItemCurrencyBinding
import com.demozov.currencyconverter.pojo.Valute

class ChoiceCurrencyAdapter(
    private val choiceCurrency: String,
    private val onItemClicked: (Valute) -> Unit
) :
    ListAdapter<Valute, ChoiceCurrencyAdapter.ItemValuteHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemValuteHolder {
        return ItemValuteHolder(
            ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ItemValuteHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { onItemClicked(current) }
        holder.bind(current, choiceCurrency)
    }

    class ItemValuteHolder(private var binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Valute, choiceCurrency: String) {
            binding.apply {
                textViewName.text = item.name
                textViewCharCode.text = item.charCode
                if (item.charCode.equals(choiceCurrency)) {
                    imageViewChoice.visibility = View.VISIBLE
                } else {
                    imageViewChoice.visibility = View.INVISIBLE
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Valute>() {
            override fun areItemsTheSame(oldItem: Valute, newItem: Valute): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Valute, newItem: Valute): Boolean {
                return oldItem.charCode == newItem.charCode
            }

        }
    }


}