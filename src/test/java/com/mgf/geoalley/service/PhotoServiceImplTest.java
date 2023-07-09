package com.mgf.geoalley.service;

import com.mgf.geoalley.exceptions.PhotoNotFoundException;
import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.repository.PhotoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PhotoServiceImplTest {

    @Mock
    private PhotoRepository mockPhotoRepository;

    private PhotoServiceImpl photoServiceImplUnderTest;

    @BeforeEach
    public void setUp() {
        photoServiceImplUnderTest = new PhotoServiceImpl(mockPhotoRepository);
    }

    @Test
    public void testGetAllPhotos() {

    	final List<Photo> photos = List.of(new Photo("url", "description", new User("username", "email", "password")));
        when(mockPhotoRepository.findAll()).thenReturn(photos);

        final List<Photo> result = photoServiceImplUnderTest.getAllPhotos();

    }

    @Test
    public void testGetAllPhotos_PhotoRepositoryReturnsNoItems() {
     
    	when(mockPhotoRepository.findAll()).thenReturn(Collections.emptyList());

        final List<Photo> result = photoServiceImplUnderTest.getAllPhotos();

        assertThat(result).isEqualTo(Collections.emptyList());
    }


    @Test
    public void testGetPhotoById() throws Exception {

    	final Optional<Photo> photo = Optional.of(
                new Photo("url", "description", new User("username", "email", "password")));
        when(mockPhotoRepository.findById(0)).thenReturn(photo);

        final Photo result = photoServiceImplUnderTest.getPhotoById(0);

    }

    @Test
    public void testGetPhotoById_PhotoRepositoryReturnsAbsent() {

    	when(mockPhotoRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> photoServiceImplUnderTest.getPhotoById(0)).isInstanceOf(PhotoNotFoundException.class);
    }


    @Test
    public void testShowEditPhoto_PhotoRepositoryReturnsAbsent() {

    	final Model model = new ConcurrentModel();
        final HttpServletRequest request = null;
        when(mockPhotoRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> photoServiceImplUnderTest.showEditPhoto(0, model, request))
                .isInstanceOf(PhotoNotFoundException.class);
    }


    @Test
    public void testEditPhoto_PhotoRepositoryFindByIdReturnsAbsent() {

    	final HttpServletRequest request = null;
        when(mockPhotoRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> photoServiceImplUnderTest.editPhoto(0, request))
                .isInstanceOf(PhotoNotFoundException.class);
    }



    @Test
    public void testFindById() throws Exception {

    	final Optional<Photo> photo = Optional.of(
                new Photo("url", "description", new User("username", "email", "password")));
        when(mockPhotoRepository.findById(0)).thenReturn(photo);

        final Photo result = photoServiceImplUnderTest.findById(0);

    }

    @Test
    public void testFindById_PhotoRepositoryReturnsAbsent() {
        when(mockPhotoRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> photoServiceImplUnderTest.findById(0)).isInstanceOf(PhotoNotFoundException.class);
    }

}
