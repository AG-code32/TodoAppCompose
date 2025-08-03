package com.example.todoapp

import android.util.Log
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

    init {
        viewModelScope.launch {
            repository.allTodos.collect { list ->
                list.forEach { todo ->
                    Log.d("TodoListDebug", "Task: ${todo.title}, Due: ${Date(todo.dueAt ?: 0)}")
                }
            }
        }
    }

    fun addTodo(title: String, dueAt: Long) {
        val newTodo = Todo(
            id = 0,
            title = title,
            createdAt = System.currentTimeMillis(),
            dueAt = dueAt
        )
        viewModelScope.launch {
            repository.insert(newTodo)
        }
    }

    /*fun addTodo(title: String) {
        val now = System.currentTimeMillis()
        val dueInMillis = now + (2 * 24 * 60 * 60 * 1000)

        val newTodo = Todo(
            id = 0,
            title = title,
            createdAt = now,
            dueAt = dueInMillis
        )

        Log.d("TodoDebug", "Task created with dueAt = ${Date(newTodo.dueAt!!)}")

        viewModelScope.launch {
            repository.insert(newTodo)
        }
    }*/

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

    fun addTodoBack(todo: Todo) {
        viewModelScope.launch {
            repository.insert(todo)
        }
    }
}
