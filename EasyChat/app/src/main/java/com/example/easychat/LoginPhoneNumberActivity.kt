package com.example.easychat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.easychat.ui.theme.EasyChatTheme
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.hbb20.CountryCodePicker
import android.view.View
import android.content.Intent




class LoginPhoneNumberActivity : AppCompatActivity() {
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var phoneInput: EditText
    private lateinit var sendOtpBtn: Button
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_phone_number)

        // Initialize your views here
        countryCodePicker = findViewById(R.id.login_countrycode)
        phoneInput = findViewById(R.id.login_mobile_number)
        sendOtpBtn = findViewById(R.id.send_otp_btn)
        progressBar = findViewById(R.id.login_progress_bar)

        progressBar.visibility = View.GONE

        countryCodePicker.registerCarrierNumberEditText(phoneInput)

        sendOtpBtn.setOnClickListener{
            // Your action when sendOtpBtn is clicked
            if (!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number not valid.")
                return@setOnClickListener
            }
            val intent = Intent(this, LoginOtpActivity::class.java).apply{
                putExtra("phone", countryCodePicker.fullNumberWithPlus)
            }
            startActivity(intent)
        }
    }
}
