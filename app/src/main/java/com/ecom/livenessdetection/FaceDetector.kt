package com.ecom.livenessdetection

import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetector(private val squareShape: View, private val greenBoxView: View, private val redBoxView: View) : ImageAnalysis.Analyzer {

    // Creating the builder instance of Face detection present in ML-kit:-
    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
    )

    // This is for images analysis:-
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = com.google.mlkit.vision.common.InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    processFaces(faces)
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    imageProxy.close()
                    e.printStackTrace()
                }
        }
    }

    // To show green box:-
    private fun showGreenBox() {
        greenBoxView.visibility = View.VISIBLE
        redBoxView.visibility = View.INVISIBLE
    }

    // To show red box:-
    private fun showRedBox() {
        greenBoxView.visibility = View.INVISIBLE
        redBoxView.visibility = View.VISIBLE
    }

    // Analyzing the face with different parameters:-
    private fun isLivenessDetected(faces: List<Face>): Boolean {
        val face = faces[0]

        val faceContour = face.getContour(FaceContour.FACE)
        val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)
        val rightEyeContour = face.getContour(FaceContour.RIGHT_EYE)
        val noseBridgeContour = face.getContour(FaceContour.NOSE_BRIDGE)
        val noseBottomContour = face.getContour(FaceContour.NOSE_BOTTOM)
        val leftEyebrowTopContour = face.getContour(FaceContour.LEFT_EYEBROW_TOP)
        val leftEyebrowBottomContour = face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM)
        val rightEyebrowTopContour = face.getContour(FaceContour.RIGHT_EYEBROW_TOP)
        val rightEyebrowBottomContour = face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM)
        val upperLipTopContour = face.getContour(FaceContour.UPPER_LIP_TOP)
        val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)
        val lowerLipTopContour = face.getContour(FaceContour.LOWER_LIP_TOP)
        val lowerLipBottomContour = face.getContour(FaceContour.LOWER_LIP_BOTTOM)
        val leftCheekCenterContour = face.getContour(FaceContour.LEFT_CHEEK)
        val rightCheekCenterContour = face.getContour(FaceContour.RIGHT_CHEEK)

        val rotX = face.headEulerAngleX
        val rotY = face.headEulerAngleY
        val rotZ = face.headEulerAngleZ

        // Customize the threshold value accordingly:-
        val rotationThresholdX = 10f
        val rotationThresholdY = 12f
        val rotationThresholdZ = 15f
        val isHeadRotatedX = rotX <= rotationThresholdX && rotX >= -rotationThresholdX
        val isHeadRotatedY = rotY <= rotationThresholdY && rotY >= -rotationThresholdY
        val isHeadRotatedZ = rotZ <= rotationThresholdZ && rotZ >= -rotationThresholdZ

        val isLeftEyeOpen = (face.leftEyeOpenProbability ?: 0f) > 0.5f
        val isRightEyeOpen = (face.rightEyeOpenProbability ?: 0f) > 0.5f
        val isSmile = (face.smilingProbability ?: 0f) > 0.003f

        val areAllLandmarksVisible = faceContour != null
                && leftEyeContour != null
                && rightEyeContour != null
                && noseBridgeContour != null
                && noseBottomContour != null
                && leftEyebrowTopContour != null
                && leftEyebrowBottomContour != null
                && rightEyebrowTopContour != null
                && rightEyebrowBottomContour != null
                && upperLipTopContour != null
                && upperLipBottomContour != null
                && lowerLipTopContour != null
                && lowerLipBottomContour != null
                && leftCheekCenterContour != null
                && rightCheekCenterContour != null

        return (areAllLandmarksVisible && isHeadRotatedX && isHeadRotatedY && isHeadRotatedZ && isLeftEyeOpen && isRightEyeOpen && isSmile)
    }

    // For real time recognition of faces:-
    private fun processFaces(faces: List<Face>) {
        val numberOfFaces = faces.size

        squareShape.post {
            when {
                numberOfFaces == 1 -> {
                    if (isLivenessDetected(faces)) {
                        // Use OpenCV preds value to determine the real or spoof
                        // If preds = model.predict(resized_face)[0] < 0.5
                        showGreenBox()
                    } else {
                        showRedBox()
                    }
                }
                numberOfFaces > 1 -> {
                    showRedBox()
                }
                else -> {
                    showRedBox()
                }
            }
        }
    }
}