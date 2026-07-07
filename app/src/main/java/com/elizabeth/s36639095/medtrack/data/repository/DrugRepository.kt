package com.elizabeth.s36639095.medtrack.data.repository

import android.content.Context
import com.elizabeth.s36639095.medtrack.data.network.APIService
import com.elizabeth.s36639095.medtrack.data.network.Drug
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DrugRepository(private val applicationContext: Context) {
    private val apiService = APIService.create()

    suspend fun getDrugInfo(search: String): List<Drug> {
        return if (isNetworkAvailable()) {
            try {
                // Fetch from API if online
                val apiDrug = withContext(Dispatchers.IO) {
                    apiService.getDrugInfo(search = """openfda.brand_name:"$search"""")
                }

                apiDrug.results.map { drug -> drug.name = search.uppercase() }
                apiDrug.results

            } catch (e: Exception) {
                // If everything fails, at least return an empty list
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }


    @Suppress("ServiceCast")
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}