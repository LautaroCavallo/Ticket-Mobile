"""
Attachment models for the helpdesk system.
"""

import os
from django.db import models
from django.utils.translation import gettext_lazy as _
from django.utils import timezone
from apps.users.models import User
from apps.tickets.models import Ticket


def attachment_upload_path(instance, filename):
    """Generate upload path for attachments."""
    return f'attachments/ticket_{instance.ticket.id}/{filename}'


class Attachment(models.Model):
    """
    Attachment model for tickets.
    """
    
    # File information
    filename = models.CharField(
        _('nombre de archivo'),
        max_length=255,
        help_text=_('Nombre original del archivo')
    )
    
    file = models.FileField(
        _('archivo'),
        upload_to=attachment_upload_path,
        help_text=_('Archivo adjunto')
    )
    
    file_size = models.PositiveIntegerField(
        _('tamaño del archivo'),
        help_text=_('Tamaño del archivo en bytes')
    )
    
    mime_type = models.CharField(
        _('tipo MIME'),
        max_length=100,
        help_text=_('Tipo MIME del archivo')
    )
    
    # Relationships
    ticket = models.ForeignKey(
        Ticket,
        on_delete=models.CASCADE,
        related_name='attachments',
        verbose_name=_('ticket'),
        help_text=_('Ticket al que pertenece el archivo')
    )
    
    user = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='attachments',
        verbose_name=_('usuario'),
        help_text=_('Usuario que subió el archivo')
    )
    
    # Privacy
    is_private = models.BooleanField(
        _('privado'),
        default=False,
        help_text=_('Si el archivo es privado (solo visible para admins)')
    )
    
    # Metadata
    created_at = models.DateTimeField(
        _('fecha de creación'),
        auto_now_add=True,
        help_text=_('Fecha de subida del archivo')
    )
    
    updated_at = models.DateTimeField(
        _('fecha de actualización'),
        auto_now=True,
        help_text=_('Fecha de última actualización')
    )
    
    class Meta:
        verbose_name = _('Archivo Adjunto')
        verbose_name_plural = _('Archivos Adjuntos')
        ordering = ['-created_at']
        indexes = [
            models.Index(fields=['ticket']),
            models.Index(fields=['user']),
            models.Index(fields=['created_at']),
            models.Index(fields=['is_private']),
        ]
    
    def __str__(self):
        return f"{self.filename} (Ticket #{self.ticket.id})"
    
    def save(self, *args, **kwargs):
        """Override save to set file information."""
        if self.file:
            self.filename = os.path.basename(self.file.name)
            self.file_size = self.file.size
            self.mime_type = getattr(self.file, 'content_type', 'application/octet-stream')
        super().save(*args, **kwargs)
    
    @property
    def file_size_human(self):
        """Return human-readable file size."""
        size = self.file_size
        for unit in ['B', 'KB', 'MB', 'GB']:
            if size < 1024.0:
                return f"{size:.1f} {unit}"
            size /= 1024.0
        return f"{size:.1f} TB"
    
    @property
    def is_image(self):
        """Check if the file is an image."""
        return self.mime_type.startswith('image/')
    
    @property
    def is_document(self):
        """Check if the file is a document."""
        document_types = [
            'application/pdf',
            'application/msword',
            'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
            'text/plain',
            'text/csv',
        ]
        return self.mime_type in document_types
    
    def can_be_viewed_by(self, user):
        """Check if a user can view this attachment."""
        if user.is_admin():
            return True
        if self.is_public:
            return True
        if self.user == user:
            return True
        return False
    
    def can_be_deleted_by(self, user):
        """Check if a user can delete this attachment."""
        if user.is_admin():
            return True
        if self.user == user:
            return True
        return False
    
    @property
    def is_public(self):
        """Check if the attachment is public."""
        return not self.is_private
