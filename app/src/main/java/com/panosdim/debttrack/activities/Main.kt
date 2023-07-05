package com.panosdim.debttrack.activities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import com.panosdim.debttrack.ui.TabScreen
import com.panosdim.debttrack.ui.theme.DebtTrackTheme
import com.panosdim.debttrack.utils.checkForNewVersion
import com.panosdim.debttrack.utils.refId

class Main : ComponentActivity() {
    private lateinit var manager: DownloadManager
    private lateinit var onComplete: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle new version installation after the download of APK file.
        manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val referenceId = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (referenceId != -1L && referenceId == refId) {
                    val apkUri = manager.getUriForDownloadedFile(refId)
                    val installIntent = Intent(Intent.ACTION_VIEW)
                    installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                    installIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    startActivity(installIntent)
                }

            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        FirebaseApp.initializeApp(this)

        // Check for new version
        checkForNewVersion(this)

        setContent {
            DebtTrackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TabScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TabScreenPreview() {
    DebtTrackTheme {
        TabScreen()
    }
}