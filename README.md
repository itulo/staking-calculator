# Kraken Staking Calculator
This program computes staking gains received from Kraken in a given fiat currency.

The program does not require your API keys, however you need to export your data from [https://www.kraken.com/u/history/export](https://www.kraken.com/u/history/export).

_Export type_ needs to be _Ledger_; select all _Ledger fields_.

NOTE that staking transactions older than 2 years are not considered.

### Build
    ./gradlew clean build
### Run
    java -jar build/libs/staking_calculator-2.0-SNAPSHOT-all.jar -f <kraken ledger file> --fiat <your fiat currency>
you can also give the `-v` argument to see staking transactions and coin value at the time of the transaction.

#### Donations
BTC: bc1qg3qq885ztne4xaltqajv6zzh9ctq0m95ax6g5c

ETH & ERC20: 0x4C6B3Fe9F7A790b74febc1cB6D1D7269eAcd96c1
