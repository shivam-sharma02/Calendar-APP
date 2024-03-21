package com.example.customcalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repository.EventRepository

class EventViewModelProviderFactory(
    val eventRepository: EventRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventViewModel(eventRepository) as T
    }
}