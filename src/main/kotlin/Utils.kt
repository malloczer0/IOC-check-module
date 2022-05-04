import java.util.*

fun byteArrayToHexString(bytes: ByteArray) = bytes
    .asSequence()
    .map { String.format("%02X", it).lowercase(Locale.getDefault()) }
    .reduce { acc, hexVal -> acc+hexVal }

infix fun <T> Boolean.ifCondition(block: () -> T) = if(this) block() else null