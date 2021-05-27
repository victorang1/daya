package bangkit.daya.app.home

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.databinding.FragmentHomeBinding
import bangkit.daya.pref.AppPreferences
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import org.koin.android.ext.android.inject

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val appPref: AppPreferences by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cvOne.setOnClickListener(this)
        binding.cvTwo.setOnClickListener(this)
        binding.fabIm.setOnClickListener(this)
        checkShouldShowAppDesc()
    }

    override fun onClick(v: View) {
        when (v) {
            binding.fabIm -> findNavController().navigate(R.id.action_homeFragment_to_imageRecognitionFragment)
            binding.cvOne -> findNavController().navigate(R.id.action_homeFragment_to_arFragment)
            binding.cvTwo -> findNavController().navigate(R.id.action_homeFragment_to_datasetListActivity)
        }
    }

    private fun checkShouldShowAppDesc() {
        if (appPref.shouldShowAppFeaturesDescription()) {
            TapTargetSequence(requireActivity()).targets(
                createFeatureOneTapTarget(),
                createFeatureTwoTapTarget(),
                createFeatureThreeTapTarget()
            ).listener(object: TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    appPref.disableShouldShowAppFeaturesDescription()
                }

                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {

                }

                override fun onSequenceCanceled(lastTarget: TapTarget?) {

                }
            }).start()
        }
    }

    private fun createFeatureOneTapTarget(): TapTarget =
        createTapTarget(binding.tvTitle, getString(R.string.text_ar_description), 100)

    private fun createFeatureTwoTapTarget(): TapTarget =
        createTapTarget(binding.tvTitleTwo, getString(R.string.text_qna_description), 100)

    private fun createFeatureThreeTapTarget(): TapTarget =
        createTapTarget(binding.fabIm, getString(R.string.text_im_description))

    private fun createTapTarget(view: View, desc: String, radius: Int = 60): TapTarget {
        return TapTarget.forView(view, getString(R.string.text_information), desc)
            .targetCircleColor(R.color.tap_target_circle_color)
            .titleTextColor(R.color.tap_target_title_text_color)
            .descriptionTextColor(R.color.tap_target_title_text_color)
            .outerCircleColor(R.color.tap_target_outer_circle_color)
            .textTypeface(Typeface.SANS_SERIF)
            .tintTarget(false)
            .cancelable(false)
            .targetRadius(radius)
    }
}