package com.example.intumit_project.repository

import com.example.intumit_project.model.Announcement
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class AnnouncementRepositoryImpl implements AnnouncementRepository {
    @Autowired
    SessionFactory sessionFactory

    private Session getCurrentSession() {
        sessionFactory.currentSession
    }

    @Override
    List<Announcement> findAll() {
        getCurrentSession().createQuery("from Announcement").list()
    }

    @Override
    Announcement findById(Long id) {
        getCurrentSession().get(Announcement, id)
    }

    @Override
    Announcement save(Announcement announcement) {
        getCurrentSession().saveOrUpdate(announcement)
        announcement
    }

    @Override
    void deleteById(Long id) {
        def announcement = findById(id)
        if (announcement) {
            getCurrentSession().delete(announcement)
        }
    }
}