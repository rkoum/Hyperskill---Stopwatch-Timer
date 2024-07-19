package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {
    private lateinit var timerTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var handler = Handler(Looper.getMainLooper())
    private var counter = -1
    private var running = false
    private var upperLimit = 0
    private var colorIndex = 0
    private val colors = arrayOf(
        Color.RED,
        Color.BLUE,
        Color.GREEN
    )
    private val runnable = object : Runnable {
        override fun run() {
            counter++
            updateTextView(timeFormat(counter))

            progressBar.indeterminateTintList = ColorStateList.valueOf(colors[colorIndex])
            colorIndex = (colorIndex + 1) % colors.size

            handler.postDelayed(this, 1000)
            if (upperLimit != 0 && upperLimit == counter) {
                timerTextView.setTextColor(Color.RED)
                val notificationBuilder =
                    NotificationCompat.Builder(this@MainActivity, NotificationChannel)
                        .setSmallIcon(R.drawable.baseline_warning_24)
                        .setContentTitle("Notification")
                        .setContentText("Time exceeded")
                        .setOnlyAlertOnce(true)

                val notification: Notification = notificationBuilder.build()
                notification.flags = Notification.FLAG_INSISTENT or notification.flags

                val notificationManager =
                    this@MainActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(393939, notification)


            }

        }
    }

    companion object {
        const val NotificationChannel = "org.hyperskill"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonStart: Button = findViewById(R.id.startButton)
        val buttonReset: Button = findViewById(R.id.resetButton)
        val buttonSettings: Button = findViewById(R.id.settingsButton)
        progressBar = findViewById(R.id.progressBar)
        timerTextView = findViewById(R.id.textView)
        timerTextView.text = getString(R.string.initialTimeString)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder name"
            val descriptionText = "Time limit exceeded notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NotificationChannel, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }


        buttonStart.setOnClickListener {
            buttonSettings.isEnabled = false
            Toast.makeText(this, "start clicked", Toast.LENGTH_SHORT).show()
            if (!running) {
                progressBar.visibility = View.VISIBLE
                running = true
                handler.post(runnable)

            }
        }
        buttonReset.setOnClickListener {
            Toast.makeText(this, "reset clicked", Toast.LENGTH_SHORT).show()
            running = false
            handler.removeCallbacks(runnable)
            counter = 0
            updateTextView(timeFormat(counter))
            counter = -1
            progressBar.visibility = View.GONE
            buttonSettings.isEnabled = true
            timerTextView.setTextColor(Color.BLACK)


        }
        buttonSettings.setOnClickListener {
            val contentView = LayoutInflater.from(this).inflate(R.layout.dialog, null, false)
            AlertDialog.Builder(this)
                .setTitle("Set upper limit in seconds")
                .setView(contentView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    upperLimit =
                        contentView.findViewById<EditText>(R.id.upperLimitEditText).text.toString()
                            .toInt()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()

        }

    }

    private fun updateTextView(text: String) {
        timerTextView.text = text
    }

    private fun timeFormat(number: Int): String {
        val minutes = number / 60
        val seconds = number % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


}

