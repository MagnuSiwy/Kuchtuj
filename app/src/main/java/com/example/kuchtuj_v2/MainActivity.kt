package com.example.kuchtuj_v2

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.example.kuchtuj_v2.RamsayQuotes
import kotlin.random.Random

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val logoutTextView: TextView = findViewById(R.id.drawer_logout)
        logoutTextView.setOnClickListener {
            logoutFromFirebase()
        }

        setRandomRamsayQuote()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, RecipesFragment())
            .commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var fragment: Fragment? = null

        when (id) {
            R.id.drawer_recipes -> fragment = RecipesFragment()
            R.id.drawer_add_recipe -> fragment = RecipeDetailsFragment()
            //R.id.drawer_about -> fragment = AboutFragment()
            else -> fragment = RecipesFragment()
        }

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.content_frame, fragment)
        ft.addToBackStack(null)
        ft.commit()

        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun logoutFromFirebase() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setRandomRamsayQuote() {
        val headerView = navigationView.getHeaderView(0)
        val drawerQuote: TextView = headerView.findViewById(R.id.drawer_ramsay_quote)
        val randomQuote = RamsayQuotes.quotes[Random.nextInt(RamsayQuotes.quotes.size)].quote
        drawerQuote.text = randomQuote
    }
}
