package com.example.intumit_project.repository

import com.example.intumit_project.model.Attachment

interface AttachmentRepository {
    Attachment findById(Long id)
    Attachment save(Attachment attachment)
    void deleteById(Long id)
}