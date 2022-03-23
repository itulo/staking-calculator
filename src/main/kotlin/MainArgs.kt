import com.xenomachina.argparser.ArgParser

class MainArgs(parser: ArgParser) {
    val verbose by parser.flagging(
        "-v", "--verbose",
        help = "enable verbose mode - shows staking transactions, and coin price of the transaction day"
    )

    val fiat by parser.storing(
        "--fiat",
        help = "fiat"
    )

    val filename by parser.storing(
        "-f", "--filename",
        help = "filename containing ledger data"
    )
}