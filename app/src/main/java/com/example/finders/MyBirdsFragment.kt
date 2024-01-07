package com.example.finders

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var zoneSpinnerEditText: EditText
private lateinit var mileageSpinner: Spinner


class MyBirdsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_my_birds, container, false)

        zoneSpinnerEditText = rootView.findViewById(R.id.ZoneSpinner)
        mileageSpinner = rootView.findViewById(R.id.MileageSpinner)

        // Set up TextWatcher for the EditText
        zoneSpinnerEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the distance in the Utility class based on the text changes
                charSequence?.toString()?.let {
                    Utility.distance = if (it.isNotEmpty()) it.toInt() else 0
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // Not needed for this example
            }
        })

        // Set up OnItemSelectedListener for the Spinner
        mileageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Utility.unitOfMeasurement = adapterView?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }


        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.unit,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mileageSpinner.adapter = adapter
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyBirdsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyBirdsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}