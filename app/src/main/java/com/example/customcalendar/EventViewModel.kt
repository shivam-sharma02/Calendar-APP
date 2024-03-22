package com.example.customcalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Event
import com.example.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(
    val eventRepository: EventRepository
): ViewModel() {
    fun saveEvent(event: Event) = viewModelScope.launch {
        eventRepository.upsert(event)
    }

//    fun getSavedEvents(date: String?) = eventRepository.getSavedEvents(date)
    fun getEvents() = eventRepository.getAllEvents()

    fun deleteEvent(event: Event) = viewModelScope.launch {
        eventRepository.deleteEvent(event)
    }
}