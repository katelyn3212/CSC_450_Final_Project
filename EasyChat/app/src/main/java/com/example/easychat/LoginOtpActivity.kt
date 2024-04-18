package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.easychat.ui.theme.EasyChatTheme
import com.example.easychat.utils.AndroidUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class LoginOtpActivity : ComponentActivity() {
    private var phoneNumber: String? = null
    private var timeoutSeconds: Long = 60L
    private var verificationCode: String? = null
    private lateinit var resendingToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var otpInput: EditText
    private lateinit var nextBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var resendOtpTextView: TextView
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_otp);

        otpInput = findViewById(R.id.login_otp)
        nextBtn = findViewById(R.id.login_next_btn)
        progressBar = findViewById(R.id.login_progress_bar)
        resendOtpTextView = findViewById(R.id.resend_otp_textview)

        phoneNumber = intent.getStringExtra("phone")

        sendOtp(phoneNumber!!, false)

        nextBtn?.setOnClickListener {
            val enteredOtp = otpInput?.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationCode!!, enteredOtp)
            signIn(credential)
            setInProgress(true)
        }
        val currentPhoneNumber = phoneNumber //capture the current value of PhoneNumber
        resendOtpTextView?.setOnClickListener{
            currentPhoneNumber?.let { sendOtp(it, true)}
        }
    }
    fun sendOtp(phoneNumber: String, isResend: Boolean) {
        startResendTimer()
        setInProgress(true)
        val builder = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber).setTimeout(timeoutSeconds, TimeUnit.SECONDS).setActivity(this).setCallbacks(object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signIn(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                AndroidUtil.showToast(applicationContext, "OTP verification failed")
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationCode = p0
                resendingToken = p1
                AndroidUtil.showToast(applicationContext, "OTP sent successfully")
                setInProgress(false)
            }
        })
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(
                builder.setForceResendingToken(resendingToken).build())
        }
        else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }


    }

    fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar!!.visibility = View.VISIBLE
            nextBtn!!.visibility = View.GONE
        } else {
            progressBar!!.visibility = View.GONE
            nextBtn!!.visibility = View.VISIBLE
        }
    }
    fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        // login and go to next activity
        setInProgress(true)
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener { task -> setInProgress(false)
        if (task.isSuccessful) {
            val intent = Intent(this, LoginUsernameActivity::class.java)
            intent.putExtra("phone", phoneNumber)
            startActivity(intent)
        }
        else{
            AndroidUtil.showToast(applicationContext, "OTP verification failed")
            }
        }
    }
    fun startResendTimer(){
        resendOtpTextView!!.isEnabled = false
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                timeoutSeconds--
                runOnUiThread{
                    resendOtpTextView!!.text = "Resend OTP in $timeoutSeconds seconds"
            }
                if (timeoutSeconds<=0){
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread {
                        resendOtpTextView!!.isEnabled = true
                    }
                }
            }
        }, 0, 1000);
    }
}



