import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.Collections

class CSVReader {
    companion object {
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
    }
}