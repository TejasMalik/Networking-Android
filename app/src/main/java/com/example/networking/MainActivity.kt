package com.example.networking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val originalList = arrayListOf<User>()

    val adapter = UserAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter.onItemClicked = {
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra("ID", it)
            startActivity(intent)

        }

        userRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchUsers(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchUsers(it) }
                return true
            }

        })

        searchView.setOnCloseListener {
            adapter.swapData(originalList)
            true
        }


        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                Client.api.getUsers()
            }

            if (response.isSuccessful) {
                response.body()?.let {
                    originalList.addAll(it)
                    adapter.swapData(it)
                }
            }
        }


//        val okHttpClient = OkHttpClient()
//
//        val request = Request.Builder().url("https://api.github.com/users/TejasMalik").build()
//
//        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

//        GlobalScope.launch(Dispatchers.Main) {
//            val response = withContext(Dispatchers.IO) {
//                okHttpClient.newCall(request).execute().body?.string()
//            }
//
////            val obj = JSONObject(response)
////            val login = obj.getString("login")
////            val name = obj.getString("name")
////            val image = obj.getString("avatar_url")
//
//            val user = gson.fromJson<User>(response, User::class.java)
//
//            textView1.text = user.name
//            textView2.text = user.login
//
//            Picasso.get().load(user.avatarUrl).into(imageView)
//
//        }
    }

    fun searchUsers(query: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                Client.api.searchUsers(query)
            }

            if (response.isSuccessful) {
                response.body()?.let {
                    it.items?.let { it1 -> adapter.swapData(it1) }
                }
            }
        }

    }
}