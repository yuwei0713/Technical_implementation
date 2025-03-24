// src/main/groovy/com/example/intumit_project/repository/AttachmentRepositoryImpl.groovy
package com.example.intumit_project.repository

import com.example.intumit_project.model.Attachment
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class AttachmentRepositoryImpl implements AttachmentRepository {
    @Autowired
    SessionFactory sessionFactory

    private Session getCurrentSession() {
        sessionFactory.currentSession
    }

    @Override
    Attachment findById(Long id) {
        getCurrentSession().get(Attachment, id)
    }

    @Override
    Attachment save(Attachment attachment) {
        getCurrentSession().saveOrUpdate(attachment)
        attachment
    }

    @Override
    void deleteById(Long id) {
        def attachment = findById(id)
        if (attachment) {
            getCurrentSession().delete(attachment)
        }
    }
}