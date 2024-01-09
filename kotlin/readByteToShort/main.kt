package com.longnh.simpleaudioplayer

import android.Manifest
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.longnh.simpleaudioplayer.extensions.JavaExt
import com.longnh.simpleaudioplayer.extensions.readShort
import com.longnh.simpleaudioplayer.ui.theme.SimpleAudioPlayerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import kotlin.system.measureTimeMillis

class MainActivity : ComponentActivity() {

    init {
        System.loadLibrary("native-lib")
    }

    companion object {
        private const val TAG = "LongTest"

        @JvmStatic
        external fun helloWorld(): String

        @JvmStatic
        external fun nativeReadBytes(byteArray: ByteArray): ShortArray

        fun readBytes(byteArray: ByteArray): ShortArray {
            val result = ShortArray(byteArray.size / 2)
            repeat(result.size) {
                result[it] = JavaExt.byteToShort(byteArray[it * 2], byteArray[it * 2 + 1])
            }

            return result
        }

    }

    private var mAudioTrack: AudioTrack? = null
    private lateinit var audioData: ShortArray
    private val isLoading = mutableStateOf(false)
    private val isSuccess = mutableStateOf(false)
    private val progress = mutableIntStateOf(0)

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ${helloWorld()}")
        setContent {
            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent(), onResult = ::readFile)
            val audioPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                rememberPermissionState(permission = Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            SimpleAudioPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = {
                            val permissionStatus = audioPermissionState.status
                            if (!permissionStatus.isGranted) {
                                if (permissionStatus.shouldShowRationale) {
                                    // Show rationale
                                } else {
                                    audioPermissionState.launchPermissionRequest()
                                }
                            } else {
                                launcher.launch("audio/*")
                            }
                        }) {
                            Text(text = "Pick audio file")
                        }

                        Button(onClick = ::playSound) {
                            Text(text = "Play sound")
                        }

                        if (isLoading.value) {
                            LinearProgressIndicator(
                                progress = progress.intValue / 100f,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (isSuccess.value) {
                            Spacer(modifier = Modifier.height(16.dp))
                            WaveView(modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp), data = audioData)
                        }
                    }
                }
            }
        }
    }

    private fun readFile(uri: Uri?) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (uri == null) {
                Log.e(TAG, "File not found: uri null")
                return@launch
            }
            val inputStream = contentResolver.openInputStream(uri)
            val wavData = WavData.fromByteStream(inputStream!!)
            val dataSize = wavData.dataSubChunk!!.dataSubChunkSize.toInt() / 2

            audioData = ShortArray(dataSize)
            isLoading.value = true

            var leftChannel: Short = 0

            val time = measureTimeMillis {
                slowRead(inputStream, dataSize)

//                fastRead(inputStream, dataSize)
                inputStream.close()

                isLoading.value = false
                isSuccess.value = true

            }

            Log.d(TAG, "took: $time ms")

            mAudioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(wavData.fmtSubChunk!!.samplesPerSec.toInt())
                        .setChannelMask(if (wavData.fmtSubChunk.numOfChannel.toInt() == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                ).build()
        }
    }

    private fun slowRead(inputStream: InputStream, dataSize: Int) {
        repeat(dataSize) { index ->
            val sample = inputStream.readShort()
            audioData[index] = sample
            if (index / (dataSize / 100) != progress.intValue) {
                progress.intValue = index / (dataSize / 100)
            }
        }
    }

    private fun fastRead(inputStream: InputStream, dataSize: Int) {
        val data = ByteArray(dataSize)
        inputStream.read(data)

        audioData = readBytes(data)
    }

    private fun playSound() {
        lifecycleScope.launch(Dispatchers.Default) {
            mAudioTrack?.play()
            mAudioTrack?.write(audioData, 0, audioData.size)
        }
//        mAudioTrack?.stop()
//        mAudioTrack?.release()
    }
}
