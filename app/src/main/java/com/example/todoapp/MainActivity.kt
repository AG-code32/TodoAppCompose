package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.todoapp.ui.theme.TodoAppTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val database = TodoDatabase.getDatabase(applicationContext)
        val repository = TodoRepository(database.todoDao())

        val viewModelFactory = TodoViewModelFactory(repository)
        val todoViewModel = ViewModelProvider(this, viewModelFactory)[TodoViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoListPage(todoViewModel)
                }
            }
        }
    }
}

