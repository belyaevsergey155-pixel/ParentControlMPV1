package com.example.parentcontrol

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Rule(val name: String, val minutes: Int, val enabled: Boolean = true)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ParentControlApp(this) }
    }
}

@Composable
fun ParentControlApp(activity: ComponentActivity) {
    var parentMode by remember { mutableStateOf(true) }
    var rules by remember { mutableStateOf(listOf(
        Rule("YouTube", 60), Rule("Minecraft", 60), Rule("Roblox", 45)
    )) }
    var locked by remember { mutableStateOf(false) }

    MaterialTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("Мой Контроль") }) }) { pad ->
            LazyColumn(
                modifier = Modifier.padding(pad).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { parentMode = true }, modifier = Modifier.weight(1f)) { Text("Родитель") }
                        OutlinedButton(onClick = { parentMode = false }, modifier = Modifier.weight(1f)) { Text("Ребёнок") }
                    }
                }
                if (parentMode) {
                    item { Text("Устройство ребёнка: не подключено", style = MaterialTheme.typography.titleMedium) }
                    item {
                        Button(onClick = { activity.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Разрешить статистику приложений")
                        }
                    }
                    item { Text("Лимиты приложений", style = MaterialTheme.typography.titleLarge) }
                    items(rules.size) { index ->
                        val r = rules[index]
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(r.name, style = MaterialTheme.typography.titleMedium)
                                Text("Лимит: ${r.minutes} мин/день")
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = {
                                        val copy = rules.toMutableList(); copy[index] = r.copy(minutes = (r.minutes - 15).coerceAtLeast(15)); rules = copy
                                    }) { Text("−15") }
                                    OutlinedButton(onClick = {
                                        val copy = rules.toMutableList(); copy[index] = r.copy(minutes = r.minutes + 15); rules = copy
                                    }) { Text("+15") }
                                    Switch(checked = r.enabled, onCheckedChange = {
                                        val copy = rules.toMutableList(); copy[index] = r.copy(enabled = it); rules = copy
                                    })
                                }
                            }
                        }
                    }
                    item {
                        Button(onClick = { locked = !locked }, modifier = Modifier.fillMaxWidth()) {
                            Text(if (locked) "Разблокировать" else "🔒 Заблокировать сейчас")
                        }
                    }
                    item { Text("Ночной режим: 21:00–07:00") }
                    item { Text("Статус: ${if (locked) "заблокировано" else "разрешено"}") }
                } else {
                    item { Text("Детский режим", style = MaterialTheme.typography.headlineSmall) }
                    item { Text("Сегодня доступно: 1 ч 45 мин") }
                    item { Text("Ограниченные приложения") }
                    rules.filter { it.enabled }.forEach { r -> item { Text("• ${r.name}: ${r.minutes} мин/день") } }
                    item { Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Попросить ещё 30 минут") } }
                }
            }
        }
    }
}
