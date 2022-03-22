import com.opencsv.bean.CsvBindByName

class KrakenLedgerRow {
    // "txid","refid","time","type","subtype","aclass","asset","amount","fee","balance"
    @CsvBindByName
    var txid: String? = null

    @CsvBindByName
    var refid: String? = null

    @CsvBindByName
    var time: String? = null

    @CsvBindByName
    var type: String? = null

    @CsvBindByName
    var subtype: String? = null

    @CsvBindByName
    var aclass: String? = null

    @CsvBindByName
    lateinit var asset: String

    @CsvBindByName
    var amount: Double = 0.0

    @CsvBindByName
    var fee: Double = 0.0

    @CsvBindByName
    var balance: Double = 0.0

    override fun toString(): String {
        //return "[time=$time, type=$type, subtype=$subtype, aclass=$aclass, asset=$asset, amount=$amount, fee=$fee, balance=$balance]"
        return "[time=$time, asset=$asset, amount=$amount, balance=$balance]"
    }
}