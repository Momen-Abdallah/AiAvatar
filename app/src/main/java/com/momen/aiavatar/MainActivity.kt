package com.momen.aiavatar

import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.momen.aiavatar.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.toString

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var tts: TextToSpeech
    private lateinit var model: GenerativeModel
    private val speechRecognitionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val speechResult =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                binding.textView.text = speechResult?.get(0) ?: "No speech recognized"
                lifecycleScope.launch {
                    val response = model.generateContent(binding.textView.text.toString())
                    speak(response.text ?: "")
                    binding.textView.text = response.text

//                    binding.response.text = response.text
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        requestPermissions(
            arrayOf(
                android.Manifest.permission.RECORD_AUDIO,

                ), 1
        )
        initializeTextToSpeech()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        model = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel("gemini-2.0-flash")

        val prompt = "Write a story about a magic backpack."

// To generate text output, call generateContent with the text input

        binding.animatedMic.setOnClickListener {
//            lifecycleScope.launch {
//                val response = model.generateContent(prompt)
//                Log.d("momen-test", "onCreate: ${response.text}")
//
//                binding.textView.text = response.text
////            print(response.text)
//            }
            startVoiceRecognitionActivityNoUI("")
//            startSpeechToText()
        }


    }

    private fun startSpeechToText() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Specify Arabic language
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar")
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")

        try {
            speechRecognitionLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun speak(text: String) {

        tts.stop()
        tts.speak(
            text,
            TextToSpeech.SUCCESS,
            null,
            null
        )

    }

    fun initializeTextToSpeech() {

        var ar = 9
        var en = 133

        Locale.getAvailableLocales().forEachIndexed { i, locale ->
            if (locale.language == "ar") {
                ar = i
                return@forEachIndexed
            }
        }

        Locale.getAvailableLocales().forEachIndexed { i, locale ->
            if (locale.language == "en") {
                en = i
                return@forEachIndexed
            }
        }


        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.getDefault()
                tts.setSpeechRate(1.3f)

                val v = Voice(
//                    if (Locale.getDefault().language == "en") "en-us-x-iol-local" else
                    "ar-xa-x-ard-network",
                    if (Locale.getDefault().language == "en") Locale.getAvailableLocales()[en]
                    else Locale.getAvailableLocales()[ar],
                    400, 200, true, setOf("male")
                )

                tts.voice = v

            }
        }


    }

    private var speechRecognizer: SpeechRecognizer? = null
    fun startVoiceRecognitionActivityNoUI(value: String) {
//        Log.i(TAG, "SPEECH Results startVoiceRecognitionActivityNoUI")
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("speechRecognizer--", "onReadyForSpeech")
                    binding.animatedMic.setAnimation(R.raw.mic_animation)
                    binding.animatedMic.playAnimation()
//                    FL.i(TAG, "SPEECH Results onReadyForSpeech")

//                    val toneGenerator = ToneGenerator(AudioManager.STREAM_DTMF, 80)
//                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP)
//                    beep.playTone(this@MainActivity, ToneGenerator.TONE_PROP_BEEP)
                }

                override fun onRmsChanged(rmsdB: Float) {
//                    Log.d("speechRecognizer--", "onRmsChanged")
//                    Toast.makeText(this@MainActivity, "onRmsChanged", Toast.LENGTH_SHORT).show()

//                    if (dialogDialUserCountDown != null) {
//                        dialogDialUserCountDown!!.setRMSMarin(30 - 3 * rmsdB)
//                    }
//                    if (XelionDialerManager.dialogPhone != null) {
//                        XelionDialerManager.dialogPhone!!.setRMSMarin(30 - 3 * rmsdB)
//                    }
                }

                override fun onBeginningOfSpeech() {


                    Toast.makeText(this@MainActivity, "onBeginningOfSpeech", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("speechRecognizer--", "onBeginningOfSpeech")

//                    FL.i(TAG, "SPEECH Results onBeginningOfSpeech")
//                    if (dialogDialUserCountDown != null) {
//                        dialogDialUserCountDown!!.setRMSMarin(30f)
//                    }
                }

                override fun onEndOfSpeech() {
                    Toast.makeText(this@MainActivity, "onEndOfSpeech", Toast.LENGTH_SHORT).show()
                    Log.d("speechRecognizer--", "onEndOfSpeech")
                    binding.animatedMic.setAnimation(R.raw.mic)
                    binding.animatedMic.playAnimation()
//                    FL.i(TAG, "SPEECH Results onEndOfSpeech")
//                    if (dialogDialUserCountDown != null) {
//                        dialogDialUserCountDown!!.setRMSMarin(30f)
//                    }
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    Log.d("speechRecognizer--", "onBufferReceived")

                }

                override fun onError(error: Int) {
                    Log.d("speechRecognizer--", "onError")
                    binding.animatedMic.setAnimation(R.raw.mic)
                    binding.animatedMic.playAnimation()
//                    Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                    Log.d("speechRecognizer--", "OnEvent")
//                    Toast.makeText(this@MainActivity, "OnEvent", Toast.LENGTH_SHORT).show()
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    Log.d("speechRecognizer--", "onPartialResults")
//                    Toast.makeText(this@MainActivity, "onPartialResults", Toast.LENGTH_SHORT).show()

//                    var data =
//                        partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                    var unstableData =
//                        partialResults?.getStringArrayList("android.speech.extra.UNSTABLE_TEXT")
//                    var matches = ArrayList<String>()
//                    if (data != null) {
//                        data.forEachIndexed { index, element ->
//                            matches.add(data.get(index) + unstableData?.get(index))
//                        }
//                        if (matches != null) {
////                               Log.d("speechRecognizer--result", matches.toString())
//                            Log.i(
//                                "TAG",
//                                "SPEECH Results onPartialResults are: " + matches.toString()
//                            )
//                        } else Log.i("TAG", "SPEECH Results onPartialResults are NULL")
//
//
////                        handleVoiceRecogMatches(matches)
//                    }
//                    FL.i(TAG, "SPEECH Results onPartialResults")
                }

                override fun onResults(results: Bundle?) {
                    Log.d("speechRecognizer--", "onResults")
                    var matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null) {
                        Log.d("speechRecognizer--result", matches.toString())
                        lifecycleScope.launch {
                            val response = model.generateContent(matches.first().toString())
                            speak(response.text ?: "")
                            binding.textView.text = response.text

//                    binding.response.text = response.text
                        }
//                        FL.i(TAG, "SPEECH Results are: " + matches.toString())
                    }
//                    else FL.i(TAG, "SPEECH Results are NULL")
//                    handleVoiceRecogMatches(matches)
                }
            })
        } else {
            speechRecognizer?.stopListening()
        }
        startListening()
//        voiceCommand = value
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar")
        speechRecognizer?.startListening(intent)
//        object : CountDownTimer(10000, 10000) {
//            override fun onTick(millisUntilFinished: Long) {}
//
//            override fun onFinish() {
//                speechRecognizer?.stopListening()
//            }
//        }.start()
    }

}