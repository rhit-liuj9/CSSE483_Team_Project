package edu.rosehulman.fangr.kitchenkit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(),
    SplashFragment.OnSignInButtonPressedListener,
    RecipeBrowserFragment.OnButtonPressedListener,
    ProfileFragment.OnLogoutPressedListener,
    MyIngredientsFragment.OnButtonPressedListener,
    AddIngredientFragment.OnAddButtonPressedListener {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
//        this.setSupportActionBar(this.toolbar)
        this.initializeListeners()
    }

    override fun onStart() {
        super.onStart()
        this.auth.addAuthStateListener(this.authStateListener)
    }

    override fun onStop() {
        super.onStop()
        this.auth.removeAuthStateListener(this.authStateListener)
    }

    private fun initializeListeners() {
        this.authStateListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
            val user = auth.currentUser
            if (user != null) {
                Log.d(Constants.TAG, "UID: ${user.uid}")
                Log.d(Constants.TAG, "Name: ${user.displayName}")
                Log.d(Constants.TAG, "E-Mail: ${user.email}")
                Log.d(Constants.TAG, "Phone: ${user.phoneNumber}")
                Log.d(Constants.TAG, "Photo: ${user.photoUrl}")
                this.switchTo(RecipeBrowserFragment())
            } else
                this.switchTo(SplashFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onSignInButtonPressed() {
        this.launchLoginUI()
    }

    private fun launchLoginUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val loginIntent =
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_logo)
                .build()

        this.startActivityForResult(loginIntent, this.RC_SIGN_IN)
    }

    private fun switchTo(fragment: Fragment) {
        val ft = this.supportFragmentManager.beginTransaction()
        val currentFragment = this.supportFragmentManager.findFragmentById(R.id.fragment_container)
        ft.replace(R.id.fragment_container, fragment)
        if (fragment !is RecipeBrowserFragment && currentFragment !is AddIngredientFragment)
            ft.addToBackStack(null)
        else
            this.supportFragmentManager.popBackStack()
        ft.commit()
    }

    override fun onProfileButtonPressed() {
        this.auth.currentUser?.displayName
            ?.let { ProfileFragment.newInstance(it) }
            ?.let { this.switchTo(it) }
    }

    override fun onMyIngredientsButtonPressed() {
        this.auth.currentUser?.uid
            ?.let { MyIngredientsFragment.newInstance(it) }
            ?.let { this.switchTo(it) }
    }

    override fun onLogoutPressed() {
        this.auth.signOut()
    }

    override fun onBackButtonPressed() {
        this.switchTo(RecipeBrowserFragment())
    }

    override fun onAddFABPressed() {
        this.auth.currentUser?.uid
            ?.let { AddIngredientFragment.newInstance(it) }
            ?.let { this.switchTo(it) }
    }

    override fun onAddButtonPressed() {
        this.auth.currentUser?.uid
            ?.let { MyIngredientsFragment.newInstance(it) }
            ?.let { this.switchTo(it) }
    }
}