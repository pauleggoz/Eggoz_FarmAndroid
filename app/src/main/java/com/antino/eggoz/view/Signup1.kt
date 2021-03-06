package com.antino.eggoz.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.antino.eggoz.R
import com.antino.eggoz.databinding.ActivitySignup1Binding
import com.antino.eggoz.modelvew.ModelMain
import com.antino.eggoz.network.NetworkConnect
import com.antino.eggoz.utils.AppSignatureHashHelper
import com.antino.eggoz.utils.CustomAlertLoadingActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates

class Signup1 : AppCompatActivity() {
    private lateinit var binding: ActivitySignup1Binding
    private lateinit var snackBar: Snackbar
//    private lateinit var pos: String
    private var internet by Delegates.notNull<Boolean>()
    private var RESOLVE_HINT=301
    private var number=""

    private var hashKey=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignup1Binding.inflate(layoutInflater)
        setContentView(binding.root)


        snackBar = Snackbar.make(
            binding.root, "Eggoz",
            Snackbar.LENGTH_LONG
        )
        dropdownsetup()

        val networkConnections = NetworkConnect(applicationContext)
        networkConnections.observe(this, Observer {

            if (it) {
                //isConnected
                if (snackBar.isShown)
                    snackBar.dismiss()
                internet = true

            } else {
                internet = false
                //isNotConnected
                if (snackBar.isShown)
                    snackBar.dismiss()
                snackBar = Snackbar.make(
                    binding.root, "Network Lost",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).setActionTextColor(Color.WHITE)
                val snackBarView = snackBar.view
                snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                val textView =
                    snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                textView.setTextColor(Color.WHITE)
                snackBar.show()


            }
        })

        binding.term.setOnClickListener {
            val uri: Uri =
                Uri.parse("https://eggoz.in/privacy-policy.html")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        requestHint()

        binding.btnSubmit.setOnClickListener {
            if (internet) {
                submit()
            }
            else
                Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
        }

        val appSignatureHashHelper=AppSignatureHashHelper(this)

        Log.d("HashKey: ", appSignatureHashHelper.getAppSignatures()[0])
        hashKey = appSignatureHashHelper.getAppSignatures()[0]
    }

    private fun dropdownsetup() {
        val name: ArrayList<String> = ArrayList()
        name.clear()
        name.add("Delhi")
        name.add("Mumbai")
        name.add("Noida")
        name.add("Gurgaon")
        val adapter = ArrayAdapter(this, android.R.layout.select_dialog_item, name)
        binding.edtAreaType.threshold = 1
        binding.edtAreaType.setAdapter(adapter)
        binding.edtAreaType.setTextColor(ContextCompat.getColor(this, R.color.app_color))

    }

    private fun submit() {
        when {
            binding.edtMobileNo.text?.isEmpty()!! -> binding.edtMobieleNoLayout.error =
                "Please enter a valid number"
            binding.edtMobileNo.text?.length!! < 10 -> binding.edtMobieleNoLayout.error =
                "Mobile no must be more then 10 digit"
            else -> {
                binding.edtMobieleNoLayout.isErrorEnabled = false
                sendOtp()
            }
        }
    }

    private fun sendOtp() {
        binding.btnSubmit.isClickable = false
        binding.edtMobileNo.isFocusable = false


//        val hash="debug"
//        val hash="release"
        val hash="playstore"

        if (binding.edtMobileNo.text.toString()=="8273217889")
            Toast.makeText(this,hashKey,Toast.LENGTH_SHORT).show()

        val loadingdialog = CustomAlertLoadingActivity(this)
        loadingdialog.stateLoading()
        val viewModel = ViewModelProvider(this).get(ModelMain::class.java)
        viewModel.signup1(
            "+91" + binding.edtMobileNo.text.toString(),hash
        ).observe(this,
            Observer {
                loadingdialog.dismiss()
                Log.d("data", "otp ${it.otp}")
                if (it.otpmessage != null) {
                    val intent = Intent(this, Signup2Otpverification::class.java)
                    intent.putExtra("mobile_no", "+91" + binding.edtMobileNo.text.toString())
                    intent.putExtra("otp", it.otp)
                    startActivity(intent)
                    binding.edtMobileNo.isFocusableInTouchMode = true
                    finish()
                } else {
                    binding.btnSubmit.isClickable = true
                    binding.edtMobileNo.isFocusableInTouchMode = true
                    Toast.makeText(this, it.errors[0].message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent = Credentials.getClient(this).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(intent.intentSender,
            RESOLVE_HINT, null, 0, 0, 0)
    }

    private fun apiCall(number:String){
        binding.btnSubmit.isClickable = false
        binding.edtMobileNo.isFocusable = false

//        val hash="debug"
//        val hash="release"
        val hash="playstore"
        if (binding.edtMobileNo.text.toString()=="8273217889") {
            Toast.makeText(this, hashKey, Toast.LENGTH_SHORT).show()
            Log.d("data",hashKey)
        }

        val loadingdialog = CustomAlertLoadingActivity(this)
        loadingdialog.stateLoading()
        val viewModel = ViewModelProvider(this).get(ModelMain::class.java)
        viewModel.signup1(
            "+91" + number,hash
        ).observe(this,
            Observer {
                loadingdialog.dismiss()
                if (it.otpmessage != null) {
                    val intent = Intent(this, Signup2Otpverification::class.java)
                    intent.putExtra("mobile_no", "+91" + binding.edtMobileNo.text.toString())
                    intent.putExtra("otp", it.otp)
                    startActivity(intent)
                    binding.edtMobileNo.isFocusableInTouchMode = true
                    finish()
                } else {
                    binding.btnSubmit.isClickable=true
                    binding.edtMobileNo.isFocusableInTouchMode = true
                    Toast.makeText(this, it.errors[0].message, Toast.LENGTH_SHORT).show()
                }

            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                val credential: Credential = data?.getParcelableExtra(Credential.EXTRA_KEY)!!
                number = credential.id.replace("+91", "")
                binding.edtMobileNo.setText(number)
                apiCall(number)
            }
        }
    }

}