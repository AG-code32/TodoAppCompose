package com.example.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

fun getTaskBackgroundColor(todo: Todo): Color {
    val now = System.currentTimeMillis()
    val timeDiff = todo.dueAt - now

    val hours = TimeUnit.MILLISECONDS.toHours(timeDiff)
    val days = TimeUnit.MILLISECONDS.toDays(timeDiff)

    return when {
        hours <= 5 -> Color(0xFFFFCDD2) // 🔴 rojo claro
        days < 1 -> Color(0xFFFFF9C4)   // 🟠 amarillo/naranja claro
        todo.isDone -> Color(0xFFB2DFDB) // ✅ completado
        else -> Color(0xFF81D4FA)        // 🔵 por defecto
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoListPage(viewModel: TodoViewModel) {
    val todoList by viewModel.todoList.collectAsState()
    var inputText by remember {
        mutableStateOf("")
    }

    var showDialog by remember { mutableStateOf(false) }
    var editingText by remember { mutableStateOf("") }
    var editingTodoId by remember { mutableStateOf<Int?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var filter by remember { mutableStateOf(TodoFilter.ALL) }

    val filteredList = when (filter) {
        TodoFilter.ALL -> todoList
        TodoFilter.ACTIVE -> todoList?.filter { !it.isDone }
        TodoFilter.COMPLETED -> todoList?.filter { it.isDone }
    }

    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(innerPadding)
        ) {
            Row(    // box and add button
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField( /*modifier = Modifier.weight(1f),*/
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Add a Task") }
                )
                /**/


                /**/
                Button(
                    onClick = {
                        if (
                            inputText.trim().isNotEmpty() &&
                            selectedDate != null &&
                            selectedTime != null
                        ) {
                            val dueAtMillis = LocalDateTime.of(selectedDate, selectedTime)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()

                            viewModel.addTodo(inputText.trim(), dueAtMillis)

                            // Reset form
                            inputText = ""
                            selectedDate = null
                            selectedTime = null
                        }
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(text = "Add")
                }
            }

            Row(    // date and time
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    val today = LocalDate.now()
                    DatePickerDialog(context, { _, year, month, dayOfMonth ->
                        selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    }, today.year, today.monthValue - 1, today.dayOfMonth).show()
                }) {
                    Text(selectedDate?.format(dateFormatter) ?: "Due date")
                }

                Button(onClick = {
                    val now = LocalTime.now()
                    TimePickerDialog(context, { _, hour, minute ->
                        selectedTime = LocalTime.of(hour, minute)
                    }, now.hour, now.minute, true).show()
                }) {
                    Text(selectedTime?.format(timeFormatter) ?: "Due time")
                }

            }

            Row(    // filter
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { filter = TodoFilter.ALL },
                    enabled = filter != TodoFilter.ALL
                ) {
                    Text("All")
                }
                Button(
                    onClick = { filter = TodoFilter.ACTIVE },
                    enabled = filter != TodoFilter.ACTIVE
                ) {
                    Text("Active")
                }
                Button(
                    onClick = { filter = TodoFilter.COMPLETED },
                    enabled = filter != TodoFilter.COMPLETED
                ) {
                    Text("Completed")
                }
            }

            // List of tasks
            filteredList?.let {
                LazyColumn(
                    content = {

                        items(it, key = { it.id }) { item ->

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart) {
                                        coroutineScope.launch {

                                            val deletedItem = item
                                            viewModel.deleteTodo(item.id)

                                            coroutineScope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Task deleted",
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.addTodoBack(deletedItem)
                                                }
                                            }
                                            /*viewModel.deleteTodo(item.id)
                                        snackbarHostState.showSnackbar("Task deleted")*/


                                        }
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                enableDismissFromEndToStart = true,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0x0581D4FA))
                                            .padding(16.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White
                                        )
                                    }
                                },
                                content = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .background(
                                                getTaskBackgroundColor(item),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .combinedClickable(
                                                onClick = { /* nada */ },
                                                onLongClick = {
                                                    editingText = item.title
                                                    editingTodoId = item.id
                                                    showDialog = true
                                                }
                                            )
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Checkbox aligned to center vertically
                                        Checkbox(
                                            checked = item.isDone,
                                            onCheckedChange = {
                                                viewModel.toggleTodoDone(item.id)
                                            }
                                        )
                                        // Title text, style depends on isDone
                                        Text(
                                            text = item.title,
                                            fontSize = 16.sp,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = 8.dp),
                                            textDecoration = if (item.isDone) TextDecoration.LineThrough else null
                                        )

                                        IconButton(
                                            onClick = {

                                                val deletedItem = item
                                                viewModel.deleteTodo(item.id)

                                                coroutineScope.launch {
                                                    val result = snackbarHostState.showSnackbar(
                                                        message = "Task deleted",
                                                        actionLabel = "Undo",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                    if (result == SnackbarResult.ActionPerformed) {
                                                        viewModel.addTodoBack(deletedItem)
                                                    }
                                                }

                                                /*viewModel.deleteTodo(item.id)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Task deleted")
                                            }*/
                                            },
                                            modifier = Modifier.align(Alignment.Top)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.Black
                                            )
                                        }
                                    }
                                }
                            )

                        }
                    }
                )

                /*
            *
            *
            *
            * */

                if (showDialog && editingTodoId != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Edit Task") },
                        text = {
                            OutlinedTextField(
                                value = editingText,
                                onValueChange = { editingText = it },
                                label = { Text("New text") }
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.editTodo(editingTodoId!!, editingText)
                                showDialog = false

                                // Show Snackbar
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Task edited")
                                }
                            }) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }


            } ?: Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "No items yet",
                fontSize = 16.sp
            )


        }
    }

}


