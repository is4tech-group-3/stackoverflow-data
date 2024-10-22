package com.stackoverflow.service.publication;

import com.stackoverflow.bo.Publication;
import com.stackoverflow.bo.User;
import com.stackoverflow.dto.publication.PublicationRequest;
import com.stackoverflow.dto.publication.PublicationResponse;
import com.stackoverflow.dto.user.UserResponse;
import com.stackoverflow.repository.PublicationRepository;
import com.stackoverflow.repository.TagRepository;
import com.stackoverflow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class PublicationServiceImplTest {

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PublicationServiceImpl publicationService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private Validator validator;

    private User mockUser;
    private Publication mockPublication;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Simulando un usuario
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John");
        mockUser.setSurname("Doe");
        mockUser.setUsername("johndoe");

        // Simulando una publicación
        mockPublication = new Publication();
        mockPublication.setIdPublication(1L);
        mockPublication.setTitle("Sample Publication");
        mockPublication.setDescription("Sample Description");
        mockPublication.setDateCreation(LocalDateTime.now());
        mockPublication.setUserId(mockUser.getId());
        mockPublication.setTags(new HashSet<>());

            // Configura el mock para devolver un usuario de prueba cuando se llame al método findUserResponseById con el ID 1
        when(userRepository.findUserResponseById(1L))
                .thenReturn(Optional.of(new UserResponse(1L, "John Doe", "johndoe@example.com", "jde", "image.png" )));


    }

    @Test
    public void testCreatePublication() {
        PublicationRequest request = new PublicationRequest();
        request.setTitle("New Publication");
        request.setDescription("New Description");
        request.setIdTags(Set.of(1L)); // Simula tags

        // Simular que el usuario existe
        when(userRepository.existsById(anyLong())).thenReturn(true); // Simular la existencia del usuario
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(publicationRepository.save(any(Publication.class))).thenReturn(mockPublication);

        // Simular autenticación
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(mockUser.getUsername());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        // Ejecutar el método a probar
        PublicationResponse response = publicationService.createPublication(request);

        // Verificaciones
        assertNotNull(response);
        assertEquals("New Publication", response.getTitle());
        verify(publicationRepository, times(1)).save(any(Publication.class));
    }


    @Test
    public void testGetPublication() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(mockPublication));

        PublicationResponse response = publicationService.findPublicationById(1L);

        assertNotNull(response);
        assertEquals(mockPublication.getTitle(), response.getTitle());
        assertEquals(mockPublication.getDescription(), response.getDescription());
    }

    @Test
    public void testGetPublicationNotFound() {
        when(publicationRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> publicationService.findPublicationById(2L));
    }

    @Test
    public void testUpdatePublication() {
        PublicationRequest request = new PublicationRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setIdTags(Set.of(1L));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser)); // Asegúrate de que el usuario existe
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(mockPublication));
        when(publicationRepository.save(any(Publication.class))).thenReturn(mockPublication);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(mockUser.getUsername());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        PublicationResponse response = publicationService.updatePublication(1L, request);

        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        verify(publicationRepository, times(1)).save(mockPublication);
    }

    @Test
    public void testUpdatePublicationNotFound() {
        PublicationRequest request = new PublicationRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");

        when(publicationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> publicationService.updatePublication(1L, request));
    }

    @Test
    public void testDeletePublication() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(mockPublication));

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(mockUser.getUsername());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUserDetails, null));

        publicationService.deletePublication(1L);

        verify(publicationRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeletePublicationNotFound() {
        when(publicationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> publicationService.deletePublication(1L));
    }
}
