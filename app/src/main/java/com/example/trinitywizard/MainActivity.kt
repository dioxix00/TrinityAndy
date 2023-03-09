package com.example.trinitywizard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.Charset
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PersonAdapter
    private var personList: List<Person> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val gson = GsonBuilder().create()
        val json = assets.open("data.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Person>>() {}.type
        personList = gson.fromJson(json, type)

        adapter = PersonAdapter(personList)
        recyclerView.adapter = adapter

        recyclerView.addOnItemClickListener(object : MainActivity.OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val person = personList[position]
                val intent = Intent(view.context, PersonDetailsActivity::class.java)
                intent.putExtra("person", person)
                startActivity(intent)
            }
        })
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    // Extension function to add OnItemClickListener to RecyclerView
    fun RecyclerView.addOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onItemClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }

    data class Person(
        var id: String,
        var firstName: String,
        var lastName: String,
        var email: String,
        var dob: String,
    ): Serializable

    // function to load JSON from asset folder
    private fun loadJsonFromAsset(fileName: String): String {
        val jsonString: String
        try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            jsonString = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return jsonString
    }

    // adapter for RecyclerView
    class PersonAdapter(private val persons: List<Person>) :
        RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.person_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val person = persons[position]
            holder.firstNameTextView.text = person.firstName
            holder.lastNameTextView.text = person.lastName

            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, PersonDetailsActivity::class.java)
                intent.putExtra("person", person as Serializable)
                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return persons.size
        }

        // ViewHolder for RecyclerView
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val firstNameTextView: TextView = itemView.findViewById(R.id.first_name)
            val lastNameTextView: TextView = itemView.findViewById(R.id.last_name)
        }
    }


}
