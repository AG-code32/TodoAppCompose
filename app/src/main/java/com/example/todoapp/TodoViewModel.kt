package com.example.todoapp

import java.util.Date
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    val todoList: StateFlow<List<Todo>> = repository.allTodos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTodo(title: String) {
        val newTodo = Todo(
            id = 0, // Room autogenera si se define como @PrimaryKey(autoGenerate = true)
            title = title,
            /*createdAt = Date()*/
        )
        viewModelScope.launch {
            repository.insert(newTodo)
        }
    }

    fun deleteTodo(id: Int) {
        viewModelScope.launch {
            todoList.value.find { it.id == id }?.let {
                repository.delete(it)
            }
        }
    }

    fun editTodo(id: Int, newTitle: String) {
        viewModelScope.launch {
            todoList.value.find { it.id == id }?.let {
                repository.update(it.copy(title = newTitle))
            }
        }
    }

    fun toggleTodoDone(id: Int) {
        viewModelScope.launch {
            todoList.value.find { it.id == id }?.let {
                repository.toggleDone(it)
            }
        }
    }
}
