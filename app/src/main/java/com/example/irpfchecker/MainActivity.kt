package com.example.irpfchecker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.irpfchecker.domain.model.User
import com.example.irpfchecker.ui.Screens
import com.example.irpfchecker.ui.theme.replyLightError
import com.example.irpfchecker.ui.theme.replyTypography
import com.example.irpfchecker.ui.theme.shapes
import com.example.irpfchecker.ui.viewmodel.CreateUserViewModel
import com.example.irpfchecker.util.formatRelativeTime
import com.example.irpfchecker.util.isValidEmail
import com.example.irpfchecker.util.showToast
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: CreateUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(viewModel = viewModel)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(viewModel: CreateUserViewModel) {
    val navController = rememberNavController()
    val users by viewModel.users.observeAsState()

    MaterialTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screens.CreateUser) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            bottomBar = {
                NavigationBar(navController = navController)
            }
        ) {
            NavHost(navController, startDestination = Screens.UserList) {
                composable(Screens.UserList) {
                    UserListScreen(
                        viewModel = viewModel,
                        users = users ?: emptyList(),
                        navController = navController,
                    )
                }
                composable(Screens.CreateUser) {
                    CreateUserScreen(
                        viewModel = viewModel,
                        navController = navController,
                        context = null
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationBar(navController: NavHostController) {
    val screens = listOf(
        Screens.UserList to "Usuários",
        Screens.CreateUser to "Criar Usuário"
    )

    NavigationBar {
        screens.forEach { (screen, label) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screens.UserList -> Icons.Default.Person
                            Screens.CreateUser -> Icons.Default.Add
                            else -> {}
                        } as ImageVector,
                        contentDescription = null
                    )
                },
                label = { Text(label) },
                selected = navController.currentDestination?.route == screen,
                onClick = {
                    navController.navigate(screen) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    users: List<User>,
    navController: NavHostController,
    viewModel: CreateUserViewModel
) {
    val searchQuery = remember { mutableStateOf("") }
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val filteredUsers = remember(searchQuery.value, users) {
        users.filter { user ->
            user.name.contains(searchQuery.value, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.CreateUser) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            ) {
                TextField(
                    value = searchQuery.value,
                    onValueChange = { query ->
                        searchQuery.value = query
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("Procure...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Procure...") },
                    trailingIcon = {
                        if (searchQuery.value.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery.value = "" }
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Limpar"
                                )
                            }
                        }
                    },
                    shape = shapes.medium,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onSearch = { }),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                        placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                viewModel.refreshUsers()
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(filteredUsers) { user ->
                        UserListItem(user = user,
                            onDelete = {
                                viewModel.deleteUser(user)
                            },
                            onEdit = {
                                TODO()
                            })
                    }
                }

                if (filteredUsers.isEmpty()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.error_message),
                        style = MaterialTheme.typography.bodyLarge,
                        color = replyLightError
                    )
                }
            }
        }
    }
}

@Composable
fun UserListItem(
    user: User,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.name,
                    style = replyTypography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = user.email,
                style = replyTypography.bodyMedium,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = formatRelativeTime(user.createdAt),
                style = replyTypography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onEdit()
                },
                text = { Text(text = "Editar") }
            )
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onDelete()
                },
                text = { Text(text = "Excluir") }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    viewModel: CreateUserViewModel,
    navController: NavHostController,
    context: Context?
) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }

    val isCreatingUser by viewModel.isCreatingUser.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Nome") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                when {
                    name.value.isEmpty() -> {
                        context?.showToast("Erro: O campo de nome não pode estar vazio!")
                    }
                    email.value.isEmpty() -> {
                        context?.showToast("Erro: O campo de e-mail não pode estar vazio!")
                    }
                    !isValidEmail(email.value) -> {
                        context?.showToast("Erro: O e-mail fornecido é inválido!")
                    }
                    else -> {
                        viewModel.createUser(name.value, email.value)
                        context?.showToast("Operação realizada com sucesso!")
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            if (isCreatingUser) {
                LinearProgressIndicator()
            } else {
                Text("Cadastrar")
            }
        }
    }
}
