"""
Comment models for the helpdesk system.
"""

from django.db import models
from django.utils.translation import gettext_lazy as _
from apps.users.models import User
from apps.tickets.models import Ticket


class Comment(models.Model):
    """
    Comment model for tickets.
    """
    
    text = models.TextField(
        _('texto'),
        help_text=_('Contenido del comentario')
    )
    
    # Relationships
    ticket = models.ForeignKey(
        Ticket,
        on_delete=models.CASCADE,
        related_name='comments',
        verbose_name=_('ticket'),
        help_text=_('Ticket al que pertenece el comentario')
    )
    
    user = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='comments',
        verbose_name=_('usuario'),
        help_text=_('Usuario que escribió el comentario')
    )
    
    # Privacy
    is_private = models.BooleanField(
        _('privado'),
        default=False,
        help_text=_('Si el comentario es privado (solo visible para admins)')
    )
    
    # Metadata
    created_at = models.DateTimeField(
        _('fecha de creación'),
        auto_now_add=True,
        help_text=_('Fecha de creación del comentario')
    )
    
    updated_at = models.DateTimeField(
        _('fecha de actualización'),
        auto_now=True,
        help_text=_('Fecha de última actualización')
    )
    
    class Meta:
        verbose_name = _('Comentario')
        verbose_name_plural = _('Comentarios')
        ordering = ['created_at']
        indexes = [
            models.Index(fields=['ticket']),
            models.Index(fields=['user']),
            models.Index(fields=['created_at']),
            models.Index(fields=['is_private']),
        ]
    
    def __str__(self):
        return f"Comentario #{self.id} en Ticket #{self.ticket.id}"
    
    @property
    def is_public(self):
        """Check if the comment is public."""
        return not self.is_private
    
    def can_be_viewed_by(self, user):
        """Check if a user can view this comment."""
        if user.is_admin():
            return True
        if self.is_public:
            return True
        if self.user == user:
            return True
        return False
    
    def can_be_edited_by(self, user):
        """Check if a user can edit this comment."""
        if user.is_admin():
            return True
        if self.user == user:
            return True
        return False
    
    def can_be_deleted_by(self, user):
        """Check if a user can delete this comment."""
        return self.can_be_edited_by(user)
