package com.family.organizer.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.family.organizer.data.auth.AuthRepository
import com.family.organizer.data.family.FamilyCloudRepository
import com.family.organizer.data.family.FamilySession
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val checkingSession: Boolean = true,
    val userId: String? = null,
    val userEmail: String? = null,
    val familyId: String? = null,
    val checkingFamily: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val familyCloudRepository: FamilyCloudRepository,
    private val familySession: FamilySession,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var familyJob: Job? = null

    init {
        viewModelScope.launch {
            authRepository.authState().collect { user ->
                familyJob?.cancel()
                if (user == null) {
                    _uiState.value = AuthUiState(checkingSession = false)
                    familySession.setFamilyId(null)
                } else {
                    _uiState.value = _uiState.value.copy(
                        checkingSession = false,
                        userId = user.uid,
                        userEmail = user.email,
                        familyId = null,
                        checkingFamily = true,
                    )
                    familySession.setFamilyId(null)
                    familyJob = viewModelScope.launch {
                        familyCloudRepository.observeUserFamilyId(user.uid).collect { familyId ->
                            _uiState.value = _uiState.value.copy(familyId = familyId, checkingFamily = false)
                            familySession.setFamilyId(familyId)
                        }
                    }
                }
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signUp(email.trim(), password)
            result.onSuccess {
                val uid = authRepository.currentUserId
                if (uid != null) familyCloudRepository.ensureUserDoc(uid, email.trim(), name.trim())
            }
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorOrNull(result))
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signIn(email.trim(), password)
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorOrNull(result))
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun createFamily(name: String) {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = familyCloudRepository.createFamily(name.trim(), uid)
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorOrNull(result))
        }
    }

    fun joinFamily(inviteCode: String) {
        val uid = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = familyCloudRepository.joinFamily(inviteCode.trim().uppercase(), uid)
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorOrNull(result))
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun errorOrNull(result: Result<*>): String? {
        val error = result.exceptionOrNull() ?: return null
        return error.message ?: "Что-то пошло не так, попробуйте ещё раз"
    }
}

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val familyCloudRepository: FamilyCloudRepository,
    private val familySession: FamilySession,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AuthViewModel(authRepository, familyCloudRepository, familySession) as T
}
