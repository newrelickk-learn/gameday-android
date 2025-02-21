package technology.nrkk.apps.socks

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.newrelic.agent.android.FeatureFlag
import technology.nrkk.apps.socks.models.User
import technology.nrkk.apps.socks.utils.APIUtils
import com.newrelic.agent.android.NewRelic

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewRelic.withApplicationToken(
            "NEW_RELIC_MOBILE_KEY"
        )
            .withCrashReportingEnabled(true)
            .withLoggingEnabled(true)
            .start(this.applicationContext)

        setContentView(R.layout.activity_login)
        usernameEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login)
        loginButton.setOnClickListener {
            login()
        }
    }

    private fun succeeded (user: User) {
        NewRelic.logInfo("${user.username} is logging in")
        runOnUiThread {
            Toast.makeText(this@LoginActivity, "Hello, %s".format(user.username), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun failed () {
        NewRelic.logError("Failed to log in")
        runOnUiThread {
            Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
        }
    }
    private fun login() {
        val email = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        APIUtils.login(applicationContext, email, password, this::succeeded, this::failed)
    }
}