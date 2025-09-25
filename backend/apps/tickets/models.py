"""
Ticket models for the helpdesk system.
"""

from django.db import models
from django.utils.translation import gettext_lazy as _
from django.utils import timezone
from apps.users.models import User
from apps.categories.models import Categoria


class Ticket(models.Model):
    """
    Ticket model for the helpdesk system.
    """
    
    class Status(models.TextChoices):
        OPEN = 'open', _('Abierto')
        IN_PROGRESS = 'in_progress', _('En Progreso')
        RESOLVED = 'resolved', _('Resuelto')
        CLOSED = 'closed', _('Cerrado')
    
    class Priority(models.TextChoices):
        LOW = 'low', _('Baja')
        MEDIUM = 'medium', _('Media')
        HIGH = 'high', _('Alta')
        URGENT = 'urgent', _('Urgente')
    
    # Basic fields
    title = models.CharField(
        _('título'),
        max_length=200,
        help_text=_('Título del ticket')
    )
    
    description = models.TextField(
        _('descripción'),
        help_text=_('Descripción detallada del problema')
    )
    
    # Status and priority
    status = models.CharField(
        _('estado'),
        max_length=20,
        choices=Status.choices,
        default=Status.OPEN,
        help_text=_('Estado actual del ticket')
    )
    
    priority = models.CharField(
        _('prioridad'),
        max_length=20,
        choices=Priority.choices,
        default=Priority.MEDIUM,
        help_text=_('Prioridad del ticket')
    )
    
    # Relationships
    creator = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='created_tickets',
        verbose_name=_('creador'),
        help_text=_('Usuario que creó el ticket')
    )
    
    assignee = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='assigned_tickets',
        verbose_name=_('asignado'),
        help_text=_('Usuario asignado para resolver el ticket')
    )
    
    categoria = models.ForeignKey(
        Categoria,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='tickets',
        verbose_name=_('categoría'),
        help_text=_('Categoría del ticket')
    )
    
    # Timestamps
    created_at = models.DateTimeField(
        _('fecha de creación'),
        auto_now_add=True,
        help_text=_('Fecha de creación del ticket')
    )
    
    updated_at = models.DateTimeField(
        _('fecha de actualización'),
        auto_now=True,
        help_text=_('Fecha de última actualización')
    )
    
    resolved_at = models.DateTimeField(
        _('fecha de resolución'),
        null=True,
        blank=True,
        help_text=_('Fecha de resolución del ticket')
    )
    
    class Meta:
        verbose_name = _('Ticket')
        verbose_name_plural = _('Tickets')
        ordering = ['-created_at']
        indexes = [
            models.Index(fields=['status']),
            models.Index(fields=['priority']),
            models.Index(fields=['creator']),
            models.Index(fields=['assignee']),
            models.Index(fields=['categoria']),
            models.Index(fields=['created_at']),
        ]
    
    def __str__(self):
        return f"#{self.id} - {self.title}"
    
    def save(self, *args, **kwargs):
        """Override save to set resolved_at when status changes to resolved."""
        if self.status == self.Status.RESOLVED and not self.resolved_at:
            self.resolved_at = timezone.now()
        elif self.status != self.Status.RESOLVED and self.resolved_at:
            self.resolved_at = None
        super().save(*args, **kwargs)
    
    @property
    def resolution_time(self):
        """Return the time taken to resolve the ticket."""
        if self.resolved_at and self.created_at:
            return self.resolved_at - self.created_at
        return None
    
    @property
    def is_open(self):
        """Check if the ticket is open."""
        return self.status == self.Status.OPEN
    
    @property
    def is_in_progress(self):
        """Check if the ticket is in progress."""
        return self.status == self.Status.IN_PROGRESS
    
    @property
    def is_resolved(self):
        """Check if the ticket is resolved."""
        return self.status == self.Status.RESOLVED
    
    @property
    def is_closed(self):
        """Check if the ticket is closed."""
        return self.status == self.Status.CLOSED
    
    @property
    def is_urgent(self):
        """Check if the ticket has urgent priority."""
        return self.priority == self.Priority.URGENT
    
    def can_be_edited_by(self, user):
        """Check if a user can edit this ticket."""
        if user.is_admin():
            return True
        if user.is_support() and self.assignee == user:
            return True
        if self.creator == user and self.status == self.Status.OPEN:
            return True
        return False
    
    def can_be_assigned_to(self, user):
        """Check if a user can be assigned to this ticket."""
        return user.can_manage_tickets()
