package com.demozov.currencyconverter.models

import androidx.lifecycle.*
import com.demozov.currencyconverter.util.ValuteXmlParser
import com.demozov.currencyconverter.pojo.Valute
import okhttp3.*
import java.io.IOException
import java.lang.IllegalArgumentException
import java.net.URI

const val SO_URL = "https://www.cbr.ru/scripts/XML_daily.asp"
const val FIRST_FIELD = 0
const val SECOND_FIELD = 1

enum class LoadStatus { ERROR, DONE }

class CurrencyViewModel : ViewModel() {

    private var _isLoad = MutableLiveData<LoadStatus>()
    val isLoad: LiveData<LoadStatus> = _isLoad

    private var _firstValute = MutableLiveData<Valute>()
    val firstValute: LiveData<Valute> = _firstValute

    private var _secondValute = MutableLiveData<Valute>()
    val secondValute: LiveData<Valute> = _secondValute

    private var listValute: List<Valute>? = null

    init {
        getCurrency()
    }

    private fun getCurrency() {
        val client = OkHttpClient()
        val call = URI(SO_URL).toURL()
        val request = Request.Builder().url(call).build()
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    _isLoad.postValue(LoadStatus.ERROR)
                }

                override fun onResponse(call: Call, response: Response) {
                    val tmp = response.body?.string()
                    val inputStream = tmp?.byteInputStream(Charsets.UTF_16)
                    listValute = inputStream.use { stream ->
                        stream?.let { ValuteXmlParser().parse(it) }
                    }
                    (listValute as MutableList).add(
                        Valute(
                            null,
                            "RUB",
                            "1",
                            "Российский Рубль",
                            "1"
                        )
                    )
//                    _firstValute.postValue((listValute as ArrayList).last())
//                    _secondValute.postValue((listValute as ArrayList).first { it.charCode == "USD" })
                    _isLoad.postValue(LoadStatus.DONE)
                }
            })
    }

    fun getListValute(): List<Valute> {
        return listValute ?: emptyList()
    }

    fun setCurrency(first: String, second: String) {
        _firstValute.value = (listValute as ArrayList).first { it.charCode == first }
        _secondValute.value = (listValute as ArrayList).first { it.charCode == second }
    }

}

class CurrencyViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            @Suppress
            return CurrencyViewModel() as T
        }
        throw IllegalArgumentException("Unknown VIewModel Class")
    }

}