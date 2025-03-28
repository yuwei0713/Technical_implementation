// src/main/groovy/com/example/intumit_project/service/AnnouncementService.groovy
package com.example.intumit_project.service

import com.example.intumit_project.model.Announcement
import com.example.intumit_project.model.Attachment
import com.example.intumit_project.repository.AnnouncementRepository
import com.example.intumit_project.repository.AttachmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class AnnouncementService {
    @Autowired
    AnnouncementRepository announcementRepository

    @Autowired
    AttachmentRepository attachmentRepository

    private static final String UPLOAD_DIR = "/var/uploads/"

    @Transactional
    void saveAnnouncement(Announcement formAnnouncement, MultipartFile[] uploadFiles, List<Long> attachmentsToDelete = []) {
        Announcement announcementToSave

        // 判斷是新增還是更新
        if (formAnnouncement.id) {
            // 更新現有公告
            announcementToSave = announcementRepository.findById(formAnnouncement.id)
            if (!announcementToSave) {
                throw new IllegalArgumentException("公告 ID ${formAnnouncement.id} 不存在")
            }
            // 合併表單資料
            announcementToSave.title = formAnnouncement.title
            announcementToSave.publisher = formAnnouncement.publisher
            announcementToSave.publishDate = formAnnouncement.publishDate
            announcementToSave.deadline = formAnnouncement.deadline
            announcementToSave.content = formAnnouncement.content
        } else {
            // 新增公告
            announcementToSave = new Announcement(
                    title: formAnnouncement.title,
                    publisher: formAnnouncement.publisher,
                    publishDate: formAnnouncement.publishDate,
                    deadline: formAnnouncement.deadline,
                    content: formAnnouncement.content
            )
        }

        // 刪除舊附件（僅更新時適用）
        if (formAnnouncement.id && attachmentsToDelete) {
            attachmentsToDelete.each { attachmentId ->
                def attachment = attachmentRepository.findById(attachmentId)
                if (attachment && attachment.announcement.id == announcementToSave.id) {
                    Path filePath = Paths.get(UPLOAD_DIR + attachment.fileName)
                    if (Files.exists(filePath)) {
                        Files.delete(filePath)
                    }
                    attachmentRepository.deleteById(attachmentId)
                    announcementToSave.attachments.removeAll { it.id == attachmentId }
                }
            }
        }

        // 保存公告（新增或更新）
        announcementRepository.save(announcementToSave)

        // 處理新上傳檔案
        if (uploadFiles && uploadFiles.length > 0) {
            Path uploadPath = Paths.get(UPLOAD_DIR)
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath)
            }
            uploadFiles.each { file ->
                if (!file.empty) {
                    String fileName = System.currentTimeMillis() + "_" + file.originalFilename
                    Path filePath = uploadPath.resolve(fileName)
                    Files.copy(file.inputStream, filePath)
                    Attachment attachment = new Attachment(
                            fileName: fileName,
                            filePath: "/uploads/" + fileName,
                            announcement: announcementToSave
                    )
                    attachmentRepository.save(attachment)
                    if (!announcementToSave.attachments) {
                        announcementToSave.attachments = []
                    }
                    announcementToSave.attachments << attachment
                }
            }
        }
    }

    @Transactional
    void deleteAnnouncement(Long id) {
        def announcement = announcementRepository.findById(id)
        if (announcement) {
            // 刪除所有相關附件（包括伺服器上的檔案）
            announcement.attachments.each { attachment ->
                Path filePath = Paths.get(UPLOAD_DIR + attachment.fileName)
                if (Files.exists(filePath)) {
                    Files.delete(filePath)
                }
                attachmentRepository.deleteById(attachment.id)
            }
            // 刪除公告
            announcementRepository.deleteById(id)
        }
    }

    @Transactional(readOnly = true)
    List<Announcement> findAllAnnouncements() {
        announcementRepository.findAll()
    }

    @Transactional(readOnly = true)
    Announcement findAnnouncementById(Long id) {
        announcementRepository.findById(id)
    }
}