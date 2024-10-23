package com.example.todoapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.*
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

private const val PICK_FILE_REQUEST = 1

@Preview(
    showBackground = true
)
@Composable
fun SimpleComposblePreview(){
    CardSms()
}

@Composable
fun CardSms(){

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(3.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Envío de SMS",
            fontSize = 30.sp
        )
        Text("Suba su archivo Excel")
    }

}

@Composable
fun ToDoListPage(viewModel: TodoViewModel){
    val todoList by viewModel.todoList.observeAsState(emptyList())
    var inputText by remember{
        mutableStateOf("")
    }

    var inputNumber by remember{
        mutableStateOf("")
    }

    var fileName = remember { "" }

    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
    ){

        CardSms()

        DocumentPicker(viewModel)

       /* OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputText,
            onValueChange = {
                inputText = it
            },
            label = {Text(text = "Nombre completo")}
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputNumber,
            onValueChange = {
                inputNumber = it
            },
            label = {Text(text = "Número de Celular")}
        )

        Button(onClick = {
            viewModel.addTodo(inputText, inputNumber)
            inputText = ""
            inputNumber = ""
        },
            modifier = Modifier.fillMaxWidth()
            ) { Text(text = "Agregar Contacto") }

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
            color = Color.Black
        )

        FloatingActionButton(onClick ={
            val extractedList = extractListFromTodoList(todoList)
            sendMessagesWithDelay(extractedList, context)
        },
            modifier = Modifier.fillMaxWidth()
            ) { Text(text = "Enviar Mensajes") }*/
    }
}

@Composable
fun DocumentPicker(viewModel: TodoViewModel){
    val context = LocalContext.current
    var fileName by remember { mutableStateOf("") }
    var excelData by remember { mutableStateOf("") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri: Uri?->
        uri?.let {
            fileName = getFileNameFromUri(context, it)
            excelData = readExcelFile(context, it, viewModel)
        }
    }

    Button(onClick = {
        filePickerLauncher.launch("*/*")
    }, modifier = Modifier.fillMaxWidth()
        .padding(3.dp, 5.dp)
    ) {
        Text("Subir Excel")
    }

    Text(text = if (fileName.isNotEmpty()) "Archivo seleccionado: $fileName" else "Ningún archivo seleccionado")

    Text(text = if (excelData.isNotEmpty()) "Contenido del Excel: $excelData" else "")
}

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var name = ""
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                name = it.getString(nameIndex) // Obtiene el nombre del archivo
            }
        }
    }
    return name
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

fun sendMessagesWithDelay(todoList: List<Todo>, context: Context){
    CoroutineScope(Dispatchers.Main).launch {
        for (item in todoList){
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage("51"+item.number, null, "Test de SMS", null, null)
                Log.d("SMS", "Success")
            } catch (e: Exception) {
                Log.d("SMS",e.toString())
            }
            Toast.makeText(context, item.title, Toast.LENGTH_SHORT).show()
        }
        delay(3000)
    }

}

fun extractListFromTodoList(todoList: List<Todo>): List<Todo> {
    return todoList
}

fun readExcelFile(context: Context, uri: Uri, viewModel: TodoViewModel): String {
    val contentResolver = context.contentResolver
    var data = ""

    var name = ""
    var cellphone = ""
    try {
        // Abre un InputStream desde la URI
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        inputStream?.use { stream ->
            // Usa Apache POI para leer el archivo Excel
            val workbook = WorkbookFactory.create(stream) // Crea un Workbook desde el InputStream
            val sheet = workbook.getSheetAt(0) // Obtiene la primera hoja

            // Itera sobre las filas y las celdas
            for (row in sheet) {
                for ((cellIndex, cell) in row.withIndex()) {
                    // Verifica si es la segunda columna (índice 1 en base 0)
                    if (cellIndex == 1 && cell.cellType == CellType.NUMERIC) {
                        // Formatea los números como texto, sin notación científica
                        data += cell.numericCellValue.toLong().toString() + " "
                        cellphone = "${cell.numericCellValue.toLong().toString()}"

                    } else {
                        // Para el resto de las celdas, usa toString normal
                        data += "${cell.toString()} "
                        name = "${cell.toString()}"
                    }
                }
                data += "\n" // Salto de línea para separar las filas
                viewModel.addTodo(name, cellphone)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return data
}
