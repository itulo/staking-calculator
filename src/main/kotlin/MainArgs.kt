import com.xenomachina.argparser.ArgParser

class MainArgs(parser: ArgParser) {
    val verbose by parser.flagging(
        "-v", "--verbose",
        help = "enable verbose mode"
    )

    val coin by parser.storing(
        "-c", "--coin",
        help = "coin"
    )

    val filename by parser.storing(
        "-f", "--filename",
        help = "filename containing ledger data"
    )
}