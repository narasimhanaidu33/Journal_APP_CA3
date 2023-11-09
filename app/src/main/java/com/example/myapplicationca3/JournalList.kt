package com.example.myapplicationca3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationca3.databinding.ActivityJournalListBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class JournalList : AppCompatActivity() {
    lateinit var binding: ActivityJournalListBinding

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference
    lateinit var journalList: MutableList<Journal>
    lateinit var adapter: JournalRecyclerAdapter
    var collectionReference: CollectionReference = db.collection("Journal")
    lateinit var noPostsTextView: TextView
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)
//        firebaseAuth = Firebase.auth
//        user = firebaseAuth.currentUser!!
//        binding.recyclerView.setHasFixedSize(true)
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        journalList = arrayListOf()
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)
        firebaseAuth = Firebase.auth
        user = firebaseAuth.currentUser!!

        journalList = arrayListOf()
        adapter = JournalRecyclerAdapter(this, journalList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> if (user != null && firebaseAuth != null) {
                val intent = Intent(this, AddJournalActivity::class.java)
                startActivity(intent)
            }

            R.id.action_signout -> if (user != null && firebaseAuth != null) {
                Firebase.auth.signOut()
                val intent = Intent(this, MainActivityJournalApp::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        val collectionReference = FirebaseFirestore.getInstance().collection("Journal")
        collectionReference.whereEqualTo("userId", user.uid).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    journalList.clear()
                    for (document in documents) {
                        val imageUrl = document.data["imageUrl"].toString()

                         //Check if the imageUrl is a valid URL
                        if (!isValidURL(imageUrl)) {
                            // Convert imageUrl to a valid URL format
                            val formattedImageUrl = formatURL(imageUrl)

                            val journal = Journal(
                                document.data["title"].toString(),
                                document.data["thoughts"].toString(),
                                "0",
                                document.data["userId"].toString(),
                                document.data["timeAdded"] as Timestamp,
                                document.data["username"].toString()
                            )
                            journalList.add(journal)
                        } else {
                            // Handle the case when imageUrl is not a valid URL
                            Log.d("Journal", "Invalid image URL: $imageUrl")
                        }
                    }

                    adapter.notifyDataSetChanged()
                    binding.listNoPosts.visibility = View.GONE
                } else {
                    binding.listNoPosts.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Something went wrong: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun isValidURL(urlString: String): Boolean {
        val urlRegex =
            Regex(pattern = "^(http|https|ftp)://([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]{1}[0-9]+)\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9]+)\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9]+)\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9]+))(:[0-9]+)?(/.*)?$")
        return urlRegex.matches(urlString)
    }

    private fun formatURL(url: String): String {
        // Remove trailing slash if present
        var formattedUrl = url.trimEnd('/')

        // Convert to HTTPS if not already using HTTPS
        if (!formattedUrl.startsWith("https://")) {
            formattedUrl = "https://$formattedUrl"
        }

        return formattedUrl
    }
}

    //getting user posts
//    override fun onStart() {
//        super.onStart()
//        collectionReference.whereEqualTo("userId",user.uid).get()
//            .addOnSuccessListener {
//                if (!it.isEmpty){
//                    for(document in it){
//                        var journal = Journal(
//                            document.data["title"].toString(),
//                            document.data["thoughts"].toString(),
//                            document.data["imageUrl"].toString(),
//                            document.data["userId"].toString(),
//                            document.data["timeAdded"] as Timestamp,
//                            document.data["username"].toString()
//                        )
//                        journalList.add(journal)
//                    }
//                    //recyclerview
//                    adapter = JournalRecyclerAdapter(this,journalList)
//                    binding.recyclerView.adapter = adapter
//                    adapter.notifyDataSetChanged()
//                }else{
//                    binding.listNoPosts.visibility = View.VISIBLE
//                }
//            }.addOnFailureListener{
//                Toast.makeText(this, "Something went wrong!",Toast.LENGTH_SHORT).show()
//            }
//    }
