package com.ecom.livenessdetection

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ecom.livenessdetection.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var viewFinder: PreviewView
    private lateinit var greenBoxView: View
    private lateinit var redBoxView: View
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var squareShape: View
    private lateinit var faceCascadePath: String
    private lateinit var jsonModelPath: String
    private lateinit var h5ModelPath: String
    private val CAMERA_PERMISSION_REQUEST_CODE = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        viewFinder = activityMainBinding.viewFinder
        greenBoxView = activityMainBinding.greenRingView
        redBoxView = activityMainBinding.redRingView
        squareShape = activityMainBinding.squareContainer
        cameraExecutor = Executors.newSingleThreadExecutor()

        loadFilesFromAssets()
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val faceAnalyzer = FaceDetector(squareShape, greenBoxView, redBoxView)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetRotation(viewFinder.display.rotation)
                .build()
                .also { it.setSurfaceProvider(viewFinder.surfaceProvider) }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also { it.setAnalyzer(cameraExecutor, faceAnalyzer) }

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
                val cameraControl = camera.cameraControl
                val zoomRatio = 1.0f
                try {
                    val zoomChangedFuture = cameraControl.setZoomRatio(zoomRatio)
                    zoomChangedFuture.addListener({}, ContextCompat.getMainExecutor(this))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Use case binding failed!", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(this, "Please give the permission first!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFilesFromAssets() {
        faceCascadePath = copyAsset("haarcascade_frontalface_default.xml")
        jsonModelPath = copyAsset("RealSpoof_MobileNet_V2_JS_Model.json")
        h5ModelPath = copyAsset("RealSpoof_MobileNet_V2_Model.h5")
    }

    private fun copyAsset(assetFileName: String): String {
        val assetManager: AssetManager = assets
        val outputDir: File = getDir("models", Context.MODE_PRIVATE)
        val outputFile = File(outputDir, assetFileName)

        try {
            val inputStream: InputStream = assetManager.open(assetFileName)
            val outputStream = FileOutputStream(outputFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            Toast.makeText(this, "Error copying asset: $assetFileName", Toast.LENGTH_SHORT).show()
        }
        return outputFile.absolutePath
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
