package com.runner.helper

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.common.BitMatrix
import com.google.zxing.WriterException
import java.lang.IllegalArgumentException
import java.util.HashMap

/**
 * Created by Administrator on 2017/5/3.
 */
object BitmapUtil {
    /**
     * 生成条码bitmap
     * @param content
     * @param format
     * @param width
     * @param height
     * @return
     */
    fun generateBitmap(content: String?, format: Int, width: Int, height: Int): Bitmap? {
        var height = height
        if (content == null || content == "") return null
        val barcodeFormat: BarcodeFormat
        when (format) {
            0 -> barcodeFormat = BarcodeFormat.UPC_A
            1 -> barcodeFormat = BarcodeFormat.UPC_E
            2 -> barcodeFormat = BarcodeFormat.EAN_13
            3 -> barcodeFormat = BarcodeFormat.EAN_8
            4 -> barcodeFormat = BarcodeFormat.CODE_39
            5 -> barcodeFormat = BarcodeFormat.ITF
            6 -> barcodeFormat = BarcodeFormat.CODABAR
            7 -> barcodeFormat = BarcodeFormat.CODE_93
            8 -> barcodeFormat = BarcodeFormat.CODE_128
            9 -> barcodeFormat = BarcodeFormat.QR_CODE
            else -> {
                barcodeFormat = BarcodeFormat.QR_CODE
                height = width
            }
        }
        val qrCodeWriter = MultiFormatWriter()
        val hints: MutableMap<EncodeHintType, Any?> = HashMap()
        hints[EncodeHintType.CHARACTER_SET] = "GBK"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        try {
            val encode = qrCodeWriter.encode(content, barcodeFormat, width, height, hints)
            val pixels = IntArray(width * height)
            for (i in 0 until height) {
                for (j in 0 until width) {
                    if (encode[j, i]) {
                        pixels[i * width + j] = 0x00000000
                    } else {
                        pixels[i * width + j] = -0x1
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565)
        } catch (e: WriterException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return null
    }
}