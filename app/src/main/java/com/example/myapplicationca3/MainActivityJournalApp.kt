package com.example.myapplicationca3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.myapplicationca3.databinding.ActivityMainJournalAppBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivityJournalApp : AppCompatActivity() {
    lateinit var binding: ActivityMainJournalAppBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_journal_app)
        binding.createAccBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.emailSignInButton.setOnClickListener {
            loginWithEmailPassword(
                binding.email.text.toString().trim(),
                binding.password.text.toString().trim()
            )
        }
        auth = Firebase.auth
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                var journal: JournalUser = JournalUser.instance!!
                journal.userId = auth.currentUser?.uid
                journal.username = auth.currentUser?.displayName
                goToJournalList()
            }else{
                Toast.makeText(this,"Authentication Failed!",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        var currentUser = auth.currentUser
        if(currentUser!=null){
            goToJournalList()
        }
    }

    private fun goToJournalList() {
        var intent = Intent(this, JournalList::class.java)
        startActivity(intent)
    }
}