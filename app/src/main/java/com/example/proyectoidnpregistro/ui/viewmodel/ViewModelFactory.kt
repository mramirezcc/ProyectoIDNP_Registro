package com.example.proyectoidnpregistro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectoidnpregistro.data.repository.AsistenteRepository
import com.example.proyectoidnpregistro.ui.lista.ListaViewModel
import com.example.proyectoidnpregistro.ui.registro.RegistroViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: AsistenteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ListaViewModel::class.java) -> {
                ListaViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegistroViewModel::class.java) -> {
                RegistroViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("ViewModel Class desconocido: ${modelClass.name}")
        }
    }
}
