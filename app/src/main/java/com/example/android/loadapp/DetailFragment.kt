package com.example.android.loadapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class DetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_detail, container, false)
        val okButton = rootView.findViewById<Button>(R.id.ok_button)
        val fileName = rootView.findViewById<TextView>(R.id.file_name)
        val status = rootView.findViewById<TextView>(R.id.status)

        when (options) {
            1 -> fileName.text = resources.getString(R.string.option1)
            2 -> fileName.text = resources.getString(R.string.option2)
            3 -> fileName.text = resources.getString(R.string.option3)
        }

        if (downloadStatus) {
            status.text = "Success"
        } else {
            status.text = "Fail"
            status.setTextColor(resources.getColor(R.color.red, null))
        }

        okButton.setOnClickListener { view: View ->
            Navigation.findNavController(view).navigate(R.id.action_detailFragment_to_mainFragment)
        }

        return rootView
    }
}