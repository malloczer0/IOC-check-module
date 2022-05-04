import java.io.File
import java.security.DigestInputStream
import java.security.MessageDigest


class IOCComparator(
    iocBaseSetup: () -> Map<HashAlgorithm, File> = { mapOf(
        HashAlgorithm.MD5 to File("base/${HashAlgorithm.MD5.algorithmName}.txt"),
        HashAlgorithm.SHA_1 to File("base/${HashAlgorithm.SHA_1.algorithmName}.txt"),
        HashAlgorithm.SHA_256 to File("base/${HashAlgorithm.SHA_256.algorithmName}.txt"),
    ) }
) {
    private val ioc by lazy(iocBaseSetup)

    private fun calculateFileHash(
        file: File,
        buildOutput: (ByteArray) -> String = ByteArray::toString
    ) = fun (
        hashFunction: HashAlgorithm
    ) = run {
        val messageDigest = MessageDigest
            .getInstance(hashFunction.algorithmName)
        file.inputStream().use { inputStream ->
            DigestInputStream(inputStream, messageDigest).use { digestInputStream ->
                digestInputStream.readAllBytes()
            }
        }
        messageDigest
            .digest()
            .let(buildOutput)
    }

    private fun checkHashIsCompromised(algorithm: HashAlgorithm, hash: String): Boolean = ioc[algorithm]
        ?.inputStream()
        ?.bufferedReader()
        ?.useLines { lines -> lines.any { it==hash } } ?: error("Source not found")

    private fun checkMD5(hash: String): Boolean = checkHashIsCompromised(HashAlgorithm.MD5, hash)

    private fun checkSHA1(hash: String): Boolean = checkHashIsCompromised(HashAlgorithm.SHA_1, hash)

    private fun checkSHA256(hash: String): Boolean = checkHashIsCompromised(HashAlgorithm.SHA_256, hash)

    fun analiseFile(file: File): Boolean = calculateFileHash(file) { bytes ->
        byteArrayToHexString(bytes).also(::println)
    }.let { calculateHash ->
        if (!checkMD5(calculateHash(HashAlgorithm.MD5))) return false
        if (!checkSHA1(calculateHash(HashAlgorithm.SHA_1))) return false
        return checkSHA256(calculateHash(HashAlgorithm.SHA_256))
    }
}