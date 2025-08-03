package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import np.com.bimalkafle.todoapp.TodoManager

class TodoViewModel : ViewModel() {

    private var _todoList = MutableLiveData<List<Todo>>(/*emptyList()*/)
    val todoList : LiveData<List<Todo>> = _todoList

    /*fun getAllTodo(){
        _todoList.value = TodoManager.getAllTodo()
    }
    fun addTodo (title : String) {
        TodoManager.addTodo(title)
        getAllTodo()
    }
    fun deleteTodo (id : Int) {
        TodoManager.deleteTodo(id)
        getAllTodo()
    }*/

    fun addTodo(title: String) {
        val currentList = _todoList.value ?: emptyList()
        val newTodo = Todo(
            id = currentList.size + 1,
            title = title,
            createdAt = java.util.Date()
        )
        _todoList.value = listOf(newTodo) + currentList
    }

    fun deleteTodo(id: Int) {
        val currentList = _todoList.value ?: emptyList()
        _todoList.value = _todoList.value?.filter { it.id != id }
    }

    fun editTodo(id: Int, newText: String) {
        _todoList.value = _todoList.value?.map{
            if (it.id == id) it.copy(title = newText) else it
        }
    }

    fun toggleTodoDone(id: Int) {
        _todoList.value = _todoList.value?.map {
            if (it.id == id) it.copy(isDone = !it.isDone) else it
        }
    }

}