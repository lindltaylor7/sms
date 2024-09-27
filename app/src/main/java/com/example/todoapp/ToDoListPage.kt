package com.example.todoapp

import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ToDoListPage(viewModel: TodoViewModel){
    val todoList by viewModel.todoList.observeAsState()
    var inputText by remember{
        mutableStateOf("")
    }

    var inputNumber by remember{
        mutableStateOf("")
    }

    Column (
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
    ){

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            OutlinedTextField(
                value = inputText,
                onValueChange = {
                inputText = it
            },
                label = {Text(text = "Nombre")}
            )

            IconButton(onClick = {
                viewModel.addTodo(inputText, inputNumber)
//                try {
//                    val smsManager = SmsManager.getDefault()
//                    smsManager.sendTextMessage("51"+inputNumber, null, inputText, null, null)
//                    Log.d("SMS", "Success")
//                } catch (e: Exception) {
//                    Log.d("SMS",e.toString())
//                }
                inputText = ""
                inputNumber = ""
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            OutlinedTextField(
                value = inputNumber,
                onValueChange = {
                    inputNumber = it
                },
                label = {Text(text = "Número")}
            )
        }

        todoList?.let{
            LazyColumn (
                content = {
                    itemsIndexed(it){index:Int, item: Todo ->
                        TodoItem(item = item, onDelete = {
                            viewModel.deleteTodo(item.id)
                        })
                    }
                }
            )
        }?: Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text="No hay ningun evento aun",
            fontSize = 16.sp,
            color = Color.White
        )


    }
}

@Composable
fun TodoItem(item: Todo, onDelete: ()->Unit){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically

    ){
        Column (
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = SimpleDateFormat(
                    "HH:mm:aa dd/mm/yyyy",
                    Locale.ENGLISH
                ).format(item.createdAt),
                fontSize = 12.sp,
                color = Color.Black
            )
            Text(
                text = item.title,
                fontSize = 20.sp,
                color = Color.White
            )
            Text(
                text = item.number,
                fontSize = 20.sp,
                color = Color.White
            )
        }
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }

    }
}