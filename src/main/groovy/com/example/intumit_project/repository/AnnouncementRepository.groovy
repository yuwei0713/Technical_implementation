package com.example.intumit_project.repository

import com.example.intumit_project.model.Announcement

interface AnnouncementRepository {
    List<Announcement> findAll()
    Announcement findById(Long id)
    Announcement save(Announcement announcement)
    void deleteById(Long id)
}