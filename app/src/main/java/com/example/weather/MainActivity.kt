package com.example.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel : MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var mainInputField: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        prepareViewPager()

        mainInputField.setEndIconOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                mainViewModel.getCoordinates(mainInputField.editText?.text.toString())
            }
        }

        mainViewModel.coordinatesResult.observe(this, Observer {
            lifecycleScope.launch(Dispatchers.Main) {
                if (it.lat == 0.0 && it.lon == 0.0) {
                    Toast.makeText(this@MainActivity, "Invalid input", Toast.LENGTH_SHORT).show()
                } else {
                    mainViewModel.getCurrentWeather(it.lat, it.lon)
                    mainViewModel.getForecast(it.lat, it.lon)
                }
            }
        })

    }

    private fun initViews() {
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        mainInputField = binding.mainInputField
    }

    private fun prepareViewPager() {
        val fragmentList = arrayListOf(
            WeatherFragment.newInstance(),
            ForecastFragment.newInstance(),
        )
        val tabTitlesArray = arrayOf("Weather", "Forecast")

        viewPager.adapter = ViewPagerAdapter(this, fragmentList)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitlesArray[position]
        }.attach()
    }

}