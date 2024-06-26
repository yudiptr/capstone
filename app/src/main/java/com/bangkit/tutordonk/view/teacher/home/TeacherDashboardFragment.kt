package com.bangkit.tutordonk.view.teacher.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bangkit.tutordonk.R
import com.bangkit.tutordonk.databinding.FragmentTeacherDashboardBinding
import com.bangkit.tutordonk.model.ListForumItem
import com.bangkit.tutordonk.model.TeacherProfileResponse
import com.bangkit.tutordonk.network.ApiServiceProvider
import com.bangkit.tutordonk.utils.SharedPreferencesHelper
import com.bangkit.tutordonk.utils.isAllFieldsNotEmpty
import com.bangkit.tutordonk.utils.navigateWithAnimation
import com.bangkit.tutordonk.view.forum.ForumActivity
import com.bangkit.tutordonk.view.teacher.TeacherActivity
import com.google.gson.Gson
import org.koin.android.ext.android.inject

class TeacherDashboardFragment : Fragment() {
    private var _binding: FragmentTeacherDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val apiServiceProvider: ApiServiceProvider by inject()
    private val sharedPreferences: SharedPreferencesHelper by inject()

    private fun shareVM() = (activity as TeacherActivity).data

    override fun onResume() {
        super.onResume()
        checkIsAllFieldNotEmpty()

        val callback = apiServiceProvider.createCallback<List<ListForumItem>>(
            onSuccess = { response ->
                with(binding) {
                    rvForumHottest.setMaxPage(5)
                    rvForumHottest.setInitialItems(response)
                    rvForumHottest.setOnItemClickListener { forumItem ->
                        startActivity(Intent(requireContext(), ForumActivity::class.java).also {
                            it.putExtra(ForumActivity.INTENT_FORUM_ITEM, Gson().toJson(forumItem))
                        })
                    }
                }
            }
        )

        apiServiceProvider.apiService.listForum().enqueue(callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setLayoutListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLayoutListener() = with(binding) {
        cvForum.setOnClickListener { startActivity(Intent(requireContext(), ForumActivity::class.java)) }
        cvTutorManagement.setOnClickListener { navController.navigateWithAnimation(R.id.teacherTutorManagement) }
        cvStudyHistory.setOnClickListener { navController.navigateWithAnimation(R.id.teacherHistoryFragment) }
    }

    private fun checkIsAllFieldNotEmpty() {
        val callback = apiServiceProvider.createCallback<TeacherProfileResponse>(
            onSuccess = { response ->
                shareVM().name.value = response.nama
                sharedPreferences.saveUsername(response.nama)

                if (response.isAllFieldsNotEmpty().not())
                    navController.navigateWithAnimation(R.id.teacherEditProfileFragment)
            },
            onFailed = { navController.navigateWithAnimation(R.id.teacherEditProfileFragment) }
        )
        apiServiceProvider.apiService.teacherGetProfile().enqueue(callback)
    }
}