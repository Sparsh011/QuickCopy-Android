package com.sparshchadha.clipboard.image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URL
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        if (intent != null && intent.action == Intent.ACTION_SEND) {
            findViewById<TextView>(R.id.tv_copying_text).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.pb).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_home).visibility = View.GONE
            when {
                intent.type?.startsWith("image/") == true -> {
                    val imageUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                    if (imageUri != null) {
                        copyImageToClipboard(imageUri)
                    }
                }
                intent.type == "text/plain" -> {
                    val sharedUrl = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (sharedUrl != null) {
                        val url = extractUrlFromText(sharedUrl)
                        if (url != null) {
                            fetchImageFromUrlAndCopy(url)
                        } else {
                            Toast.makeText(this, "Unable to extract image", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        } else {
            findViewById<TextView>(R.id.tv_copying_text).visibility = View.GONE
            findViewById<ProgressBar>(R.id.pb).visibility = View.GONE
            findViewById<TextView>(R.id.tv_home).visibility = View.VISIBLE
        }


    }

    private fun extractUrlFromText(text: String): String? {
        val urlPattern = Pattern.compile(
            "(https?:\\/\\/\\S+)" // Regex pattern to match URLs starting with http or https
        )
        val matcher = urlPattern.matcher(text)
        return if (matcher.find()) {
            matcher.group(0) // Return the first matched URL
        } else {
            null
        }
    }

    private fun copyImageToClipboard(imageUri: Uri) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newUri(contentResolver, "Image", imageUri)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Image copied to clipboard", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun fetchImageFromUrlAndCopy(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val document = Jsoup.connect(url).get()
                val imageUrl = document.select("meta[property=og:image]").attr("content")

                if (imageUrl.isNotEmpty()) {
                    val bitmap = downloadImage(imageUrl)

                    if (bitmap != null) {
                        withContext(Dispatchers.Main) {
                            copyImageBitmapToClipboard(bitmap)
                        }
                    } else {
                        showError("Failed to download the image.")
                    }
                } else {
                    showError("No image found on the page.")
                }
            } catch (e: Exception) {
                showError("Error fetching image from URL.")
                e.printStackTrace()
            }
        }
    }

    private suspend fun downloadImage(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val image = BitmapFactory.decodeStream(url.openStream())
                image
            } catch (e: IOException) {
                println(e)
                null
            }
        }
    }

    private fun copyImageBitmapToClipboard(bitmap: Bitmap) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val uri = getImageUriFromBitmap(this, bitmap)
        if (uri != null) {
            val clip = ClipData.newUri(contentResolver, "Image", uri)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Image copied to clipboard", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to copy image to clipboard", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri? {
        val path = android.provider.MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Image", null)
        return if (path != null) Uri.parse(path) else null
    }

    private suspend fun showError(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
