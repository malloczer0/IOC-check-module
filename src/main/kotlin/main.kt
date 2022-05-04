import java.io.File

fun main(args: Array<String>) {
    val file = File(args[0])
    val iocComparator = IOCComparator()
    iocComparator.analiseFile(file).also { println("Compromised: $it") }
}