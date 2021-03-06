import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class KrakenOHLCResponse {

    lateinit var error: List<String>

    private lateinit var result: JsonObject

    fun getHistoricalData(ticker: String): List<KrakenOHLC> {
        val historicalDataTicker = result.get(
            when(ticker) {
                "ETHEUR" -> "XETHZEUR"
                "ETHUSD" -> "XETHZUSD"
                else -> ticker
            }
        )

        val gson = Gson()
        val listType: Type = object : TypeToken<List<List<String>>>() {}.type
        val list: List<List<String>> = gson.fromJson(historicalDataTicker, listType)

        return list.map { arr ->
            KrakenOHLC(
                arr[0].toLong(),
                arr[1].toDouble(),
                arr[2].toDouble(),
                arr[3].toDouble(),
                arr[4].toDouble()
            )
        }
    }
}