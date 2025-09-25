"""
User models for the helpdesk system.
"""

from django.contrib.auth.models import AbstractUser
from django.db import models
from django.utils.translation import gettext_lazy as _


class User(AbstractUser):
    """
    Custom User model extending Django's AbstractUser.
    """
    
    class Role(models.TextChoices):
        USER = 'user', _('Usuario')
        SUPPORT = 'support', _('Soporte')
        SYSADMIN = 'sysAdmin', _('Administrador del Sistema')
    
    # Override email to make it unique and required
    email = models.EmailField(
        _('email address'),
        unique=True,
        help_text=_('Dirección de correo electrónico única')
    )
    
    # Custom fields
    role = models.CharField(
        _('rol'),
        max_length=20,
        choices=Role.choices,
        default=Role.USER,
        help_text=_('Rol del usuario en el sistema')
    )
    
    profile_picture = models.ImageField(
        _('foto de perfil'),
        upload_to='profiles/',
        blank=True,
        null=True,
        help_text=_('Foto de perfil del usuario')
    )
    
    last_login = models.DateTimeField(
        _('último acceso'),
        blank=True,
        null=True,
        help_text=_('Último inicio de sesión')
    )
    
    # Metadata
    created_at = models.DateTimeField(
        _('fecha de creación'),
        auto_now_add=True,
        help_text=_('Fecha de creación de la cuenta')
    )
    updated_at = models.DateTimeField(
        _('fecha de actualización'),
        auto_now=True,
        help_text=_('Fecha de última actualización')
    )
    
    class Meta:
        verbose_name = _('Usuario')
        verbose_name_plural = _('Usuarios')
        ordering = ['-created_at']
    
    def __str__(self):
        return f"{self.first_name} {self.last_name} ({self.email})"
    
    @property
    def full_name(self):
        """Return the user's full name."""
        return f"{self.first_name} {self.last_name}".strip()
    
    def is_support(self):
        """Check if user has support role."""
        return self.role == self.Role.SUPPORT
    
    def is_admin(self):
        """Check if user is system admin."""
        return self.role == self.Role.SYSADMIN
    
    def can_manage_tickets(self):
        """Check if user can manage tickets."""
        return self.role in [self.Role.SUPPORT, self.Role.SYSADMIN]
    
    def can_manage_users(self):
        """Check if user can manage other users."""
        return self.role == self.Role.SYSADMIN
