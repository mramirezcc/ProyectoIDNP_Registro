package com.example.proyectoidnpregistro.ui.registro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.proyectoidnpregistro.R
import com.example.proyectoidnpregistro.data.local.AppDatabase
import com.example.proyectoidnpregistro.data.model.TipoAsistente
import com.example.proyectoidnpregistro.data.provider.UbigeoProvider
import com.example.proyectoidnpregistro.data.repository.AsistenteRepositoryImpl
import com.example.proyectoidnpregistro.databinding.ActivityRegistroBinding
import com.example.proyectoidnpregistro.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var viewModel: RegistroViewModel

    private var asistenteIdParaEditar: Long = -1L
    private var isUpdatingFromViewModel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getInstance(applicationContext)
        val repository = AsistenteRepositoryImpl(database.asistenteDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RegistroViewModel::class.java]

        asistenteIdParaEditar = intent.getLongExtra(EXTRA_ASISTENTE_ID, -1L)

        setupToolbar()
        setupStaticDropdowns()
        setupCascadeUbigeo()
        setupListeners()
        observeViewModel()

        if (asistenteIdParaEditar > 0) {
            binding.toolbar.title = getString(R.string.editar_registro)
            binding.btnGuardar.text = "Actualizar Asistente"
            viewModel.cargarAsistente(asistenteIdParaEditar)
        } else {
            // Modo nuevo: precargar la ubicación por defecto automáticamente
            precargarUbicacionPorDefecto()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupStaticDropdowns() {
        // Grupo Sanguíneo
        val adapterGrupo = ArrayAdapter(
            this,
            R.layout.item_dropdown,
            UbigeoProvider.gruposSanguineos
        )
        binding.actvGrupoSanguineo.setAdapter(adapterGrupo)

        // Departamentos
        val adapterDeptos = ArrayAdapter(
            this,
            R.layout.item_dropdown,
            UbigeoProvider.getNombresDepartamentos()
        )
        binding.actvDepartamento.setAdapter(adapterDeptos)
    }

    /**
     * Autocompleta por defecto la ubicación del congreso sin que el usuario tenga que escribir nada.
     */
    private fun precargarUbicacionPorDefecto() {
        // Departamento: Arequipa
        binding.actvDepartamento.setText(UbigeoProvider.DEFAULT_DEPARTAMENTO, false)
        actualizarProvincias(UbigeoProvider.DEFAULT_DEPARTAMENTO, limpiarSeleccion = false)

        // Provincia: Arequipa
        binding.actvProvincia.setText(UbigeoProvider.DEFAULT_PROVINCIA, false)
        actualizarDistritos(UbigeoProvider.DEFAULT_DEPARTAMENTO, UbigeoProvider.DEFAULT_PROVINCIA, limpiarSeleccion = false)

        // Distrito: Yanahuara
        binding.actvDistrito.setText(UbigeoProvider.DEFAULT_DISTRITO, false)
    }

    private fun setupCascadeUbigeo() {
        // 1. Al cambiar Departamento manualmente
        binding.actvDepartamento.setOnItemClickListener { _, _, position, _ ->
            val depto = binding.actvDepartamento.adapter.getItem(position).toString()
            viewModel.onDepartamentoChanged(depto)
            actualizarProvincias(depto, limpiarSeleccion = true)
        }

        // 2. Al cambiar Provincia manualmente
        binding.actvProvincia.setOnItemClickListener { _, _, position, _ ->
            val depto = binding.actvDepartamento.text.toString()
            val prov = binding.actvProvincia.adapter.getItem(position).toString()
            viewModel.onProvinciaChanged(prov)
            actualizarDistritos(depto, prov, limpiarSeleccion = true)
        }

        // 3. Al cambiar Distrito
        binding.actvDistrito.setOnItemClickListener { _, _, position, _ ->
            val dist = binding.actvDistrito.adapter.getItem(position).toString()
            viewModel.onDistritoChanged(dist)
        }
    }

    private fun actualizarProvincias(depto: String, limpiarSeleccion: Boolean) {
        val provincias = UbigeoProvider.getNombresProvincias(depto)
        val adapterProv = ArrayAdapter(this, R.layout.item_dropdown, provincias)
        binding.actvProvincia.setAdapter(adapterProv)

        if (limpiarSeleccion) {
            binding.actvProvincia.setText("", false)
            binding.actvDistrito.setText("", false)
            binding.tilDistrito.isEnabled = false
        }
        binding.tilProvincia.isEnabled = provincias.isNotEmpty()
    }

    private fun actualizarDistritos(depto: String, prov: String, limpiarSeleccion: Boolean) {
        val distritos = UbigeoProvider.getNombresDistritos(depto, prov)
        val adapterDist = ArrayAdapter(this, R.layout.item_dropdown, distritos)
        binding.actvDistrito.setAdapter(adapterDist)

        if (limpiarSeleccion) {
            binding.actvDistrito.setText("", false)
        }
        binding.tilDistrito.isEnabled = distritos.isNotEmpty()
    }

    private fun setupListeners() {
        // Nombres en tiempo real
        binding.etNombres.doAfterTextChanged {
            if (!isUpdatingFromViewModel) {
                viewModel.onNombresChanged(it?.toString().orEmpty())
            }
        }

        // Apellidos en tiempo real
        binding.etApellidos.doAfterTextChanged {
            if (!isUpdatingFromViewModel) {
                viewModel.onApellidosChanged(it?.toString().orEmpty())
            }
        }

        // Dirección en tiempo real
        binding.etDireccion.doAfterTextChanged {
            if (!isUpdatingFromViewModel) {
                viewModel.onDireccionChanged(it?.toString().orEmpty())
            }
        }

        // Grupo Sanguíneo
        binding.actvGrupoSanguineo.setOnItemClickListener { _, _, position, _ ->
            val grupo = binding.actvGrupoSanguineo.adapter.getItem(position).toString()
            viewModel.onGrupoSanguineoChanged(grupo)
        }

        // Tipo de Asistente
        binding.rgTipoAsistente.setOnCheckedChangeListener { _, checkedId ->
            if (!isUpdatingFromViewModel) {
                val tipo = when (checkedId) {
                    R.id.rbProfesional -> TipoAsistente.PROFESIONAL.displayName
                    R.id.rbEstudiante -> TipoAsistente.ESTUDIANTE.displayName
                    R.id.rbPublico -> TipoAsistente.PUBLICO_GENERAL.displayName
                    else -> TipoAsistente.PUBLICO_GENERAL.displayName
                }
                viewModel.onTipoAsistenteChanged(tipo)
            }
        }

        // Botón Guardar
        binding.btnGuardar.setOnClickListener {
            viewModel.guardarAsistente()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.formularioState.collect { state ->
                        renderState(state)
                    }
                }

                launch {
                    viewModel.eventGuardadoExitoso.collect { mensaje ->
                        Toast.makeText(this@RegistroActivity, mensaje, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun renderState(state: FormularioState) {
        // Actualizar la Barra de Progreso Superior
        binding.progressIndicator.setProgressCompat(state.porcentajeCompletado, true)
        binding.tvProgresoTexto.text = "Progreso del formulario: ${state.porcentajeCompletado}%"
        binding.tvCamposCompletados.text = "${state.totalCamposValidos} de 6 completados"

        // Mostrar u ocultar errores en tiempo real
        binding.tilNombres.error = state.nombresError
        binding.tilApellidos.error = state.apellidosError
        binding.tilDepartamento.error = state.departamentoError
        binding.tilProvincia.error = state.provinciaError
        binding.tilDistrito.error = state.distritoError
        binding.tilDireccion.error = state.direccionError
        binding.tilGrupoSanguineo.error = state.grupoSanguineoError

        binding.btnGuardar.isEnabled = !state.isSaving

        // En modo edición, rellenar campos
        if (state.isEditMode && binding.etNombres.text.isNullOrEmpty() && state.nombres.isNotEmpty()) {
            isUpdatingFromViewModel = true
            binding.etNombres.setText(state.nombres)
            binding.etApellidos.setText(state.apellidos)
            binding.etDireccion.setText(state.direccion)

            binding.actvDepartamento.setText(state.departamento, false)
            actualizarProvincias(state.departamento, limpiarSeleccion = false)

            binding.actvProvincia.setText(state.provincia, false)
            actualizarDistritos(state.departamento, state.provincia, limpiarSeleccion = false)

            binding.actvDistrito.setText(state.distrito, false)
            binding.actvGrupoSanguineo.setText(state.grupoSanguineo, false)

            when (state.tipoAsistente) {
                TipoAsistente.PROFESIONAL.displayName -> binding.rbProfesional.isChecked = true
                TipoAsistente.ESTUDIANTE.displayName -> binding.rbEstudiante.isChecked = true
                TipoAsistente.PUBLICO_GENERAL.displayName -> binding.rbPublico.isChecked = true
            }
            isUpdatingFromViewModel = false
        }
    }

    companion object {
        const val EXTRA_ASISTENTE_ID = "extra_asistente_id"

        fun newIntent(context: Context, asistenteId: Long = -1L): Intent {
            return Intent(context, RegistroActivity::class.java).apply {
                putExtra(EXTRA_ASISTENTE_ID, asistenteId)
            }
        }
    }
}
