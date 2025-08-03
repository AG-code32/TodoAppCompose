package com.example.todoapp

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {

    val allTodos: Flow<List<Todo>> = todoDao.getAllTodos()

    suspend fun insert(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    suspend fun delete(todo: Todo) {
        todoDao.deleteTodo(todo)
    }

    suspend fun update(todo: Todo) {
        todoDao.updateTodo(todo)
    }

    suspend fun toggleDone(todo: Todo) {
        val updated = todo.copy(isDone = !todo.isDone)
        todoDao.updateTodo(updated)
    }
}
