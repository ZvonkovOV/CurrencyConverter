package com.demozov.currencyconverter.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.demozov.currencyconverter.databinding.FragmentConverterBinding
import com.demozov.currencyconverter.models.CurrencyViewModel
import com.demozov.currencyconverter.models.CurrencyViewModelFactory
import com.demozov.currencyconverter.models.FIRST_FIELD
import com.demozov.currencyconverter.models.SECOND_FIELD
import com.demozov.currencyconverter.pojo.Valute

class ConverterFragment : Fragment() {

    private val viewModel: CurrencyViewModel by activityViewModels {
        CurrencyViewModelFactory()
    }

    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: ConverterFragmentArgs by navArgs()

    private var firstValute: Valute? = null
    private var secondValute: Valute? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setCurrency(navigationArgs.firstValute, navigationArgs.secondValute)

        viewModel.firstValute.observe(this.viewLifecycleOwner) {
            firstValute = it
            setFirstCurrency(it)
        }
        viewModel.secondValute.observe(this.viewLifecycleOwner) {
            secondValute = it
            setSecondCurrency(it)
        }
        binding.textViewChangeFirstCurrency.setOnClickListener {
            val action =
                ConverterFragmentDirections.actionConverterFragmentToChoiceCurrencyFragment(
                    choiceCurrency = firstValute?.charCode.toString(),
                    anotherCurrency = secondValute?.charCode.toString(),
                    FIRST_FIELD
                )
            this.findNavController().navigate(action)
        }
        binding.textViewChangeSecondCurrency.setOnClickListener {
            val action =
                ConverterFragmentDirections.actionConverterFragmentToChoiceCurrencyFragment(
                    choiceCurrency = secondValute?.charCode.toString(),
                    anotherCurrency = firstValute?.charCode.toString(),
                    SECOND_FIELD
                )
            this.findNavController().navigate(action)
        }
    }

    private fun setFirstCurrency(item: Valute) {
        binding.apply {
            firstCurrency.text = item.charCode
            firsValue.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (firsValue.hasFocus()) {
                        if (firsValue.text.toString() == "") {
                            secondValue.setText("0")
                        } else {
                            secondValue.setText(
                                calculation(
                                    getValue(firstValute!!),
                                    getValue(secondValute!!),
                                    firsValue
                                )
                            )
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
        }
    }

    private fun setSecondCurrency(item: Valute) {
        binding.apply {
            secondCurrency.text = item.charCode
            secondValue.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (secondValue.hasFocus()) {
                        if (secondValue.text.toString() == "") {
                            firsValue.setText("0")
                        } else {
                            firsValue.setText(
                                calculation(
                                    getValue(secondValute!!),
                                    getValue(firstValute!!),
                                    secondValue
                                )
                            )
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    private fun calculation(inputValue: Double, outputValue: Double, editText: EditText): String {
        val tmp = editText.text.toString().valuteToDouble()
        val b = (inputValue * tmp) / outputValue
        return String.format("%.2f", b)
    }

    private fun String.valuteToDouble(): Double {
        return this.replace(',', '.').toDouble()
    }

    private fun getValue(item: Valute): Double =
        ((item.value?.valuteToDouble())!!) / (item.nominal?.toInt()!!)

}