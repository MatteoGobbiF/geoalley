package com.mgf.geoalley.service;

import com.mgf.geoalley.model.Photo;
import com.mgf.geoalley.model.PhotoGuess;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.repository.PhotoGuessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PhotoGuessServiceImplTest {

    @Mock
    private PhotoGuessRepository mockPhotoGuessRepository;

    private PhotoGuessServiceImpl photoGuessServiceImplUnderTest;

    @BeforeEach
    public void setUp() {
        photoGuessServiceImplUnderTest = new PhotoGuessServiceImpl(mockPhotoGuessRepository);
    }

    @Test
    public void testAddGuess() {

        final PhotoGuess guess = new PhotoGuess("comment",
                new Photo("url", "description", new User("username", "email", "password")),
                new User("username", "email", "password"), 0.0, 0.0);

        final PhotoGuess photoGuess = new PhotoGuess("comment",
                new Photo("url", "description", new User("username", "email", "password")),
                new User("username", "email", "password"), 0.0, 0.0);
        when(mockPhotoGuessRepository.save(any(PhotoGuess.class))).thenReturn(photoGuess);

        photoGuessServiceImplUnderTest.addGuess(guess);

        verify(mockPhotoGuessRepository).save(any(PhotoGuess.class));
    }


}
