"""
Category models for the helpdesk system.
"""

from django.db import models
from django.utils.translation import gettext_lazy as _


class Categoria(models.Model):
    """
    Category model for ticket classification.
    """
    
    nombre = models.CharField(
        _('nombre'),
        max_length=100,
        unique=True,
        help_text=_('Nombre de la categoría')
    )
    
    descripcion = models.TextField(
        _('descripción'),
        blank=True,
        null=True,
        help_text=_('Descripción detallada de la categoría')
    )
    
    # Metadata
    created_at = models.DateTimeField(
        _('fecha de creación'),
        auto_now_add=True,
        help_text=_('Fecha de creación de la categoría')
    )
    updated_at = models.DateTimeField(
        _('fecha de actualización'),
        auto_now=True,
        help_text=_('Fecha de última actualización')
    )
    
    class Meta:
        verbose_name = _('Categoría')
        verbose_name_plural = _('Categorías')
        ordering = ['nombre']
    
    def __str__(self):
        return self.nombre
    
    @property
    def ticket_count(self):
        """Return the number of tickets in this category."""
        return self.tickets.count()
