package com.lunarian_forge.vulkan_tracking

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (allPermissionGranted() ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.camera_permissions_not_given),
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        val rightWristTextView = findViewById<TextView>(R.id.right_wrist)
        val rightThumbTextView = findViewById<TextView>(R.id.right_thumb)
        val rightElbowTextView = findViewById<TextView>(R.id.right_elbow)

        val previewView = findViewById<PreviewView>(R.id.viewFinder)

        val notStartedView = findViewById<TextView>(R.id.not_started)
        val posesDataView = findViewById<LinearLayout>(R.id.pose_data)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor,
                        PoseAnalyzer { pose ->
                            if (notStartedView.visibility != View.GONE) {
                                notStartedView.visibility = View.GONE
                            }

                            val allPoseLandmarks = pose.allPoseLandmarks

                            if (pose.allPoseLandmarks.size == 0) {
                                return@PoseAnalyzer
                            }

                            posesDataView.visibility = View.VISIBLE

                            val rightWrist = allPoseLandmarks[PoseLandmark.RIGHT_WRIST]
                            val rightElbow = allPoseLandmarks[PoseLandmark.RIGHT_ELBOW]
                            val rightThumb = allPoseLandmarks[PoseLandmark.RIGHT_THUMB]

                            rightWristTextView.text = getString(
                                R.string.right_wrist_position,
                                rightWrist.position.x,
                                rightWrist.position.y
                            )

                            rightElbowTextView.text = getString(
                                R.string.right_elbow_position,
                                rightElbow.position.x,
                                rightElbow.position.y
                            )

                            rightThumbTextView.text = getString(
                                R.string.right_thumb_position,
                                rightThumb.position.x,
                                rightThumb.position.y
                            )
                        }
                    )
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                Log.e(TAG, "use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "VulkanTracking"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}
