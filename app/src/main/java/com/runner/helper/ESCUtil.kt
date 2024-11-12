package com.runner.helper

import com.runner.helper.ESCUtil
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.lang.Exception

//常用指令封装
object ESCUtil {
    const val ESC: Byte = 0x1B // Escape
    const val FS: Byte = 0x1C // Text delimiter
    const val GS: Byte = 0x1D // Group separator
    const val DLE: Byte = 0x10 // data link escape
    const val EOT: Byte = 0x04 // End of transmission
    const val ENQ: Byte = 0x05 // Enquiry character
    const val SP: Byte = 0x20 // Spaces
    const val HT: Byte = 0x09 // Horizontal list
    const val LF: Byte = 0x0A //Print and wrap (horizontal orientation)
    const val CR: Byte = 0x0D // Home key
    const val FF: Byte =
        0x0C // Carriage control (print and return to the standard mode (in page mode))
    const val CAN: Byte = 0x18 // Canceled (cancel print data in page mode)

    //初始化打印机
    fun init_printer(): ByteArray {
        val result = ByteArray(2)
        result[0] = ESC
        result[1] = 0x40
        return result
    }

    //打印浓度指令
    fun setPrinterDarkness(value: Int): ByteArray {
        val result = ByteArray(9)
        result[0] = GS
        result[1] = 40
        result[2] = 69
        result[3] = 4
        result[4] = 0
        result[5] = 5
        result[6] = 5
        result[7] = (value shr 8).toByte()
        result[8] = value.toByte()
        return result
    }

    /**
     * 打印单个二维码 sunmi自定义指令
     * @param code:			二维码数据
     * @param modulesize:	二维码块大小(单位:点, 取值 1 至 16 )
     * @param errorlevel:	二维码纠错等级(0 至 3)
     * 0 -- 纠错级别L ( 7%)
     * 1 -- 纠错级别M (15%)
     * 2 -- 纠错级别Q (25%)
     * 3 -- 纠错级别H (30%)
     */
    fun getPrintQRCode(code: String, modulesize: Int, errorlevel: Int): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(setQRCodeSize(modulesize))
            buffer.write(setQRCodeErrorLevel(errorlevel))
            buffer.write(getQCodeBytes(code))
            buffer.write(bytesForPrintQRCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }

