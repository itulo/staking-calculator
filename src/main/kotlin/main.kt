import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import com.xenomachina.argparser.ArgParser
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*

fun <T> readCSV(filename: String, clazz: Class<T>): List<T> {

    var fileReader: BufferedReader? = null
    val csvToBean: CsvToBean<T>?

    try {
        fileReader = BufferedReader(FileReader(filename))
        csvToBean = CsvToBeanBuilder<T>(fileReader)
            .withType(clazz)
            .withIgnoreLeadingWhiteSpace(true)
            .build()

        return csvToBean.parse()

    } catch (e: Exception) {
        println("Reading CSV Error!")
        e.printStackTrace()
    } finally {
        try {
            fileReader!!.close()
        } catch (e: IOException) {
            println("Closing fileReader/csvParser Error!")
            e.printStackTrace()
        }
    }

    return Collections.emptyList()
}

suspend fun getHistoricalData(coin: String): List<KrakenOHLC> {
    val ticker = coin + "EUR"
    val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
    // NOTE: this will return max 720 data points
    val res =
        client.get<KrakenOHLCResponse>("https://api.kraken.com/0/public/OHLC?pair=$ticker&interval=1440")
    client.close()

    return res.getHistoricalData(ticker)
}

fun calculateStakingAmountInEUR(
    stakingRows: List<KrakenLedgerRow>,
    historicalData: List<KrakenOHLC>,
    coin: String
) {
    var sum = 0.0
    var rowsWithHistoricalData = 0

    for (row in stakingRows) {
        val time = SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(row.time).time / 1000

        var dateData: KrakenOHLC
        for (i in 0 until historicalData.size - 1) {
            if (time >= historicalData[i].time && time <= historicalData[i + 1].time) {
                dateData = historicalData[i]
                val partialSum = row.amount * dateData.open

                if (verboseLog) {
                    println("row: ${row} historicalData: ${historicalData[i]}: received ${partialSum}")
                }
                sum += partialSum
                rowsWithHistoricalData++
                break
            }
        }
    }

    println("\nFound historical data for $rowsWithHistoricalData rows. Staking for $coin total is $sum€")
}

var verboseLog = false
const val staking = "staking"
suspend fun main(args: Array<String>) {
    var ledgerFilename: String
    var coinTicker: String
    ArgParser(args).parseInto(::MainArgs).run {
        coinTicker = coin
        ledgerFilename = filename
        verboseLog = verbose
    }

    val stakingRows = readCSV(
        ledgerFilename,
        KrakenLedgerRow::class.java
    ).filter { r -> (r.type == staking) && r.asset.contains(coinTicker) }
    if (verboseLog) {
        println("${stakingRows.size} staking transactions found for coin ${coinTicker}")
    }

    val historicalData = getHistoricalData(coinTicker)
    if (verboseLog) {
        println("${historicalData.size} days of historical data fetched")
        println(
            "from ${
                Instant.ofEpochSecond(historicalData[0].time).atZone(ZoneId.of("UTC")).toLocalDate()
            }" +
                    " to  ${
                        Instant.ofEpochSecond(historicalData[historicalData.size - 1].time)
                            .atZone(ZoneId.of("UTC")).toLocalDate()
                    }"
        )

    }

    calculateStakingAmountInEUR(stakingRows, historicalData, coinTicker)
}