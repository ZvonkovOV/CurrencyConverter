package com.demozov.currencyconverter.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.demozov.currencyconverter.R
import com.demozov.currencyconverter.databinding.FragmentLoadBinding
import com.demozov.currencyconverter.models.CurrencyViewModel
import com.demozov.currencyconverter.models.CurrencyViewModelFactory
import com.demozov.currencyconverter.models.LoadStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoadFragment : Fragment() {

    private val viewModel: CurrencyViewModel by activityViewModels {
        CurrencyViewModelFactory()
    }

    private var _binding: FragmentLoadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isLoad.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                LoadStatus.DONE -> {
                    val action = LoadFragmentDirections.actionLoadFragmentToConverterFragment()
                    this.findNavController().navigate(action)
                }
                LoadStatus.ERROR -> {
                    showConfirmationDialog()
                }
                else -> {
                }
            }
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.internet_connection_lost))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                activity?.finishAffinity()
            }
            .show()
    }
}