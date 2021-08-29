package com.demozov.currencyconverter.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.demozov.currencyconverter.R
import com.demozov.currencyconverter.adapter.ChoiceCurrencyAdapter
import com.demozov.currencyconverter.databinding.FragmentChoiceCorrencyBinding
import com.demozov.currencyconverter.models.CurrencyViewModel
import com.demozov.currencyconverter.models.CurrencyViewModelFactory
import com.demozov.currencyconverter.models.FIRST_FIELD


class ChoiceCurrencyFragment : Fragment() {

    private val viewModel: CurrencyViewModel by activityViewModels {
        CurrencyViewModelFactory()
    }

    private var _binding: FragmentChoiceCorrencyBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: ChoiceCurrencyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentChoiceCorrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ChoiceCurrencyAdapter(navigationArgs.choiceCurrency) { itemCurrency ->
            val action = when (navigationArgs.field) {
                FIRST_FIELD -> ChoiceCurrencyFragmentDirections
                    .actionChoiceCurrencyFragmentToConverterFragment(
                        firstValute = itemCurrency.charCode.toString(),
                        secondValute = navigationArgs.anotherCurrency
                    )
                else -> ChoiceCurrencyFragmentDirections
                    .actionChoiceCurrencyFragmentToConverterFragment(
                        firstValute = navigationArgs.anotherCurrency,
                        secondValute = itemCurrency.charCode.toString()
                    )
            }
            this.findNavController().navigate(action)
        }
        binding.recyclerViewChoice.adapter = adapter
        binding.recyclerViewChoice.layoutManager = LinearLayoutManager(this.context)
        adapter.submitList(viewModel.getListValute().filter {
            !it.charCode.equals(navigationArgs.anotherCurrency)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.favorite -> {
                this.findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}