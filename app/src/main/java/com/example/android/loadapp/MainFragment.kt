package com.example.android.loadapp

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast

var options = 0

class MainFragment : Fragment() {
    var downloadId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        val radioGroup = rootView.findViewById<RadioGroup>(R.id.radioGroup)
        val loadingButton = rootView.findViewById<LoadingButton>(R.id.loadingButton)

        var option1 = false
        var option2 = false
        var option3 = false

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButton1 -> option1 = true
                R.id.radioButton2 -> option2 = true
                R.id.radioButton3 -> option3 = true
            }
        }

        loadingButton.setOnClickListener {
            if (option1) {
                downloadAndAnimation("https://github.com/bumptech/glide", 1, loadingButton)
            } else if (option2) {
                downloadAndAnimation(
                    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter",
                    2, loadingButton
                )
            } else if (option3) {
                downloadAndAnimation("https://github.com/square/retrofit", 3, loadingButton)
            } else {
                loadingButton.state = State.COMPLETED
                Toast.makeText(
                    requireContext(),
                    "Please select the file to download",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return rootView
    }

    private fun downloadAndAnimation(downloadUrl: String, option: Int, loadingButton: LoadingButton) {
        loadingButton.state = State.LOADING
        startDownload(downloadUrl)
        loadingButton.startAnimation(loadingButton)
        options = option
    }

    @SuppressLint("ServiceCast")
    fun startDownload(downloadUrl: String) {
        val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(downloadUrl))

        downloadId = downloadManager.enqueue(request)
    }
}
