package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.test.services.events.TimeStamp
import com.example.easychat.model.UserModel
import com.example.easychat.ui.theme.EasyChatTheme
import com.example.easychat.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class LoginUsernameActivity : ComponentActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var letMeInBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var phoneNumber: String
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_username)

        usernameInput = findViewById(R.id.login_username)
        letMeInBtn = findViewById(R.id.login_let_me_in_btn)
        progressBar = findViewById(R.id.login_progress_bar)

        phoneNumber = intent.getStringExtra("phone").toString()
        getUsername()

        letMeInBtn.setOnClickListener {
            setUsername()
        }
    }
       private fun setUsername() {
            val username: String = usernameInput.text.toString()
            if (username.isEmpty() || username.length < 3) {
                usernameInput.error = "Username length should be at least 3 characters"
                return
            }
            setInProgress(true)
            if (::userModel.isInitialized){
                userModel.username = username
            }
            else{
                userModel = UserModel(phoneNumber, username, Timestamp.now())
            }
            FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener{ task -> setInProgress(false)
                if (task.isSuccessful){
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }

   private fun getUsername(){
        setInProgress(true)
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener { task ->
            setInProgress(false)
            if (task.isSuccessful) {
                userModel = task.result?.toObject(UserModel::class.java) ?: UserModel()
                userModel.let {
                    usernameInput.setText(it.username)
                }
            }
        }
    }
    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar!!.visibility = View.VISIBLE
            letMeInBtn!!.visibility = View.GONE
        } else {
            progressBar!!.visibility = View.GONE
            letMeInBtn!!.visibility = View.VISIBLE
        }
    }
}