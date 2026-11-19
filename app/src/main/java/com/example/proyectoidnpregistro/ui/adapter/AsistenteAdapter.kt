package com.example.proyectoidnpregistro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoidnpregistro.R
import com.example.proyectoidnpregistro.data.local.AsistenteEntity
import com.example.proyectoidnpregistro.data.model.TipoAsistente
import com.example.proyectoidnpregistro.databinding.ItemAsistenteBinding

class AsistenteAdapter(
    private val onItemClick: (AsistenteEntity) -> Unit,
    private val onDeleteClick: (AsistenteEntity) -> Unit
) : ListAdapter<AsistenteEntity, AsistenteAdapter.AsistenteViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsistenteViewHolder {
        val binding = ItemAsistenteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AsistenteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AsistenteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AsistenteViewHolder(
        private val binding: ItemAsistenteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(asistente: AsistenteEntity) {
            val context = binding.root.context
            binding.tvNombreCompleto.text = asistente.nombreCompleto
            binding.tvUbicacion.text = "${asistente.distrito} - ${asistente.provincia}"
            binding.tvDireccion.text = "${asistente.direccion} (Dpto: ${asistente.departamento})"
            binding.tvGrupoSanguineo.text = asistente.grupoSanguineo
            binding.badgeTipoAsistente.text = asistente.tipoAsistente

            // Colores dinámicos del Badge según categoría
            val (badgeBgColor, badgeTextColor) = when (asistente.tipoAsistente) {
                TipoAsistente.PROFESIONAL.displayName -> Pair(
                    ContextCompat.getColor(context, R.color.badge_profesional_bg),
                    ContextCompat.getColor(context, R.color.badge_profesional_text)
                )
                TipoAsistente.ESTUDIANTE.displayName -> Pair(
                    ContextCompat.getColor(context, R.color.badge_estudiante_bg),
                    ContextCompat.getColor(context, R.color.badge_estudiante_text)
                )
                else -> Pair(
                    ContextCompat.getColor(context, R.color.badge_publico_bg),
                    ContextCompat.getColor(context, R.color.badge_publico_text)
                )
            }

            binding.badgeTipoAsistente.setBackgroundColor(badgeBgColor)
            binding.badgeTipoAsistente.setTextColor(badgeTextColor)

            binding.root.setOnClickListener {
                onItemClick(asistente)
            }

            binding.btnEliminar.setOnClickListener {
                onDeleteClick(asistente)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AsistenteEntity>() {
        override fun areItemsTheSame(oldItem: AsistenteEntity, newItem: AsistenteEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AsistenteEntity, newItem: AsistenteEntity): Boolean {
            return oldItem == newItem
        }
    }
}
