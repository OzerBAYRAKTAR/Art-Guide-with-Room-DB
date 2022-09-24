package com.example.kotlinartbookfragment.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.kotlinartbookfragment.Fragments.MainFragmentDirections
import com.example.kotlinartbookfragment.R


class MainActivity : AppCompatActivity() {

    private lateinit var navigationController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        navigationController = Navigation.findNavController(this, R.id.frag)
        NavigationUI.setupActionBarWithNavController(this,navigationController)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //inflater
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.art_menu, menu)
        return super.onCreateOptionsMenu(menu)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController=this.findNavController(R.id.frag)
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_art_item) {
            val action=MainFragmentDirections.actionMainFragmentToUploadFragment("new",0)
            Navigation.findNavController(this, R.id.frag).navigate(action)

        }
        return super.onOptionsItemSelected(item)
    }
}