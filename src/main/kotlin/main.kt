import com.xenomachina.argparser.ArgParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId

fun transformCoinTicker(coin: String): String {
    return when (coin) {
        "ETH2", "XETH" -> "ETH"
        else -> coin
    }
}

fun transformTickerResult(ticker: String): String {
    return when (ticker) {
        "BTCEUR" -> "XXBTZEUR"
        else -> ticker
    }
}

suspend fun getHistoricalData(coin: String, fiat: String): List<KrakenOHLC> {
    val ticker = transformCoinTicker(coin) + fiat
    val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
    // NOTE: this will return max 720 data points
        val res =
            client.get<KrakenOHLCResponse>("https://api.kraken.com/0/public/OHLC?pair=$ticker&interval=1440")
        client.close()

    return res.getHistoricalData(transformTickerResult(ticker))
}

suspend fun calculateearningAmountInFiat(
    earningRowsMap: Map<String, List<KrakenLedgerRow>>,
    fiat: String
) {
    var totalSum = 0.0

    for ((stackedCoin, earningRows) in earningRowsMap) {
        val historicalData = getHistoricalData(stackedCoin, fiat)
        if (verboseLog) {
            println("${historicalData.size} days of historical data fetched for $stackedCoin " +
                    "from ${Instant.ofEpochSecond(historicalData[0].time).atZone(ZoneId.of("UTC")).toLocalDate()} " +
                    "to ${Instant.ofEpochSecond(historicalData[historicalData.size - 1].time).atZone(ZoneId.of("UTC")).toLocalDate()}")

        }

        var coinSum = 0.0
        var rowsWithHistoricalData = 0

        for (row in earningRows) {
            val time = SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(row.time).time / 1000

            var dateData: KrakenOHLC
            for (i in 0 until historicalData.size - 1) {
                if (time >= historicalData[i].time && time <= historicalData[i + 1].time) {
                    dateData = historicalData[i]
                    val partialSum = row.amount * dateData.open

                    if (verboseLog) {
                        println("row: $row historicalData: ${historicalData[i]}: received $partialSum")
                    }
                    coinSum += partialSum
                    rowsWithHistoricalData++
                    break
                }
            }
        }

        println("Found historical data for $rowsWithHistoricalData earning rows. earning for $stackedCoin total is $coinSum $fiat\n")
        totalSum += coinSum
    }

    println("Total earning rewards: $totalSum $fiat\n")
}

fun transformAssetName(rows: List<KrakenLedgerRow>) {
    rows.forEach {
        it.asset = when {
            it.asset == "DOT28.S" -> "DOT"        // DOT 28 day bounded earning
            it.asset == "MATIC04.S" -> "MATIC"    // MATIC 4 day bounded earning
            // some earning row asset is appended by a '.S' (e.g. there can be rows with asset FLOW and FLOW.S)
            // get rid of the append, so we can group by asset
            it.asset.endsWith(".S") -> it.asset.replace(".S", "")
            else -> it.asset
        }
    }
    return
}

var verboseLog = false
const val earning = "earn"
suspend fun main(args: Array<String>) {
    var ledgerFilename: String
    var fiatSymbol: String
    ArgParser(args).parseInto(::MainArgs).run {
        ledgerFilename = filename
        fiatSymbol = fiat
        verboseLog = verbose
    }

    val earningRows = CSVReader.readCSV(
        ledgerFilename,
        KrakenLedgerRow::class.java
    ).filter { it.type == earning }

    transformAssetName(earningRows)

    val earningRowsMap = earningRows.groupBy { it.asset }

    println("coins with earning transactions found: ${earningRowsMap.keys}\n")

    calculateearningAmountInFiat(earningRowsMap, fiatSymbol)
}
