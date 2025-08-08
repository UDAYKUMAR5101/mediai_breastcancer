package com.simats.mediai_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simats.mediai_app.responses.ProfileData

class ProfileViewModel : ViewModel() {
    
    private val _profileData = MutableLiveData<ProfileData?>()
    val profileData: LiveData<ProfileData?> = _profileData
    
    private val _isProfileUpdated = MutableLiveData<Boolean>()
    val isProfileUpdated: LiveData<Boolean> = _isProfileUpdated
    
    fun updateProfile(profileData: ProfileData?) {
        _profileData.value = profileData
        _isProfileUpdated.value = true
    }
    
    fun clearProfileUpdate() {
        _isProfileUpdated.value = false
    }
    
    fun getCurrentProfile(): ProfileData? {
        return _profileData.value
    }
}
