package com.example.irpfchecker.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irpfchecker.data.repository.UserRepository
import com.example.irpfchecker.domain.model.User
import com.example.irpfchecker.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ulid.ULID
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val context: Context
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _filteredUsers = MutableLiveData<List<User>>()
    val filteredUsers: LiveData<List<User>> get() = _filteredUsers

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    private val _isCreatingUser = MutableStateFlow(false)
    val isCreatingUser: StateFlow<Boolean> get() = _isCreatingUser.asStateFlow()

    init {
        refreshUsers()
        viewModelScope.launch {
            getAllUsers()
        }
    }

    fun createUser(name: String, email: String) {
        viewModelScope.launch {
            try {
                _isCreatingUser.value = true
                val user = User(ULID.randomULID(), name, email, Calendar.getInstance().time)
                userRepository.createUser(user)
                getAllUsers()
                context.showToast("Usuário criado com sucesso!")
            } catch (e: Exception) {
                context.showToast( "Erro ao tentar cadastrar um usuário!")
            } finally {
                _isCreatingUser.value = false
            }
        }
    }

    private suspend fun getAllUsers() {
        try {
            val userList = userRepository.findAll()
            _users.value = userList
        } catch (e: Exception) {
            context.showToast("Erro ao buscar usuários!")
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user)
            }catch (e: Exception) {
                context.showToast( "Erro ao tentar atualizar um usuário!")
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(user)
            }catch (e: Exception) {
                context.showToast( "Erro ao tentar excluir um usuário!")
            }
        }
    }

    fun refreshUsers() {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                val userList = userRepository.findAll()
                _users.value = userList
                context.showToast("Usuários atualizados com sucesso!")
            } catch (e: Exception) {
                context.showToast("Erro ao atualizar usuários!")
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}