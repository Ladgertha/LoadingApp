package ru.ladgertha.loadingapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.ladgertha.loadingapplication.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

}