/**
 * PdfPrint for printing
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */
package com.runner.printdemo.printprocess;

import android.content.Context;

import com.brother.ptouch.sdk.PrinterInfo.ErrorCode;
import com.runner.printdemo.common.MsgDialog;
import com.runner.printdemo.common.MsgHandle;


public class PdfPrint extends BasePrint {

    private int startIndex;
    private int endIndex;
    private String mPdfFile;

    public PdfPrint(Context context, MsgHandle mHandle, MsgDialog mDialog) {
        super(context, mHandle, mDialog);
    }

    /**
     * get print pdf pages
     */
    public int getPdfPages(String file) {
        return mPrinter.getPDFFilePages(file);
    }

    /**
     * set print pdf pages
     */
    public void setPrintPage(int start, int end) {

        startIndex = start;
        endIndex = end;
    }

    /**
     * set print data
     */
    public void setFiles(String file) {
        mPdfFile = file;

    }

    /**
     * do the particular print
     */
    @Override
    protected void doPrint() {

        int pageIndexOfPrint = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            mPrintResult = mPrinter.printPdfPage(mPdfFile, i, pageIndexOfPrint, endIndex - startIndex + 1);

            if (mPrintResult.errorCode != ErrorCode.ERROR_NONE) {
                break;
            }
            pageIndexOfPrint++;
        }
    }

}
