package com.example.proyectoidnpregistro

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectoidnpregistro.data.local.AppDatabase
import com.example.proyectoidnpregistro.data.local.AsistenteEntity
import com.example.proyectoidnpregistro.data.model.TipoAsistente
import com.example.proyectoidnpregistro.data.repository.AsistenteRepositoryImpl
import com.example.proyectoidnpregistro.databinding.ActivityMainBinding
import com.example.proyectoidnpregistro.ui.adapter.AsistenteAdapter
import com.example.proyectoidnpregistro.ui.lista.ListaViewModel
import com.example.proyectoidnpregistro.ui.registro.RegistroActivity
import com.example.proyectoidnpregistro.ui.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ListaViewModel
    private lateinit var adapter: AsistenteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getInstance(applicationContext)
        val repository = AsistenteRepositoryImpl(database.asistenteDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ListaViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        setupFilterChips()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = AsistenteAdapter(
            onItemClick = { asistente ->
                // Abrir pantalla en modo edición
                val intent = RegistroActivity.newIntent(this, asistente.id)
                startActivity(intent)
            },
            onDeleteClick = { asistente ->
                mostrarDialogoEliminar(asistente)
            }
        )

        binding.rvAsistentes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSearch() {
        binding.etBuscar.doAfterTextChanged { text ->
            viewModel.onSearchQueryChanged(text?.toString().orEmpty())
        }
    }

    private fun setupFilterChips() {
        binding.chipGroupFiltros.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: R.id.chipTodos
            val tipoSeleccionado = when (checkedId) {
                R.id.chipProfesional -> TipoAsistente.PROFESIONAL.displayName
                R.id.chipEstudiante -> TipoAsistente.ESTUDIANTE.displayName
                R.id.chipPublico -> TipoAsistente.PUBLICO_GENERAL.displayName
                else -> "TODOS"
            }
            viewModel.onFiltroTipoChanged(tipoSeleccionado)
        }
    }

    private fun setupFab() {
        binding.fabNuevoAsistente.setOnClickListener {
            val intent = RegistroActivity.newIntent(this)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // 1. Dashboard de Contadores en tiempo real
                    binding.tvCountTotal.text = state.totalCount.toString()
                    binding.tvCountProfesionales.text = state.countProfesionales.toString()
                    binding.tvCountEstudiantes.text = state.countEstudiantes.toString()
                    binding.tvCountPublico.text = state.countPublico.toString()

                    // 2. Lista en RecyclerView con animaciones automáticas de DiffUtil
                    adapter.submitList(state.asistentes)

                    // 3. Estado Vacío (Empty State)
                    if (state.asistentes.isEmpty()) {
                        binding.layoutEmptyState.visibility = View.VISIBLE
                        binding.rvAsistentes.visibility = View.GONE
                    } else {
                        binding.layoutEmptyState.visibility = View.GONE
                        binding.rvAsistentes.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun mostrarDialogoEliminar(asistente: AsistenteEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirmar_eliminar_titulo)
            .setMessage(getString(R.string.confirmar_eliminar_mensaje, asistente.nombreCompleto))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                viewModel.eliminarAsistente(asistente)
                Snackbar.make(
                    binding.root,
                    "Asistente '${asistente.nombreCompleto}' eliminado",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}
