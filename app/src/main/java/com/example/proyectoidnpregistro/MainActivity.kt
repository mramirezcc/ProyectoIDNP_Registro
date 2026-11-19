package com.example.proyectoidnpregistro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoidnpregistro.databinding.ActivityMainBinding
import com.example.proyectoidnpregistro.ui.registro.RegistroActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabNuevoAsistente.setOnClickListener {
            val intent = RegistroActivity.newIntent(this)
            startActivity(intent)
        }
    }
}
