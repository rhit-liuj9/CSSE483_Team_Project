package edu.rosehulman.fangr.kitchenkit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import edu.rosehulman.rosefire.Rosefire

class MainActivity : AppCompatActivity(),
    SplashFragment.OnSignInButtonPressedListener,
    RecipeBrowserFragment.OnButtonPressedListener,
    ProfileFragment.OnButtonsPressedListener,
    MyIngredientsFragment.OnButtonPressedListener,
    AddIngredientFragment.OnAddButtonPressedListener {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private val RC_SIGN_IN = 1
    private val RC_ROSE_SIGN_IN = 2

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == this.RC_ROSE_SIGN_IN) {
            val result = Rosefire.getSignInResultFromIntent(data)
            if (result.isSuccessful) {
                this.auth.signInWithCustomToken(result.token)
                Log.d(Constants.TAG, "Username: ${result.username}")
                Log.d(Constants.TAG, "Name: ${result.name}")
                Log.d(Constants.TAG, "E-Mail: ${result.email}")
                Log.d(Constants.TAG, "Group: ${result.group}")
            } else
                Log.d(Constants.TAG, "Rosefire Failed")
        }
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
        if (fragment !is RecipeBrowserFragment && currentFragment !is AddIngredientFragment && fragment !is SplashFragment)
            ft.addToBackStack(null)
        else
            this.supportFragmentManager.popBackStackImmediate()
        ft.commit()
    }

    override fun onSignInButtonPressed() {
        this.launchLoginUI()
    }

    override fun onRoseSignInButtonPressed() {
        val roseFireSignInIntent =
            Rosefire.getSignInIntent(this, this.getString(R.string.token_rosefire_log_in))
        this.startActivityForResult(roseFireSignInIntent, this.RC_ROSE_SIGN_IN)
    }

    override fun onProfileButtonPressed() {
        this.auth.currentUser?.uid
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

    override fun onProfileBackPressed() {
        this.switchTo(RecipeBrowserFragment())
    }

    override fun onEditButtonPressed() {
        this.auth.currentUser?.uid
            ?.let { EditProfileFragment.newInstance(it) }
            ?.let { this.switchTo(it) }
    }

    override fun onMyIngredientsFragmentBackButtonPressed() {
        this.switchTo(RecipeBrowserFragment())
    }

    override fun onAddIngredientFragmentBackButtonPressed() {
        this.switchTo(MyIngredientsFragment())
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