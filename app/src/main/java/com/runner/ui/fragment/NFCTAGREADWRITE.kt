package com.runner.ui.fragment

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.runner.R
import java.io.IOException


class NFCTAGREADWRITE : BottomSheetDialogFragment() {
    private var scanCardTextView: TextView? = null
    private var iVcancel: ImageView? = null
    private var cancelTextView: TextView? = null
    var nfcAdapter: NfcAdapter?=null
    private val flags = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
   var code2Contact=""
   var code2ContactURI=""
    private lateinit var pendingIntent: PendingIntent
    companion object {
        const val ARG_PARAMETER = "arg_parameter"

        fun newInstance(parameterValue: Bundle): NFCTAGREADWRITE {
            val fragment = NFCTAGREADWRITE()
            val args = Bundle()
            args.putBundle(ARG_PARAMETER, parameterValue)
            fragment.arguments = args
            return fragment
        }
    }
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_scan_nfc, container, false)
        scanCardTextView = rootView.findViewById(R.id.scanCardTextView)
        cancelTextView = rootView.findViewById(R.id.cancelTextView)
        isCancelable=false
        cancelTextView!!.setOnClickListener(object : View.OnClickListener {
           override fun onClick(v: View?) {
                // Handle scan card action
                dismiss() // Close the bottom sheet dialog
            }
        })
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        val parameterValue = arguments?.getBundle(ARG_PARAMETER)
        // Now you can use the parameterValue in your fragment
        if (parameterValue != null) {

            code2Contact=parameterValue.getString("contact","")
            code2ContactURI=parameterValue.getString("uri","")
           // Log.e("contact",code2Contact)
            // Do something with parameterValue
        }


        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize NFC adapter and setup filters here
        enableNfcReader()

    }
    private fun enableNfcReader() {
      //  nfcAdapter!!.enableReaderMode(activity, this, flags, null)


     nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

        val intentFilters = arrayOf(
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        )
        val techLists = arrayOf(arrayOf(Ndef::class.java.name))
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            Intent(requireActivity(), requireActivity()::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )
        nfcAdapter!!.enableForegroundDispatch(requireActivity(), pendingIntent, intentFilters, techLists)

    }

    private fun disableNfcReader() {
//      nfcAdapter!!.disableReaderMode(activity)
        val activity = requireActivity()
        nfcAdapter!!.disableForegroundDispatch(activity)

    }
    override fun onResume() {
        super.onResume()

        enableNfcReader()
    }

    override fun onPause() {
        super.onPause()
        // musicStop()
        disableNfcReader()
    }
   fun onNewIntent(intent: Intent) {
       Log.e("@@111","2"+NfcAdapter.ACTION_NDEF_DISCOVERED+"intent.action: "+intent.action)
        // Handle the NFC event

       if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.action)) {
           val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!
           val ndef = Ndef.get(tag)
           val uriRecord = NdefRecord.createUri(code2ContactURI)
           /*var ndefMessage = NdefMessage(
               arrayOf<NdefRecord>(
                   NdefRecord.createMime("text/vcard", code2Contact.trimIndent().toByteArray()),uriRecord
               )
           )*/

           var ndefMessage = NdefMessage(
               arrayOf<NdefRecord>(
                   uriRecord
               )
           )
           //if (NfcAdapter.ACTION_NDEF_DISCOVERED == requireActivity().intent.getAction()) {
           //   val tag: Tag = requireActivity().intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!
           Log.e(  "@@", "1" + ndef)
           if (ndef != null) {
               try {
                   ndef.connect()
                   if (ndef.maxSize < ndefMessage.toByteArray().size) {
                       /* ndefMessage = NdefMessage(
                           arrayOf<NdefRecord>(
                              uriRecord
                           )
                       )*/
                       Toast.makeText(activity,"The Nfc tag length size is "+ndefMessage.toByteArray().size,Toast.LENGTH_LONG).show()
                       return
                   }

                   ndef.writeNdefMessage(ndefMessage)
                   dismiss()
                   val dialog = Dialog(requireActivity(), R.style.my_dialog)
                   dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                   dialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
                   dialog.setContentView(R.layout.toast_center)
                   dialog.setCancelable(false)
                   var textView=dialog.findViewById<TextView>(R.id.toastText);
                   textView.textSize=14f
                   textView.setText("Transfer successful")
                   dialog.show()
                   Handler().postDelayed({
                       try {
                           if(dialog!=null && dialog.isShowing)
                           {
                               dialog.dismiss()
                           }
                       }
                       catch (E:Exception)
                       {

                       }


                   }, 1500L)
                  // Toast.makeText(activity, "Transfer successful", Toast.LENGTH_LONG).show()

               } catch (e: IOException) {
                   Log.e("@@ee1", "" + e.message+"ndefMessage"+ndefMessage+"ndef: "+ndef)
                   Toast.makeText(activity, "Please try again", Toast.LENGTH_LONG).show()
                   e.printStackTrace()
               } catch (e: FormatException) {
                   Log.e("@@ee2", "" + e.message)
                   Toast.makeText(activity, "Please try again", Toast.LENGTH_LONG).show()
                   e.printStackTrace()
               } finally {
                   try {
                       Log.e("@@ee4", "" + "close")
                       ndef.close()
                   } catch (e: IOException) {
                       Log.e("@@ee3", "" + e.message)
                       e.printStackTrace()
                   }
               }
           }
           // }
       }
    }

}