    /**
     * 使用光栅位图打印两个二维码
     * 将多个二维码转换为光栅位图打印
     */
    fun getPrintDoubleQRCode(qr1: String?, qr2: String?, size: Int): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = 0x00
        val bytes2 = BytesUtil.getZXingQRCode(qr1, qr2, size)
        return BytesUtil.byteMerger(bytes1, bytes2)
    }

    /**
     * 打印一维条形码
     */
    fun getPrintBarCode(
        data: String,
        symbology: Int,
        height: Int,
        width: Int,
        textposition: Int
    ): ByteArray {
        var height = height
        var width = width
        var textposition = textposition
        if (symbology < 0 || symbology > 10) {
            return byteArrayOf(LF)
        }
        if (width < 2 || width > 6) {
            width = 2
        }
        if (textposition < 0 || textposition > 3) {
            textposition = 0
        }
        if (height < 1 || height > 255) {
            height = 162
        }
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(
                byteArrayOf(
                    0x1D, 0x66, 0x01, 0x1D, 0x48, textposition.toByte(),
                    0x1D, 0x77, width.toByte(), 0x1D, 0x68, height.toByte(), 0x0A
                )
            )
            val barcode: ByteArray
            barcode = if (symbology == 10) {
                BytesUtil.getBytesFromDecString(data)
            } else {
                data.toByteArray(charset("GB18030"))
            }
            if (symbology > 7) {
                buffer.write(
                    byteArrayOf(
                        0x1D,
                        0x6B,
                        0x49,
                        (barcode.size + 2).toByte(),
                        0x7B,
                        (0x41 + symbology - 8).toByte()
                    )
                )
            } else {
                buffer.write(
                    byteArrayOf(
                        0x1D,
                        0x6B,
                        (symbology + 0x41).toByte(),
                        barcode.size.toByte()
                    )
                )
            }
            buffer.write(barcode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }

    //光栅位图打印
    fun printBitmap(bitmap: Bitmap?): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = 0x00
        val bytes2 = BytesUtil.getBytesFromBitMap(bitmap)
        return BytesUtil.byteMerger(bytes1, bytes2)
    }

    //光栅位图打印 设置mode
    fun printBitmap(bitmap: Bitmap?, mode: Int): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = mode.toByte()
        val bytes2 = BytesUtil.getBytesFromBitMap(bitmap)
        return BytesUtil.byteMerger(bytes1, bytes2)
    }

    //光栅位图打印
    fun printBitmap(bytes: ByteArray?): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = 0x00
        return BytesUtil.byteMerger(bytes1, bytes)
    }

    /*
	*	选择位图指令 设置mode
	*	需要设置1B 33 00将行间距设为0
	 */
    fun selectBitmap(bitmap: Bitmap?, mode: Int): ByteArray {
        return BytesUtil.byteMerger(
            BytesUtil.byteMerger(
                byteArrayOf(0x1B, 0x33, 0x00),
                BytesUtil.getBytesFromBitMap(bitmap, mode)
            ), byteArrayOf(0x0A, 0x1B, 0x32)
        )
    }

    /**
     * 跳指定行数
     */
    fun nextLine(lineNum: Int): ByteArray {
        val result = ByteArray(lineNum)
        for (i in 0 until lineNum) {
            result[i] = LF
        }
        return result
    }

    // ------------------------style set-----------------------------
    //设置默认行间距
    fun setDefaultLineSpace(): ByteArray {
        return byteArrayOf(0x1B, 0x32)
    }

    //设置行间距
    fun setLineSpace(height: Int): ByteArray {
        return byteArrayOf(0x1B, 0x33, height.toByte())
    }

    // ------------------------underline-----------------------------
    //设置下划线1点
	@JvmStatic
	fun underlineWithOneDotWidthOn(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 45
        result[2] = 1
        return result
    }

    //设置下划线2点
    fun underlineWithTwoDotWidthOn(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 45
        result[2] = 2
        return result
    }

    //取消下划线
	@JvmStatic
	fun underlineOff(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 45
        result[2] = 0
        return result
    }
    // ------------------------bold-----------------------------
    /**
     * 字体加粗
     */
	@JvmStatic
	fun boldOn(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 69
        result[2] = 0xF
        return result
    }

    /**
     * 取消字体加粗
     */
	@JvmStatic
	fun boldOff(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 69
        result[2] = 0
        return result
    }

    // ------------------------character-----------------------------
    /*
	*单字节模式开启
	 */
    fun singleByte(): ByteArray {
        val result = ByteArray(2)
        result[0] = FS
        result[1] = 0x2E
        return result
    }

    /*
	*单字节模式关闭
 	*/
    fun singleByteOff(): ByteArray {
        val result = ByteArray(2)
        result[0] = FS
        result[1] = 0x26
        return result
    }

    /**
     * 设置单字节字符集
     */
    fun setCodeSystemSingle(charset: Byte): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 0x74
        result[2] = charset
        return result
    }

    /**
     * 设置多字节字符集
     */
    fun setCodeSystem(charset: Byte): ByteArray {
        val result = ByteArray(3)
        result[0] = FS
        result[1] = 0x43
        result[2] = charset
        return result
    }
    // ------------------------Align-----------------------------
    /**
     * 居左
     */
    fun alignLeft(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 97
        result[2] = 0
        return result
    }

    /**
     * 居中对齐
     */
    fun alignCenter(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 97
        result[2] = 1
        return result
    }

    /**
     * 居右
     */
    fun alignRight(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 97
        result[2] = 2
        return result
    }

    //切刀
    fun cutter(): ByteArray {
        return byteArrayOf(0x1d, 0x56, 0x01)
    }

    /**
     * 标签or黑标模式定位
     * 由于兼容性原因只有手持机可以支持SDK,其他设备必须使用指令方法
     * For compatibility reasons, only the handheld can support the SDK, other devices must use the command method
     */
    fun labellocate(): ByteArray {
        return byteArrayOf(0x1C, 0x28, 0x4C, 0x02, 0x00, 0x43, 0x31)
    }

    /**
     * 标签or黑标模式送出
     * 由于兼容性原因只有手持机可以支持SDK,其他设备必须使用指令方法
     * For compatibility reasons, only the handheld can support the SDK, other devices must use the command method
     */
    fun labelout(): ByteArray {
        return byteArrayOf(0x1C, 0x28, 0x4C, 0x02, 0x00, 0x42, 0x31)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////          private                /////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////
    private fun setQRCodeSize(modulesize: Int): ByteArray {
        //二维码块大小设置指令
        val dtmp = ByteArray(8)
        dtmp[0] = GS
        dtmp[1] = 0x28
        dtmp[2] = 0x6B
        dtmp[3] = 0x03
        dtmp[4] = 0x00
        dtmp[5] = 0x31
        dtmp[6] = 0x43
        dtmp[7] = modulesize.toByte()
        return dtmp
    }

    private fun setQRCodeErrorLevel(errorlevel: Int): ByteArray {
        //二维码纠错等级设置指令
        val dtmp = ByteArray(8)
        dtmp[0] = GS
        dtmp[1] = 0x28
        dtmp[2] = 0x6B
        dtmp[3] = 0x03
        dtmp[4] = 0x00
        dtmp[5] = 0x31
        dtmp[6] = 0x45
        dtmp[7] = (48 + errorlevel).toByte()
        return dtmp
    }

    //打印已存入数据的二维码
    private val bytesForPrintQRCode: ByteArray
        private get() {
            val dtmp = ByteArray(9)
            dtmp[0] = 0x1D
            dtmp[1] = 0x28
            dtmp[2] = 0x6B
            dtmp[3] = 0x03
            dtmp[4] = 0x00
            dtmp[5] = 0x31
            dtmp[6] = 0x51
            dtmp[7] = 0x30
            dtmp[8] = 0x0A
            return dtmp
        }

    private fun getQCodeBytes(code: String): ByteArray {
        //二维码存入指令
        val buffer = ByteArrayOutputStream()
        try {
            val d = code.toByteArray(charset("GB18030"))
            var len = d.size + 3
            if (len > 7092) len = 7092
            buffer.write(0x1D.toByte().toInt())
            buffer.write(0x28.toByte().toInt())
            buffer.write(0x6B.toByte().toInt())
            buffer.write(len.toByte().toInt())
            buffer.write((len shr 8).toByte().toInt())
            buffer.write(0x31.toByte().toInt())
            buffer.write(0x50.toByte().toInt())
            buffer.write(0x30.toByte().toInt())
            var i = 0
            while (i < d.size && i < len) {
                buffer.write(d[i].toInt())
                i++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }
}