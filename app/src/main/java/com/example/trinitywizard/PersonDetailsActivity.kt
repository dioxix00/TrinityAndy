package com.example.trinitywizard

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.trinitywizard.MainActivity.Person
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*

class PersonDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.person_details)

        var person = intent.getSerializableExtra("person") as Person
        var profileImageView = findViewById<ImageView>(R.id.profile_image)
        var firstNameEditText = findViewById<EditText>(R.id.first_name_edittext)
        var lastNameEditText = findViewById<EditText>(R.id.last_name_edittext)
        var emailEditText = findViewById<EditText>(R.id.email_edittext)
        var dobEditText = findViewById<EditText>(R.id.dob_edittext)
        var saveButton = findViewById<Button>(R.id.save)


        firstNameEditText.setText(person.firstName)
        lastNameEditText.setText(person.lastName)
        emailEditText.setText(person.email)
        dobEditText.setText(person.dob)

        saveButton.setOnClickListener {
            // Update person details
            person.firstName = firstNameEditText.text.toString()
            person.lastName = lastNameEditText.text.toString()
            person.email = emailEditText.text.toString()
            person.dob = dobEditText.text.toString()

            // Read the contents of data.json from the app's assets directory
            val inputStream = applicationContext.assets.open("data.json")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()

            val file = File(applicationContext.filesDir, "data.json")
            val outputStream = FileOutputStream(file)
            outputStream.write(buffer)
            outputStream.close()

            // Update the person object with the new values
            person.firstName = firstNameEditText.text.toString()
            person.lastName = lastNameEditText.text.toString()
            person.email = emailEditText.text.toString()
            person.dob = dobEditText.text.toString()

            // Read the JSON data from the file into a string
            val json = file.readText(Charsets.UTF_8)

            // Parse the JSON string into a list of Person objects
            val persons = Gson().fromJson(json, Array<Person>::class.java).toMutableList()

            // Find the index of the person to update
            val index = persons.indexOfFirst { it.id == person.id }

            // Update the person in the list
            persons[index] = person

            // Convert the list of persons back to JSON
            val updatedJson = Gson().toJson(persons)

            // Write the updated JSON back to the file
            file.writeText(updatedJson, Charsets.UTF_8)

            // Copy the updated file back to the assets directory
            val assetManager = applicationContext.assets
            val outputStream2 = assetManager.openFd("data.json").createOutputStream()

            val inputStream2 = FileInputStream(file)
            val buffer2 = ByteArray(1024)
            var bytesRead = inputStream2.read(buffer2)
            while (bytesRead != -1) {
                outputStream2.write(buffer2, 0, bytesRead)
                bytesRead = inputStream2.read(buffer2)
            }

            inputStream2.close()
            outputStream2.flush()
            outputStream2.close()
        }
    }
}
