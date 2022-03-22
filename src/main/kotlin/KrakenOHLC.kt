class KrakenOHLC(val time: Long, val open: Double, val high: Double, val low: Double, val close: Double) {

    override fun toString(): String {
        return "[time=$time, open=$open]"
    }
}