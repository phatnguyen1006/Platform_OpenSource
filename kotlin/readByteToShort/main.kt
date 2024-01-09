// Extensions for InputStream to read string, unsigned int, unsigned short, short, etc...

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun InputStream.readString(length: Int): String? {
    val byteArray = ByteArray(length)
    val size = read(byteArray)
    if (size < length) return null
    return String(byteArray)
}

fun InputStream.readUInt(): UInt {
    val byteArray = ByteArray(UInt.SIZE_BYTES)
    val size = read(byteArray)
    if (size < UInt.SIZE_BYTES) throw Exception("not enough")
    return byteArray.map{ it.toUByte().toUInt() }.reduceIndexed { index, acc, element ->
        acc + element.shl(index * 8)
    }
}

fun InputStream.readUShort(): UShort {
    val byteArray = ByteArray(UShort.SIZE_BYTES)
    val size = read(byteArray)
    if (size < UShort.SIZE_BYTES) throw Exception("not enough: $size")
    return byteArray.map{ it.toUByte().toUInt() }.reduceIndexed { index, acc, element -> acc + element.shl(index * 8) }.toUShort()
}

fun InputStream.readShort(): Short {
    val byteArray = ByteArray(Short.SIZE_BYTES)
    val size = read(byteArray)
    if (size < Short.SIZE_BYTES) throw Exception("not enough: $size")
//    val byteBuffer = ByteBuffer.allocate(Short.SIZE_BYTES)
//    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
//    byteArray.forEach(byteBuffer::put)
//    return byteBuffer.getShort(0)

    return JavaExt.byteArrayToShort(byteArray, true)
}
